package simcom.SimhashSimilarity;

import java.util.Arrays;

import com.google.common.hash.HashFunction;

import simcom.AuxiliaryUtility;


class Simhash {
    private HashFunction hashFunction;
    private int simhashLength;
    private int[] vector;
    private byte[] simhash;

    Simhash(HashFunction hashFunction) {
        this.hashFunction = hashFunction;
        this.simhashLength = hashFunction.newHasher().putInt(0).hash().bits();
        this.vector = new int[simhashLength];
        this.simhash = new byte[simhashLength / 8];
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

        // Debug
        System.out.println("asBytes() (BE):  [" + AuxiliaryUtility.ByteArrayAsHexBigEndian(hash) + "]");
        System.out.println("Hash      (LE): 0x" + AuxiliaryUtility.ByteArrayAsHexLittleEndian(hash));
        System.out.println("          (LE): 0b" + AuxiliaryUtility.ByteArrayAsBinLittleEndian(hash));
    }

    void putVertex(int indegree, int outdegree, String label, int level) {
        // Make hash
        byte[] hash = hashFunction.newHasher()
                .putInt(indegree)
                .putInt(outdegree)
                .putUnencodedChars(label)
                .putInt(level)
                .hash().asBytes();

        // Update vector
        updateVector(hash);
    }

    void putLevelSeparator(int level, int length) {
        // Make hash
        byte[] hash = hashFunction.newHasher()
                .putInt(level)
                .putInt(length)
                .hash().asBytes();

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

        // Debug
        System.out.println();
        System.out.println("Vector        : " + Arrays.toString(vector));
        System.out.println("Simhash   (BE): 0x" + AuxiliaryUtility.ByteArrayAsHexBigEndian(simhash));
        System.out.println("          (LE): 0x" + AuxiliaryUtility.ByteArrayAsHexLittleEndian(simhash));
        System.out.println("          (LE): 0b" + AuxiliaryUtility.ByteArrayAsBinLittleEndian(simhash));
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

}
