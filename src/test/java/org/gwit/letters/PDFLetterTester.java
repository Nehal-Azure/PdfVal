package org.gwit.letters;

import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.gwit.letters.CSVDataReader.readDataFromCSV;
import static org.gwit.letters.PDFTextExtractor.extractTextFromPDF;

public class PDFLetterTester {
    public static void main(String[] args) {
        String pdfDirectoryPath = "path/to/your/pdf/directory";
        String csvPath = "path/to/your/testdata.csv";

        try {
            // Read the CSV file to get the test data
            Map<String, Map<String, String>> testData = readDataFromCSV(csvPath);

            // Process each entry in the test data
            for (Map.Entry<String, Map<String, String>> entry : testData.entrySet()) {
                String reference = entry.getKey();
                Map<String, String> fields = entry.getValue();

                // Find the target PDF file by reference number
                File pdfFile = findPdfFileByReference(pdfDirectoryPath, reference);
                if (pdfFile == null) {
                    System.out.println("PDF file not found for reference: " + reference);
                    continue;
                }

                // Extract text from the PDF
                String pdfText = extractTextFromPDF(pdfFile.getAbsolutePath());

                // Validate the merge fields
                boolean mergeFieldsValid = validateMergeFields(pdfText, fields);
                if (mergeFieldsValid) {
                    System.out.println("Validation passed for reference: " + reference);
                } else {
                    System.out.println("Validation failed for reference: " + reference);
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }

    // Find the PDF file by reference number
    private static File findPdfFileByReference(String directoryPath, String reference) {
        File dir = new File(directoryPath);
        for (File file : dir.listFiles((d, name) -> name.endsWith(".pdf") && name.contains(reference))) {
            return file;
        }
        return null;
    }

    // Validate merge fields
    private static boolean validateMergeFields(String text, Map<String, String> testData) {
        for (Map.Entry<String, String> entry : testData.entrySet()) {
            if (!text.contains(entry.getValue())) {
                System.out.println("Mismatch found for field: " + entry.getKey());
                return false;
            }
        }
        return true;
    }
}