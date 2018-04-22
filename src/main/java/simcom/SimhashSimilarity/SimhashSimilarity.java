package simcom.SimhashSimilarity;

import simcom.CustomGraph;
import simcom.CustomGraphVertex;
import simcom.SimilarityMeasure.SimilarityMeasure;

import java.util.Hashtable;
import java.util.ArrayList;


public class SimhashSimilarity extends SimilarityMeasure {
    private Hashtable<HashAlgorithm, Simhash> simhashTable1 = new Hashtable<>();
    private Hashtable<HashAlgorithm, Simhash> simhashTable2 = new Hashtable<>();
    private Hashtable<HashAlgorithm, EvaluationResult> evaluationResults = new Hashtable<>();

    public SimhashSimilarity(CustomGraph graph1, CustomGraph graph2) {
        super(graph1, graph2);

        for (HashAlgorithm hashAlgorithm : HashAlgorithm.values()) {
            simhashTable1.put(hashAlgorithm, new Simhash(hashAlgorithm.getHashFunction()));
            simhashTable2.put(hashAlgorithm, new Simhash(hashAlgorithm.getHashFunction()));
            evaluationResults.put(hashAlgorithm, new EvaluationResult());
        }
    }

    // Inner class for storing results of evaluation
    private class EvaluationResult {
        private int hammingDistance;
        private double similarity;

        int getHammingDistance() {
            return hammingDistance;
        }

        void setHammingDistance(int hammingDistance) {
            this.hammingDistance = hammingDistance;
        }

        double getSimilarity() {
            return similarity;
        }

        void setSimilarity(double similarity) {
            this.similarity = similarity;
        }
    }

    private void makeSimHashTable(CustomGraph graph, Hashtable<HashAlgorithm, Simhash> simhashTable) {
        int levelNumber = 0;

        // Decompose graph to the tokens and calculate their hashes
        for (ArrayList<CustomGraphVertex> level : graph) {

            // Vertices
            for (CustomGraphVertex vertex : level) {

                final int indegree = graph.inDegreeOf(vertex);
                final int outdegree = graph.outDegreeOf(vertex);
                final String label = vertex.getLabel();

                for (HashAlgorithm hashAlgorithm : HashAlgorithm.values()) {
                    simhashTable.get(hashAlgorithm).putVertex(indegree, outdegree, label, levelNumber);
                }

            }

            // End of levels
            for (HashAlgorithm hashAlgorithm : HashAlgorithm.values()) {
                simhashTable.get(hashAlgorithm).putLevelSeparator(levelNumber, level.size());
            }

            levelNumber++;
        }

        // Make simhashes from already prepared hashes
        for (HashAlgorithm hashAlgorithm : HashAlgorithm.values()) {
            simhashTable.get(hashAlgorithm).makeSimhash();
        }

        // Debug
        System.out.println();

    }

    private int calculateHammingDistance(byte[] simhash1, byte[] simhash2) {
        int rv = 0;

        for (int i = 0; i < simhash1.length; i++) {
            byte x = (byte) (simhash1[i] ^ simhash2[i]);
            byte mask = 1;
            for (int j = 0; j < 8; j++) {
                if ((x & mask) != 0) {
                    rv++;
                }
                mask <<= 1;
            }
        }

        return rv;
    }

    @Override
    public void evaluateSimilarity() {
        makeSimHashTable(graph1, simhashTable1);
        makeSimHashTable(graph2, simhashTable2);

        // Evaluate similarity
        for (HashAlgorithm hashAlgorithm : HashAlgorithm.values()) {
            int hammingDistance = calculateHammingDistance(
                    simhashTable1.get(hashAlgorithm).getSimhashAsBytes(),
                    simhashTable2.get(hashAlgorithm).getSimhashAsBytes()
            );
            evaluationResults.get(hashAlgorithm).setHammingDistance(hammingDistance);
            evaluationResults.get(hashAlgorithm).setSimilarity(1 - (hammingDistance / (double) simhashTable1.get(hashAlgorithm).getSimhashLength()));
        }

        // Assemble result string
        for (HashAlgorithm hashAlgorithm : HashAlgorithm.values()) {
            resultString = resultString.concat(String.format(
                    "%nHash algorithm: %s (%s-bit)%n" +
                            "     Left graph (%s) simhash = 0x%s%n" +
                            "     Right graph (%s) simhash = 0x%s%n" +
                            "     Hamming distance = %d%n" +
                            "     Similarity = %.4f%n",
                    hashAlgorithm.getName(),
                    simhashTable1.get(hashAlgorithm).getSimhashLength(),
                    graph1.getName(),
                    simhashTable1.get(hashAlgorithm).getSimhashAsHexString(),
                    graph2.getName(),
                    simhashTable2.get(hashAlgorithm).getSimhashAsHexString(),
                    evaluationResults.get(hashAlgorithm).getHammingDistance(),
                    evaluationResults.get(hashAlgorithm).getSimilarity()
            ));
        }
    }
}
