package simcom;

import java.util.Set;
import java.util.Hashtable;
import java.util.ArrayList;


class SimilarityMeasure2 {
    private CustomGraph graph1;
    private CustomGraph graph2;
    private Hashtable<CustomGraphHashAlgorithm, CustomGraphSimhash> simhashTable1 = new Hashtable<>();
    private Hashtable<CustomGraphHashAlgorithm, CustomGraphSimhash> simhashTable2 = new Hashtable<>();
    private Set<CustomGraphHashAlgorithm> hashAlgorithms;

    SimilarityMeasure2(CustomGraph graph1, CustomGraph graph2) {
        this.graph1 = graph1;
        this.graph2 = graph2;

        for (CustomGraphHashAlgorithm hashAlgorithm : CustomGraphHashAlgorithm.values()) {
            simhashTable1.put(hashAlgorithm, new CustomGraphSimhash(hashAlgorithm.getHashFunction()));
            simhashTable2.put(hashAlgorithm, new CustomGraphSimhash(hashAlgorithm.getHashFunction()));
        }

        this.hashAlgorithms = simhashTable1.keySet();
    }

    private void prepareSimHashTable(CustomGraph graph, Hashtable<CustomGraphHashAlgorithm, CustomGraphSimhash> simhashTable) {
        int levelNumber = 0;

        for (ArrayList<CustomGraphVertex> level : graph.getLevels()) {

            for (CustomGraphVertex vertex : level) {

                final int indegree = graph.inDegreeOf(vertex);
                final int outdegree = graph.outDegreeOf(vertex);
                final String label = vertex.getLabel();

                for (CustomGraphHashAlgorithm hashAlgorithm : hashAlgorithms) {
                    simhashTable.get(hashAlgorithm).putVertex(indegree, outdegree, label, levelNumber);
                }

            }

            for (CustomGraphHashAlgorithm hashAlgorithm : hashAlgorithms) {
                simhashTable.get(hashAlgorithm).putLevelSeparator(levelNumber, level.size());
            }

            levelNumber++;
        }

        // Make simhashes
        for (CustomGraphHashAlgorithm hashAlgorithm : hashAlgorithms) {
            simhashTable.get(hashAlgorithm).makeSimhash();
        }

    }

    void prepareSimHashTables() {
        prepareSimHashTable(graph1, simhashTable1);

        // Debug
        System.out.println();

        prepareSimHashTable(graph2, simhashTable2);
    }

    String getResultString() {
        String rv = String.format("%nLeft graph (%s) simhash%n", graph1.getName());
        for (CustomGraphHashAlgorithm hashAlgorithm : hashAlgorithms) {
            rv = rv.concat(String.format("    %s (%s-bit): 0x%s%n",
                    hashAlgorithm.getName(),
                    simhashTable1.get(hashAlgorithm).getSimhashLength(),
                    simhashTable1.get(hashAlgorithm).getSimhashAsHexString()));
        }

        rv = rv.concat(String.format("%nRight graph (%s) simhash%n", graph2.getName()));
        for (CustomGraphHashAlgorithm hashAlgorithm : hashAlgorithms) {
            rv = rv.concat(String.format("    %s (%s-bit): 0x%s%n",
                    hashAlgorithm.getName(),
                    simhashTable2.get(hashAlgorithm).getSimhashLength(),
                    simhashTable2.get(hashAlgorithm).getSimhashAsHexString()));
        }

        return rv;
    }

}
