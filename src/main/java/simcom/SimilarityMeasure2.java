package simcom;

import java.util.Hashtable;
import java.util.ArrayList;


class SimilarityMeasure2 {

    // Inner class for storing results of evaluation
    private class EvaluationResult {
        private int hammingDistance;
        private float similarity;

        int getHammingDistance() {
            return hammingDistance;
        }

        void setHammingDistance(int hammingDistance) {
            this.hammingDistance = hammingDistance;
        }

        float getSimilarity() {
            return similarity;
        }

        void setSimilarity(float similarity) {
            this.similarity = similarity;
        }
    }

    private CustomGraph graph1;
    private CustomGraph graph2;
    private Hashtable<CustomGraphHashAlgorithm, CustomGraphSimhash> simhashTable1 = new Hashtable<>();
    private Hashtable<CustomGraphHashAlgorithm, CustomGraphSimhash> simhashTable2 = new Hashtable<>();
    private Hashtable<CustomGraphHashAlgorithm, EvaluationResult> evaluationResults = new Hashtable<>();

    SimilarityMeasure2(CustomGraph graph1, CustomGraph graph2) {
        this.graph1 = graph1;
        this.graph2 = graph2;

        for (CustomGraphHashAlgorithm hashAlgorithm : CustomGraphHashAlgorithm.values()) {
            simhashTable1.put(hashAlgorithm, new CustomGraphSimhash(hashAlgorithm.getHashFunction()));
            simhashTable2.put(hashAlgorithm, new CustomGraphSimhash(hashAlgorithm.getHashFunction()));
            evaluationResults.put(hashAlgorithm, new EvaluationResult());
        }
    }

    private void makeSimHashTable(CustomGraph graph, Hashtable<CustomGraphHashAlgorithm, CustomGraphSimhash> simhashTable) {
        int levelNumber = 0;

        // Decompose graph to the tokens and calculate their hashes
        for (ArrayList<CustomGraphVertex> level : graph.getLevels()) {

            // Vertices
            for (CustomGraphVertex vertex : level) {

                final int indegree = graph.inDegreeOf(vertex);
                final int outdegree = graph.outDegreeOf(vertex);
                final String label = vertex.getLabel();

                for (CustomGraphHashAlgorithm hashAlgorithm : CustomGraphHashAlgorithm.values()) {
                    simhashTable.get(hashAlgorithm).putVertex(indegree, outdegree, label, levelNumber);
                }

            }

            // End of levels
            for (CustomGraphHashAlgorithm hashAlgorithm : CustomGraphHashAlgorithm.values()) {
                simhashTable.get(hashAlgorithm).putLevelSeparator(levelNumber, level.size());
            }

            levelNumber++;
        }

        // Make simhashes from already prepared hashes
        for (CustomGraphHashAlgorithm hashAlgorithm : CustomGraphHashAlgorithm.values()) {
            simhashTable.get(hashAlgorithm).makeSimhash();
        }

        // Debug
        System.out.println();
    }

    void makeSimHashTables() {
        makeSimHashTable(graph1, simhashTable1);
        makeSimHashTable(graph2, simhashTable2);
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

    void evaluateSimilarity() {

        for (CustomGraphHashAlgorithm hashAlgorithm : CustomGraphHashAlgorithm.values()) {
            int hammingDistance = calculateHammingDistance(
                    simhashTable1.get(hashAlgorithm).getSimhashAsBytes(),
                    simhashTable2.get(hashAlgorithm).getSimhashAsBytes()
            );
            evaluationResults.get(hashAlgorithm).setHammingDistance(hammingDistance);
            evaluationResults.get(hashAlgorithm).setSimilarity(1 - (hammingDistance / (float) simhashTable1.get(hashAlgorithm).getSimhashLength()));
        }
    }

    String getResultString() {
        String rv = "";
        for (CustomGraphHashAlgorithm hashAlgorithm : CustomGraphHashAlgorithm.values()) {
            rv = rv.concat(String.format(
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
        return rv;
    }

}
