package tasks.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import tasks.model.ArrayTaskList;
import tasks.model.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TasksServiceTest {
    private final TasksService tasksService = new TasksService(new ArrayTaskList(), new DateService());
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd, HH:mm");

    // Test 1: Valid Task with title "Meeting"
    @ParameterizedTest
    @CsvSource({
            "'Meeting', '2025-04-03, 16:00', null, false, 0, true"
    })
    @DisplayName("Test valid task creation (Meeting)")
    void testValidTaskCreationECP(String title, String startDateTime, String endDateTime,
                                  boolean isRepeated, int interval, boolean isActive) {

        String datePart = startDateTime.split(",")[0]; // Extract the date part

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); // Define the date format

        Date startDate = null; // Parse the date
        try {
            startDate = formatter.parse(datePart);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Parsed Date: " + startDate);

        // Act
        Task task = tasksService.buildTask(title, startDate, null, isRepeated, interval, isActive);

        // Assert
        assertNotNull(task);
        assertEquals(title, task.getTitle());
        System.out.println("task entry created with title \"Meeting\"");

    }

    // Test 2: Task where endDateTime equals startDateTime
    @ParameterizedTest
    @CsvSource({
            "'Shopping', '2025-04-05, 00:00', null, false, 0, true"
    })
    @DisplayName("Test task with equal start and end date (Shopping)")
    void testTaskWithEqualStartAndEndDateECP(String title, String startDateTime, String endDateTime,
                                             boolean isRepeated, int interval, boolean isActive) {
        // Arrange

        String datePart = startDateTime.split(",")[0]; // Extract the date part

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); // Define the date format

        Date startDate = null; // Parse the date
        try {
            startDate = formatter.parse(datePart);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Parsed Date: " + startDate);
        // Act
        Task task = tasksService.buildTask(title, startDate, startDate, isRepeated, interval, isActive);

        // Assert
        assertNotNull(task);
        assertEquals(title, task.getTitle());

        System.out.println("task entry created, with endDateTime = startEndTime");
    }

    // Test 3: Task with empty title
    @ParameterizedTest
    @CsvSource({
            "'', '2025-04-03, 16:00', '2025-04-04, 16:00', true, 3600, true"
    })
    @DisplayName("Test empty title throws exception")
    void testEmptyTitleThrowsExceptionECP(String title, String startDateTime, String endDateTime,
                                          boolean isRepeated, int interval, boolean isActive) {

        String datePart = startDateTime.split(",")[0]; // Extract the date part

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); // Define the date format

        Date startDate = null; // Parse the date
        try {
            startDate = formatter.parse(datePart);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Parsed Date: " + startDate);

        // Act & Assert
        Date finalStartDate = startDate;
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            tasksService.buildTask(title, finalStartDate, null, isRepeated, interval, isActive);
        });

        assertNotNull(exception);
        assertEquals("Title must be a non-empty string with a maximum length of 255 characters", exception.getMessage());

        System.out.println("Title must be a non-empty string with a maximum length of 255 characters");
    }

    // Test 4: Task with invalid endDateTime (should throw exception)
    @ParameterizedTest
    @CsvSource({
            "'Task', '2025-04-01, 10:30', '2025-04-01, 09:00', true, 1800, false"
    })
    @DisplayName("Test endDateTime before startDateTime throws exception")
    void testEndDateBeforeStartDateThrowsExceptionECP(String title, String startDateTime, String endDateTime,
                                                      boolean isRepeated, int interval, boolean isActive) throws ParseException {
        // Arrange

        Date startDate = null; // Parse the date
        startDate = formatter.parse(startDateTime);
        System.out.println("Parsed Date: " + startDate);

        Date endDate = formatter.parse(endDateTime); // Extract the date part

        System.out.println("Parsed Date: " + startDate);

        // Act & Assert
        Date finalStartDate = startDate;
        Date finalEndDate = endDate;
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            tasksService.buildTask(title, finalStartDate, finalEndDate, isRepeated, interval, isActive);
        });

        assertNotNull(exception);
        System.out.println("Start time must be before end time");


    }

    // Test 5: Valid Task with title "A"
    @ParameterizedTest
    @CsvSource({
            "'A', '2025-04-03, 10:00', '2025-04-03, 19:00', true, 1800, true"
    })
    @DisplayName("Test valid task creation with title 'A'")
    void testValidTaskCreationWithABVA(String title, String startDateTime, String endDateTime,
                                       boolean isRepeated, int interval, boolean isActive) throws ParseException {
        // Arrange
        Date startDate = null; // Parse the date
        startDate = formatter.parse(startDateTime);
        System.out.println("Parsed Date: " + startDate);

        Date endDate = formatter.parse(endDateTime); // Extract the date part

        System.out.println("Parsed Date: " + endDate);

        // Act
        Task task = tasksService.buildTask(title, startDate, endDate, isRepeated, interval, isActive);

        // Assert
        assertNotNull(task);
        assertEquals(title, task.getTitle());
        assertTrue(task.isActive());
        System.out.println("task entry created with title \"A\"");
    }

    // Test 6: Title length exceeds limit (exception thrown)
    @ParameterizedTest
    @CsvSource({
            "'A'.repeat(256), '2025-04-03, 10:00', null, false, 0, false"
    })
    @DisplayName("Test title length exceeds limit throws exception")
    void testTitleLengthExceedsLimitBVA(String title, String startDateTime, String endDateTime,
                                        boolean isRepeated, int interval, boolean isActive) throws ParseException {
        // Arrange
        Date startDate = null; // Parse the date
        startDate = formatter.parse(startDateTime);
        System.out.println("Parsed Date: " + startDate);


        String repetitive_title = "A".repeat(256);

        // Act & Assert
        Date finalStartDate = startDate;
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            tasksService.buildTask(repetitive_title, finalStartDate, null, isRepeated, interval, isActive);
        });

        assertNotNull(exception);
        assertEquals("Title must be a non-empty string with a maximum length of 255 characters", exception.getMessage());

        System.out.println("Title must be a non-empty string with a maximum length of 255 characters");
    }

    // Test 7: Repeated task entry created (valid scenario)
    @ParameterizedTest
    @CsvSource({
            "'Task', '2025-04-03, 10:00', '2025-04-03, 10:01', true, 6000, true"
    })
    @DisplayName("Test repeated task entry created")
    void testRepeatedTaskCreationBVA(String title, String startDateTime, String endDateTime,
                                     boolean isRepeated, int interval, boolean isActive) throws ParseException {
        // Arrange
        Date startDate = null; // Parse the date
        startDate = formatter.parse(startDateTime);
        System.out.println("Parsed Date: " + startDate);

        Date endDate = formatter.parse(endDateTime); // Extract the date part

        System.out.println("Parsed Date: " + endDate);

        // Act
        Task task = tasksService.buildTask(title, startDate, endDate, isRepeated, interval, isActive);

        // Assert
        assertNotNull(task);
        assertTrue(task.isRepeated());
        System.out.println("repeated task entry created");
    }

    // Test 8: End date before start date (exception thrown)
    @ParameterizedTest
    @CsvSource({
            "'Task', '2025-04-03, 10:00', '2025-04-03, 09:59', true, 60, false"
    })
    @DisplayName("Test endDate before startDate throws exception")
    void testEndDateBeforeStartDateThrowsExceptionWithABVA(String title, String startDateTime, String endDateTime,
                                                           boolean isRepeated, int interval, boolean isActive) throws ParseException {
        // Arrange
        Date startDate = null; // Parse the date
        startDate = formatter.parse(startDateTime);
        System.out.println("Parsed Date: " + startDate);

        Date endDate = formatter.parse(endDateTime); // Extract the date part

        System.out.println("Parsed Date: " + endDate);

        Date finalStartDate = startDate;
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            tasksService.buildTask(title, finalStartDate, endDate, isRepeated, interval, isActive);
        });

        assertNotNull(exception);
        assertEquals("Start time must be before end time", exception.getMessage());

        System.out.println("Start time must be before end time");
    }
}