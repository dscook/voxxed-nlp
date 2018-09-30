package uk.co.bitcat.graph;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.co.bitcat.FilmPlotText;
import uk.co.bitcat.graph.model.Edge;
import uk.co.bitcat.graph.model.Graph;
import uk.co.bitcat.graph.model.Node;
import uk.co.bitcat.helpers.Normalisers;
import uk.co.bitcat.model.RelationTripleWithSpan;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class GraphBuilder {

    private final List<RelationTripleWithSpan> triples;
    private final Map<String, String> entitiesToTypes;

    public GraphBuilder(List<RelationTripleWithSpan> triples, Map<String, String> entitiesToTypes) {
        this.triples = triples;
        this.entitiesToTypes = entitiesToTypes;
    }

    public void createGraph() throws Exception {

        Graph graph = new Graph();

        for (RelationTripleWithSpan tripleWithSpan : triples) {
            Node sourceNode = createNode(tripleWithSpan.triple.subjectLemmaGloss(), graph);
            Node targetNode = createNode(tripleWithSpan.triple.objectLemmaGloss(), graph);

            String sentence = FilmPlotText.JURASSIC_PARK_PLOT.substring(
                    tripleWithSpan.sentenceSpan.first, tripleWithSpan.sentenceSpan.second);

            sentence = highlightPhrase(tripleWithSpan.triple.subjectGloss(), sentence);
            sentence = highlightPhrase(" " + tripleWithSpan.triple.relationGloss() + " ", sentence);
            sentence = highlightPhrase(tripleWithSpan.triple.objectGloss(), sentence);

            Edge edge = new Edge(
                    sourceNode.id,
                    targetNode.id,
                    Normalisers.whitespacesToUnderscores(tripleWithSpan.triple.relationLemmaGloss()),
                    sentence);
            graph.addEdge(edge);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("model.json"))) {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(graph);
            writer.write(json);
        }
    }

    private Node createNode(String nodeName, Graph graph) throws Exception {
        String normalisedNodeName = Normalisers.normaliseNames(nodeName);
        Node node = new Node(normalisedNodeName, entitiesToTypes.get(nodeName));
        graph.addNode(node);
        return node;
    }

    private String highlightPhrase(String phrase, String sentence) {
        return sentence.replaceAll(phrase, "<strong>" + phrase + "</strong>");
    }
}
