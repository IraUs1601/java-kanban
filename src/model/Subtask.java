package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(int id, String name, String description, Status status, int epicId, Duration duration, LocalDateTime startTime) {
        super(id, name, description, status, duration, startTime);

        if (epicId == id) {
            throw new IllegalArgumentException("Подзадача не может быть отдельным эпиком.");
        }
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, Status status, int epicId) {
        this(id, name, description, status, epicId, Duration.ofMinutes(0), LocalDateTime.now());
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId &&
                getId() == subtask.getId() &&
                Objects.equals(getName(), subtask.getName()) &&
                Objects.equals(getDescription(), subtask.getDescription()) &&
                getStatus() == subtask.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.getId(), super.getName(), super.getDescription(), super.getStatus(), epicId);
    }

    @Override
    public String toString() {
        return String.format("%d,SUBTASK,%s,%s,%s,%d",
                getId(), getName(), getStatus(), getDescription(), getEpicId());

    }
}