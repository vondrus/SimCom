package simcom.EditDistanceSimilarity;

public class SimpleFunction implements SimilarityFunction {

    @Override
    public double getGapElementValue(double element) {
        return 1.0;
    }

    @Override
    public double getElementGapValue(double element) {
        return 1.0;
    }

    @Override
    public double getElementElementValue(double element1, double element2) {
        if (element1 == element2) {
            return 0;
        } else {
            return 1.0;
        }
    }

}
