package simcom.SimhashSimilarity;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashFunction;

public enum HashAlgorithm {

    // Here is the only place to insert additional hash algorithm.
    MURMUR("Murmur3A",
           Hashing.murmur3_32(),
           "MediumBlue"
    ),

    SIPHASH("SipHash-2-4",
            Hashing.sipHash24(),
            "ForestGreen"
    ),

    FARMHASH("FarmHash's Fingerprint64",
             Hashing.farmHashFingerprint64(),
             "Chocolate"
    ),

    // MD5 is not used in security context, warning is suppressed.
    @SuppressWarnings("deprecation")
    MD5("MD5",
        Hashing.md5(),
        "Crimson"
    ),

    SHA256("SHA-256",
           Hashing.sha256(),
           "MediumOrchid"
    );

    private String name;
    private HashFunction hashFunction;
    private String summaryColor;

    HashAlgorithm(String name, HashFunction hashFunction, String summaryColor) {
        this.name = name;
        this.hashFunction = hashFunction;
        this.summaryColor = summaryColor;
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
}
