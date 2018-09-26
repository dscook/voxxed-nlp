package uk.co.bitcat;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Pair;
import uk.co.bitcat.model.RelationTripleWithSpan;

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

    public static List<RelationTripleWithSpan> extractRelationships(Annotation doc) {
        List<RelationTripleWithSpan> triples = new ArrayList<>();

        for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
            // Get the character span of the sentence
            int minCharacterIndex = Integer.MAX_VALUE;
            int maxCharacterIndex = Integer.MIN_VALUE;
            for (CoreLabel word : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                if (word.beginPosition() < minCharacterIndex) {
                    minCharacterIndex = word.beginPosition();
                }
                if (word.endPosition() > maxCharacterIndex) {
                    maxCharacterIndex = word.endPosition();
                }
            }
            Pair<Integer, Integer> sentenceSpan = new Pair<>(minCharacterIndex, maxCharacterIndex);

            // Get the OpenIE triples for the sentence
            for (RelationTriple triple : sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class)) {
                RelationTripleWithSpan tripleWithSpan = new RelationTripleWithSpan(triple, sentenceSpan);
                triples.add(tripleWithSpan);
            }

        }
        return triples;
    }

    public static List<RelationTripleWithSpan> filterRelationshipsBasedOnEntities(
            List<RelationTripleWithSpan> triples, Map<String, String> entitiesToTypes) {
        List<RelationTripleWithSpan> filteredTriples = new ArrayList<>();
        for (RelationTripleWithSpan tripleWithSpan : triples) {
            if (entitiesToTypes.get(tripleWithSpan.triple.subjectLemmaGloss()) != null &&
                    entitiesToTypes.get(tripleWithSpan.triple.objectLemmaGloss()) != null) {
                filteredTriples.add(tripleWithSpan);
            }
        }
        return filteredTriples;
    }
}
