package simcom;


class CustomHash {
    private long hash;
    private int length;
    private String name;

    CustomHash(long hash, int length, String name) {
        this.hash = hash;
        this.length = length;
        this.name = name;
    }

    long getHash() {
        return hash;
    }

    public void setHash(long hash) {
        this.hash = hash;
    }

    int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
