package tasks.model;

import org.apache.log4j.Logger;
import tasks.services.TaskIO;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Task implements Serializable {
    private String title;
    private Date time;
    private Date start;
    private Date end;
    private int interval;
    private boolean active;

    private static final Logger log = Logger.getLogger(Task.class.getName());
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static SimpleDateFormat getDateFormat() {
        return sdf;
    }

    // Constructor for non-repeated task
    public Task(String title, Date time) {
        if (time.getTime() < 0) {
            log.error("Time below bound");
            throw new IllegalArgumentException("Time cannot be negative");
        }
        this.title = title;
        this.time = new Date(time.getTime());
        this.start = this.time;
        this.end = this.time;
    }

    // Constructor for repeated task
    public Task(String title, Date start, Date end, int interval) {
        if (start.getTime() < 0 || end.getTime() < 0) {
            log.error("Time below bound");
            throw new IllegalArgumentException("Time cannot be negative");
        }
        if (interval < 1) {
            log.error("Interval < 1");
            throw new IllegalArgumentException("Interval should be > 1");
        }
        this.title = title;
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());
        this.interval = interval;
        this.time = this.start;
    }

    // Copy Constructor
    public Task(Task other) {
        this.title = other.title;
        this.time = new Date(other.time.getTime());
        this.start = new Date(other.start.getTime());
        this.end = new Date(other.end.getTime());
        this.interval = other.interval;
        this.active = other.active;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = new Date(time.getTime());
        this.start = this.time;
        this.end = this.time;
        this.interval = 0;
    }

    public Date getStartTime() {
        return start;
    }

    public Date getEndTime() {
        return end;
    }

    public int getRepeatInterval() {
        return (interval > 0) ? interval : 0;
    }

    public void setTime(Date start, Date end, int interval) {
        this.time = new Date(start.getTime());
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());
        this.interval = interval;
    }

    public boolean isRepeated() {
        return interval > 0;
    }

    // Refactored nextTimeAfter method
    public Date nextTimeAfter(Date current) {
        if (!isActive() || current.after(end) || current.equals(end)) {
            return null;
        }
        if (!isRepeated()) {
            return (current.before(time)) ? time : null;
        }
        return getNextRepeatedTime(current);
    }

    // Helper method to simplify logic for repeated tasks
    private Date getNextRepeatedTime(Date current) {
        if (current.before(start)) {
            return start;
        }
        long nextTime = start.getTime();
        while (nextTime <= end.getTime()) {
            long nextAfter = nextTime + (interval * 1000);
            if (current.before(new Date(nextAfter))) {
                return new Date(nextAfter);
            }
            nextTime = nextAfter;
        }
        return null;
    }

    // Duplicate methods for TableView
    public String getFormattedDateStart() {
        return sdf.format(start);
    }

    public String getFormattedDateEnd() {
        return sdf.format(end);
    }

    public String getFormattedRepeated() {
        return isRepeated() ? "Every " + TaskIO.getFormattedInterval(interval) : "No";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return interval == task.interval &&
                active == task.active &&
                title.equals(task.title) &&
                time.equals(task.time) &&
                start.equals(task.start) &&
                end.equals(task.end);
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + time.hashCode();
        result = 31 * result + start.hashCode();
        result = 31 * result + end.hashCode();
        result = 31 * result + interval;
        result = 31 * result + (active ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", time=" + time +
                ", start=" + start +
                ", end=" + end +
                ", interval=" + interval +
                ", active=" + active +
                '}';
    }
}
