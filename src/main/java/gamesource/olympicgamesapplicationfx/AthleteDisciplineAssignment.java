package gamesource.olympicgamesapplicationfx;

import gamesource.olympicgamesapplicationfx.SportsDisciplineManagementController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

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

public class AthleteDisciplineAssignment {

    @FXML
    private ListView<String> athleteListView;

    @FXML
    private ListView<String> categoryListView;

    @FXML
    private ListView<String> disciplineListView;

    @FXML
    private Label messageLabel;

    private Stage stage;

    // Map to store disciplines categorized by category
    private Map<String, ObservableList<String>> disciplineMap = new HashMap<>();

    // Map to store athlete candidates categorized by category and discipline
    private Map<String, Map<String, List<String>>> athleteMap = new HashMap<>();

    // Method to read data from athletesData.xlsx file
    private void readAthletesData() {
        try {
            Path dataDir = Paths.get("data");
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            // Define the path to the Excel file inside the data directory
            File file = new File(dataDir.toFile(), "athletesData.xlsx");


            FileInputStream fis = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(fis);

            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

            ObservableList<String> athletes = FXCollections.observableArrayList();

            // Iterate over rows
            for (Row row : sheet) {
                // Assuming data is in the first column
                Cell cell = row.getCell(0);
                if (cell != null) {
                    String athleteName = cell.getStringCellValue();
                    if(!athleteName.equalsIgnoreCase("Name")){
                        athletes.add(athleteName);
                    }
                }
            }

            workbook.close();
            fis.close();

            athleteListView.setItems(athletes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to read data from sports_disciplines.xlsx file
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

    // Method to update discipline list view based on selected category
    private void updateDisciplineListView(String category) {
        ObservableList<String> disciplines = disciplineMap.getOrDefault(category.toLowerCase(), FXCollections.observableArrayList());
        disciplineListView.setItems(disciplines);
    }


    @FXML
    private void handleSubmitButtonAction() {
        String selectedAthlete = athleteListView.getSelectionModel().getSelectedItem();
        String selectedCategory = categoryListView.getSelectionModel().getSelectedItem();
        String selectedDiscipline = disciplineListView.getSelectionModel().getSelectedItem();

        if (selectedAthlete == null || selectedCategory == null || selectedDiscipline == null) {
            // Display an error message if any field is not selected
            messageLabel.setText("Please select athlete, category, and discipline.");
        } else {
            // Save information to Excel sheet
            saveToExcel(selectedCategory, selectedDiscipline, selectedAthlete);
            // Display a success message
            //messageLabel.setText("Information saved successfully.");
        }
    }




    private void saveToExcel(String category, String discipline, String athlete) {
        try {
            // Open an existing workbook or create a new one
            Workbook workbook;

            Path dataDir = Paths.get("data");
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            // Define the path to the Excel file inside the data directory
            File file = new File(dataDir.toFile(), "athlete_discipline_info.xlsx");

            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                workbook = new XSSFWorkbook(fis);
                fis.close();
            } else {
                workbook = new XSSFWorkbook();
            }

            // Get or create a sheet
            Sheet sheet = workbook.getSheet("Athlete Discipline Info");
            if (sheet == null) {
                sheet = workbook.createSheet("Athlete Discipline Info");
            }

            // Find or create the row for the category and discipline
            int rowIndex = findRowIndex(sheet, category, discipline);

            if (rowIndex == -1) {
                rowIndex = sheet.getLastRowNum() + 1;
                Row newRow = sheet.createRow(rowIndex);
                newRow.createCell(0).setCellValue(category);
                newRow.createCell(1).setCellValue(discipline);
            }

            // Get the row and check if the athlete already exists in the row
            Row dataRow = sheet.getRow(rowIndex);
            if (dataRow == null) {
                dataRow = sheet.createRow(rowIndex);
            } else {
                for (int i = 2; i < dataRow.getLastCellNum(); i++) {
                    Cell cell = dataRow.getCell(i);
                    if (cell != null && cell.getCellType() == CellType.STRING && cell.getStringCellValue().equalsIgnoreCase(athlete)) {
                        // The athlete is already listed, no need to add again

                        messageLabel.setText("The Athlete is Already Listed!");
                        messageLabel.setStyle("-fx-text-fill: red");

                        workbook.close();
                        return ;
                    }
                }
            }

            // Find the last column index and add the athlete to the next available column
            int lastColumnIndex = dataRow.getLastCellNum();
            Cell athleteCell = dataRow.createCell(lastColumnIndex);
            athleteCell.setCellValue(athlete);

            // Write the workbook to a file
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }

            // Close the workbook
            workbook.close();
            messageLabel.setText("Information saved successfully.");
            messageLabel.setStyle("-fx-text-fill: green");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private int findRowIndex(Sheet sheet, String category, String discipline) {
        System.out.println("Here =>"+category+ " ; "+discipline);
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                Cell categoryCell = row.getCell(0);
                Cell disciplineCell = row.getCell(1);
                if (categoryCell != null && disciplineCell != null) {
                    String cellCategory = categoryCell.getStringCellValue();
                    String cellDiscipline = disciplineCell.getStringCellValue();
                    if (cellCategory.equalsIgnoreCase(category) && cellDiscipline.equalsIgnoreCase(discipline)) {
                        return i;
                    }
                }
            }
        }
        return -1; // Row not found
    }































    @FXML
    private void handleBackButtonAction() {
        stage.hide();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        // Read data from Excel files and populate list views
        readAthletesData();
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
}
