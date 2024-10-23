package org.gwit.letters;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PDFLetterTesterTESTNG {
    private static final String BASELINE_PDF_PATH = "path/to/your/baseline.pdf";
    private static final String PDF_DIRECTORY_PATH = "path/to/your/pdf/directory";
    private static final String CSV_PATH = "path/to/your/testdata.csv";
    private String baselineText;
    private Map<String, Map<String, String>> testData;

    @BeforeClass
    public void setUp() throws IOException, CsvValidationException {
        // Extract text from the baseline PDF
        baselineText = extractTextFromPDF(BASELINE_PDF_PATH);
        // Read the CSV file to get the test data
        testData = readDataFromCSV(CSV_PATH);
    }

    @DataProvider(name = "pdfData")
    public Object[][] providePdfData() {
        return testData.keySet().stream()
                .map(ref -> new Object[]{ref, testData.get(ref)})
                .toArray(Object[][]::new);
    }

    @Test(dataProvider = "pdfData")
    public void validateStaticContent(String reference, Map<String, String> fields) {
        File pdfFile = findPdfFileByReference(PDF_DIRECTORY_PATH, reference);
        Assert.assertNotNull(pdfFile, "PDF file not found for reference: " + reference);

        try {
            String pdfText = extractTextFromPDF(pdfFile.getAbsolutePath());
            boolean matchesBaseline = BaselineMatcher.compareWithBaseline(baselineText, pdfText, fields);
            Assert.assertTrue(matchesBaseline, "Static text does not match the baseline for reference: " + reference);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("Error while extracting text from PDF for reference: " + reference);
        }
    }

    @Test(dataProvider = "pdfData")
    public void validateMergeFields(String reference, Map<String, String> fields) {
        File pdfFile = findPdfFileByReference(PDF_DIRECTORY_PATH, reference);
        Assert.assertNotNull(pdfFile, "PDF file not found for reference: " + reference);

        try {
            String pdfText = extractTextFromPDF(pdfFile.getAbsolutePath());
            boolean mergeFieldsValid = validateMergeFieldss(pdfText, fields);
            Assert.assertTrue(mergeFieldsValid, "Merge fields validation failed for reference: " + reference);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("Error while extracting text from PDF for reference: " + reference);
        }
    }

    private static String extractTextFromPDF(String filePath) throws IOException {
        PDDocument document = PDDocument.load(new File(filePath));
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(document);
        document.close();
        return text;
    }

    private static Map<String, Map<String, String>> readDataFromCSV(String filePath) throws IOException, CsvValidationException {
        Map<String, Map<String, String>> testData = new HashMap<>();
        CSVReader csvReader = new CSVReader(new FileReader(filePath));
        String[] headers = csvReader.readNext(); // Read headers
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

    private static File findPdfFileByReference(String directoryPath, String reference) {
        File dir = new File(directoryPath);
        if (dir.isDirectory()) {
            for (File file : dir.listFiles((d, name) -> name.endsWith(".pdf") && name.contains(reference))) {
                return file;
            }
        }
        return null;
    }

    private static boolean validateMergeFieldss(String text, Map<String, String> testData) {
        for (Map.Entry<String, String> entry : testData.entrySet()) {
            if (!text.contains(entry.getValue())) {
                System.out.println("Mismatch found for field: " + entry.getKey());
                return false;
            }
        }
        return true;
    }
}
