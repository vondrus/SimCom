package simcom;

import java.io.*;
import java.util.*;

import javax.annotation.Nonnull;

import javafx.scene.image.Image;

import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.ClassBasedEdgeFactory;


public class CustomGraph extends SimpleDirectedGraph<CustomGraphVertex, CustomGraphEdge> implements Iterable<CustomGraphLevel> {
    private static final long serialVersionUID = 160210295726081101L;
    private List<CustomGraphLevel> levels = new ArrayList<>();
    private String name;
    private int componentCount;

    CustomGraph(Class<? extends CustomGraphEdge> edgeClass, String name) {
        super(new ClassBasedEdgeFactory<>(edgeClass));
        this.name = name;
    }

    @Override
    @Nonnull
    public Iterator<CustomGraphLevel> iterator() {
        return Collections.unmodifiableList(levels).iterator();
    }

    public String getName() {
        return name;
    }

    int getComponentCount() {
        return componentCount;
    }

    public int getDepth() {
        return levels.size();
    }

    public CustomGraphLevel getLevel(int levelNumber) {
        if (levelNumber < getDepth()) {
            return levels.get(levelNumber);
        } else {
            return null;
        }
    }

    Image getImage() throws IOException {
        final ProcessBuilder builder = new ProcessBuilder("/usr/bin/dot", "-Tpng");
        final Process process = builder.start();
        final Image[] image = new Image[1];

        Thread thread = new Thread(() -> {
            final InputStream inputStream = process.getInputStream();
            image[0] = new Image(inputStream);
        });
        thread.start();

        OutputStream outStream = process.getOutputStream();
        PrintWriter pWriter = new PrintWriter(outStream);
        // TODO: Refactor (platform dependent newlines)
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
        if (levels.size() < level + 1) {
            levels.add(new CustomGraphLevel());
        }

        // Add the vertex to the appropriate level
        levels.get(level).add(vertex);

        level++;
        for (CustomGraphVertex successor : getVertexSuccessors(vertex))
            if (successor.getStatus() == CustomGraphVertex.Status.FRESH)
                depthFirstSearch(successor, level);
        vertex.setStatus(CustomGraphVertex.Status.CLOSED);
    }

    void createHierarchicalStructure() {
        for (CustomGraphVertex vertex : this.vertexSet()) {
            if (vertex.getStatus() == CustomGraphVertex.Status.FRESH) {
                depthFirstSearch(vertex, 0);
                componentCount++;
            }
        }
    }

}
