package gamesource.olympicgamesapplicationfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RegController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField countryField;
    @FXML
    private TextField ageField;
    @FXML
    private RadioButton maleRadio;
    @FXML
    private RadioButton femaleRadio;
    @FXML
    private TextField emailField;
    @FXML
    private Label messageLabel;

    private Stage stage; // Stage reference

    // Method to set the stage
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        // Add event handlers to toggle radio buttons
        maleRadio.setOnAction(event -> {
            if (maleRadio.isSelected() && femaleRadio.isSelected()) {
                femaleRadio.setSelected(false);
            }
        });

        femaleRadio.setOnAction(event -> {
            if (femaleRadio.isSelected() && maleRadio.isSelected()) {
                maleRadio.setSelected(false);
            }
        });
    }

    public void handleBackButtonAction(ActionEvent actionEvent){
        stage.hide();
    }

    @FXML
    public void handleSubmitButtonAction(ActionEvent actionEvent) {
        String name = nameField.getText();
        String country = countryField.getText();
        String age = ageField.getText();
        String gender = "";
        String email = emailField.getText();

        if (maleRadio.isSelected()) {
            gender = "Male";
        } else if (femaleRadio.isSelected()) {
            gender = "Female";
        }

        // Print selected values for debugging
        System.out.println("name: " + name + "\ncountry: " + country + "\nage: " + age + "\ngender: " + gender + "\nemail:" + email);

        if (name.isEmpty() || country.isEmpty() || age.isEmpty() || gender.isEmpty() || email.isEmpty()) {
            messageLabel.setText("Please fill in all fields");
            messageLabel.setTextFill(Color.RED); // Set text color to red
        } else {
            // Process the submitted data (e.g., save to database, send to server)
            try {
                Path dataDir = Paths.get("data");
                if (!Files.exists(dataDir)) {
                    Files.createDirectories(dataDir);
                }

                // Define the path to the Excel file inside the data directory
                File file = new File(dataDir.toFile(), "athletesData.xlsx");

                if (!file.exists()) {
                    // Create new workbook and sheet if file doesn't exist
                    Workbook workbook = new XSSFWorkbook();
                    Sheet sheet = workbook.createSheet("User Data");

                    // Create header row
                    Row headerRow = sheet.createRow(0);
                    headerRow.createCell(0).setCellValue("Name");
                    headerRow.createCell(1).setCellValue("Country");
                    headerRow.createCell(2).setCellValue("Email");
                    headerRow.createCell(3).setCellValue("Gender");
                    headerRow.createCell(4).setCellValue("Age");

                    // Write data to cells
                    Row row = sheet.createRow(1);
                    row.createCell(0).setCellValue(name);
                    row.createCell(1).setCellValue(country);
                    row.createCell(2).setCellValue(email);
                    row.createCell(3).setCellValue(gender);
                    row.createCell(4).setCellValue(age);

                    // Write workbook to file
                    try (FileOutputStream outputStream = new FileOutputStream(file)) {
                        workbook.write(outputStream);
                    }

                    // Close workbook
                    workbook.close();

                    messageLabel.setText("Registration successful for " + name);
                    messageLabel.setTextFill(Color.GREEN); // Set text color to green
                } else {
                    // Open existing workbook and sheet
                    FileInputStream inputStream = new FileInputStream(file);
                    Workbook workbook = new XSSFWorkbook(inputStream);
                    Sheet sheet = workbook.getSheetAt(0);

                    // Check if data already exists
                    boolean alreadyExists = false;
                    for (Row row : sheet) {
                        Cell cell = row.getCell(0); // Assuming name is in the first column
                        if (cell != null && cell.getCellType() == CellType.STRING && cell.getStringCellValue().equalsIgnoreCase(name)) {
                            alreadyExists = true;
                            break;
                        }
                    }

                    if (alreadyExists) {
                        messageLabel.setText("Registration failed: Data already exists for " + name);
                        messageLabel.setTextFill(Color.RED); // Set text color to red
                    } else {
                        // Append new row
                        int rowCount = sheet.getPhysicalNumberOfRows();
                        Row row = sheet.createRow(rowCount);
                        row.createCell(0).setCellValue(name);
                        row.createCell(1).setCellValue(country);
                        row.createCell(2).setCellValue(email);
                        row.createCell(3).setCellValue(gender);
                        row.createCell(4).setCellValue(age);

                        // Write workbook to file
                        try (FileOutputStream outputStream = new FileOutputStream(file)) {
                            workbook.write(outputStream);
                        }

                        // Close streams and show success message
                        inputStream.close();
                        workbook.close();

                        messageLabel.setText("Registration successful for " + name);
                        messageLabel.setTextFill(Color.GREEN); // Set text color to green
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                messageLabel.setText("Error processing data");
                messageLabel.setTextFill(Color.RED); // Set text color to red
            }
        }
    }

}



