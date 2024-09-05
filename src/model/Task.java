package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private int id;
    private final String name;
    private final String description;
    private Status status;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(int id, String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(int id, String name, String description, Status status) {
        this(id, name, description, status, Duration.ofMinutes(0), LocalDateTime.now());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    public boolean isTimeOverlapping(Task other) {
        if (this.getStartTime() == null || other.getStartTime() == null) {
            return false;
        }

        LocalDateTime thisEndTime = this.getEndTime();
        LocalDateTime otherEndTime = other.getEndTime();

        return this.getStartTime().isBefore(otherEndTime) && other.getStartTime().isBefore(thisEndTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s",
                id,
                TaskType.TASK,
                name,
                status,
                description);
    }

    public enum Status {
        NEW,
        IN_PROGRESS,
        DONE
    }

    public enum TaskType {
        TASK,
        EPIC,
        SUBTASK
    }
}