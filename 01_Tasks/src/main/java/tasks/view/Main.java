package tasks.view;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import tasks.controller.TaskManagerController;
import tasks.controller.Notificator;
import tasks.model.ArrayTaskList;
import tasks.repository.TaskIO;
import tasks.services.DateService;
import tasks.services.TasksService;

import java.io.File;
import java.io.IOException;

public class Main extends Application {
    public static Stage primaryStage;
    private static final int defaultWidth = 820;
    private static final int defaultHeight = 520;
    private static final Logger log = Logger.getLogger(Main.class.getName());

    private final ArrayTaskList savedTasksList = new ArrayTaskList();

    private static ClassLoader classLoader = Main.class.getClassLoader();
    public static File savedTasksFile = new File("data/tasks.txt");

    private TasksService tasksService;
    private DateService dateService;

    @Override
    public void start(Stage primaryStage) throws Exception {

        log.info("saved data reading");
        if (savedTasksFile.length() != 0) {
            TaskIO.readBinary(savedTasksList, savedTasksFile);
        }
        try {
            log.info("application start");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = loader.load();    // loader.load(this.getClass().getResource("/fxml/main.fxml"));
            TaskManagerController ctrl= loader.getController();

            dateService = new DateService();
            tasksService = new TasksService(savedTasksList, dateService);

            ctrl.setService(tasksService, dateService);
            primaryStage.setTitle("Task Manager");
            primaryStage.setScene(new Scene(root, defaultWidth, defaultHeight));
            primaryStage.setMinWidth(defaultWidth);
            primaryStage.setMinHeight(defaultHeight);
            primaryStage.show();
        }
        catch (IOException e){
            e.printStackTrace();
            log.error("error reading main.fxml");
        }
        primaryStage.setOnCloseRequest(we -> {
            System.exit(0);
        });
        new Notificator(FXCollections.observableArrayList(tasksService.getObservableList())).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}