package SimCom.EditDistanceSimilarity;

/**
 * This class carries out the calculation of Minimal Edit Distance
 * of two sequences. These sequences are represented by double arrays.
 *
 * @author Petr Vondrus
 */
class SequenceAlignment {
    private double[] sequence1;
    private double[] sequence2;
    private double[][] matrix;
    private SimilarityFunction similarityFunction;

    SequenceAlignment(double[] sequence1, double[] sequence2, SimilarityFunction similarityFunction) {
        this.sequence1 = sequence1;
        this.sequence2 = sequence2;
        this.matrix = new double[sequence1.length + 1][sequence2.length + 1];
        this.similarityFunction = similarityFunction;

        fillMatrix();
    }

    /**
     * Creates Edit Distance Table by the dynamic programming
     * with use of memoization technique.
     */
    private void fillMatrix() {
        // First column
        for (int i = 1; i < matrix.length; i++) {
            matrix[i][0] = matrix[i - 1][0] + similarityFunction.getElementGapValue(sequence1[i - 1]);
        }

        // First row
        for (int j = 1; j < matrix[0].length; j++) {
            matrix[0][j] = matrix[0][j - 1] + similarityFunction.getGapElementValue(sequence2[j - 1]);
        }

        // Inner of the matrix
        for (int i = 1; i < matrix.length; i++) {
            for (int j = 1; j < matrix[0].length; j++) {
                double substituteCost = matrix[i - 1][j - 1] + similarityFunction.getElementElementValue(sequence1[i - 1], sequence2[j - 1]);
                double insertCost = matrix[i][j - 1] + similarityFunction.getGapElementValue(sequence2[j - 1]);
                double deleteCost = matrix[i - 1][j] + similarityFunction.getElementGapValue(sequence1[i - 1]);
                matrix[i][j] = Math.min(Math.min(insertCost, deleteCost), substituteCost);
            }
        }
    }

    /**
     * Returns minimal edit distance of two sequences.
     * @return double Minimal edit distance
     */
    double getMinimalEditDistance() {
        return matrix[matrix.length - 1][matrix[0].length - 1];
    }

    /**
     * Returns the Edit Distance Table
     * @return String Edit Distance Table in human readable format (for debug mode)
     */
    String getDebugString() {
        final String INIT_CELL = "    *    ";
        final String EMPTY_CELL = "  Matrix ";
        final String HORIZONTAL_SEPARATOR_CELL = "---------";
        final String VERTICAL_SEPARATOR_CELL = " | ";
        final String CROSS_SEPARATOR_CELL = " + ";
        final String FLOAT_FORMAT = "%9.4f";

        StringBuilder rv = new StringBuilder(
                String.format("%n%s%s%s",
                        EMPTY_CELL,
                        VERTICAL_SEPARATOR_CELL,
                        INIT_CELL
                )
        );

        StringBuilder hs = new StringBuilder(
                String.format("%s%s%s",
                        HORIZONTAL_SEPARATOR_CELL,
                        CROSS_SEPARATOR_CELL,
                        HORIZONTAL_SEPARATOR_CELL
                )
        );

        for (double a : sequence2) {
            rv.append(String.format(FLOAT_FORMAT, a));
            hs.append(HORIZONTAL_SEPARATOR_CELL);
        }

        rv.append(String.format("%n%s%n", hs));

        for (int i = 0; i < matrix.length; i++) {
            if (i == 0) {
                rv.append(INIT_CELL);
            } else {
                rv.append(String.format(FLOAT_FORMAT, sequence1[i - 1]));
            }

            rv.append(VERTICAL_SEPARATOR_CELL);

            for (int j = 0; j < matrix[0].length; j++) {
                rv.append(String.format(FLOAT_FORMAT, matrix[i][j]));
            }

            rv.append(String.format("%n"));
        }

        rv.append(String.format("%n"));

        return rv.toString();
    }

}
