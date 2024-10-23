package org.gwit.letters;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaselineMatcher {

    public static boolean compareWithBaseline(String baselineText, String newText, Map<String, String> mergeFields) {
        // Ignore the merge fields when comparing
        String cleanedBaselineText = removeMergeFields(baselineText);
        String cleanedNewText = replaceActualValuesWithPlaceholders(newText, mergeFields);

        return cleanedBaselineText.equals(cleanedNewText);
    }



    private static String removeMergeFields(String text) {
        // Replace typical merge fields placeholders in the text
        text = text.replaceAll("<Name>", "")
                .replaceAll("<AddressLine1>", "")
                .replaceAll("<AddressLine2>", "")
                .replaceAll("<City>", "")
                .replaceAll("<State>", "")
                .replaceAll("<ZipCode>", "");
        return text.trim();
    }

    private static String replaceActualValuesWithPlaceholders(String text, Map<String, String> mergeFields) {
        for (Map.Entry<String, String> entry : mergeFields.entrySet()) {
            String actualValue = entry.getValue();
            String placeholder = "<" + entry.getKey() + ">";
            text = text.replaceAll(Pattern.quote(actualValue), Matcher.quoteReplacement(placeholder));
        }
        return text.trim();
    }
}