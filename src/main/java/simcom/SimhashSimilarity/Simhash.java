package simcom.SimhashSimilarity;

import java.util.Arrays;

import com.google.common.hash.HashFunction;

import simcom.AuxiliaryUtility;

class Simhash {
    private HashFunction hashFunction;
    private int simhashLength;
    private int[] vector;
    private byte[] simhash;
    private StringBuilder debugString;

    Simhash(HashFunction hashFunction) {
        this.hashFunction = hashFunction;
        this.simhashLength = hashFunction.newHasher().putInt(0).hash().bits();
        this.vector = new int[simhashLength];
        this.simhash = new byte[simhashLength / 8];
        this.debugString = new StringBuilder();
    }

    private void updateVector(byte[] hash) {
        for (int i = 0; i < hash.length; i++) {
            byte mask = 1;
            for (int j = 0; j < 8; j++) {
                if ((hash[i] & mask) == 0) {
                    vector[i * 8 + j]--;
                } else {
                    vector[i * 8 + j]++;
                }
                mask <<= 1;
            }
        }

        // Debug mode
        debugString.append(String.format(
                "    asBytes() (BE):  [%s]%n" +
                "    Hash      (LE): 0x%s%n" +
                "              (LE): 0b%s%n",
                AuxiliaryUtility.ByteArrayAsHexBigEndian(hash),
                AuxiliaryUtility.ByteArrayAsHexLittleEndian(hash),
                AuxiliaryUtility.ByteArrayAsBinLittleEndian(hash)
        ));
    }

    void putVertex(int indegree, int outdegree, String label, int level) {
        // Make hash
        byte[] hash = hashFunction.newHasher()
                .putInt(indegree)
                .putInt(outdegree)
                .putUnencodedChars(label)
                .putInt(level)
                .hash().asBytes();

        // Debug mode
        debugString.append(String.format("%n    Vertex (level: %d, label: %s, indegree: %d, outdegree: %d)%n",
                level,
                label,
                indegree,
                outdegree
        ));

        // Update vector
        updateVector(hash);
    }

    void putLevelSeparator(int level, int length) {
        // Make hash
        byte[] hash = hashFunction.newHasher()
                .putInt(level)
                .putInt(length)
                .hash().asBytes();

        // Debug mode
        debugString.append(String.format("%n    Separator (level: %d, length: %d)%n",
                level,
                length
        ));

        // Update vector
        updateVector(hash);
    }

    void makeSimhash() {
        for (int i = 0; i < simhashLength / 8; i++) {
            byte mask = 1;
            for (int j = 0; j < 8; j++) {
                if (vector[i * 8 + j] >= 0) {
                    simhash[i] |= mask;
                }
                mask <<= 1;
            }
        }

        // Debug mode
        debugString.append(String.format(
                "%n" +
                "  Vector        : %s%n" +
                "  Simhash   (BE): 0x%s%n" +
                "            (LE): 0x%s%n" +
                "            (LE): 0b%s%n",
                Arrays.toString(vector),
                AuxiliaryUtility.ByteArrayAsHexBigEndian(simhash),
                AuxiliaryUtility.ByteArrayAsHexLittleEndian(simhash),
                AuxiliaryUtility.ByteArrayAsBinLittleEndian(simhash)
        ));
    }

    byte[] getSimhashAsBytes() {
        return simhash;
    }

    String getSimhashAsHexString() {
        return AuxiliaryUtility.ByteArrayAsHexLittleEndian(simhash);
    }

    int getSimhashLength() {
        return simhashLength;
    }

    String getDebugString() {
        String rv = debugString.toString();
        debugString = new StringBuilder();
        return rv;
    }
}
