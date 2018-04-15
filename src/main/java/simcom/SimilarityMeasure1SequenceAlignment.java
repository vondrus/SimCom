package simcom;

import static java.lang.Math.exp;
import static java.lang.Math.pow;

class SimilarityMeasure1SequenceAlignment {
    final private double PARAMETER_KSI = 0.01;
    final private double PARAMETER_SIGMA_2 = 1.0;
    private int[] sequence1;
    private int[] sequence2;
    private double[][] matrix;

    SimilarityMeasure1SequenceAlignment(int[] sequence1, int[] sequence2) {
        this.sequence1 = sequence1;
        this.sequence2 = sequence2;
        this.matrix = new double[sequence1.length + 1][sequence2.length + 1];

        initMatrix();
        printMatrix();
    }

    private double beta(double x, double y, double sigma) {
        double a = (-1 / 2.0) * ((pow(x - y, 2) / pow(sigma, 2)));
        return 1 - exp(a);
    }

    private void printMatrix() {
        final String EMPTY_POSITION = "        -";
        final String FLOAT_FORMAT = "%9.4f";
        final String DECIMAL_FORMAT = "%9d";
        String rv = "         " + EMPTY_POSITION;

        for (int i : sequence2) {
            rv = rv.concat(String.format(DECIMAL_FORMAT, i));
        }

        rv = rv.concat(String.format("%n"));

        for (int i = 0; i < matrix.length; i++) {
            if (i == 0) {
                rv = rv.concat(EMPTY_POSITION);
            } else {
                rv = rv.concat(String.format(DECIMAL_FORMAT, sequence1[i - 1]));
            }
            for (int j = 0; j < matrix[0].length; j++) {
                rv = rv.concat(String.format(FLOAT_FORMAT, matrix[i][j]));
            }
            rv = rv.concat(String.format("%n"));
        }

        System.out.println(rv);
    }

    private void initMatrix() {
        // First column
        for (int i = 1; i < matrix.length; i++) {
            matrix[i][0] = matrix[i - 1][0] + beta((double) sequence1[i - 1], PARAMETER_KSI, PARAMETER_SIGMA_2);
        }

        // First row
        for (int j = 1; j < matrix[0].length; j++) {
            matrix[0][j] = matrix[0][j - 1] + beta(PARAMETER_KSI, (double) sequence2[j - 1], PARAMETER_SIGMA_2);
        }
    }
}
