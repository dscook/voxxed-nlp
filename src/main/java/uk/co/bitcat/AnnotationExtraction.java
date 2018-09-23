package uk.co.bitcat;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnotationExtraction {

    public static Map<String, String> extractEntitiesAndTheirTypes(Annotation doc) {
        Map<String, String> entitiesToTypes = new HashMap<>();
        for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
            for (CoreMap entityMention : sentence.get(CoreAnnotations.MentionsAnnotation.class)) {
                entitiesToTypes.put(
                        entityMention.get(CoreAnnotations.TextAnnotation.class),
                        entityMention.get(CoreAnnotations.NamedEntityTagAnnotation.class));
            }
        }
        return entitiesToTypes;
    }

    public static List<RelationTriple> extractRelationships(Annotation doc) {
        List<RelationTriple> triples = new ArrayList<>();
        for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
            // Get the OpenIE triples for the sentence
            triples.addAll(sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class));
        }
        return triples;
    }

    public static List<RelationTriple> filterRelationshipsBasedOnEntities(
            List<RelationTriple> triples, Map<String, String> entitiesToTypes) {
        List<RelationTriple> filteredTriples = new ArrayList<>();
        for (RelationTriple triple : triples) {
            if (entitiesToTypes.get(triple.subjectLemmaGloss()) != null &&
                    entitiesToTypes.get(triple.objectLemmaGloss()) != null) {
                filteredTriples.add(triple);
            }
        }
        return filteredTriples;
    }
}
