package simcom;

import java.io.Serializable;


class CustomGraphVertex implements Serializable {
    private static final long serialVersionUID = 6690978526162487994L;
    public enum Status { FRESH, OPEN, CLOSED }

    private String label;
    private Status status;
    private int level;

    CustomGraphVertex(String label) {
        this.label = label;
        this.status = Status.FRESH;
        this.level = 0;
    }

    String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    Status getStatus() {
        return status;
    }

    void setStatus(Status status) {
        this.status = status;
    }

    int getLevel() {
        return level;
    }

    void setLevel(int level) {
        this.level = level;
    }

    public int hashCode() {
        if (label == null)
            return 0;
        else
            return label.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CustomGraphVertex)) {
            return false;
        }

        CustomGraphVertex vertex = (CustomGraphVertex) obj;
        if (label == null)
            return vertex.label == null;
        else
            return label.equals(vertex.label);
    }

    @Override
    public String toString() {
        return label;
    }
}
