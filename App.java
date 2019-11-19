package de.exxcellent.challenge;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public final class App {

    public static void main(String... args) {

        ArrayList<DataItem> dataItems;
        //class App called with args, e.g., java App --football football.csv
        if (args.length == 2) {
            String arg1 = args[0];
            String arg2 = args[1];
            dataItems = new DataImport().importData(arg2);
            if (arg1.equals("--football")) {
                showFootballResult(teamWithSmallestGoalSpread(dataItems));
            } else if (arg1.equals("--weather")) {
                showWeatherResult(dayWithSmallestTempSpread(dataItems));
            }
            //class App called without args
        } else {
            dataItems = new DataImport().importData("football.csv");
            showFootballResult(teamWithSmallestGoalSpread(dataItems));
            dataItems = new DataImport().importData("weather.csv");
            showWeatherResult(dayWithSmallestTempSpread(dataItems));
        }

    }

    //outputs team name with smallest goal spread
    private static void showFootballResult(String teamWithSmallestGoalSpread) {
        System.out.printf("Team with smallest goal spread       : %s%n", teamWithSmallestGoalSpread);
    }

    //outputs day number with smallest temperature spread
    private static void showWeatherResult(String dayWithSmallestTempSpread) {
        System.out.printf("Day with smallest temperature spread : %s%n", dayWithSmallestTempSpread);
    }

    //returns team name with smallest goal spread
    private static String teamWithSmallestGoalSpread(ArrayList<DataItem> dataItems) {
        String result;
        //value keys are delivered as Strings
        result = identifierWithSmallestValueSpread(dataItems, "Goals", "Goals Allowed");
        return result;
    }

    //returns day number with smallest temperature spread
    private static String dayWithSmallestTempSpread(ArrayList<DataItem> dataItems) {
        String result;
        //value keys are delivered as Strings
        result = identifierWithSmallestValueSpread(dataItems, "MxT", "MnT");
        return result;
    }

    //returns identifier with smallest value spread; Strings idFirstValue and idSecondValue are search keys for the two values 
    private static String identifierWithSmallestValueSpread(ArrayList<DataItem> dataItems, String idFirstValue, String idSecondValue) {
        String back;
        ArrayList<Double> valueSpread = valueSpread(dataItems, idFirstValue, idSecondValue);
        DataItem dataItemWithSmallestValueSpread = dataItems.get(indexOfMinimum(valueSpread));
        back = dataItemWithSmallestValueSpread.getIdentifier();
        return back;
    }

    //returns value spread; Strings idFirstValue and idSecondValue are search keys for the two values
    private static ArrayList<Double> valueSpread(ArrayList<DataItem> dataItems, String idFirstValue, String idSecondValue) {
        ArrayList<Double> back = new ArrayList<>();

        for (int i = 0; i < dataItems.size(); i++) {
            double firstValue = dataItems.get(i).getDataValue(idFirstValue);
            double secondValue = dataItems.get(i).getDataValue(idSecondValue);
            double valueSpread = Math.abs(firstValue - secondValue);
            back.add(i, valueSpread);
        }
        return back;
    }

    //returns minimum of a list of double values
    private static int indexOfMinimum(ArrayList<Double> doubleValues) {
        double min = doubleValues.get(0);
        int indexOfMinimum = 0;
        for (int i = 0; i < doubleValues.size(); i++) {
            if (doubleValues.get(i) < min) {
                min = doubleValues.get(i);
                indexOfMinimum = i;
            }
        }
        return indexOfMinimum;
    }

}

//-----------------------------------------------------------------------------------------------------
class DataImport implements ImportData {

    //returns list of data items from import file
    @Override
    public ArrayList<DataItem> importData(String fileName) {
        ArrayList<String[]> rawData;
        ArrayList<DataItem> convertedData = new ArrayList();

        if (isValidFileType(fileName)) {
            rawData = readCSVFile(fileName);
            convertedData = convertToDataItems(rawData);
        } else {
            System.out.println("Illegal input file!");
        }
        return convertedData;
    }

    //converts raw data from import file to list of data items
    private static ArrayList<DataItem> convertToDataItems(ArrayList<String[]> rawData) {
        ArrayList<DataItem> back = new ArrayList();

        for (int i = 1; i < rawData.size(); i++) {
            String identifier = rawData.get(i)[0];
            HashMap data = new HashMap();
            for (int j = 1; j < rawData.get(i).length; j++) {
                data.put(rawData.get(0)[j], rawData.get(i)[j]);
            }
            back.add(new DataItem(identifier, data));
        }
        return back;
    }

    //checks whether import file is valid file type; in this case, .csv
    @Override
    public boolean isValidFileType(String fileName) {
        return fileName.endsWith(".csv");
    }

    //returns raw data from import file
    private static ArrayList<String[]> readCSVFile(String fileName) {

        ArrayList<String[]> rawData = new ArrayList<>();
        BufferedReader br = null;
        String line;
        String separator = ",";
        int lineIndex = 0;

        try {
            InputStream input = App.class.getResourceAsStream("/de/exxcellent/challenge/" + fileName);
            br = new BufferedReader(new InputStreamReader(input, "UTF-8"));
            while ((line = br.readLine()) != null) {

                String[] lineData = line.split(separator);
                rawData.add(lineIndex, lineData);
                lineIndex++;
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        } catch (IOException e) {
            System.out.println("Data import failed!");
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println("Data import failed!");
                }
            }
        }
        return rawData;
    }

}

//-----------------------------------------------------------------------------------------------------
class DataItem {

    private final String identifier;
    private final HashMap data;

    DataItem(String identifier, HashMap data) {
        this.identifier = identifier;
        this.data = new HashMap(data);
    }

    public String getIdentifier() {
        return this.identifier;
    }

    private HashMap getData() {
        return new HashMap(data);
    }

    public double getDataValue(String dataKey) {
        double dataValue;
        String dataValueString = (String) getData().get(dataKey);
        dataValue = Double.parseDouble(dataValueString);
        return dataValue;
    }
}

//-----------------------------------------------------------------------------------------------------
interface ImportData {

    public abstract ArrayList<DataItem> importData(String fileName);

    public abstract boolean isValidFileType(String fileName);
}
