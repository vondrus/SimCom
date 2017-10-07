package simcom;

import org.jgrapht.ext.*;

import java.io.File;


class GraphUtils {

    private static GraphImporter<CustomGraphVertex, CustomGraphEdge> createImporter() {
        VertexProvider<CustomGraphVertex> vertexProvider
                = (label, attributes) -> new CustomGraphVertex(label);
        EdgeProvider<CustomGraphVertex, CustomGraphEdge> edgeProvider
                = (from, to, label, attributes) -> new CustomGraphEdge(from.getLabel() + " -> " + to.getLabel());
        return new DOTImporter<>(vertexProvider, edgeProvider);
    }

    private static String createGraphName(File filepath) {
        String name = filepath.getName();
        if (name.indexOf('.') > 0) {
            name = name.substring(0, name.lastIndexOf('.'));
        }
        return name.substring(0, Math.min(name.length(), 16));
    }

    /* POZOR!
       org.jgrapht.ext DOTImporter<V,E> pripousti ve vstupnim souboru v definicich hran jako oddelovac vrcholu
       pouze tuto syntaxi: vertex1 + " -> " + vertex2
     */
    static CustomGraph loadGraphFromDotFile(File filepath) {
        CustomGraph graph = new CustomGraph(CustomGraphEdge.class, createGraphName(filepath));
        GraphImporter<CustomGraphVertex, CustomGraphEdge> importer = createImporter();
        try {
            importer.importGraph(graph, filepath);
            return graph;
        } catch (ImportException e) {
            Dialogs.exceptionDialog(e);
            return null;
        }
    }
}
