package gamesource.olympicgamesapplicationfx;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class AtheleteManagementViewController {
    @FXML
    public ImageView circleImageView;
    private Stage stage; // Stage reference

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    @FXML
    private TableView<Athlete> AthleteTableView;
    @FXML
    private TableColumn<Athlete, String> athleteName;
    @FXML
    private TableColumn<Athlete, String> athleteCountry;
    @FXML
    private TableColumn<Athlete, Integer> athleteAge;
    @FXML
    private TableColumn<Athlete, String> athleteGender;
    @FXML
    private TableColumn<Athlete, String> athleteEmail;


    @FXML
    public void initialize() {
        Circle clip = new Circle(50, 50, 50); // x, y, radius
        circleImageView.setClip(clip);
        athleteName.setCellValueFactory(new PropertyValueFactory<>("name"));
        athleteCountry.setCellValueFactory(new PropertyValueFactory<>("country"));
        athleteAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        athleteGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        athleteEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Load initial data
        updateTableView();
    }

    public  void updateTableView() {
        Path dataDir = Paths.get("data");
        if (!Files.exists(dataDir)) {
            try {
                Files.createDirectories(dataDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Define the path to the Excel file inside the data directory
        File file = new File(dataDir.toFile(), "athletesData.xlsx");

        // Load data from Excel file
        ObservableList<Athlete> data = ExcelReader.readExcelFile(file.getPath());
        AthleteTableView.setItems(data);
    }

    public static class ExcelReader {
        public static ObservableList<Athlete> readExcelFile(String filePath) {
            ObservableList<Athlete> data = FXCollections.observableArrayList();
            try (FileInputStream fis = new FileInputStream(new File(filePath));
                 Workbook workbook = new XSSFWorkbook(fis)) {
                System.out.println("Entered");

                Sheet sheet = workbook.getSheetAt(0);
                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue; // Skip header row
                    Cell nameCell = row.getCell(0);
                    Cell countryCell = row.getCell(1);
                    Cell emailCell = row.getCell(2);
                    Cell genderCell = row.getCell(3);
                    Cell ageCell = row.getCell(4);

                    String name = nameCell.getStringCellValue();
                    String country = countryCell.getStringCellValue();
                    String email = emailCell.getStringCellValue();
                    String gender = genderCell.getStringCellValue();
                    System.out.println("name:"+name);

                   // System.out.println("ageCell.getNumericCellValue();=>"+ageCell.getNumericCellValue());
                    //System.out.println("ageCell.sting();"+ageCell.getStringCellValue());

                    int age = Integer.parseInt(String.valueOf(ageCell.getStringCellValue()));
//                    System.out.println("pore");
                    System.out.println("----------------------");
                    System.out.println("name:"+name);
                    System.out.println("country:"+country);
                    System.out.println("email:"+email);
                    System.out.println("gender:"+gender);
                    System.out.println("age:"+age);
                    System.out.println("^^^^^^^^^^^^^^^^^^^^^^");

                    data.add(new Athlete(name, country, age, gender, email));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // need data sort as category
            //FXCollections.sort(data, Comparator.comparing(Athlete::getCategory));


            return data;
        }
    }

    public static class Athlete {
        private SimpleStringProperty name;
        private SimpleStringProperty country;
        private SimpleStringProperty email;
        private SimpleStringProperty gender;
        private SimpleIntegerProperty age;

        public Athlete(String name, String country, int age, String gender, String email) {
            this.name = new SimpleStringProperty(name);
            this.country = new SimpleStringProperty(country);
            this.age = new SimpleIntegerProperty(age);
            this.gender = new SimpleStringProperty(gender);
            this.email = new SimpleStringProperty(email);
        }

        public String getName() {
            return name.get();
        }

        public void setName(String name) {
            this.name.set(name);
        }

        public String getCountry() {
            return country.get();
        }

        public void setCountry(String country) {
            this.country.set(country);
        }

        public int getAge() {
            return age.get();
        }

        public void setAge(int age) {
            this.age.set(age);
        }

        public String getGender() {
            return gender.get();
        }

        public void setGender(String gender) {
            this.gender.set(gender);
        }

        public String getEmail() {
            return email.get();
        }

        public void setEmail(String email) {
            this.email.set(email);
        }
    }


    private void navigateBackToMenu(ActionEvent event) {
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

    public void navigateToRegController(ActionEvent actionEvent) {
        try {
            // Load the FXML file and create a new stage
            FXMLLoader loader = new FXMLLoader(getClass().getResource("reg-view.fxml"));
            Parent root = loader.load();
            Stage regStage = new Stage();
            regStage.setTitle("Registration");
            regStage.initModality(Modality.APPLICATION_MODAL); // Make the window modal
            regStage.initOwner(stage); // Set the owner stage

            // Pass any necessary data to the controller of reg-view.fxml
            RegController regController = loader.getController();
            // If needed, set the stage to the controller
            regController.setStage(regStage);

            // Set the scene and show the stage
            Scene scene = new Scene(root);
            regStage.setScene(scene);
            regStage.showAndWait(); // Show the stage and wait for it to be closed
            updateTableView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void navigateToModifyController(ActionEvent actionEvent) {
        try {
            // Load the FXML file and create a new stage
            FXMLLoader loader = new FXMLLoader(getClass().getResource("modify-view.fxml"));
            Parent root = loader.load();
            Stage modifyStage = new Stage();
            modifyStage.setTitle("Modify");
            modifyStage.initModality(Modality.APPLICATION_MODAL); // Set as modal dialog
            modifyStage.initOwner(stage); // Set the owner stage

            // Pass any necessary data or references to the controller of modify-view.fxml
            ModifyController modifyController = loader.getController();
            modifyController.setStage(modifyStage);

            // Set the scene and show the stage
            Scene scene = new Scene(root);
            modifyStage.setScene(scene);
            modifyStage.showAndWait(); // Show the stage and wait for it to be closed
            updateTableView();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void navigateToMenuController(ActionEvent actionEvent) {
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
