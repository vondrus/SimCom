package simcom;

class SimilarityMeasure1 {
    private CustomGraph graph1;
    private CustomGraph graph2;

    SimilarityMeasure1(CustomGraph graph1, CustomGraph graph2) {
        this.graph1 = graph1;
        this.graph2 = graph2;
    }

    void evaluateSimilarity() {
        int[] sq1 = {1, 2, 3, 4, 5};
        int[] sq2 = {1, 2, 3, 4};

        SimilarityMeasure1SequenceAlignment a = new SimilarityMeasure1SequenceAlignment(sq1, sq2);

    }
}
