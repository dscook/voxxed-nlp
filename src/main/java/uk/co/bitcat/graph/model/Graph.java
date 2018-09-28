package uk.co.bitcat.graph.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Graph {

    public Set<Node> nodes = new HashSet<>();
    public List<Edge> edges = new ArrayList<>();;

    public void addNode(Node node) {
        if (!nodes.contains(node)) {
            nodes.add(node);
        }
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }
}
