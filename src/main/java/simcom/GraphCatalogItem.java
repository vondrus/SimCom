package simcom;

import javafx.scene.image.Image;


class GraphCatalogItem {

    private CustomGraph graph;
    private Image image;

    GraphCatalogItem(CustomGraph graph, Image image) {
        this.graph = graph;
        this.image = image;
    }

    CustomGraph getGraph() {
        return graph;
    }

    Image getImage() {
        return image;
    }
}
