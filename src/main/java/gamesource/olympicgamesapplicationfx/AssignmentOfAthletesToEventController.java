package gamesource.olympicgamesapplicationfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class AssignmentOfAthletesToEventController {

    @FXML
    private ListView<String> EventListView;

    @FXML
    private ListView<String> AthleteListView;

    private Map<String, String[]> eventCategoryDisciplineMap = new HashMap<>();

    @FXML
    private Label messageLabel;

    private Stage stage; // Stage reference

    // Method to set the stage
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void initialize() {
        // Read data from events.xlsx and populate EventListView
        readEventsData();

        // Bind selection listener to EventListView
        EventListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Update AthleteListView based on selected event
                updateAthleteListView(newValue);
            }
        });
    }

    private void readEventsData() {
        try {
            Path dataDir = Paths.get("data");
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }

            // Define the path to the Excel file inside the data directory
            File file = new File(dataDir.toFile(), "events.xlsx");



            FileInputStream fis = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

            for (Row row : sheet) {
                Cell eventNameCell = row.getCell(0);
                Cell categoryCell = row.getCell(4);
                Cell disciplineCell = row.getCell(5);
                if (eventNameCell != null && eventNameCell.getStringCellValue() != null) {
                    String eventName = eventNameCell.getStringCellValue();
                    String category = (categoryCell != null) ? categoryCell.getStringCellValue() : "";
                    String discipline = (disciplineCell != null) ? disciplineCell.getStringCellValue() : "";
                    if (!eventName.equalsIgnoreCase("Event Name")) {
                        eventCategoryDisciplineMap.put(eventName, new String[]{category, discipline});
                    }
                }
            }

            fis.close();
            workbook.close();

            EventListView.getItems().addAll(eventCategoryDisciplineMap.keySet());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateAthleteListView(String selectedEvent) {
//        System.out.println("here");
        // Retrieve category and discipline for the selected event from the map
        String[] categoryDiscipline = eventCategoryDisciplineMap.get(selectedEvent);

        if (categoryDiscipline != null) {
            System.out.println("category discipline not null");
            String category = categoryDiscipline[0];
            String discipline = categoryDiscipline[1];

            System.out.println("========>Category:"+category+" discipline:"+discipline);

            try {
                List<String> athletes = new ArrayList<>();

                Path dataDir = Paths.get("data");
                if (!Files.exists(dataDir)) {
                    Files.createDirectories(dataDir);
                }
                // Define the path to the Excel file inside the data directory
                File file = new File(dataDir.toFile(), "athlete_discipline_info.xlsx");




                FileInputStream fis = new FileInputStream(file);
                Workbook workbook = new XSSFWorkbook(fis);
                Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

                for (Row row : sheet) {
//                    System.out.println("row");
                    Cell categoryCell = row.getCell(0);
                    Cell disciplineCell = row.getCell(1);
                    Cell athleteCell = row.getCell(2);
                    if (categoryCell != null && disciplineCell != null && athleteCell != null) {
//                        System.out.println("here2");
                        String categoryValue = categoryCell.getStringCellValue();
                        String disciplineValue = disciplineCell.getStringCellValue();

                        String athlete = athleteCell.getStringCellValue();
                        System.out.println("cat:"+categoryValue +" ; dis:"+disciplineValue+ " at:"+athlete);
                        if (category.equalsIgnoreCase(categoryValue) && discipline.equalsIgnoreCase(disciplineValue)) {
//                            System.out.println("here3");
                            athletes.add(athlete);
                            int indx=3;

                            while(row.getCell(indx)!=null){
                                athletes.add(row.getCell(indx).getStringCellValue());
                                indx++;
                            }
                        }
                    }
                }
//                System.out.println("here5");

                fis.close();
                workbook.close();

                AthleteListView.getItems().clear();
                AthleteListView.getItems().addAll(athletes);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public void handleSubmitButtonAction(ActionEvent actionEvent) {
        String selectedEvent = EventListView.getSelectionModel().getSelectedItem();
        String selectedAthlete = AthleteListView.getSelectionModel().getSelectedItem();

        if (selectedEvent != null && selectedAthlete != null) {
            try {
                Path dataDir = Paths.get("data");
                if (!Files.exists(dataDir)) {
                    Files.createDirectories(dataDir);
                }
                // Define the path to the Excel file inside the data directory
                File file = new File(dataDir.toFile(), "eventCandidates.xlsx");

                Workbook workbook;

                // If the file does not exist, create a new workbook
                if (!file.exists()) {
                    workbook = new XSSFWorkbook();
                } else {
                    // If the file exists, read the existing workbook
                    FileInputStream fis = new FileInputStream(file);
                    workbook = new XSSFWorkbook(fis);
                    fis.close();
                }

                // Get the sheet or create a new one if it doesn't exist
                Sheet sheet = workbook.getSheet("Event Candidates");
                if (sheet == null) {
                    sheet = workbook.createSheet("Event Candidates");
                }

                // Find the row corresponding to the selected event, if it exists
                Row eventRow = null;
                for (Row row : sheet) {
                    Cell eventCell = row.getCell(0);
                    if (eventCell != null && eventCell.getCellType() == CellType.STRING && eventCell.getStringCellValue().equals(selectedEvent)) {
                        eventRow = row;
                        break;
                    }
                }

                if (eventRow == null) {
                    // If the event row doesn't exist, create a new row for the event
                    int lastRow = sheet.getLastRowNum();
                    eventRow = sheet.createRow(lastRow + 1);
                    eventRow.createCell(0).setCellValue(selectedEvent);
                } else {
                    // Check if the athlete already exists in the event row
                    for (int i = 1; i < eventRow.getLastCellNum(); i++) {
                        Cell athleteCell = eventRow.getCell(i);
                        if (athleteCell != null && athleteCell.getCellType() == CellType.STRING && athleteCell.getStringCellValue().equals(selectedAthlete)) {
                            // The athlete is already associated with the event, no need to add again
                            workbook.close();
                            messageLabel.setText("The athlete is already associated with the event.");
                            messageLabel.setTextFill(Color.RED);
                            return;
                        }
                    }
                }

                // Find the last column index for the event row
                int lastColumn = eventRow.getLastCellNum();

                // Create a new cell for the athlete
                Cell cell = eventRow.createCell(lastColumn);
                cell.setCellValue(selectedAthlete);

                // Write the workbook to the file
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    workbook.write(fos);
                }

                // Close the workbook
                workbook.close();

                // Display success message
                messageLabel.setText("The athlete is associated with the event.");
                messageLabel.setTextFill(Color.GREEN);
//                stage.hide();
            } catch (IOException e) {
                e.printStackTrace();
                // Display error message if failed to save
                messageLabel.setText("Error saving selected event and athlete.");
                messageLabel.setTextFill(Color.RED);
            }
        } else {
            // If either event or athlete is not selected, display an error message
            messageLabel.setText("Please select an event and an athlete.");
            messageLabel.setTextFill(Color.RED);
        }
    }



    @FXML
    public void handleBackButtonAction(ActionEvent actionEvent) {
        stage.hide();
    }
}
