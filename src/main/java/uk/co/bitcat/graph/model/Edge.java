package uk.co.bitcat.graph.model;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class Edge {

    public String id;
    public String source;
    public String target;
    public String label;
    public String sentence;

    public Edge(String source, String target, String label, String sentence) throws UnsupportedEncodingException {
        String idString = source + target + label;
        this.id = UUID.nameUUIDFromBytes(idString.getBytes("UTF-8")).toString();;
        this.source = source;
        this.target = target;
        this.label = label;
        this.sentence = sentence;
    }
}
