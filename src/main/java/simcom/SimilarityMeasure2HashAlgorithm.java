package simcom;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashFunction;


public enum SimilarityMeasure2HashAlgorithm {

    // Here is the only place to insert additional hash algorithm.
    MURMUR("Murmur3A", Hashing.murmur3_32()),
    SIPHASH("SipHash-2-4", Hashing.sipHash24()),
    FARMHASH("FarmHash's Fingerprint64", Hashing.farmHashFingerprint64()),
    // MD5 is not used in security context, warning is suppressed.
    @SuppressWarnings("deprecation")
    MD5("MD5", Hashing.md5()),
    SHA256("SHA-256", Hashing.sha256());

    private String name;
    private HashFunction hashFunction;

    SimilarityMeasure2HashAlgorithm(String name, HashFunction hashFunction) {
        this.name = name;
        this.hashFunction = hashFunction;
    }

    public String getName() {
        return name;
    }

    public HashFunction getHashFunction() {
        return hashFunction;
    }

}
