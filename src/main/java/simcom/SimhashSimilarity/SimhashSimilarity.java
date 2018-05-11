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

    public SimhashSimilarity(CustomGraph graph1, CustomGraph graph2, String technique) {
        super(graph1, graph2, technique);

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

        // Debug mode
        debugString.append(String.format("%n  Graph name: %s%n", graph.getName()));

        // Decompose graph to the tokens and calculate their hashes
        for (ArrayList<CustomGraphVertex> level : graph) {

            // Vertices
            for (CustomGraphVertex vertex : level) {

                final int indegree = graph.inDegreeOf(vertex);
                final int outdegree = graph.outDegreeOf(vertex);
                final String label = vertex.getLabel();

                for (HashAlgorithm hashAlgorithm : HashAlgorithm.values()) {
                    // Debug mode
                    debugString.append(String.format(
                            "%n    Algorithm: %s (%s-bit)",
                            hashAlgorithm.getName(), simhashTable.get(hashAlgorithm).getSimhashLength()
                    ));

                    simhashTable.get(hashAlgorithm).putVertex(indegree, outdegree, label, levelNumber);

                    // Debug mode
                    debugString.append(simhashTable.get(hashAlgorithm).getDebugString());
                }

            }

            // End of levels
            for (HashAlgorithm hashAlgorithm : HashAlgorithm.values()) {
                // Debug mode
                debugString.append(String.format(
                        "%n    Algorithm: %s (%s-bit)",
                        hashAlgorithm.getName(), simhashTable.get(hashAlgorithm).getSimhashLength()
                ));

                simhashTable.get(hashAlgorithm).putLevelSeparator(levelNumber, level.size());

                // Debug mode
                debugString.append(simhashTable.get(hashAlgorithm).getDebugString());
            }

            levelNumber++;
        }

        // Make simhashes from already prepared hashes
        for (HashAlgorithm hashAlgorithm : HashAlgorithm.values()) {
            // Debug mode
            debugString.append(String.format(
                    "%n  Algorithm: %s (%s-bit)",
                    hashAlgorithm.getName(), simhashTable.get(hashAlgorithm).getSimhashLength()
            ));

            simhashTable.get(hashAlgorithm).makeSimhash();

            // Debug mode
            debugString.append(simhashTable.get(hashAlgorithm).getDebugString());
        }

        // Debug mode
        debugString.append(String.format("%n"));

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

            double similarity = 1 - (hammingDistance / (double) simhashTable1.get(hashAlgorithm).getSimhashLength());

            resultArrayList.add(similarity);

            evaluationResults.get(hashAlgorithm).setHammingDistance(hammingDistance);
            evaluationResults.get(hashAlgorithm).setSimilarity(similarity);
        }

        // Fill up the result string
        for (HashAlgorithm hashAlgorithm : HashAlgorithm.values()) {
            String result = String.format(
                    "Hash algorithm: %s (%s-bit)%n" +
                    "        Graph %s simhash = 0x%s%n" +
                    "        Graph %s simhash = 0x%s%n" +
                    "        Hamming distance = %d%n" +
                    "        Similarity = %.4f%n",
                    hashAlgorithm.getName(), simhashTable1.get(hashAlgorithm).getSimhashLength(),
                    graph1.getName(), simhashTable1.get(hashAlgorithm).getSimhashAsHexString(),
                    graph2.getName(), simhashTable2.get(hashAlgorithm).getSimhashAsHexString(),
                    evaluationResults.get(hashAlgorithm).getHammingDistance(),
                    evaluationResults.get(hashAlgorithm).getSimilarity()
            );
            resultString.append(String.format("%n    %s", result));

            // Debug mode
            debugString.append(String.format("%n  %s", result));
        }
    }
}
