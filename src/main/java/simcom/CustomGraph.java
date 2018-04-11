package simcom;

import java.io.*;
import java.util.*;

import javafx.scene.image.Image;

import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.SimpleDirectedGraph;


class CustomGraph extends SimpleDirectedGraph<CustomGraphVertex, CustomGraphEdge> {
    private static final long serialVersionUID = 160210295726081101L;
    private ArrayList<ArrayList<CustomGraphVertex>> levels;
    private String name;
    private int componentCount;

    CustomGraph(Class<? extends CustomGraphEdge> edgeClass, String name) {
        super(new ClassBasedEdgeFactory<>(edgeClass));
        this.levels = new ArrayList<>();
        this.name = name;
    }

    ArrayList<ArrayList<CustomGraphVertex>> getLevels() {
        return levels;
    }

    String getName() {
        return name;
    }

    int getComponentCount() {
        return componentCount;
    }

    int getDepth() {
        return levels.size();
    }

/* ------------------------------------------------------------------------------------------------------------------ */

    Image getImage() throws IOException {
        final ProcessBuilder builder = new ProcessBuilder(GlobalConstants.DOT_EXEC_FILE_PATH, "-Tpng");
        final Process process = builder.start();
        final Image[] image = new Image[1];

        Thread thread = new Thread(() -> {
            final InputStream inputStream = process.getInputStream();
            image[0] = new Image(inputStream);
        });
        thread.start();

        OutputStream outStream = process.getOutputStream();
        PrintWriter pWriter = new PrintWriter(outStream);

        pWriter.println("digraph name {\n\tratio=compress;\n\tnode [shape=circle, style=filled, fillcolor=\"lightgray\", fontcolor=\"black\"];\n");

        for (CustomGraphEdge edge : this.edgeSet()) {
            pWriter.println(this.getEdgeSource(edge) + " -> " + this.getEdgeTarget(edge) + ";\n");
        }

        pWriter.println('}');
        pWriter.flush();
        pWriter.close();

        try {
            thread.join();
            return image[0];
        } catch (InterruptedException e) {
            Dialogs.exceptionDialog(e);
            return null;
        }
    }

/* ------------------------------------------------------------------------------------------------------------------ */

    private List<CustomGraphVertex> getVertexSuccessors(CustomGraphVertex vertex) {
        ArrayList<CustomGraphVertex> successors = new ArrayList<>();
        Set<CustomGraphEdge> outgoingEdges = this.outgoingEdgesOf(vertex);
        for (CustomGraphEdge edge : outgoingEdges) {
            successors.add(this.getEdgeTarget(edge));
        }
        return successors;
    }

    private void depthFirstSearch(CustomGraphVertex vertex, int level) {
        // Mark the vertex as open
        vertex.setStatus(CustomGraphVertex.Status.OPEN);

        // If necessary add new level to the hierarchy
        if (this.levels.size() < level + 1) {
            this.levels.add(new ArrayList<>());
        }

        // Add the vertex to the appropriate level
        this.levels.get(level).add(vertex);

        level++;
        for (CustomGraphVertex successor : this.getVertexSuccessors(vertex))
            if (successor.getStatus() == CustomGraphVertex.Status.FRESH)
                depthFirstSearch(successor, level);
        vertex.setStatus(CustomGraphVertex.Status.CLOSED);
    }

    void createHierarchicalStructure() {
        for (CustomGraphVertex vertex : this.vertexSet()) {
            if (vertex.getStatus() == CustomGraphVertex.Status.FRESH) {
                depthFirstSearch(vertex, 0);
                this.componentCount++;
            }
        }
    }

    void printHierarchicalStructure() {
        for (ArrayList<CustomGraphVertex> level : levels) {
            for (CustomGraphVertex vertex : level) {
                System.out.print(vertex.getLabel() + ",");
            }
            System.out.println();
        }
    }

}
