package simcom;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashFunction;


public enum CustomGraphHashAlgorithm {

    //MURMUR("Murmur3A", Hashing.murmur3_32());
    SIPHASH("SipHash-2-4", Hashing.sipHash24());
    // FARMHASH("FarmHash's Fingerprint64", Hashing.farmHashFingerprint64());

    private String name;
    private HashFunction hashFunction;

    CustomGraphHashAlgorithm(String name, HashFunction hashFunction) {
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
