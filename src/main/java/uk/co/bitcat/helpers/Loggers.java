package uk.co.bitcat.helpers;

import edu.stanford.nlp.ie.util.RelationTriple;

import java.util.List;
import java.util.Map;

public class Loggers {

    public static void logEntities(Map<String, String> entitiesToTypes) {
        System.out.println("----------ENTITIES----------");
        for (Map.Entry<String, String> entry : entitiesToTypes.entrySet()) {
            System.out.println(entry);
        }
    }

    public static void logTriples(List<RelationTriple> triples) {
        System.out.println("----------UNFILTERED----------");
        for (RelationTriple triple : triples) {
            System.out.println(triple.confidence + "," +
                    triple.subjectLemmaGloss() + "," +
                    triple.relationLemmaGloss() + "," +
                    triple.objectLemmaGloss());
        }
    }

    public static void logTriplesWithEntityTypes(List<RelationTriple> triples, Map<String, String> entitiesToTypes) {
        System.out.println("----------FILTERED----------");
        for (RelationTriple triple : triples) {
            System.out.println(triple.confidence + "," +
                    triple.subjectLemmaGloss() + "(" + entitiesToTypes.get(triple.subjectLemmaGloss()) + ")," +
                    triple.relationLemmaGloss() + "," +
                    triple.objectLemmaGloss() + "(" + entitiesToTypes.get(triple.objectLemmaGloss()) + ")");
        }
    }
}
