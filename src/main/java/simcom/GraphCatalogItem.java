package simcom;

import javafx.scene.image.Image;


class GraphCatalogItem {
    private CustomGraph graph;
    private Image image;
    private boolean selected;

    GraphCatalogItem(CustomGraph graph, Image image) {
        this.graph = graph;
        this.image = image;
        this.selected = false;
    }

    CustomGraph getGraph() {
        return graph;
    }

    Image getImage() {

        return image;
    }

    boolean isSelected() {
        return selected;
    }

    void setSelected(boolean selected) {
        this.selected = selected;
    }

}
