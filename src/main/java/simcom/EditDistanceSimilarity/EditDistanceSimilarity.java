package simcom.EditDistanceSimilarity;

import static java.lang.Math.max;

import simcom.CustomGraph;
import simcom.CustomGraphLevel;
import simcom.SimilarityMeasure.SimilarityMeasure;

public class EditDistanceSimilarity extends SimilarityMeasure {
    private enum Attribute { INDEGREE, OUTDEGREE, LABEL }

    public EditDistanceSimilarity(CustomGraph graph1, CustomGraph graph2, String technique) {
        super(graph1, graph2, technique);
    }

    private double[] levelToArray(CustomGraph graph, int levelNumber, Attribute attribute) {
        CustomGraphLevel level = graph.getLevel(levelNumber);

        if (level != null) {
            double[] rv = new double[level.size()];
            StringBuilder sb = new StringBuilder();

            debugString.append(String.format(
                    "  Attribute: %s%n" +
                    "    Vertices: ",
                    attribute
            ));

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
                debugString.append(String.format("%s ", level.get(i).getLabel()));
            }

            // Labels of vertices
            if (attribute == Attribute.LABEL) {
                String labelsString = sb.toString();
                double[] labelsArray = new double[labelsString.length()];

                for (int i = 0; i < labelsArray.length; i++) {
                    labelsArray[i] = (int) labelsString.charAt(i);
                }

                debugString.append(String.format("%n    Spliced labels: %s", labelsString));

                rv = labelsArray;
            }

            debugString.append(String.format("%n"));

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

        // Iterate over graph levels
        for (int i = 0; i < depth; i++) {

            debugString.append(String.format("%nLevel: %d%n", i));

            double[] sequenceIndegree1 = levelToArray(graph1, i, Attribute.INDEGREE);
            double[] sequenceIndegree2 = levelToArray(graph2, i, Attribute.INDEGREE);
            double[] sequenceOutdegree1 = levelToArray(graph1, i, Attribute.OUTDEGREE);
            double[] sequenceOutdegree2 = levelToArray(graph2, i, Attribute.OUTDEGREE);
            double[] sequenceLabel1 = levelToArray(graph1, i, Attribute.LABEL);
            double[] sequenceLabel2 = levelToArray(graph2, i, Attribute.LABEL);

            SequenceAlignment alignmentIndegree12 =
                    new SequenceAlignment(sequenceIndegree1, sequenceIndegree2, new ExponentialFunction());
            debugString.append(alignmentIndegree12.getDebugString());

            SequenceAlignment alignmentIndegree21 =
                    new SequenceAlignment(sequenceIndegree2, sequenceIndegree1, new ExponentialFunction());
            debugString.append(alignmentIndegree21.getDebugString());

            SequenceAlignment alignmentOutdegree12 =
                    new SequenceAlignment(sequenceOutdegree1, sequenceOutdegree2, new ExponentialFunction());
            debugString.append(alignmentOutdegree12.getDebugString());

            SequenceAlignment alignmentOutdegree21 =
                    new SequenceAlignment(sequenceOutdegree2, sequenceOutdegree1, new ExponentialFunction());
            debugString.append(alignmentOutdegree21.getDebugString());

            SequenceAlignment alignmentLabel =
                    new SequenceAlignment(sequenceLabel1, sequenceLabel2, new SimpleFunction());
            debugString.append(alignmentLabel.getDebugString());


            double gammaIndegree = 1 - (alignmentIndegree12.getMinimalEditDistance() + alignmentIndegree21.getMinimalEditDistance())
                    / (sequenceIndegree1.length + sequenceIndegree2.length);

            double gammaOutdegree = 1 - (alignmentOutdegree12.getMinimalEditDistance() + alignmentOutdegree21.getMinimalEditDistance())
                    / (sequenceOutdegree1.length + sequenceOutdegree2.length);

            double gammaLabel = 1 - alignmentLabel.getMinimalEditDistance()
                    / max(sequenceLabel1.length, sequenceLabel2.length);

            double gammaFinal = PARAMETER_ZETA * gammaIndegree + PARAMETER_ZETA * gammaOutdegree + PARAMETER_ZETA * gammaLabel;

            sumOfGammaFinals += gammaFinal;
            productOfGammaFinals *= gammaFinal;

            debugString.append(String.format(
                    "gammaIndegree  = %f%n" +
                    "gammaOutdegree = %f%n" +
                    "gammaLabel     = %f%n" +
                    "gammaFinal     = %f%n",
                    gammaIndegree,
                    gammaOutdegree,
                    gammaLabel,
                    gammaFinal
            ));
        }

        double similarity = (depth * productOfGammaFinals) / sumOfGammaFinals;

        debugString.append(String.format(
                "%n" +
                "sumOfGammaFinals     = %f%n" +
                "productOfGammaFinals = %f%n%n" +
                "Similarity = %f%n",
                sumOfGammaFinals,
                productOfGammaFinals,
                similarity
        ));

        resultString.append(String.format("%n    Similarity = %.4f%n", similarity));
    }

}
