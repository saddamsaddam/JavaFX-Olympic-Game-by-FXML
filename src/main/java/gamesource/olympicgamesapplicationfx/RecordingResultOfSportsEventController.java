package gamesource.olympicgamesapplicationfx;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
public class RecordingResultOfSportsEventController {
    public TextField timeField;
    public TextField scoreField;
    public Label messageLabel;

    private Stage stage; // Stage reference

    @FXML
    private ListView<String> EventListView;
    @FXML
    private ListView<String> DisciplineListView;
    @FXML
    private ListView<String> AthleteListView;

    private Map<String, String[]> eventCategoryDisciplineMap = new HashMap<>();

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
                List<String> disciplineName = new ArrayList<>();
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
                disciplineName.add(discipline);

                AthleteListView.getItems().clear();
                AthleteListView.getItems().addAll(athletes);

                DisciplineListView.getItems().clear();
                DisciplineListView.getItems().addAll(disciplineName);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleSubmitButtonAction(ActionEvent actionEvent) throws IOException {
        String selectedEvent = EventListView.getSelectionModel().getSelectedItem();
        String selectedDiscipline = DisciplineListView.getSelectionModel().getSelectedItem();
        String selectedAthlete = AthleteListView.getSelectionModel().getSelectedItem();
        String recordedTime = timeField.getText();
        String recordedScore = scoreField.getText();

        // Validate input
        if (selectedEvent == null || selectedDiscipline == null || selectedAthlete == null || recordedTime.isEmpty() || recordedScore.isEmpty()) {
            messageLabel.setText("Please fill in all fields");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        // Check if the combination already exists in the Excel file
        if (isRecordedResultExists(selectedEvent, selectedDiscipline, selectedAthlete)) {
            messageLabel.setText("The recorded result already exists");
            messageLabel.setTextFill(Color.RED);
            return;
        }


        Path dataDir = Paths.get("data");
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
        }
        // Define the path to the Excel file inside the data directory
        File file = new File(dataDir.toFile(), "recorded_results.xlsx");

        try {
            if (!file.exists() || file.length() == 0) {
                // Create a new file if it doesn't exist or is empty
                createNewFileAndAddData(file, selectedEvent, selectedDiscipline, selectedAthlete,recordedTime,recordedScore);
            } else {
                // File exists and has data, append to it
                appendDataToFile(file, selectedEvent, selectedDiscipline, selectedAthlete,recordedTime,recordedScore);
            }
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error handling file");
            messageLabel.setStyle("-fx-text-fill: red");
        }
    }

    private boolean isRecordedResultExists(String selectedEvent, String selectedDiscipline, String selectedAthlete) throws IOException {
        // Open the existing workbook
        Path dataDir = Paths.get("data");
        File file = new File(dataDir.toFile(), "recorded_results.xlsx");

        if (!file.exists()) {
            return false; // File doesn't exist, so the combination can't exist
        }

        try (FileInputStream inputStream = new FileInputStream(file)) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            // Iterate over each row and check if the combination exists
            for (Row row : sheet) {
                Cell eventCell = row.getCell(0);
                Cell disciplineCell = row.getCell(1);
                Cell athleteCell = row.getCell(2);

                if (eventCell.getStringCellValue().equalsIgnoreCase(selectedEvent) &&
                        disciplineCell.getStringCellValue().equalsIgnoreCase(selectedDiscipline) &&
                        athleteCell.getStringCellValue().equalsIgnoreCase(selectedAthlete)) {
                    workbook.close();
                    return true; // Combination already exists
                }
            }
            workbook.close();
        }

        return false; // Combination does not exist
    }

    private void createNewFileAndAddData(File file, String selectedEvent, String selectedDiscipline, String  selectedAthlete, String recordedTime, String recordedScore) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sports Disciplines");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Event Name");
        headerRow.createCell(1).setCellValue("Discipline");
        headerRow.createCell(2).setCellValue("Athlete");
        headerRow.createCell(3).setCellValue("Time");
        headerRow.createCell(4).setCellValue("Score");

        Row newRow = sheet.createRow(1);
        newRow.createCell(0).setCellValue(selectedEvent);
        newRow.createCell(1).setCellValue(selectedDiscipline);
        newRow.createCell(2).setCellValue(selectedAthlete);
        newRow.createCell(3).setCellValue(recordedTime);
        newRow.createCell(4).setCellValue(recordedScore);


        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
            messageLabel.setText("New file created and data added successfully");
            messageLabel.setStyle("-fx-text-fill: green");
        }
    }

    private void appendDataToFile(File file, String selectedEvent, String selectedDiscipline, String  selectedAthlete, String recordedTime, String recordedScore) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();

            Row newRow = sheet.createRow(lastRowNum + 1);
            newRow.createCell(0).setCellValue(selectedEvent);
            newRow.createCell(1).setCellValue(selectedDiscipline);
            newRow.createCell(2).setCellValue(selectedAthlete);
            newRow.createCell(3).setCellValue(recordedTime);
            newRow.createCell(4).setCellValue(recordedScore);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
                messageLabel.setText("Record added successfully");
                stage.hide();
                messageLabel.setStyle("-fx-text-fill: green");
            }
        }
    }

    public void handleBackButtonAction(ActionEvent actionEvent) {
        stage.hide();
    }
}
