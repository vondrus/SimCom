package SimCom;

import java.util.List;
import java.util.ArrayList;


class GraphCatalog {

    private List<GraphCatalogItem> items;

    GraphCatalog() {
        items = new ArrayList<>();
    }

    List<GraphCatalogItem> getItems() {
        return items;
    }

    boolean add(GraphCatalogItem item) {
        return items.add(item);
    }

    int size() {
        return items.size();
    }

    int indexOf(CustomGraph graph) {
        for (GraphCatalogItem item: items) {
            if (item.getGraph().equals(graph))
                return items.indexOf(item);
        }
        return -1;
    }
}
