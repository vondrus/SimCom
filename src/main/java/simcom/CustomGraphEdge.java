package simcom;

import org.jgrapht.graph.DefaultEdge;


@SuppressWarnings("unused") public
class CustomGraphEdge extends DefaultEdge {
    private static final long serialVersionUID = 1467254477829189996L;
    private String label;

    CustomGraphEdge(String label) {
        this.label = label;
    }

    String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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
        if (!(obj instanceof CustomGraphEdge)) {
            return false;
        }

        CustomGraphEdge edge = (CustomGraphEdge) obj;
        if (label == null)
            return edge.label == null;
        else
            return label.equals(edge.label);
    }

    @Override
    public String toString() {
        return label;
    }
}
