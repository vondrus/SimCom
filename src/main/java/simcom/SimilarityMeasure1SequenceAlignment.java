package simcom;

import static java.lang.Math.exp;
import static java.lang.Math.pow;

class SimilarityMeasure1SequenceAlignment {
    final private double PARAMETER_KSI = 0.1;
    final private double PARAMETER_SIGMA_1 = 1.0;
    final private double PARAMETER_SIGMA_2 = 1.0;
    private double[] sequence1;
    private double[] sequence2;
    private double[][] matrix;

    SimilarityMeasure1SequenceAlignment(double[] sequence1, double[] sequence2) {
        this.sequence1 = sequence1;
        this.sequence2 = sequence2;
        this.matrix = new double[sequence1.length + 1][sequence2.length + 1];

        fillMatrix();
        printMatrix();
    }

    private double beta(double x, double y, double sigma) {
        double a = (-1 / 2.0) * ((pow(x - y, 2) / pow(sigma, 2)));
        return 1 - exp(a);
    }

    private void printMatrix() {
        final String INIT_CELL = "    *    ";
        final String EMPTY_CELL = "         ";
        final String HORIZONTAL_SEPARATOR_CELL = "---------";
        final String VERTICAL_SEPARATOR_CELL = " | ";
        final String CROSS_SEPARATOR_CELL = " + ";
        final String FLOAT_FORMAT = "%9.4f";

        String rv = EMPTY_CELL
                  + VERTICAL_SEPARATOR_CELL
                  + INIT_CELL;
        String hs = HORIZONTAL_SEPARATOR_CELL
                  + CROSS_SEPARATOR_CELL
                  + HORIZONTAL_SEPARATOR_CELL;

        for (double a : sequence2) {
            rv = rv.concat(String.format(FLOAT_FORMAT, a));
            hs = hs.concat(HORIZONTAL_SEPARATOR_CELL);
        }

        rv = rv.concat(String.format("%n%s%n", hs));

        for (int i = 0; i < matrix.length; i++) {
            if (i == 0) {
                rv = rv.concat(INIT_CELL);
            } else {
                rv = rv.concat(String.format(FLOAT_FORMAT, sequence1[i - 1]));
            }

            rv = rv.concat(VERTICAL_SEPARATOR_CELL);

            for (int j = 0; j < matrix[0].length; j++) {
                rv = rv.concat(String.format(FLOAT_FORMAT, matrix[i][j]));
            }

            rv = rv.concat(String.format("%n"));
        }

        System.out.println(rv);
    }

    private void fillMatrix() {
        // First column
        for (int i = 1; i < matrix.length; i++) {
            matrix[i][0] = matrix[i - 1][0] + beta(sequence1[i - 1], PARAMETER_KSI, PARAMETER_SIGMA_2);
        }

        // First row
        for (int j = 1; j < matrix[0].length; j++) {
            matrix[0][j] = matrix[0][j - 1] + beta(PARAMETER_KSI, sequence2[j - 1], PARAMETER_SIGMA_2);
        }

        // Inner of the matrix
        for (int i = 1; i < matrix.length; i++) {
            for (int j = 1; j < matrix[0].length; j++) {
                double substituteCost = matrix[i - 1][j - 1] + beta(sequence1[i - 1], sequence2[j - 1], PARAMETER_SIGMA_1);
                double insertCost = matrix[i][j - 1] + beta(PARAMETER_KSI, sequence2[j - 1], PARAMETER_SIGMA_2);
                double deleteCost = matrix[i - 1][j] + beta(sequence1[i - 1], PARAMETER_KSI, PARAMETER_SIGMA_2);
                matrix[i][j] = Math.min(Math.min(insertCost, deleteCost), substituteCost);
            }
        }
    }

    double getMinimalEditDistance() {
        return matrix[matrix.length - 1][matrix[0].length - 1];
    }
}
