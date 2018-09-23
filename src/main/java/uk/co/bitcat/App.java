package uk.co.bitcat;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.util.CoreMap;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.RDF;

import java.util.*;

public class App {

    public static void main( String[] args ) {
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

        Model model = ModelFactory.createDefaultModel();

        Resource alice = ResourceFactory.createResource("http://example.org/alice");
        Resource bob = ResourceFactory.createResource("http://example.org/bob");
        model.add(alice, RDF.type, FOAF.Person);
        model.add(alice, FOAF.name, "Alice");
        model.add(alice, FOAF.mbox, ResourceFactory.createResource("mailto:alice@example.org"));
        model.add(alice, FOAF.knows, bob);

        model.write(System.out, "TURTLE");
    }
}
