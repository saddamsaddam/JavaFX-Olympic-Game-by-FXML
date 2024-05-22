package gamesource.olympicgamesapplicationfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MenuController {


    public DatePicker eventDatePicker;
    @FXML
    private ImageView circleImageView;

    private Stage stage;


    @FXML
    private ListView<String>EventsListView;

    public void setStage(Stage stage) {
        this.stage = stage;
//        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
//        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
       // this.stage.setTitle("Olympic Games Application");
       // this.stage.setFullScreen(true);
    }

    @FXML
    public void initialize() {
        eventDatePicker.setOnAction(event -> loadEvents(eventDatePicker.getValue()));
        loadEvents(null); // Load all events initially
// Set up the circular clip for the ImageView
       // Circle clip = new Circle(60, 60, 60); // x, y, radius
       // circleImageView.setClip(clip);

        // Define an array of colors to use sequentially
        String[] colors = {"lightyellow", "lightpink"};

        // Set a custom cell factory
        EventsListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> listView) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item);
                            // Set background color based on the index
                            int index = getIndex();
                            String color = colors[index % colors.length];
                            setStyle("-fx-background-color: " + color + ";");
                        } else {
                            setText(null);
                            setStyle("");
                        }
                    }
                };
            }
        });

    }

    private void loadEvents(LocalDate selectedDate) {
        System.out.println("load");
        try {
            System.out.println("not null");
            EventsListView.getItems().clear(); // Clear previous items


            Path dataDir = Paths.get("data");
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }

            // Define the path to the Excel file inside the data directory
            File file = new File(dataDir.toFile(), "events.xlsx");


            FileInputStream fis = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(fis);

            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

            Map<LocalDate, List<String>> eventsWithDates = new TreeMap<>(); // Using TreeMap for automatic sorting
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Adjust this pattern as per your Excel date format

            // Iterate over rows starting from the second row (skipping header)
            Iterator<Row> rowIterator = sheet.iterator();
            rowIterator.next(); // Skip header row
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Cell eventNameCell = row.getCell(0); // Event name column
                Cell eventDateCell = row.getCell(1); // Event date column

                if (eventNameCell != null && eventDateCell != null) {
                    String eventName = eventNameCell.getStringCellValue();
                    LocalDate eventDate = LocalDate.parse(eventDateCell.getStringCellValue(), formatter); // Parse date string to LocalDate

                    eventsWithDates.computeIfAbsent(eventDate, k -> new ArrayList<>()).add(eventName);
                }
            }

            workbook.close();
            fis.close();

            if (eventsWithDates.isEmpty()) {
                System.out.println("Empty");
            } else {
                for (Map.Entry<LocalDate, List<String>> entry : eventsWithDates.entrySet()) {
                    if (selectedDate == null || entry.getKey().isEqual(selectedDate)) {
                        for (String event : entry.getValue()) {
                            String displayText = event + " - " + entry.getKey().format(formatter); // Format date as string
                            EventsListView.getItems().add(displayText);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void navigateToAthleteManagement(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("athelete-management-view.fxml"));
            Parent root = loader.load();
            // Pass the stage reference to the RegController
            AtheleteManagementViewController atheleteManagementViewController = loader.getController();
            atheleteManagementViewController.setStage(stage);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void navigateToDisciplineManagement(ActionEvent event) {
        System.out.println("Navigate to Sports Discipline Management");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("sports-discipline-management.fxml"));
            Parent root = loader.load();
            // Pass the stage reference to the RegController
            SportsDisciplineManagementController sportsDisciplineManagementController = loader.getController();
            sportsDisciplineManagementController.setStage(stage);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void navigateToEventManagement(ActionEvent event) {
        System.out.println("Navigate to Event Management");
        // Add your navigation logic here

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("event-management.fxml"));
            Parent root = loader.load();
            // Pass the stage reference to the RegController
            EventManagementController eventManagementController = loader.getController();
            eventManagementController.setStage(stage);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void navigateToRecordingResults(ActionEvent event) {
        System.out.println("Navigate to Recording Results");
//         Add your navigation logic here
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("recording-result.fxml"));
            Parent root = loader.load();
            // Pass the stage reference to the RegController
            RecordingResultController recordingResultController = loader.getController();
            recordingResultController.setStage(stage);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void navigateToMadelStanding(ActionEvent actionEvent) {
        try {
            System.out.println("here");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("madel-standing.fxml"));
            Parent root = loader.load();

            // Create a new stage for the popup
            Stage madelStage = new Stage();
            madelStage.setTitle("Madel Standing");
            madelStage.initModality(Modality.APPLICATION_MODAL); // Set as modal dialog
            madelStage.initOwner(stage); // Set the owner stage

            // Pass the stage reference to the controller
            MadelStandingController controller = loader.getController();
            controller.setStage(madelStage);

            // Set the scene and show the stage
            Scene scene = new Scene(root);
            madelStage.setScene(scene);
            madelStage.showAndWait(); // Show the stage and wait for it to be closed
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


