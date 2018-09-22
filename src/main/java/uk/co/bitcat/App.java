package uk.co.bitcat;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;

public class App {

    public static void main( String[] args ) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,depparse,coref,natlog,openie");
        props.setProperty("openie.resolve_coref", "true");

        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation doc = new Annotation(FilmPlotText.JURASSIC_PARK_PLOT);
        pipeline.annotate(doc);

        // Create hash map of entities to their types
        Map<String, String> entitiesToTypes = new HashMap<>();
        for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
            for (CoreMap entityMention : sentence.get(CoreAnnotations.MentionsAnnotation.class)) {
                entitiesToTypes.put(
                        entityMention.get(CoreAnnotations.TextAnnotation.class),
                        entityMention.get(CoreAnnotations.NamedEntityTagAnnotation.class));
            }
        }

        System.out.println("----------ENTITIES--------------");
        for (Map.Entry<String, String> entry : entitiesToTypes.entrySet()) {
            System.out.println(entry);
        }

        // Extract the relationships in the document
        List<RelationTriple> triples = new ArrayList<>();
        for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
            // Get the OpenIE triples for the sentence
            triples.addAll(sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class));
        }

        // Remove triples where either end doesn't conform to a named entity type
        List<RelationTriple> filteredTriples = new ArrayList<>();
        for (RelationTriple triple : triples) {
            if (entitiesToTypes.get(triple.subjectLemmaGloss()) != null &&
                    entitiesToTypes.get(triple.objectLemmaGloss()) != null) {
                filteredTriples.add(triple);
            }
        }
        System.out.println("----------UNFILTERED--------------");
        // Print the unfilteredtriples
        for (RelationTriple triple : triples) {
            System.out.println(triple.confidence + "," +
                    triple.subjectLemmaGloss() + "," +
                    triple.relationLemmaGloss() + "," +
                    triple.objectLemmaGloss());
        }

        System.out.println("-----------FILTERED-------------");

        // Print the triples
        for (RelationTriple triple : filteredTriples) {
            System.out.println(triple.confidence + "," +
                    triple.subjectLemmaGloss() + "(" + entitiesToTypes.get(triple.subjectLemmaGloss()) + ")," +
                    triple.relationGloss() + "," +
                    triple.objectLemmaGloss() + "(" + entitiesToTypes.get(triple.objectLemmaGloss()) + ")");
        }
    }
}
