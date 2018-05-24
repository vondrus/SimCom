package SimCom.SimhashSimilarity;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashFunction;

public enum HashAlgorithm {

    // Here is the only place to insert additional hash algorithm.
    MURMUR("Murmur3A",
           Hashing.murmur3_32(),
           "MediumBlue",
           // The length is one half of hash length (32/2, 64/2, 128/2, 256/2 ...)
           new double[] { 0,
                   0.000000, 0.000000, 0.000001, 0.000010, 0.000057, 0.000268, 0.001051, 0.003500,
                   0.010031, 0.025051, 0.055092, 0.107664, 0.188543, 0.298307, 0.430025, 0.569975
           }
    ),

    SIPHASH("SipHash-2-4",
            Hashing.sipHash24(),
            "ForestGreen",
            new double[] { 0,
                    0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
                    0.000000, 0.000000, 0.000000, 0.000000, 0.000001, 0.000004, 0.000012, 0.000039,
                    0.000113, 0.000309, 0.000781, 0.001845, 0.004073, 0.008429, 0.016383, 0.029971,
                    0.051711, 0.084321, 0.130218, 0.190866, 0.266154, 0.353990, 0.450327, 0.549673
            }
    ),

    FARMHASH("FarmHash's Fingerprint64",
             Hashing.farmHashFingerprint64(),
             "Chocolate",
             new double[] { 0,
                     0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
                     0.000000, 0.000000, 0.000000, 0.000000, 0.000001, 0.000004, 0.000012, 0.000039,
                     0.000113, 0.000309, 0.000781, 0.001845, 0.004073, 0.008429, 0.016383, 0.029971,
                     0.051711, 0.084321, 0.130218, 0.190866, 0.266154, 0.353990, 0.450327, 0.549673
             }
    ),

    // MD5 is not used in security context, warning is suppressed.
    @SuppressWarnings("deprecation")
    MD5("MD5",
        Hashing.md5(),
        "Crimson",
        new double[] { 0,
                0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
                0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
                0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
                0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
                0.000000, 0.000000, 0.000000, 0.000000, 0.000001, 0.000002, 0.000006, 0.000013,
                0.000029, 0.000063, 0.000129, 0.000258, 0.000498, 0.000931, 0.001687, 0.002963,
                0.005045, 0.008335, 0.013368, 0.020819, 0.031504, 0.046345, 0.066312, 0.092341,
                0.125220, 0.165468, 0.213220, 0.268134, 0.329351, 0.395504, 0.464807, 0.535193
        }
    ),

    SHA256("SHA-256",
           Hashing.sha256(),
           "MediumOrchid",
           new double[] { 0,
                   0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
                   0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
                   0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
                   0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
                   0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
                   0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
                   0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
                   0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
                   0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
                   0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
                   0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
                   0.000001, 0.000001, 0.000002, 0.000004, 0.000007, 0.000013, 0.000022, 0.000038,
                   0.000064, 0.000107, 0.000174, 0.000280, 0.000445, 0.000694, 0.001067, 0.001615,
                   0.002409, 0.003541, 0.005126, 0.007314, 0.010285, 0.014254, 0.019476, 0.026236,
                   0.034850, 0.045656, 0.058999, 0.075217, 0.094624, 0.117484, 0.143995, 0.174261,
                   0.208279, 0.245921, 0.286931, 0.330916, 0.377365, 0.425658, 0.475090, 0.524910
           }
    );

    private String name;
    private HashFunction hashFunction;
    private String summaryColor;
    private double[] probNormalization;

    HashAlgorithm(String name, HashFunction hashFunction, String summaryColor, double[] probNormalization) {
        this.name = name;
        this.hashFunction = hashFunction;
        this.summaryColor = summaryColor;
        this.probNormalization = probNormalization;
    }

    public String getName() {
        return name;
    }

    public HashFunction getHashFunction() {
        return hashFunction;
    }

    public String getSummaryColor() {
        return summaryColor;
    }

    public double getProbNormalization(int hammingDistance) {
        if (hammingDistance > hashFunction.bits() / 2) {
            return probNormalization[hashFunction.bits() / 2];
        } else {
            return probNormalization[hammingDistance];
        }
    }
}