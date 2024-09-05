package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Subtask> subtasks;
    private LocalDateTime endTime;

    public Epic(int id, String name, String description) {
        super(id, name, description, Status.NEW, Duration.ZERO, null);
        this.subtasks = new ArrayList<>();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        this.subtasks.add(subtask);
        updateEpicFields();
        updateStatus();
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        updateEpicFields();
        updateStatus();
    }

    @Override
    public String toString() {
        return String.format("%d,EPIC,%s,%s,%s",
                getId(), getName(), getStatus(), getDescription());
    }

    public void updateEpicFields() {
        Duration totalDuration = Duration.ZERO;
        LocalDateTime earliestStart = null;
        LocalDateTime latestEnd = null;

        for (Subtask subtask : subtasks) {
            totalDuration = totalDuration.plus(subtask.getDuration());
            if (earliestStart == null || subtask.getStartTime().isBefore(earliestStart)) {
                earliestStart = subtask.getStartTime();
            }
            if (latestEnd == null || subtask.getEndTime().isAfter(latestEnd)) {
                latestEnd = subtask.getEndTime();
            }
        }

        setDuration(totalDuration);
        setStartTime(earliestStart);
        this.endTime = latestEnd;
    }

    public void updateStatus() {
        boolean allDone = subtasks.stream().allMatch(subtask -> subtask.getStatus() == Status.DONE);
        boolean allNew = subtasks.stream().allMatch(subtask -> subtask.getStatus() == Status.NEW);

        if (allDone) {
            setStatus(Status.DONE);
        } else if (allNew) {
            setStatus(Status.NEW);
        } else {
            setStatus(Status.IN_PROGRESS);
        }
    }
}