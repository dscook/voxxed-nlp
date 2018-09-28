package uk.co.bitcat.rdf;

import edu.stanford.nlp.ie.util.RelationTriple;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;
import uk.co.bitcat.helpers.Normalisers;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RdfModelBuilder {

    private static final String NAMESPACE = "http://www.bitcat.co.uk/";
    private final List<RelationTriple> triples;
    private final Map<String, String> entitiesToTypes;
    private final Model model = ModelFactory.createDefaultModel();
    private final Map<String, Resource> uriToResource = new HashMap<>();


    public RdfModelBuilder(List<RelationTriple> triples, Map<String, String> entitiesToTypes) {
        this.triples = triples;
        this.entitiesToTypes = entitiesToTypes;
    }

    public void createRdfModel() throws Exception {
        // Add each triple to the model
        for (RelationTriple triple : triples) {
            Resource subject = retrieveExistingResourceOrCreateNew(triple.subjectGloss(), true);
            Resource object = retrieveExistingResourceOrCreateNew(triple.objectGloss(), true);
            Property predicate = model.createProperty(
                    NAMESPACE + Normalisers.whitespacesToUnderscores(triple.relationLemmaGloss()));
            model.add(subject, predicate, object);
        }

        // Write model to file
        File file = new File("model.ttl");
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            if (!file.exists()) {
                file.createNewFile();
            }
            model.write(outputStream, "TURTLE");
        }
    }

    private Resource retrieveExistingResourceOrCreateNew(String resource, boolean addToModel) {
        String sanitisedResource = Normalisers.normaliseNames(resource);

        Resource rdfResource = uriToResource.get(sanitisedResource);
        if (rdfResource == null) {
            rdfResource = ResourceFactory.createResource(NAMESPACE + sanitisedResource);
            uriToResource.put(sanitisedResource, rdfResource);

            if (addToModel) {
                // First time we have seen this entity, add its type to the RDF model
                String resourceType = entitiesToTypes.get(resource);
                Resource rdfType = retrieveExistingResourceOrCreateNew("ontology/" + resourceType, false);
                model.add(rdfResource, RDF.type, rdfType);
            }
        }
        return rdfResource;
    }
}
