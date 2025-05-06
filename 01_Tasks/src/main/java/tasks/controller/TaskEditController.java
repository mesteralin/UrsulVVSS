package tasks.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import tasks.model.Task;
import tasks.services.DateService;
import tasks.services.TasksService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

public class TaskEditController {

    private static Button clickedButton;

    private static final Logger log = Logger.getLogger(TaskEditController.class.getName());

    public static void setClickedButton(Button clickedButton) {
        TaskEditController.clickedButton = clickedButton;
    }

    public static void setCurrentStage(Stage currentStage) {
        TaskEditController.currentStage = currentStage;
    }

    private static Stage currentStage;

    private Task currentTask;
    private ObservableList<Task> tasksList;
    private TasksService service;
    private DateService dateService;


    private boolean incorrectInputMade;
    @FXML
    private TextField fieldTitle;
    @FXML
    private DatePicker datePickerStart;
    @FXML
    private TextField txtFieldTimeStart;
    @FXML
    private DatePicker datePickerEnd;
    @FXML
    private TextField txtFieldTimeEnd;
    @FXML
    private TextField fieldInterval;
    @FXML
    private CheckBox checkBoxActive;
    @FXML
    private CheckBox checkBoxRepeated;

    private static final String DEFAULT_START_TIME = "8:00";
    private static final String DEFAULT_END_TIME = "10:00";
    private static final String DEFAULT_INTERVAL_TIME = "0:30";

    public void setTasksList(ObservableList<Task> tasksList) {
        this.tasksList = tasksList;
    }

    public void setService(TasksService tasksService, DateService dateService) {
        this.service = tasksService;
        this.dateService = dateService;
    }

    public void setCurrentTask(Task task) {
        this.currentTask = task;
        switch (clickedButton.getId()) {
            case "btnNew":
                initNewWindow("New Task");
                break;
            case "btnEdit":
                initEditWindow("Edit Task");
                break;
        }
    }

    @FXML
    public void initialize() {
        log.info("new/edit window initializing");
//        switch (clickedButton.getId()){
//            case  "btnNew" : initNewWindow("New Task");
//                break;
//            case "btnEdit" : initEditWindow("Edit Task");
//                break;
//        }

    }

    private void initNewWindow(String title) {
        currentStage.setTitle(title);
        datePickerStart.setValue(LocalDate.now());
        txtFieldTimeStart.setText(DEFAULT_START_TIME);
    }

    private void initEditWindow(String title) {
        currentStage.setTitle(title);
        fieldTitle.setText(currentTask.getTitle());
        datePickerStart.setValue(dateService.getLocalDateValueFromDate(currentTask.getStartTime()));
        txtFieldTimeStart.setText(dateService.getTimeOfTheDayFromDate(currentTask.getStartTime()));

        if (currentTask.isRepeated()) {
            checkBoxRepeated.setSelected(true);
            hideRepeatedTaskModule(false);
            datePickerEnd.setValue(dateService.getLocalDateValueFromDate(currentTask.getEndTime()));
            fieldInterval.setText(service.getIntervalInHours(currentTask));
            txtFieldTimeEnd.setText(dateService.getTimeOfTheDayFromDate(currentTask.getEndTime()));
        }
        if (currentTask.isActive()) {
            checkBoxActive.setSelected(true);

        }
    }

    @FXML
    public void switchRepeatedCheckbox(ActionEvent actionEvent) {
        CheckBox source = (CheckBox) actionEvent.getSource();
        if (source.isSelected()) {
            hideRepeatedTaskModule(false);
        } else if (!source.isSelected()) {
            hideRepeatedTaskModule(true);
        }
    }

    private void hideRepeatedTaskModule(boolean toShow) {
        datePickerEnd.setDisable(toShow);
        fieldInterval.setDisable(toShow);
        txtFieldTimeEnd.setDisable(toShow);

        datePickerEnd.setValue(LocalDate.now());
        txtFieldTimeEnd.setText(DEFAULT_END_TIME);
        fieldInterval.setText(DEFAULT_INTERVAL_TIME);
    }

    @FXML
    public void saveChanges() {
        Task collectedFieldsTask = collectFieldsData();
        if (incorrectInputMade) return;

        if (currentTask == null) {   // no task was chosen -> add button was pressed
            service.addTask(collectedFieldsTask, tasksList);
        } else {
            service.editTask(currentTask, collectedFieldsTask, tasksList);
            currentTask = null;
        }
        TaskManagerController.editNewStage.close();
    }

    @FXML
    public void closeDialogWindow() {
        TaskManagerController.editNewStage.close();
    }

    private Task collectFieldsData() {
        incorrectInputMade = false;
        Task result = null;
        try {
            String title = fieldTitle.getText();
            Date startDateTime = dateService.getDateMergedWithTime(txtFieldTimeStart.getText(), dateService.getDateValueFromLocalDate(datePickerStart.getValue()));

            int interval = 0;
            Date endDateTime = null;
            if (checkBoxRepeated.isSelected()) {
                endDateTime = dateService.getDateMergedWithTime(txtFieldTimeEnd.getText(), dateService.getDateValueFromLocalDate(datePickerEnd.getValue()));
                interval = dateService.parseFromStringToSeconds(fieldInterval.getText());
            }

            result = service.buildTask(title, startDateTime, endDateTime, checkBoxRepeated.isSelected(), interval, checkBoxActive.isSelected());
        } catch (RuntimeException e) {
            incorrectInputMade = true;
            try {
                Stage stage = new Stage();
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/field-validator.fxml")));
                stage.setScene(new Scene(root, 350, 150));
                stage.setResizable(false);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();
            } catch (IOException ioe) {
                log.error("error loading field-validator.fxml");
            }
        }

        System.out.println(result);
        return result;
    }
}