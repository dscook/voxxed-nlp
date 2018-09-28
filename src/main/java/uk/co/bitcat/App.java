package uk.co.bitcat;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.pipeline.*;
import uk.co.bitcat.graph.GraphBuilder;
import uk.co.bitcat.helpers.Loggers;
import uk.co.bitcat.model.RelationTripleWithSpan;
import uk.co.bitcat.rdf.RdfModelBuilder;

import java.util.*;
import java.util.stream.Collectors;

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

        List<RelationTripleWithSpan> triples = AnnotationExtraction.extractRelationships(doc);
        Loggers.logTriples(getRawTriples(triples));

        // Remove triples where either end doesn't conform to a named entity type
        List<RelationTripleWithSpan> filteredTriples =
                AnnotationExtraction.filterRelationshipsBasedOnEntities(triples, entitiesToTypes);
        Loggers.logTriplesWithEntityTypes(getRawTriples(filteredTriples), entitiesToTypes);

        // Create and write an RDF model to disk
        RdfModelBuilder rdfModelBuilder = new RdfModelBuilder(getRawTriples(filteredTriples), entitiesToTypes);
        rdfModelBuilder.createRdfModel();

        // Create and write a JSON graph file
        GraphBuilder graphBuilder = new GraphBuilder(filteredTriples, entitiesToTypes);
        graphBuilder.createGraph();
    }

    private static List<RelationTriple> getRawTriples(List<RelationTripleWithSpan> triplesWithSpans) {
        return triplesWithSpans.stream().map(t -> t.triple).collect(Collectors.toList());
    }
}
