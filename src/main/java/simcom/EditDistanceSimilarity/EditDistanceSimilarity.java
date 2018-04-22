package simcom.EditDistanceSimilarity;

import static java.lang.Math.max;

import simcom.CustomGraph;
import simcom.CustomGraphLevel;
import simcom.SimilarityMeasure.SimilarityMeasure;

public class EditDistanceSimilarity extends SimilarityMeasure {
    private enum Attribute { INDEGREE, OUTDEGREE, LABEL }

    public EditDistanceSimilarity(CustomGraph graph1, CustomGraph graph2) {
        super(graph1, graph2);
    }

    private double[] levelToArray(CustomGraph graph, int levelNumber, Attribute attribute) {
        CustomGraphLevel level = graph.getLevel(levelNumber);

        if (level != null) {
            double[] rv = new double[level.size()];
            StringBuilder sb = new StringBuilder();

            System.out.println("  Attribute: " + attribute);
            System.out.print("    Vertices: ");

            for (int i = 0; i < rv.length; i++) {
                switch (attribute) {
                    case INDEGREE:
                        rv[i] = graph.inDegreeOf(level.get(i));
                        break;

                    case OUTDEGREE:
                        rv[i] = graph.outDegreeOf(level.get(i));
                        break;

                    case LABEL:
                        sb.append(level.get(i).getLabel());
                        break;
                }

                System.out.print(level.get(i).getLabel() + " ");

            }

            // Lejbly
            if (attribute == Attribute.LABEL) {
                String labelsString = sb.toString();
                double[] labelsArray = new double[labelsString.length()];

                for (int i = 0; i < labelsArray.length; i++) {
                    labelsArray[i] = (int) labelsString.charAt(i);
                }

                System.out.println();
                System.out.print("    Spliced labels: " + labelsString);

                rv = labelsArray;
            }

            System.out.println();

            return rv;

        } else {
            return new double[0];
        }
    }

    @Override
    public void evaluateSimilarity() {
        final double PARAMETER_ZETA = 1.0 / 3.0;
        int depth1 = graph1.getDepth();
        int depth2 = graph2.getDepth();
        int depth = Math.max(depth1, depth2);
        double sumOfGammaFinals = 0;
        double productOfGammaFinals = 1;

        //
        for (int i = 0; i < depth; i++) {

            System.out.println("Level: " + i);

            double[] sequenceIndegree1 = levelToArray(graph1, i, Attribute.INDEGREE);
            double[] sequenceIndegree2 = levelToArray(graph2, i, Attribute.INDEGREE);
            double[] sequenceOutdegree1 = levelToArray(graph1, i, Attribute.OUTDEGREE);
            double[] sequenceOutdegree2 = levelToArray(graph2, i, Attribute.OUTDEGREE);
            double[] sequenceLabel1 = levelToArray(graph1, i, Attribute.LABEL);
            double[] sequenceLabel2 = levelToArray(graph2, i, Attribute.LABEL);

            SequenceAlignment alignmentIndegree12 =
                    new SequenceAlignment(sequenceIndegree1, sequenceIndegree2, new ExponentialFunction());

            SequenceAlignment alignmentIndegree21 =
                    new SequenceAlignment(sequenceIndegree2, sequenceIndegree1, new ExponentialFunction());

            SequenceAlignment alignmentOutdegree12 =
                    new SequenceAlignment(sequenceOutdegree1, sequenceOutdegree2, new ExponentialFunction());

            SequenceAlignment alignmentOutdegree21 =
                    new SequenceAlignment(sequenceOutdegree2, sequenceOutdegree1, new ExponentialFunction());

            SequenceAlignment alignmentLabel =
                    new SequenceAlignment(sequenceLabel1, sequenceLabel2, new SimpleFunction());


            double gammaIndegree = 1 - (alignmentIndegree12.getMinimalEditDistance() + alignmentIndegree21.getMinimalEditDistance())
                    / (sequenceIndegree1.length + sequenceIndegree2.length);

            double gammaOutdegree = 1 - (alignmentOutdegree12.getMinimalEditDistance() + alignmentOutdegree21.getMinimalEditDistance())
                    / (sequenceOutdegree1.length + sequenceOutdegree2.length);

            double gammaLabel = 1 - alignmentLabel.getMinimalEditDistance()
                    / max(sequenceLabel1.length, sequenceLabel2.length);

            double gammaFinal = PARAMETER_ZETA * gammaIndegree + PARAMETER_ZETA * gammaOutdegree + PARAMETER_ZETA * gammaLabel;

            sumOfGammaFinals += gammaFinal;
            productOfGammaFinals *= gammaFinal;


            System.out.println("gammaIndegree  = " + gammaIndegree);
            System.out.println("gammaOutdegree = " + gammaOutdegree);
            System.out.println("gammaLabel     = " + gammaLabel);
            System.out.println("gammaFinal     = " + gammaFinal);
            System.out.println();
        }

        System.out.println();
        System.out.println("sumOfGammaFinals     = " + sumOfGammaFinals);
        System.out.println("productOfGammaFinals = " + productOfGammaFinals);
        System.out.println();

        double similarity = (depth * productOfGammaFinals) / sumOfGammaFinals;

        System.out.println("Similarity = " + similarity + "\n");
    }

}
