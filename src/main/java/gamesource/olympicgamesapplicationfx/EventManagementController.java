package gamesource.olympicgamesapplicationfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EventManagementController {
    @FXML
    public ImageView circleImageView;
    private Stage stage; // Stage reference
    // Method to set the stage

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private TableView<Event> eventTableView;
    @FXML
    private TableColumn<Event, String> eventName;
    @FXML
    private TableColumn<Event, String> eventDate;
    @FXML
    private TableColumn<Event, String> eventLocation;
    @FXML
    private TableColumn<Event, String> eventCategory;
    @FXML
    private TableColumn<Event, String> eventDiscipline;

    @FXML
    public void initialize() {

        Circle clip = new Circle(50, 50, 50); // x, y, radius
        circleImageView.setClip(clip);
        eventName.setCellValueFactory(new PropertyValueFactory<>("name"));
        eventDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        eventLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        eventCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        eventDiscipline.setCellValueFactory(new PropertyValueFactory<>("discipline"));

        // Load initial data
        updateTableView();
    }

    public void updateTableView() {
        // Load data from source
        List<Event> eventData = readEventDataFromSource();
        // Populate TableView
        eventTableView.getItems().setAll(eventData);
    }

    private static List<Event> readEventDataFromSource() {
        List<Event> eventData = new ArrayList<>();

        Path dataDir = Paths.get("data");
        if (!Files.exists(dataDir)) {
            try {
                Files.createDirectories(dataDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Define the path to the Excel file inside the data directory
        File file = new File(dataDir.toFile(), "events.xlsx");


        try (FileInputStream inputStream = new FileInputStream(new File(file.getPath()));
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = sheet.iterator();

            // Skip the header row
            if (iterator.hasNext()) {
                iterator.next();
            }

            while (iterator.hasNext()) {
                Row currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();

                String name = cellIterator.next().getStringCellValue();
                String date = cellIterator.next().getStringCellValue();
                String location = cellIterator.next().getStringCellValue();
                String description = cellIterator.next().getStringCellValue();
                String category = cellIterator.next().getStringCellValue();
                String discipline = cellIterator.next().getStringCellValue();

                eventData.add(new Event(name, date, location, category, discipline));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return eventData;
    }

    // Define the Event class
    public static class Event {
        private String name;
        private String date;
        private String location;
        private String category;
        private String discipline;

        public Event(String name, String date, String location, String category, String discipline) {
            this.name = name;
            this.date = date;
            this.location = location;
            this.category = category;
            this.discipline = discipline;
        }

        public String getName() {
            return name;
        }

        public String getDate() {
            return date;
        }

        public String getLocation() {
            return location;
        }

        public String getCategory() {
            return category;
        }

        public String getDiscipline() {
            return discipline;
        }
    }




    public void navigateToCreateEvent(ActionEvent actionEvent) {
        try {
            System.out.println("navigate to create event");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("create-event.fxml"));
            Parent root = loader.load();

            // Create a new stage for the popup
            Stage createEventStage = new Stage();
            createEventStage.setTitle("Create Event");
            createEventStage.initModality(Modality.APPLICATION_MODAL); // Set as modal dialog
            createEventStage.initOwner(stage); // Set the owner stage

            // Pass the stage reference to the controller
            CreateEventController createEventController = loader.getController();
            createEventController.setStage(createEventStage);

            // Set the scene and show the stage
            Scene scene = new Scene(root);
            createEventStage.setScene(scene);
            createEventStage.showAndWait(); // Show the stage and wait for it to be closed
            updateTableView();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Done");
    }

    public void navigateToAssignmentAthleteEvent(ActionEvent actionEvent) throws IOException {
        System.out.println("navigate to assignment of athletes to event");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("assignment-of-athletes-to-event.fxml"));
        Parent root = loader.load();

        // Create a new stage for the popup
        Stage assignmentStage = new Stage();
        assignmentStage.setTitle("Assignment of Athletes to Event");
        assignmentStage.initModality(Modality.APPLICATION_MODAL); // Set as modal dialog
        assignmentStage.initOwner(stage); // Set the owner stage

        // Pass the stage reference to the controller
        AssignmentOfAthletesToEventController assignmentOfAthletesToEventController = loader.getController();
        assignmentOfAthletesToEventController.setStage(assignmentStage);

        // Set the scene and show the stage
        Scene scene = new Scene(root);
        assignmentStage.setScene(scene);
        assignmentStage.showAndWait(); // Show the stage and wait for it to be closed
    }

    public void navigateToMenu(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
            Parent root = loader.load();

            // Pass the stage reference to the MenuController
            MenuController menuController = loader.getController();
            menuController.setStage(stage);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
