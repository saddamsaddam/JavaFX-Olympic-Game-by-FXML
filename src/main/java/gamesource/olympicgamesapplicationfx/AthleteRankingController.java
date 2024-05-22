package gamesource.olympicgamesapplicationfx;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


import javafx.beans.property.SimpleIntegerProperty;

public class AthleteRankingController {


    public Label messageLabel;
    private Stage stage; // Stage reference

    // Method to set the stage
    public void setStage(Stage stage) {
        this.stage = stage;

        readAthleteData();
        readResultData();
    }
    @FXML
    private TableView<CountryAthleteData> rankingTable;

    @FXML
    private TableColumn<CountryAthleteData, String> countryColumn;

    @FXML
    private TableColumn<CountryAthleteData, Integer> athleteCountColumn;

    @FXML
    private TableColumn<CountryAthleteData, Integer> medalCountColumn;


    @FXML
    private TableView<AthleteData> athleteRankingTable;

    @FXML
    private TableColumn<AthleteData, String> athleteNameColumn;

    @FXML
    private TableColumn<AthleteData, String> countryColumn2;

    @FXML
    private TableColumn<AthleteData, Integer> medalCountColumn2;




    // Name -> Country
    private Map<String, String> athleteToCountry = new HashMap<>();
    private Map<String, AthleteData> athleteDataMap= new HashMap<>();
    private Map<String, CountryAthleteData> countryDataMap= new HashMap<>();
    private Map<String, List<String[]>> eventDataMap = new HashMap<>();


    private Map<String, Integer> countryMadelCounting = new HashMap<>(); // country -> how many madel
    private Map<String, Integer> countryAthletCounting = new HashMap<>(); //  country-> how many athlete
    private Map<String, Boolean> checkexistAthlete = new HashMap<>();


    private List<CountryAthleteData> countryAthleteDataList = new ArrayList<>();
    private List<AthleteData> athleteDataList = new ArrayList<>();

    // Sample data
    private final ObservableList<CountryAthleteData> countryAthleteData = FXCollections.observableArrayList(
    );

    private final ObservableList<AthleteData> athleteData = FXCollections.observableArrayList(
    );

    @FXML
    public void initialize() {
        // Initialize table columns
        countryColumn.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        athleteCountColumn.setCellValueFactory(cellData -> cellData.getValue().athleteCountProperty().asObject());
        medalCountColumn.setCellValueFactory(cellData -> cellData.getValue().medalCountProperty().asObject());

        // Add sample data to the table
        rankingTable.setItems(countryAthleteData);



        // Initialize table columns for athlete ranking
        athleteNameColumn.setCellValueFactory(cellData -> cellData.getValue().athleteNameProperty());
        countryColumn2.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        medalCountColumn2.setCellValueFactory(cellData -> cellData.getValue().medalCountProperty().asObject());

        athleteRankingTable.setItems(athleteData);

    }



    public void readAthleteData(){
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

            //  Name	Country	    Email	Gender	    Age

            for (Row row : sheet) {
                Cell nameCell = row.getCell(0);
                Cell countryCell = row.getCell(1);

                String name = nameCell.getStringCellValue();
                String country = countryCell.getStringCellValue();

//                System.out.println("-------------Athlete data--------------");
//                System.out.println(name);
//                System.out.println(country);
//                System.out.println("///////////////////////////////");

                if(!name.isEmpty() && !country.isEmpty()){
                    AthleteData athleteData = new AthleteData(name, country);
                    athleteDataMap.put(name, athleteData);
                    athleteToCountry.put(name, country);
                }
            }

//            System.out.println("\n\n\n======================================\n");
            fis.close();
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void readResultData() {
        try {
            Path dataDir = Paths.get("data");
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            // Define the path to the Excel file inside the data directory
            File file = new File(dataDir.toFile(), "recorded_results.xlsx");


            FileInputStream fis = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

            //  Event_Name	    Discipline      Athlete     Time    Score

            for (Row row : sheet) {
                Cell eventNameCell = row.getCell(0);
                Cell disciplineCell = row.getCell(1);
                Cell athleteCell = row.getCell(2);
                Cell timeCell = row.getCell(3);
                Cell scoreCell = row.getCell(4);

                String eventName = eventNameCell.getStringCellValue();
                String discipline = disciplineCell.getStringCellValue();
                String athlete  = athleteCell.getStringCellValue();
                String time = timeCell.getStringCellValue();
                String score  = scoreCell.getStringCellValue();

//                System.out.println("-------------Event data--------------");
//                System.out.println(event_name);
//                System.out.println(discipline);
//                System.out.println(athlete);
//                System.out.println(time);
//                System.out.println(score);

                String country = "nun";
                if(athleteToCountry.containsKey(athlete)){
//                    System.out.println(athleteToCountry.get(athlete));
                    country = athleteToCountry.get(athlete);
                }
//                else{
//
//                    //System.out.println("******************");
//                }

                String[] details = {athlete,country,discipline,time,score};

                if (!eventDataMap.containsKey(eventName)) {
                    eventDataMap.put(eventName, new ArrayList<>());
                }
                eventDataMap.get(eventName).add(details);

//                System.out.println("///////////////////////////////");
            }
            fis.close();
            workbook.close();

            // Sort details for each event based on score
            for (List<String[]> eventDetails : eventDataMap.values()) {
                eventDetails.sort((detail1, detail2) -> {
                    double score1 = Double.parseDouble(detail1[4]);
                    double score2 = Double.parseDouble(detail2[4]);
                    return Double.compare(score2, score1); // Sort in descending order
                });
            }


            System.out.println("-----------------data----------------");
            /////    sort korte hobe event wise  then okhan theke gold,silver,bronze

            for (String key : eventDataMap.keySet()) {
                System.out.println("^^^^^^^^^ "+ key +" ^^^^^^^^^");
                List<String[]> dataList = eventDataMap.get(key);


                // Result Validation
                if(!dataList.isEmpty()){     // Gold Madel
                    String[] data = dataList.get(0);
                    String athlete = data[0];
                    String country = data[1];
                    String discipline = data[2];
                    String time = data[3];
                    String score  = data[4];

                    if(athleteDataMap.containsKey(athlete)){
                        AthleteData  athleteGold = athleteDataMap.get(athlete);
                        athleteGold.goldIncrease();

                        System.out.println(athlete+ "; Madels:"+athleteGold.getMedalCount());

                        if(!countryAthletCounting.containsKey(country)){
                            countryAthletCounting.put(country,0);
                        }
                        if(!countryMadelCounting.containsKey(country)){
                            countryMadelCounting.put(country,0);
                        }

                        Integer athlete_number = countryAthletCounting.get(country);
                        Integer madel_number = countryMadelCounting.get(country);

                        if(!checkexistAthlete.containsKey(athlete)){
                            countryAthletCounting.put(country, athlete_number+1);
                            checkexistAthlete.put(athlete,Boolean.TRUE);
                        }
                        countryMadelCounting.put(country, madel_number+1);
                    }
                }

                if(dataList.size()>=2){     // Silver Madel
                    String[] data = dataList.get(1);
                    String athlete = data[0];
                    String country = data[1];
                    String discipline = data[2];
                    String time = data[3];
                    String score  = data[4];
                    if(athleteDataMap.containsKey(athlete)){
                        AthleteData  athleteGold = athleteDataMap.get(athlete);
                        athleteGold.silverIncrease();

                        System.out.println(athlete+ "; Madels:"+athleteGold.getMedalCount());
                    }

                    if(!countryAthletCounting.containsKey(country)){
                        countryAthletCounting.put(country,0);
                    }
                    if(!countryMadelCounting.containsKey(country)){
                        countryMadelCounting.put(country,0);
                    }

                    Integer athlete_number = countryAthletCounting.get(country);
                    Integer madel_number = countryMadelCounting.get(country);

                    if(!checkexistAthlete.containsKey(athlete)){
                        countryAthletCounting.put(country, athlete_number+1);
                        checkexistAthlete.put(athlete,Boolean.TRUE);
                    }
                    countryMadelCounting.put(country, madel_number+1);
                }

                if(dataList.size()>=3){     // Bronze Madel
                    String[] data = dataList.get(2);
                    String athlete = data[0];
                    String country = data[1];
                    String discipline = data[2];
                    String time = data[3];
                    String score  = data[4];
                    if(athleteDataMap.containsKey(athlete)){
                        AthleteData  athleteGold = athleteDataMap.get(athlete);
                        athleteGold.bronzeIncrease();

                        System.out.println(athlete+ "; Madels:"+athleteGold.getMedalCount());
                    }

                    if(!countryAthletCounting.containsKey(country)){
                        countryAthletCounting.put(country,0);
                    }
                    if(!countryMadelCounting.containsKey(country)){
                        countryMadelCounting.put(country,0);
                    }




                    Integer athlete_number = countryAthletCounting.get(country);
                    Integer madel_number = countryMadelCounting.get(country);

                    if(!checkexistAthlete.containsKey(athlete)){
                        countryAthletCounting.put(country, athlete_number+1);
                        checkexistAthlete.put(athlete,Boolean.TRUE);
                    }

                    countryMadelCounting.put(country, madel_number+1);
                }
                for(String[] data : dataList){
                    String athlete = data[0];
                    String country = data[1];
                    String discipline = data[2];
                    String time = data[3];
                    String score  = data[4];

                    System.out.println("--------------");
                    System.out.println(athlete);
                    System.out.println(country);
                    System.out.println(discipline);
                    System.out.println(time);
                    System.out.println(score);
                    System.out.println("..............");

                }
            }

            System.out.println("#####################################");
            for(String country_name: countryMadelCounting.keySet()){
                System.out.println("coutry name: "+country_name+ " ; Madels: "+countryMadelCounting.get(country_name)+" ; Athletes:"+countryAthletCounting.get(country_name));

                CountryAthleteData countryAthleteData1 = new CountryAthleteData(country_name, countryAthletCounting.get(country_name), countryMadelCounting.get(country_name));
//                countryDataMap.put(country_name, countryAthleteData1);
                if(country_name!="nun")
                    countryAthleteDataList.add(countryAthleteData1);
            }
            countryAthleteData.setAll(countryAthleteDataList);
            System.out.println("////////////////////////////////////");

           // athleteDataMap


            for(String athlete_name: athleteDataMap.keySet()){
                AthleteData athleteData1 = athleteDataMap.get(athlete_name);
                System.out.println("athlete_name: "+athlete_name+ " ; Country: "+athleteData1.getCountry()+" ; Madels:"+athleteData1.getMedalCount());

                if(!athlete_name.equalsIgnoreCase("Name"))
                    athleteDataList.add(athleteData1);
            }


            Comparator<AthleteData> byMedalCount = Comparator.comparingInt(AthleteData::getMedalCount).reversed();
            // Sort athleteDataList using the custom comparator
            Collections.sort(athleteDataList, byMedalCount);
            // Refresh the TableView to reflect the sorted data
//            athleteRankingTable.setItems(FXCollections.observableArrayList(athleteDataList));

            athleteData.addAll(athleteDataList);


            saveToExcel();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleBackButtonAction(ActionEvent actionEvent) {
        stage.hide();
        //
    }

    public void Export(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Add Confirmation");
        alert.setHeaderText("Do you want to Export country_result and athlete_result ?");
        alert.setContentText("Choose your option.");

        ButtonType addButton = new ButtonType("Export");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(addButton, cancelButton);

        // Get the stage (window) from the action event
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        // Set the owner window for the alert
        alert.initOwner(stage);

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == addButton) {
                // Add logic goes here
                saveToExcel();
                Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
                alert1.setTitle(" Result");
                alert1.setHeaderText("Successfully data was exported to country_result.xlsx and athlete_result.xlsx");

                ButtonType cancelButton1 = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert1.getButtonTypes().setAll(cancelButton1);

                // Set the owner window for the second alert
                alert1.initOwner(stage);

                alert1.showAndWait().ifPresent(buttonType1 -> {
                    // Handle button click if needed
                });
            } else {
                // Cancel logic goes here
                System.out.println("Cancelled.");
            }
        });
    }



    public class AthleteData {
        private final SimpleStringProperty name;
        private final SimpleStringProperty country;
        private int goldMedal;
        private int silverMedal;
        private int bronzeMedal;

        public AthleteData(String name, String country) {

            this.country = new SimpleStringProperty(country);
            this.name = new SimpleStringProperty(name);

            this.goldMedal = 0; // Default value is 0
            this.silverMedal = 0; // Default value is 0
            this.bronzeMedal = 0;
        }

        public void goldIncrease(){
            goldMedal++;
        }
        public void silverIncrease(){
            silverMedal++;
        }
        public void bronzeIncrease(){
            bronzeMedal++;
        }


        public String getCountry() {
            return country.get();
        }

        public SimpleStringProperty countryProperty() {
            return country;
        }

        public String getAthleteName() {
            return name.get();
        }

        public SimpleStringProperty athleteNameProperty() {
            return name;
        }

        public int getMedalCount() {
            return goldMedal+silverMedal+bronzeMedal;
        }

        public SimpleIntegerProperty medalCountProperty() {
            return new SimpleIntegerProperty(goldMedal+silverMedal+bronzeMedal);
        }
    }


    public class CountryAthleteData {
        private final SimpleStringProperty country;
        private final SimpleIntegerProperty athleteCount;
        private final SimpleIntegerProperty medalCount;

        public CountryAthleteData(String country, int athleteCount, int medalCount) {
            this.country = new SimpleStringProperty(country);
            this.athleteCount = new SimpleIntegerProperty(athleteCount);
            this.medalCount = new SimpleIntegerProperty(medalCount);
        }

        public String getCountry() {
            return country.get();
        }

        public SimpleStringProperty countryProperty() {
            return country;
        }

        public int getAthleteCount() {
            return athleteCount.get();
        }

        public SimpleIntegerProperty athleteCountProperty() {
            return athleteCount;
        }

        public int getMedalCount() {
            return medalCount.get();
        }

        public SimpleIntegerProperty medalCountProperty() {
            return medalCount;
        }
    }






    public void saveToExcel() {
        try {
            // Create a new workbook
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Athlete Result");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Athlete Name");
            headerRow.createCell(1).setCellValue("Country");
            headerRow.createCell(2).setCellValue("Medal Count");

            // Populate data rows
            for (int i = 0; i < athleteData.size(); i++) {
                AthleteData athlete = athleteData.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(athlete.getAthleteName());
                row.createCell(1).setCellValue(athlete.getCountry());
                row.createCell(2).setCellValue(athlete.getMedalCount());
            }

            // Write the workbook to a file
            Path dataDir = Paths.get("data");
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            // Define the path to the Excel file inside the data directory
            File file = new File(dataDir.toFile(), "athlete_result.xlsx");

            FileOutputStream fileOut = new FileOutputStream(file);
            workbook.write(fileOut);
            fileOut.close();

            // Close the workbook
            workbook.close();

            System.out.println("Data saved to athlete_result.xlsx");

        } catch (IOException e) {
            e.printStackTrace();
        }



        try {
            // Create a new workbook
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Country Result");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Country");
            headerRow.createCell(1).setCellValue("Athlete Count");
            headerRow.createCell(2).setCellValue("Medal Count");

            // Populate data rows
            for (int i = 0; i < countryAthleteData.size(); i++) {
                CountryAthleteData countryAthleteData1 = countryAthleteData.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(countryAthleteData1.getCountry());
                row.createCell(1).setCellValue(countryAthleteData1.getAthleteCount());
                row.createCell(2).setCellValue(countryAthleteData1.getMedalCount());
            }

            // Write the workbook to a file
            Path dataDir = Paths.get("data");
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            // Define the path to the Excel file inside the data directory
            File file = new File(dataDir.toFile(), "country_result.xlsx");

            FileOutputStream fileOut = new FileOutputStream(file);
            workbook.write(fileOut);
            fileOut.close();

            // Close the workbook
            workbook.close();

            System.out.println("Data saved to country_result.xlsx");

        } catch (IOException e) {
            e.printStackTrace();
        }

        //messageLabel.setText("data saved to country_result.xlsx and athlete_result.xlsx");
       // messageLabel.setTextFill(Color.GREEN); // Set text color to green
    }
}
