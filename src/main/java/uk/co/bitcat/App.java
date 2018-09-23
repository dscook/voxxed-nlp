package uk.co.bitcat;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.pipeline.*;

import java.util.*;

public class App {

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,depparse,coref,natlog,openie");
        props.setProperty("openie.resolve_coref", "true");

        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation doc = new Annotation(FilmPlotText.JURASSIC_PARK_PLOT);
        pipeline.annotate(doc);

        Map<String, String> entitiesToTypes = AnnotationExtraction.extractEntitiesAndTheirTypes(doc);
        Loggers.logEntities(entitiesToTypes);

        List<RelationTriple> triples = AnnotationExtraction.extractRelationships(doc);
        Loggers.logTriples(triples);

        // Remove triples where either end doesn't conform to a named entity type
        List<RelationTriple> filteredTriples =
                AnnotationExtraction.filterRelationshipsBasedOnEntities(triples, entitiesToTypes);
        Loggers.logTriplesWithEntityTypes(filteredTriples, entitiesToTypes);

        // Create and write an RDF model to disk
        RdfModelBuilder model = new RdfModelBuilder(filteredTriples, entitiesToTypes);
        model.createRdfModel();
    }
}
