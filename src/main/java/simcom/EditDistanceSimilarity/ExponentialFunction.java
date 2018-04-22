package simcom.EditDistanceSimilarity;

import static java.lang.Math.exp;
import static java.lang.Math.pow;

public class ExponentialFunction implements SimilarityFunction {
    private final static double PARAMETER_KSI = 0.001;
    private final static double PARAMETER_SIGMA_1 = 1.0;
    private final static double PARAMETER_SIGMA_2 = 5.0;

    @Override
    public double getGapElementValue(double element) {
        return 1 - exp((-1 / 2.0) * (pow(PARAMETER_KSI - element, 2) / pow(PARAMETER_SIGMA_1, 2)));
    }

    @Override
    public double getElementGapValue(double element) {
        return 1 - exp((-1 / 2.0) * (pow(element - PARAMETER_KSI, 2) / pow(PARAMETER_SIGMA_1, 2)));
    }

    @Override
    public double getElementElementValue(double element1, double element2) {
        return 1 - exp((-1 / 2.0) * (pow(element1 - element2, 2) / pow(PARAMETER_SIGMA_2, 2)));
    }

}
