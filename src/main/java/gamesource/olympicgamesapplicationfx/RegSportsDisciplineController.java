package gamesource.olympicgamesapplicationfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RegSportsDisciplineController {
    public TextField categoryField;
    public TextField nameField;
    public Label messageLabel;
    private Stage stage;
    // Method to set the stage
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void handleSubmitButtonAction(ActionEvent actionEvent) throws IOException {
        String name = nameField.getText();
        String category = categoryField.getText();

        if (name.isEmpty() || category.isEmpty()) {
            messageLabel.setText("Please fill in all fields");
            messageLabel.setStyle("-fx-text-fill: red");
        } else {

            Path dataDir = Paths.get("data");
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            // Define the path to the Excel file inside the data directory
            File file = new File(dataDir.toFile(), "sports_disciplines.xlsx");

            try {
                if (!file.exists() || file.length() == 0) {
                    // Create a new file if it doesn't exist or is empty
                    createNewFileAndAddData(file, name, category);
                } else {
                    if (isDisciplineExists(file, name, category)) {
                        messageLabel.setText("Discipline already exists");
                        messageLabel.setStyle("-fx-text-fill: red");
                    } else {
                        appendDataToFile(file, category,name);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                messageLabel.setText("Error handling file");
                messageLabel.setStyle("-fx-text-fill: red");
            }
        }
    }

    private boolean isDisciplineExists(File file, String category, String disciplineName) throws IOException {
        Workbook workbook = new XSSFWorkbook(new FileInputStream(file));
        Sheet sheet = workbook.getSheetAt(0);
        for (Row row : sheet) {
            Cell categoryCell = row.getCell(0); // Assuming category is in the first column
            Cell disciplineCell = row.getCell(1); // Assuming discipline names are in the second column
            if (categoryCell != null && disciplineCell != null &&
                    categoryCell.getCellType() == CellType.STRING &&
                    disciplineCell.getCellType() == CellType.STRING &&
                    categoryCell.getStringCellValue().equalsIgnoreCase(category) &&
                    disciplineCell.getStringCellValue().equalsIgnoreCase(disciplineName)) {
                workbook.close();
                return true; // Category and discipline combination already exists
            }
        }
        workbook.close();
        return false; // Category and discipline combination doesn't exist
    }


    private void createNewFileAndAddData(File file, String name, String category) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sports Disciplines");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Name");
        headerRow.createCell(1).setCellValue("Category");

        Row newRow = sheet.createRow(1);
        newRow.createCell(0).setCellValue(name);
        newRow.createCell(1).setCellValue(category);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
            messageLabel.setText("New file created and data added successfully");
            messageLabel.setStyle("-fx-text-fill: green");
        }
    }

    private void appendDataToFile(File file, String name, String category) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();

            Row newRow = sheet.createRow(lastRowNum + 1);
            newRow.createCell(0).setCellValue(name);
            newRow.createCell(1).setCellValue(category);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
                messageLabel.setText("Record added successfully");
                messageLabel.setStyle("-fx-text-fill: green");
                stage.hide();
            }
        }
    }



    public void handleBackButtonAction(ActionEvent actionEvent) {
        stage.hide();
    }
}
