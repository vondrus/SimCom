package SimCom.EditDistanceSimilarity;

public interface SimilarityFunction {

    double getGapElementValue(double element);

    double getElementGapValue(double element);

    double getElementElementValue(double element1, double element2);

}
