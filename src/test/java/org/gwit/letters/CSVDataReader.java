package org.gwit.letters;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CSVDataReader {
    // Read data from CSV
    public static Map<String, Map<String, String>> readDataFromCSV(String filePath) throws IOException, CsvValidationException {
        Map<String, Map<String, String>> testData = new HashMap<>();
        CSVReader csvReader = new CSVReader(new FileReader(filePath));
        String[] headers = csvReader.readNext(); // read headers
        String[] nextLine;
        while ((nextLine = csvReader.readNext()) != null) {
            if (nextLine.length >= 2) {
                String reference = nextLine[0];
                Map<String, String> fields = new HashMap<>();
                for (int i = 1; i < headers.length; i++) {
                    fields.put(headers[i], nextLine[i]);
                }
                testData.put(reference, fields);
            }
        }
        csvReader.close();
        return testData;
    }
}