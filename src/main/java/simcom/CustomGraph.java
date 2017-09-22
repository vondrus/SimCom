package simcom;

import java.util.*;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashFunction;

import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.SimpleDirectedGraph;


class CustomGraph extends SimpleDirectedGraph<CustomGraphVertex, CustomGraphEdge> {
    private static final long serialVersionUID = 160210295726081101L;
    private String name;
    private int verticesLevels;

    CustomGraph(Class<? extends CustomGraphEdge> edgeClass, String name) {
        super(new ClassBasedEdgeFactory<>(edgeClass));
        this.name = name;
        this.verticesLevels = 0;
    }

    String getName() {
        return name;
    }

    int getVerticesLevels() {
        return verticesLevels;
    }

    void initAttributes() {
        this.verticesLevels = 0;
        for (CustomGraphVertex vertex: this.vertexSet()) {
            vertex.setStatus(CustomGraphVertex.Status.FRESH);
        }
    }

    List<CustomGraphVertex> getVertexSuccessors(CustomGraphVertex vertex) {
        ArrayList<CustomGraphVertex> successors = new ArrayList<>();
        Set<CustomGraphEdge> outgoingEdges = this.outgoingEdgesOf(vertex);
        for (CustomGraphEdge edge : outgoingEdges) {
            successors.add(this.getEdgeTarget(edge));
        }
        return successors;
    }

    private int getMaxLevelOfVertices() {
        int maxLevel = 0;
        for (CustomGraphVertex vertex : this.vertexSet()) {
            if (vertex.getLevel() > maxLevel) {
                maxLevel = vertex.getLevel();
            }
        }
        return maxLevel;
    }

    void updateMaxLevelOfVertices() {
        this.verticesLevels = this.getMaxLevelOfVertices();
    }

    private Set<CustomHash> getVertexHash(CustomGraphVertex vertex) {
        final int indegree = this.inDegreeOf(vertex);
        final int outdegree = this.outDegreeOf(vertex);
        final int level = vertex.getLevel();
        final String label = vertex.getLabel();

        final Set<CustomHash> rv = new HashSet<>();
        HashFunction hf;

        // Murmur3A
        hf = Hashing.murmur3_32();
        rv.add(new CustomHash(hf.newHasher()
                .putInt(indegree)
                .putInt(outdegree)
                .putInt(level)
                .putUnencodedChars(label)
                .hash().asInt(),
                32, "Murmur3A"));

        // SipHash-2-4
        hf = Hashing.sipHash24();
        rv.add(new CustomHash(hf.newHasher()
                .putInt(indegree)
                .putInt(outdegree)
                .putInt(level)
                .putUnencodedChars(label)
                .hash().asLong(),
                64, "SipHash-2-4"));

        //FarmHash's Fingerprint64
        hf = Hashing.farmHashFingerprint64();
        rv.add(new CustomHash(hf.newHasher()
                .putInt(indegree)
                .putInt(outdegree)
                .putInt(level)
                .putUnencodedChars(label)
                .hash().asLong(),
                64, "FarmHash's Fingerprint64"));

        return rv;
    }

    private void updateHashVector(int[] vector, long hash, int length) {
        long mask = 1;
        for (int i = 0; i < length; i++) {
            if ((hash & mask) == 0)
                vector[i]--;
            else
                vector[i]++;
            mask = mask << 1;
        }
    }
/*
    private void putEndOfLevelMark(long level, Map<String, int[]> simHashVectors, CustomGraphVertex vertex) {
        for (CustomHash hash : getVertexHash(vertex)) {
            long eol = 0;
            for (int i = hash.getLength(); i > 0; i -= 8) {
                eol = (eol << 8) | level;
            }
            updateHashVector(simHashVectors.get(hash.getName()), eol, hash.getLength());
        }
    }
*/

    private void putEndOfLevelMark(long level, Map<String, int[]> simHashVectors, CustomGraphVertex vertex) {
        for (CustomHash hash : getVertexHash(vertex)) {
            updateHashVector(simHashVectors.get(hash.getName()), level, hash.getLength());
        }
    }

    Set<CustomHash> getSimhash() {
        final Iterator<CustomGraphVertex> iterator = this.vertexSet().iterator();

        if (iterator.hasNext()) {
            final CustomGraphVertex firstVertex = iterator.next();
            final Map<String, int[]> simHashVectors = new HashMap<>();
            final Set<CustomHash> rv = new HashSet<>();

            for (CustomHash hash : getVertexHash(firstVertex)) {
                simHashVectors.put(hash.getName(), new int[hash.getLength()]);
            }

            long level = 0;
            for (CustomGraphVertex vertex : this.vertexSet()) {
                // Konec urovne - vlozit EOL znacku
                if (level != vertex.getLevel()) {
                    level = vertex.getLevel();
                    putEndOfLevelMark(level, simHashVectors, firstVertex);
                }
                // Dalsi vrchol
                for (CustomHash hash : getVertexHash(vertex)) {
                    updateHashVector(simHashVectors.get(hash.getName()), hash.getHash(), hash.getLength());
                }
            }
            // Posledni EOL znacka
            level++;
            putEndOfLevelMark(level, simHashVectors, firstVertex);

            for (CustomHash hash : getVertexHash(firstVertex)) {
                long mask = 1;
                long simhash = 0;
                for (int i = 0; i < hash.getLength(); i++) {
                    if (simHashVectors.get(hash.getName())[i] >= 0) {
                        simhash = simhash | mask;
                    }
                    mask = mask << 1;
                }
                rv.add(new CustomHash(simhash, hash.getLength(), hash.getName()));
            }

            return rv;
        }
        else {
            return null;
        }
    }
}
