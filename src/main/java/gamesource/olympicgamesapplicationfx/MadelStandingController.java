package gamesource.olympicgamesapplicationfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class MadelStandingController {
    private Stage stage; // Stage reference

    // Method to set the stage
    public void setStage(Stage stage) {

        System.out.println("stage!");
        this.stage = stage;
        fetchData();
        fetchData2();

//        readAthleteData();
//        readResultData();
    }


    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;


    @FXML
    private BarChart<String, Number> barChart2;

    @FXML
    private CategoryAxis xAxis2;

    @FXML
    private NumberAxis yAxis2;

    private List<String> countryName;
    private List<Integer> countryMadel;

    private List<String> athleteName;
    private List<Integer> athleteMadel;


    public void initialize() {
        System.out.println("initialize");

        // Set the label for the x-axis
        xAxis.setLabel("Country");
        yAxis.setLabel("Medals");

        // Set the label for the x-axis
        xAxis2.setLabel("Athlete Name");
        yAxis2.setLabel("Medals");
    }




    private void fetchData() {
        System.out.println("fetch");
        try {
            Path dataDir = Paths.get("data");
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }

            // Define the path to the Excel file inside the data directory
            File file = new File(dataDir.toFile(), "country_result.xlsx");

            System.out.println("jjj");

            FileInputStream fis = new FileInputStream(file);
            System.out.println("kkkk");
            Workbook workbook = new XSSFWorkbook(fis);
            System.out.println("llll");
            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

            // Initialize your lists
            countryName = new ArrayList<>();
            countryMadel = new ArrayList<>();

            for (Row row : sheet) {
                Cell countryNameCell = row.getCell(0);
                Cell madelCountCell = row.getCell(2);

                System.out.println("row");
                if (countryNameCell != null && madelCountCell != null) {
                    System.out.println("ssss");
                    String countryName1 = countryNameCell.getStringCellValue();
                    System.out.println("dhur");

                    System.out.println(countryName1);
                    System.out.println("ttt");
                    if (!countryName1.equalsIgnoreCase("Country")) {

                        Integer madelCount1 = (int) madelCountCell.getNumericCellValue();

                        System.out.println(countryName1 + "  ; " + madelCount1);

                        countryName.add(countryName1);
                        countryMadel.add(madelCount1);
                    }
                }
            }

            // Close the input stream and workbook
            fis.close();
            workbook.close();

            // Create series for the bar chart
            XYChart.Series<String, Number> series = new XYChart.Series<>();

            // Add data to the series
            for (int i = 0; i < countryName.size(); i++) {
                series.getData().add(new XYChart.Data<>(countryName.get(i), countryMadel.get(i)));
            }

            // Clear existing data in the bar chart
            barChart.getData().clear();

            // Add the series to the bar chart
            barChart.getData().add(series);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




    private void fetchData2() {
        System.out.println("fetch");
        try {
            Path dataDir = Paths.get("data");
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }

            // Define the path to the Excel file inside the data directory
            File file = new File(dataDir.toFile(), "athlete_result.xlsx");

            System.out.println("jjj");

            FileInputStream fis = new FileInputStream(file);
            System.out.println("kkkk");
            Workbook workbook = new XSSFWorkbook(fis);
            System.out.println("llll");
            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

            // Initialize your lists
            athleteName = new ArrayList<>();
            athleteMadel = new ArrayList<>();

            for (Row row : sheet) {
                Cell atheleteNameCell = row.getCell(0);
                Cell madelCountCell = row.getCell(2);

                System.out.println("row");
                if (atheleteNameCell != null && madelCountCell != null) {
                    System.out.println("ssss");
                    String athleteName1 = atheleteNameCell.getStringCellValue();
                    System.out.println("dhur");

                    System.out.println(athleteName1);
                    System.out.println("ttt");
                    if (!athleteName1.equalsIgnoreCase("Athlete Name")) {

                        Integer madelCount1 = (int) madelCountCell.getNumericCellValue();

                        System.out.println(athleteName1 + "  ; " + madelCount1);

                        athleteName.add(athleteName1);
                        athleteMadel.add(madelCount1);
                    }
                }
            }

            // Close the input stream and workbook
            fis.close();
            workbook.close();

            // Create series for the bar chart
            XYChart.Series<String, Number> series = new XYChart.Series<>();

            // Add data to the series
            for (int i = 0; i < athleteName.size(); i++) {
                series.getData().add(new XYChart.Data<>(athleteName.get(i), athleteMadel.get(i)));
            }

            // Clear existing data in the bar chart
            barChart2.getData().clear();

            // Add the series to the bar chart
            barChart2.getData().add(series);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }










    public void handleBackButtonAction(ActionEvent actionEvent) {
        stage.hide();
    }
}
