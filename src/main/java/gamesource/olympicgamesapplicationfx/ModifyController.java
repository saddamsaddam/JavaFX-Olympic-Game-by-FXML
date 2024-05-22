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
import java.util.Iterator;
import java.util.stream.Collectors;

import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ModifyController {

    public Label messageLabel;
  //  public Label messageLabel;

    @FXML
    private ListView<String> userListView;

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

    private final ObservableList<User> userList = FXCollections.observableArrayList();


    private Stage stage; // Stage reference

    // Method to set the stage
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void initialize() {

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

        try {
            Path dataDir = Paths.get("data");
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            // Define the path to the Excel file inside the data directory
            File file = new File(dataDir.toFile(), "athletesData.xlsx");


            if (file.exists()) {
                System.out.println("file exists");

                FileInputStream inputStream = new FileInputStream(file);
                Workbook workbook = new XSSFWorkbook(inputStream);
                Sheet sheet = workbook.getSheetAt(0);

                // Skip the header row
                Iterator<Row> iterator = sheet.iterator();
                if (iterator.hasNext()) {
                    iterator.next(); // Skip header row
                }

                // Iterate over remaining rows
                while (iterator.hasNext()) {
                    Row row = iterator.next();

                    String name = "";
                    if (row.getCell(0) != null && row.getCell(0).getCellType() != CellType.BLANK) {
                        name = row.getCell(0).getStringCellValue();
                    }

                    String country = "";
                    if (row.getCell(1) != null && row.getCell(1).getCellType() != CellType.BLANK) {
                        country = row.getCell(1).getStringCellValue();
                    }
                    String email = "";
                    if (row.getCell(2) != null && row.getCell(2).getCellType() != CellType.BLANK) {
                        email = row.getCell(2).getStringCellValue();
                    }
                    String gender = "";
                    if (row.getCell(3) != null && row.getCell(3).getCellType() != CellType.BLANK) {
                        gender = row.getCell(3).getStringCellValue();
                    }

                    String ageString = "";
                    if (row.getCell(4) != null && row.getCell(4).getCellType() != CellType.BLANK) {
                        ageString = row.getCell(4).getStringCellValue();
                    }

                    int age = 0;
                    if (!ageString.isEmpty()) {
                        try {
                            age = Integer.parseInt(ageString);
                        } catch (NumberFormatException e) {
                            // Handle parsing error
                            System.err.println("Error parsing age: " + ageString);
                        }
                    }

                    if (!name.isEmpty()) {
                        User user = new User(name, country, age, gender, email);
                        userList.add(user);
                    }
                }

                inputStream.close();
                workbook.close();

                userListView.setItems(userList.stream().map(User::getName).collect(Collectors.toCollection(FXCollections::observableArrayList)));

                userListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        User selectedUser = userList.stream().filter(user -> user.getName().equals(newValue)).findFirst().orElse(null);
                        if (selectedUser != null) {
                            populateForm(selectedUser);
                        }
                    }
                });
            } else {
                messageLabel.setText("Excel file not found.");
                messageLabel.setTextFill(Color.RED); // Set text color to red
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void populateForm(User user) {
        nameField.setText(user.getName());
        countryField.setText(user.getCountry());
        ageField.setText(String.valueOf(user.getAge()));
        if (user.getGender().equalsIgnoreCase("Male")) {
            maleRadio.setSelected(true);
            femaleRadio.setSelected(false);
        } else {
            maleRadio.setSelected(false);
            femaleRadio.setSelected(true);
        }
        emailField.setText(user.getEmail());
    }

    @FXML
    private void handleUpdateButtonAction(ActionEvent actionEvent) throws IOException {

        System.out.println("a");

        String name = nameField.getText();
        String country = countryField.getText();
        String age = ageField.getText();
        String gender = maleRadio.isSelected() ? "Male" : "Female";
        String email = emailField.getText();
        System.out.println("b");

        if (name.isEmpty() || country.isEmpty() || age.isEmpty() || email.isEmpty()) {
            messageLabel.setText("Please fill in all fields");
            messageLabel.setTextFill(Color.RED);
        } else {
            System.out.println("c");
            Path dataDir = Paths.get("data");
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            // Define the path to the Excel file inside the data directory
            File file = new File(dataDir.toFile(), "athletesData.xlsx");

            if (file.exists()) {
                System.out.println("d");
                try (FileInputStream inputStream = new FileInputStream(file); Workbook workbook = new XSSFWorkbook(inputStream)) {
                    Sheet sheet = workbook.getSheetAt(0);
                    System.out.println("e");
                    for (Row row : sheet) {
                        System.out.println("row");
                        if (row.getCell(2).getStringCellValue().equalsIgnoreCase(email)) {
                            row.getCell(0).setCellValue(name);
                            row.getCell(1).setCellValue(country);
                            row.getCell(3).setCellValue(gender);
                            row.getCell(4).setCellValue(age);
                            break;
                        }
                    }
                    System.out.println("f");
                    try (FileOutputStream outputStream = new FileOutputStream(file)) {
                        System.out.println("w");
                        workbook.write(outputStream);
                        System.out.println("x");
                    }
                    System.out.println("y");
                    messageLabel.setText("Update successful for " + name);
                    messageLabel.setTextFill(Color.GREEN);
                    System.out.println("z");
                    // Refresh the user list and the ListView
                    refreshUserList();
                    System.out.println("done");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                messageLabel.setText("Excel file not found.");
                messageLabel.setTextFill(Color.RED);
            }
        }
    }

    private void refreshUserList() {
        System.out.println("asche");
        userList.clear(); // Clear the existing user list
        try {
            System.out.println("try");
            Path dataDir = Paths.get("data");
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            // Define the path to the Excel file inside the data directory
            File file = new File(dataDir.toFile(), "athletesData.xlsx");

            if (file.exists()) {
                System.out.println("exist");
                FileInputStream inputStream = new FileInputStream(file);
                Workbook workbook = new XSSFWorkbook(inputStream);
                Sheet sheet = workbook.getSheetAt(0);
                Iterator<Row> iterator = sheet.iterator();
                // Skip the header row
                if (iterator.hasNext()) {
                    System.out.println("next");
                    iterator.next();
                }
                // Iterate over remaining rows
                // Iterate over remaining rows
                while (iterator.hasNext()) {
                    Row row = iterator.next();

                    String name = "";
                    if (row.getCell(0) != null && row.getCell(0).getCellType() != CellType.BLANK) {
                        name = row.getCell(0).getStringCellValue();
                    }

                    String country = "";
                    if (row.getCell(1) != null && row.getCell(1).getCellType() != CellType.BLANK) {
                        country = row.getCell(1).getStringCellValue();
                    }
                    String email = "";
                    if (row.getCell(2) != null && row.getCell(2).getCellType() != CellType.BLANK) {
                        email = row.getCell(2).getStringCellValue();
                    }
                    String gender = "";
                    if (row.getCell(3) != null && row.getCell(3).getCellType() != CellType.BLANK) {
                        gender = row.getCell(3).getStringCellValue();
                    }

                    String ageString = "";
                    if (row.getCell(4) != null && row.getCell(4).getCellType() != CellType.BLANK) {
                        ageString = row.getCell(4).getStringCellValue();
                    }

                    int age = 0;
                    if (!ageString.isEmpty()) {
                        try {
                            age = Integer.parseInt(ageString);
                        } catch (NumberFormatException e) {
                            // Handle parsing error
                            System.err.println("Error parsing age: " + ageString);
                        }
                    }

                    if (!name.isEmpty()) {
                        User user = new User(name, country, age, gender, email);
                        userList.add(user);
                    }
                }
                System.out.println("close");
                inputStream.close();
                workbook.close();
                // Set items to the ListView after refreshing userList
                userListView.setItems(userList.stream().map(User::getName).collect(Collectors.toCollection(FXCollections::observableArrayList)));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }








    @FXML
    public void handleDeleteButtonAction(ActionEvent actionEvent) {
        String selectedUserName = userListView.getSelectionModel().getSelectedItem();
        if (selectedUserName != null) {
            User selectedUser = userList.stream().filter(user -> user.getName().equals(selectedUserName)).findFirst().orElse(null);
            if (selectedUser != null) {
                userList.remove(selectedUser);
                try {
                    Path dataDir = Paths.get("data");
                    if (!Files.exists(dataDir)) {
                        Files.createDirectories(dataDir);
                    }
                    // Define the path to the Excel file inside the data directory
                    File file = new File(dataDir.toFile(), "athletesData.xlsx");
                    if (file.exists()) {
                        FileInputStream inputStream = new FileInputStream(file);
                        Workbook workbook = new XSSFWorkbook(inputStream);
                        Sheet sheet = workbook.getSheetAt(0);
                        Iterator<Row> iterator = sheet.iterator();
                        while (iterator.hasNext()) {
                            Row row = iterator.next();
                            Cell cell = row.getCell(0); // Assuming user name is in the first column
                            if (cell != null && cell.getCellType() == CellType.STRING) {
                                String cellValue = cell.getStringCellValue().trim(); // Trim to remove leading/trailing whitespace
                                if (cellValue.equalsIgnoreCase(selectedUser.getName())) {
                                    int rowNum = row.getRowNum();
                                    sheet.removeRow(row);
                                    shiftRowsUp(sheet, rowNum + 1);
                                    break;
                                }
                            }
                        }
                        inputStream.close();
                        try (FileOutputStream outputStream = new FileOutputStream(file)) {
                            workbook.write(outputStream);
                        }
                        workbook.close();
                        messageLabel.setText("Deleted user: " + selectedUserName);
                        messageLabel.setTextFill(Color.GREEN);

                        userListView.setItems(userList.stream().map(User::getName).collect(Collectors.toCollection(FXCollections::observableArrayList)));
                    } else {
                        messageLabel.setText("Excel file not found.");
                        messageLabel.setTextFill(Color.RED);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    messageLabel.setText("Error deleting user.");
                    messageLabel.setTextFill(Color.RED);
                }
            }
        } else {
            messageLabel.setText("Please select a user to delete.");
            messageLabel.setTextFill(Color.RED);
        }
    }

    private void shiftRowsUp(Sheet sheet, int startRow) {
        int lastRowNum = sheet.getLastRowNum();
        for (int i = startRow; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                sheet.shiftRows(i, lastRowNum, -1);
            }
        }
    }

    public void handleBackButtonAction(ActionEvent actionEvent) {
        stage.hide();

    }


    private static class User {
        private String name;
        private String country;
        private int age;
        private String gender;
        private String email;

        public User(String name, String country, int age, String gender, String email) {
            this.name = name;
            this.country = country;
            this.age = age;
            this.gender = gender;
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public String getCountry() {
            return country;
        }

        public int getAge() {
            return age;
        }

        public String getGender() {
            return gender;
        }

        public String getEmail() {
            return email;
        }
    }
}
