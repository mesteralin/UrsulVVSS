package tasks.services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tasks.model.ArrayTaskList;
import tasks.model.Task;
import tasks.repository.TaskIO;
import tasks.utils.TasksOperations;

import java.time.LocalDate;
import java.util.Date;

public class TasksService {

    private final ArrayTaskList tasks;
    private final DateService dateService;

    public TasksService(ArrayTaskList tasks, DateService dateService) {
        this.tasks = tasks;
        this.dateService = dateService;
    }

    public ObservableList<Task> getObservableList() {
        return FXCollections.observableArrayList(tasks.getAll());
    }

    public String getIntervalInHours(Task task) {
        int seconds = task.getRepeatInterval();
        int minutes = seconds / DateService.SECONDS_IN_MINUTE;
        int hours = minutes / DateService.MINUTES_IN_HOUR;
        minutes = minutes % DateService.MINUTES_IN_HOUR;
        return dateService.formTimeUnit(hours) + ":" + dateService.formTimeUnit(minutes);//hh:MM
    }

    public Iterable<Task> filterTasks(Date start, Date end) {
        TasksOperations tasksOps = new TasksOperations(getObservableList());
        Iterable<Task> filtered = tasksOps.incoming(start, end);
//        Iterable<Task> filtered = tasks.incoming(start, end);

        return filtered;
    }

    public Task buildTask(String title, Date startDateTime, Date endDateTime, boolean isRepeated, int interval, boolean isActive) {
        Task task;

        if (title.isEmpty() || title.length() > 255) {
            throw new IllegalArgumentException("Title must be a non-empty string with a maximum length of 255 characters");
        }

        if (isRepeated) {
            if (startDateTime.after(endDateTime)) {
                throw new IllegalArgumentException("Start time must be before end time");
            }
            task = new Task(title, startDateTime, endDateTime, interval);
        } else {
            task = new Task(title, startDateTime);
        }

        task.setActive(isActive);
        return task;
    }

    public void addTask(Task task, ObservableList<Task> taskList) {
        taskList.add(task);
        TaskIO.rewriteFile(taskList);
    }

    public void editTask(Task existingTask, Task updatedTask, ObservableList<Task> taskList) {
        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.get(i).equals(existingTask)) {
                taskList.set(i, updatedTask);
                break;
            }
        }
        TaskIO.rewriteFile(taskList);
    }
}