package uk.co.bitcat.graph.model;

import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.UUID;

public class Node {

    public String id;
    public String label;
    public String entityType;

    public Node(String label, String entityType) throws UnsupportedEncodingException {
        this.id = UUID.nameUUIDFromBytes(label.getBytes("UTF-8")).toString();
        this.label = label;
        this.entityType = entityType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(id, node.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
