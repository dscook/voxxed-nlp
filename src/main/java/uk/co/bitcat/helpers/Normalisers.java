package uk.co.bitcat.helpers;

import java.util.Arrays;
import java.util.List;

public class Normalisers {

    public static String normaliseNames(String resource) {
        // For the purposes of the presentation, slight hack to take surnames only
        return resource.substring(resource.lastIndexOf(" ")+1);
    }

    public static String whitespacesToUnderscores(String sentence) {
        List<String> splitString = Arrays.asList(sentence.split(" "));
        return splitString.stream().reduce("", (a, b) -> {
            if (!a.isEmpty()) {
                return a + "_" + b;
            } else {
                return b;
            }
        });
    }
}
