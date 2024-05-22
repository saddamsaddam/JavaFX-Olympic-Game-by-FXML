package gamesource.olympicgamesapplicationfx;

import javafx.beans.property.SimpleStringProperty;
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

public class SportsDisciplineManagementController {
    @FXML
    public ImageView circleImageView;
    private Stage stage;

    @FXML
    private TableView<SportsDiscipline> SportsTableView;
    @FXML
    private TableColumn<SportsDiscipline, String> sportCategory;
    @FXML
    private TableColumn<SportsDiscipline, String> sportsDiscipline;

    @FXML
    public void initialize() throws IOException {

        Circle clip = new Circle(50, 50, 50); // x, y, radius
        circleImageView.setClip(clip);
        sportCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        sportsDiscipline.setCellValueFactory(new PropertyValueFactory<>("discipline"));

        Path dataDir = Paths.get("data");
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
        }
        // Define the path to the Excel file inside the data directory
        File file = new File(dataDir.toFile(), "sports_disciplines.xlsx");

        // Load data from Excel file
        ObservableList<SportsDiscipline> data = ExcelReader.readExcelFile(file.getPath());
        SportsTableView.setItems(data);
    }

    public static class ExcelReader {
        public static ObservableList<SportsDiscipline> readExcelFile(String filePath) {
            ObservableList<SportsDiscipline> data = FXCollections.observableArrayList();
            try (FileInputStream fis = new FileInputStream(new File(filePath));
                 Workbook workbook = new XSSFWorkbook(fis)) {

                Sheet sheet = workbook.getSheetAt(0);
                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue; // Skip header row
                    Cell categoryCell = row.getCell(1);
                    Cell disciplineCell = row.getCell(0);

                    String category = categoryCell.getStringCellValue();
                    String discipline = disciplineCell.getStringCellValue();

                    data.add(new SportsDiscipline(category, discipline));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // need data sort as category
            FXCollections.sort(data, Comparator.comparing(SportsDiscipline::getCategory));


            return data;
        }
    }


    public static  class SportsDiscipline {
        private SimpleStringProperty category;
        private SimpleStringProperty discipline;

        public SportsDiscipline(String category, String discipline) {
            this.category = new SimpleStringProperty(category);
            this.discipline = new SimpleStringProperty(discipline);
        }

        public String getCategory() {
            return category.get();
        }

        public void setCategory(String category) {
            this.category.set(category);
        }

        public String getDiscipline() {
            return discipline.get();
        }

        public void setDiscipline(String discipline) {
            this.discipline.set(discipline);
        }
    }








    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void navigateToAthleteDisciplineAssignment(ActionEvent actionEvent) {
        try {
            // Load the FXML file and create a new stage
            FXMLLoader loader = new FXMLLoader(getClass().getResource("athlete-discipline-assignment.fxml"));
            Parent root = loader.load();
            Stage assignmentStage = new Stage();
            assignmentStage.setTitle("Athlete Discipline Assignment");
            assignmentStage.initModality(Modality.APPLICATION_MODAL); // Set as modal dialog
            assignmentStage.initOwner(stage); // Set the owner stage

            // Pass any necessary data or references to the controller of athlete-discipline-assignment.fxml
            AthleteDisciplineAssignment assignmentController = loader.getController();
            assignmentController.setStage(assignmentStage);

            // Set the scene and show the stage
            Scene scene = new Scene(root);
            assignmentStage.setScene(scene);
            assignmentStage.showAndWait(); // Show the stage and wait for it to be closed
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void navigateToRegOfSportsDiscipline(ActionEvent actionEvent) {
        System.out.println("Navigate to Registration of Sports Discipline");
        try {
            // Load the FXML file and create a new stage
            FXMLLoader loader = new FXMLLoader(getClass().getResource("reg-sports-discipline.fxml"));
            Parent root = loader.load();
            Stage regStage = new Stage();
            regStage.setTitle("Registration of Sports Discipline");
            regStage.initModality(Modality.APPLICATION_MODAL); // Set as modal dialog
            regStage.initOwner(stage); // Set the owner stage

            // Pass any necessary data or references to the controller of reg-sports-discipline.fxml
            RegSportsDisciplineController regController = loader.getController();
            regController.setStage(regStage);

            // Set the scene and show the stage
            Scene scene = new Scene(root);
            regStage.setScene(scene);
            regStage.showAndWait(); // Show the stage and wait for it to be closed

            //i want to update table view here
            updateTableView();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void updateTableView() {
        Path dataDir = Paths.get("data");
        if (!Files.exists(dataDir)) {
            try {
                Files.createDirectories(dataDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Define the path to the Excel file inside the data directory
        File file = new File(dataDir.toFile(), "sports_disciplines.xlsx");

        // Load data from Excel file
        ObservableList<SportsDiscipline> data = ExcelReader.readExcelFile(file.getPath());

        SportsTableView.setItems(data);
    }

    public void navigateToMenu(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
            Parent root = loader.load();
            // Pass the stage reference to the RegController
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
