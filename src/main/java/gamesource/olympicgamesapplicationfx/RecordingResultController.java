package gamesource.olympicgamesapplicationfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class RecordingResultController {
    @FXML
    public ImageView circleImageView;
    private Stage stage; // Stage reference

    // Method to set the stage
    public void setStage(Stage stage) {
        this.stage = stage;
    }


    @FXML
    private TableView<ResultRecord> resultTableView;

    @FXML
    private TableColumn<ResultRecord, String> resultEventName;

    @FXML
    private TableColumn<ResultRecord, String> resultDiscipline;

    @FXML
    private TableColumn<ResultRecord, String> resultAthleteName;

    @FXML
    private TableColumn<ResultRecord, String> resultTime;

    @FXML
    private TableColumn<ResultRecord, Integer> resultScore;

    public void initialize() {
        Circle clip = new Circle(50, 50, 50); // x, y, radius
        circleImageView.setClip(clip);
        // Set cell value factories to extract data from ResultRecord object properties
        resultEventName.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        resultDiscipline.setCellValueFactory(new PropertyValueFactory<>("discipline"));
        resultAthleteName.setCellValueFactory(new PropertyValueFactory<>("athleteName"));
        resultTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        resultScore.setCellValueFactory(new PropertyValueFactory<>("score"));

        // Call a method to populate the table with data
        populateResultTableView();
    }


    private void populateResultTableView() {
        Path dataDir = Paths.get("data");
        if (!Files.exists(dataDir)) {
            try {
                Files.createDirectories(dataDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Define the path to the Excel file inside the data directory
        File file = new File(dataDir.toFile(), "recorded_results.xlsx");
        List<ResultRecord> resultRecords = readRecordsFromExcel(file.getPath());
        Collections.sort(resultRecords, Comparator.comparing(ResultRecord::getEventName));





        resultTableView.getItems().addAll(resultRecords);
    }

    private List<ResultRecord> readRecordsFromExcel(String filePath) {
        List<ResultRecord> resultRecords = new ArrayList<>();

        try (FileInputStream inputStream = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = sheet.iterator();

            // Skip the header row
            if (iterator.hasNext()) {
                iterator.next();
            }

            while (iterator.hasNext()) {
                Row currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();

                String eventName = cellIterator.next().getStringCellValue();
                String discipline = cellIterator.next().getStringCellValue();
                String athleteName = cellIterator.next().getStringCellValue();
                String time = cellIterator.next().getStringCellValue();

//                System.out.println("here");
//                System.out.println("mone hoi:"+cellIterator.next().getStringCellValue());
//                System.out.println("akhane:"+cellIterator.next().getNumericCellValue());
//                int score = (int) cellIterator.next().getNumericCellValue();
//                System.out.println("asenai");

                int score = Integer.parseInt(String.valueOf(cellIterator.next().getStringCellValue()));
                ResultRecord record = new ResultRecord(eventName, discipline, athleteName, time, score);
                resultRecords.add(record);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultRecords;
    }





public class ResultRecord {
        private String eventName;
        private String discipline;
        private String athleteName;
        private String time;
        private int score;

        public ResultRecord(String eventName, String discipline, String athleteName, String time, int score) {
            this.eventName = eventName;
            this.discipline = discipline;
            this.athleteName = athleteName;
            this.time = time;
            this.score = score;
        }

        // Getters and setters for the properties
        public String getEventName() {
            return eventName;
        }

        public void setEventName(String eventName) {
            this.eventName = eventName;
        }

        public String getDiscipline() {
            return discipline;
        }

        public void setDiscipline(String discipline) {
            this.discipline = discipline;
        }

        public String getAthleteName() {
            return athleteName;
        }

        public void setAthleteName(String athleteName) {
            this.athleteName = athleteName;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }
    }




    public void navigateToRecordingOfResult(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("recording-result-of-sports-event.fxml"));
            Parent root = loader.load();

            // Create a new stage for the popup
            Stage recordingStage = new Stage();
            recordingStage.setTitle("Recording Result of Sports Event");
            recordingStage.initModality(Modality.APPLICATION_MODAL); // Set as modal dialog
            recordingStage.initOwner(stage); // Set the owner stage

            // Pass the stage reference to the controller
            RecordingResultOfSportsEventController controller = loader.getController();
            controller.setStage(recordingStage);

            // Set the scene and show the stage
            Scene scene = new Scene(root);
            recordingStage.setScene(scene);
            recordingStage.showAndWait(); // Show the stage and wait for it to be closed
            populateResultTableView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void navigateToAthleteAndCountryPerformanceRanking(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("athlete-ranking.fxml"));
            Parent root = loader.load();

            // Create a new stage for the popup
            Stage rankingStage = new Stage();
            rankingStage.setTitle("Athlete and Country Performance Ranking");
            rankingStage.initModality(Modality.APPLICATION_MODAL); // Set as modal dialog
            rankingStage.initOwner(stage); // Set the owner stage

            // Pass the stage reference to the controller
            AthleteRankingController controller = loader.getController();
            controller.setStage(rankingStage);

            // Set the scene and show the stage
            Scene scene = new Scene(root);
            rankingStage.setScene(scene);
            rankingStage.showAndWait(); // Show the stage and wait for it to be closed
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
