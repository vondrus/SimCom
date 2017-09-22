package simcom;

import java.io.*;

import javafx.scene.image.Image;

import org.jgrapht.ext.*;


final class GraphUtils {

    private static Image image;

    static Image createGraphImage(CustomGraph graph) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(GlobalConstants.DOT_EXEC_FILE_PATH, "-Tpng");
        Process process = builder.start();

        Thread thread = new Thread(() -> {
            final InputStream inputStream = process.getInputStream();
            image = new Image(inputStream);
        });
        thread.start();

        OutputStream outStream = process.getOutputStream();
        PrintWriter pWriter = new PrintWriter(outStream);

        pWriter.println("digraph name {\n\tratio=compress;\n\tnode [shape=circle, style=filled, fillcolor=\"lightgray\", fontcolor=\"black\"];\n");

        for (CustomGraphEdge edge : graph.edgeSet()) {
            pWriter.println(graph.getEdgeSource(edge) + " -> " + graph.getEdgeTarget(edge) + ";\n");
        }

        pWriter.println('}');
        pWriter.flush();
        pWriter.close();

        try {
            thread.join();
            return image;
        } catch (InterruptedException e) {
            Dialogs.exceptionDialog(e);
            return null;
        }
    }

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

    static boolean evaluateGraph(CustomGraph graph) {
        graph.initAttributes();
        int components = 0;
        for (CustomGraphVertex vertex : graph.vertexSet()) {
            if (vertex.getStatus() == CustomGraphVertex.Status.FRESH) {
                depthFirstSearch(graph, vertex, 0);
                components++;
            }
        }
        graph.updateMaxLevelOfVertices();
        return components == 1;
    }

    private static void depthFirstSearch(CustomGraph graph, CustomGraphVertex vertex, int level) {
        vertex.setStatus(CustomGraphVertex.Status.OPEN);
        vertex.setLevel(level);
        level++;
        for (CustomGraphVertex successor : graph.getVertexSuccessors(vertex))
            if (successor.getStatus() == CustomGraphVertex.Status.FRESH)
                depthFirstSearch(graph, successor, level);
        vertex.setStatus(CustomGraphVertex.Status.CLOSED);
    }
}
