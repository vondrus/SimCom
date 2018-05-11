package simcom.SimilarityMeasure;

import java.util.ArrayList;

import simcom.CustomGraph;

public abstract class SimilarityMeasure {
    protected CustomGraph graph1;
    protected CustomGraph graph2;
    protected ArrayList<Double> resultArrayList;
    protected StringBuilder resultString;
    protected StringBuilder debugString;

    public SimilarityMeasure(CustomGraph graph1, CustomGraph graph2, String technique) {
        this.graph1 = graph1;
        this.graph2 = graph2;
        this.resultArrayList = new ArrayList<>();
        String s = String.format("%n### %s : %s (%s) ###%n", graph1.getName(), graph2.getName(), technique);
        this.resultString = new StringBuilder(s);
        this.debugString = new StringBuilder(s);
    }

    public abstract void evaluateSimilarity();

    public ArrayList<Double> getResultArrayList() {
        return resultArrayList;
    }

    public String getResultString() {
        return resultString.toString();
    }

    public String getDebugString() {
        return debugString.toString();
    }

}
