package tasks.model;

import org.apache.log4j.Logger;

import java.util.*;

public class ArrayTaskList extends TaskList {

    private Task[] tasks;
    private int numberOfTasks;
    private int currentCapacity;
    private static final Logger log = Logger.getLogger(ArrayTaskList.class.getName());

    private class ArrayTaskListIterator implements Iterator<Task> {
        private int cursor;
        private int lastCalled = -1;

        @Override
        public boolean hasNext() {
            return cursor < numberOfTasks;
        }

        @Override
        public Task next() {
            if (!hasNext()) {
                log.error("Next iterator element doesn't exist");
                throw new NoSuchElementException("No next element");
            }
            lastCalled = cursor;
            return getTask(cursor++);
        }

        @Override
        public void remove() {
            if (lastCalled == -1) {
                throw new IllegalStateException("Cannot remove before calling next()");
            }
            ArrayTaskList.this.remove(getTask(lastCalled));
            cursor = lastCalled;
            lastCalled = -1;
        }
    }

    // Default Constructor
    public ArrayTaskList() {
        this.currentCapacity = 10;
        this.tasks = new Task[currentCapacity];
    }

    // Copy Constructor
    public ArrayTaskList(ArrayTaskList other) {
        this.currentCapacity = other.currentCapacity;
        this.numberOfTasks = other.numberOfTasks;
        this.tasks = new Task[other.currentCapacity];

        for (int i = 0; i < other.numberOfTasks; i++) {
            this.tasks[i] = other.getTask(i);
        }
    }

    @Override
    public Iterator<Task> iterator() {
        return new ArrayTaskListIterator();
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            throw new NullPointerException("Task shouldn't be null");
        }

        if (numberOfTasks == currentCapacity) { // Ensure capacity before adding
            currentCapacity *= 2;
            tasks = Arrays.copyOf(tasks, currentCapacity);
        }

        tasks[numberOfTasks++] = task;
    }

    @Override
    public boolean remove(Task task) {
        if (task == null) return false;

        int indexOfTaskToDelete = -1;
        for (int i = 0; i < numberOfTasks; i++) {
            if (task.equals(tasks[i])) {
                indexOfTaskToDelete = i;
                break;
            }
        }

        if (indexOfTaskToDelete >= 0) {
            System.arraycopy(tasks, indexOfTaskToDelete + 1, tasks, indexOfTaskToDelete, numberOfTasks - indexOfTaskToDelete - 1);
            tasks[--numberOfTasks] = null; // Prevent memory leaks
            return true;
        }

        return false;
    }

    @Override
    public int size() {
        return this.numberOfTasks;
    }

    @Override
    public Task getTask(int index) {
        if (index < 0 || index >= numberOfTasks) {
            log.error("Index out of bounds: " + index);
            throw new IndexOutOfBoundsException("Index not found");
        }
        return tasks[index];
    }

    @Override
    public List<Task> getAll() {
        return Collections.unmodifiableList(new ArrayList<>(Arrays.asList(Arrays.copyOf(tasks, numberOfTasks))));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArrayTaskList that = (ArrayTaskList) o;
        return numberOfTasks == that.numberOfTasks && Arrays.equals(Arrays.copyOf(tasks, numberOfTasks), Arrays.copyOf(that.tasks, that.numberOfTasks));
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(Arrays.copyOf(tasks, numberOfTasks));
        result = 31 * result + numberOfTasks;
        result = 31 * result + currentCapacity;
        return result;
    }

    @Override
    public String toString() {
        return "ArrayTaskList{" +
                "tasks=" + Arrays.toString(Arrays.copyOf(tasks, numberOfTasks)) +
                ", numberOfTasks=" + numberOfTasks +
                ", currentCapacity=" + currentCapacity +
                '}';
    }
}
