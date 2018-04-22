package simcom.SimilarityMeasure;

import simcom.CustomGraph;

public abstract class SimilarityMeasure {
    protected CustomGraph graph1;
    protected CustomGraph graph2;
    protected String resultString;

    public SimilarityMeasure(CustomGraph graph1, CustomGraph graph2) {
        this.graph1 = graph1;
        this.graph2 = graph2;
        this.resultString = "";
    }

    public abstract void evaluateSimilarity();

    public String getResultString() {
        return resultString;
    }

}
