package uk.co.bitcat.model;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.util.Pair;

public class RelationTripleWithSpan {

    public RelationTripleWithSpan(RelationTriple triple, Pair<Integer, Integer> sentenceSpan) {
        this.triple = triple;
        this.sentenceSpan = sentenceSpan;
    }

    public RelationTriple triple;
    public Pair<Integer, Integer> sentenceSpan;
}
