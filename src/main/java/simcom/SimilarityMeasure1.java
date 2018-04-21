package simcom;

class SimilarityMeasure1 {
    private enum Attribute { INDEGREE, OUTDEGREE, LABEL }
    private CustomGraph graph1;
    private CustomGraph graph2;

    SimilarityMeasure1(CustomGraph graph1, CustomGraph graph2) {
        this.graph1 = graph1;
        this.graph2 = graph2;
    }

    private double[] levelToArray(CustomGraph graph, int levelNumber, Attribute attribute) {
        CustomGraphLevel level = graph.getLevel(levelNumber);

        if (level != null) {
            double[] rv = new double[level.size()];

            for (int i = 0; i < rv.length; i++) {
                switch (attribute) {
                    case INDEGREE:
                        rv[i] = graph.inDegreeOf(level.get(i));
                        break;

                    case OUTDEGREE:
                        rv[i] = graph.outDegreeOf(level.get(i));
                        break;
                }
                System.out.println("vertex: " + level.get(i).getLabel() + ", ");
            }
            System.out.println();

            return rv;

        } else {
            return new double[0];
        }
    }

    void evaluateSimilarity() {
        final double PARAMETER_ZETA = 0.5;
        int depth1 = graph1.getDepth();
        int depth2 = graph2.getDepth();
        int depth = Math.max(depth1, depth2);
        double sumOfGammaFinals = 0;
        double productOfGammaFinals = 1;

        //
        for (int i = 0; i < depth; i++) {
            double[] sequenceIndegree1 = levelToArray(graph1, i, Attribute.INDEGREE);
            double[] sequenceIndegree2 = levelToArray(graph2, i, Attribute.INDEGREE);
            double[] sequenceOutdegree1 = levelToArray(graph1, i, Attribute.OUTDEGREE);
            double[] sequenceOutdegree2 = levelToArray(graph2, i, Attribute.OUTDEGREE);

            SimilarityMeasure1SequenceAlignment alignmentIndegree12 = new SimilarityMeasure1SequenceAlignment(sequenceIndegree1, sequenceIndegree2);
            SimilarityMeasure1SequenceAlignment alignmentIndegree21 = new SimilarityMeasure1SequenceAlignment(sequenceIndegree2, sequenceIndegree1);
            SimilarityMeasure1SequenceAlignment alignmentOutdegree12 = new SimilarityMeasure1SequenceAlignment(sequenceOutdegree1, sequenceOutdegree2);
            SimilarityMeasure1SequenceAlignment alignmentOutdegree21 = new SimilarityMeasure1SequenceAlignment(sequenceOutdegree2, sequenceOutdegree1);

            double gammaIndegree = 1 - (alignmentIndegree12.getMinimalEditDistance() + alignmentIndegree21.getMinimalEditDistance())
                    / (sequenceIndegree1.length + sequenceIndegree2.length);

            double gammaOutdegree = 1 - (alignmentOutdegree12.getMinimalEditDistance() + alignmentOutdegree21.getMinimalEditDistance())
                    / (sequenceOutdegree1.length + sequenceOutdegree2.length);

            double gammaFinal = PARAMETER_ZETA * gammaOutdegree + (1 - PARAMETER_ZETA) * gammaIndegree;

            System.out.println("gammaIndegree  = " + gammaIndegree);
            System.out.println("gammaOutdegree = " + gammaOutdegree);
            System.out.println("gammaFinal     = " + gammaFinal);

            sumOfGammaFinals += gammaFinal;
            productOfGammaFinals *= gammaFinal;
        }

        System.out.println();
        System.out.println("sumOfGammaFinals     = " + sumOfGammaFinals);
        System.out.println("productOfGammaFinals = " + productOfGammaFinals);
        System.out.println();

        double similarity = (depth * productOfGammaFinals) / sumOfGammaFinals;

        System.out.println("Similarity = " + similarity + "\n");
    }

}
