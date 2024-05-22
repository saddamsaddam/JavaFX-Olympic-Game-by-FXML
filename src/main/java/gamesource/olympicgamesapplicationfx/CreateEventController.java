package gamesource.olympicgamesapplicationfx;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;

import javafx.util.Callback;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class CreateEventController {
    public Label messageLabel;
    private Stage stage; // Stage reference
    // Method to set the stage
    public void setStage(Stage stage) {
        this.stage = stage;
    }


    @FXML
    private TextField eventNameTextField;

    @FXML
    private DatePicker eventDatePicker;

    @FXML
    private TextField locationTextField;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private ListView<String> categoryListView;

    @FXML
    private ListView<String> disciplineListView;
    private Map<String, ObservableList<String>> disciplineMap = new HashMap<>();



    public void initialize() {
        // Read data from Excel files and populate list views
        readSportsDisciplinesData();

        // Bind category selection listener
        categoryListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updateDisciplineListView(newValue);
            }
        });

        // Configure list view cell factory to display categories properly
        categoryListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item);
                        }
                    }
                };
            }
        });
    }


    private void readSportsDisciplinesData() {
        try {
            Path dataDir = Paths.get("data");
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            // Define the path to the Excel file inside the data directory
            File file = new File(dataDir.toFile(), "sports_disciplines.xlsx");


            FileInputStream fis = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(fis);

            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

            ObservableList<String> categories = FXCollections.observableArrayList(); // Stores unique categories

            // Iterate over rows
            for (Row row : sheet) {
                // Assuming data is in the first column
                Cell disciplineCell = row.getCell(0);
                Cell categoryCell = row.getCell(1);
                if (disciplineCell != null && categoryCell != null) {
                    String disciplineName = disciplineCell.getStringCellValue();
                    String categoryName = categoryCell.getStringCellValue().toLowerCase(); // Convert to lowercase

                    // Add discipline to the map
                    disciplineMap.computeIfAbsent(categoryName, k -> FXCollections.observableArrayList()).add(disciplineName);

                    if (!categories.stream().anyMatch(category -> category.equalsIgnoreCase(categoryName))) {
                        if(!categoryName.equalsIgnoreCase("category")){
                            categories.add(categoryName);
                        }

                    }
                }
            }

            workbook.close();
            fis.close();
            categoryListView.setItems(categories); // Populate categories list view
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void updateDisciplineListView(String category) {
        ObservableList<String> disciplines = disciplineMap.getOrDefault(category.toLowerCase(), FXCollections.observableArrayList());
        disciplineListView.setItems(disciplines);
    }

    private void saveEventToExcel(String eventName, String eventDate, String location, String description, String category, String discipline) {
        try {
            // Create or open the Excel file
            Path dataDir = Paths.get("data");
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            // Define the path to the Excel file inside the data directory
            File file = new File(dataDir.toFile(), "events.xlsx");

            Workbook workbook;

            if (file.exists() && file.length() > 0) {
                // If the file exists and is not empty, open it
                FileInputStream fis = new FileInputStream(file);
                workbook = WorkbookFactory.create(fis);
            } else {
                // If the file doesn't exist or is empty, create a new workbook
                workbook = new XSSFWorkbook();
            }

            // Check if the sheet exists, if not, create a new one
            Sheet sheet = workbook.getSheet("Events");
            if (sheet == null) {
                sheet = workbook.createSheet("Events");
                // Create header row
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Event Name");
                headerRow.createCell(1).setCellValue("Event Date");
                headerRow.createCell(2).setCellValue("Location");
                headerRow.createCell(3).setCellValue("Description");
                headerRow.createCell(4).setCellValue("Category");
                headerRow.createCell(5).setCellValue("Discipline");
            }

            // Check if the event name already exists
            for (Row row : sheet) {
                Cell eventNameCell = row.getCell(0);
                if (eventNameCell != null && eventNameCell.getCellType() == CellType.STRING && eventNameCell.getStringCellValue().equalsIgnoreCase(eventName)) {
                    // Event name already exists, do not add a duplicate
                    messageLabel.setText("Event Name is Already Listed!");
                    messageLabel.setStyle("-fx-text-fill: red");
                    workbook.close();
                    return;
                }
            }

            // Get the last row number to append data
            int lastRowNum = sheet.getLastRowNum();

            // Append new event details to the next available row
            Row newRow = sheet.createRow(lastRowNum + 1);
            newRow.createCell(0).setCellValue(eventName);
            newRow.createCell(1).setCellValue(eventDate);
            newRow.createCell(2).setCellValue(location);
            newRow.createCell(3).setCellValue(description);
            newRow.createCell(4).setCellValue(category);
            newRow.createCell(5).setCellValue(discipline);

            // Write the workbook to the file
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }

            workbook.close();
            messageLabel.setText("Event saved successfully.");
            messageLabel.setTextFill(Color.GREEN); // Set text color to green
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    public void handleBackButtonAction(ActionEvent actionEvent) {
        stage.hide();
    }

    @FXML
    public void handleCreateEventButtonAction(ActionEvent actionEvent) {
        // Get the entered information from the form fields
        String eventName = eventNameTextField.getText();
        LocalDate eventDate = eventDatePicker.getValue();
        String location = locationTextField.getText();
        String description = descriptionTextArea.getText();
        String category = categoryListView.getSelectionModel().getSelectedItem();
        String discipline = disciplineListView.getSelectionModel().getSelectedItem();

        // Check if any required field is empty
        if (eventName.isEmpty() || eventDate == null || location.isEmpty() || category == null || discipline == null) {
            System.out.println("Some required fields are empty. Event not saved.");
            messageLabel.setText("Some required fields are empty. Event not saved.");
            messageLabel.setTextFill(Color.RED); // Set text color to green
            return;
        }

        // Check if the selected event date is in the past
        if (eventDate.isBefore(LocalDate.now())) {
            System.out.println("Event date cannot be in the past. Event not saved.");
            messageLabel.setText("Event date cannot be in the past. Event not saved.");
            messageLabel.setTextFill(Color.RED); // Set text color to green
            return;
        }

        // Save event details to Excel file
        saveEventToExcel(eventName, eventDate.toString(), location, description, category, discipline);
        System.out.println("Event saved successfully.");
//        stage.hide();


        // For demonstration purposes, print the entered information
        System.out.println("Event Name: " + eventName);
        System.out.println("Event Date: " + eventDate);
        System.out.println("Location: " + location);
        System.out.println("Description: " + description);
        System.out.println("Category: " + category);
        System.out.println("Discipline: " + discipline);

        // Additional actions can be performed here, such as displaying a confirmation message.
    }

}
