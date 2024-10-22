package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTasksTest {

    private HttpTaskServer server;
    private TaskManager manager;
    private Gson gson;

    @BeforeEach
    void setUp() throws IOException {
        manager = new InMemoryTaskManager();

        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new TypeAdapter<Duration>() {
                    @Override
                    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
                        jsonWriter.value(duration.toSeconds());
                    }

                    @Override
                    public Duration read(JsonReader jsonReader) throws IOException {
                        return Duration.ofSeconds(jsonReader.nextLong());
                    }
                })
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        server = new HttpTaskServer(manager, gson);
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        @Override
        public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
            jsonWriter.value(localDateTime.format(formatter));
        }

        @Override
        public LocalDateTime read(JsonReader jsonReader) throws IOException {
            return LocalDateTime.parse(jsonReader.nextString(), formatter);
        }
    }

    @Test
    public void testGetTasksWithoutTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался ответ с кодом 200, но пришёл: " + response.statusCode());

        Task[] tasks = gson.fromJson(response.body(), Task[].class);
        assertEquals(0, tasks.length, "Ожидалось 0 задач, но вернулось: " + tasks.length);
    }

    @Test
    public void testMethodNotAllowed() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(405, response.statusCode(), "Ожидался код 405 Method Not Allowed");
    }

    @Test
    public void testCreateTask() throws IOException, InterruptedException {
        Task task = new Task(0, "Тест задачи", "Описание", Task.Status.NEW, Duration.ofMinutes(60), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Ожидался код 201 Created");

        Task[] tasks = manager.getAllTasks().toArray(new Task[0]);
        assertEquals(1, tasks.length, "Ожидалась 1 задача, но вернулось: " + tasks.length);
        assertEquals("Тест задачи", tasks[0].getName(), "Название задачи не совпадает");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task(1, "Тест задачи", "Описание", Task.Status.NEW, Duration.ofMinutes(60), LocalDateTime.now());
        manager.create(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks?id=1"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался код 200 OK");

        Task[] tasks = manager.getAllTasks().toArray(new Task[0]);
        assertEquals(0, tasks.length, "Ожидалось 0 задач, но вернулось: " + tasks.length);
    }
}