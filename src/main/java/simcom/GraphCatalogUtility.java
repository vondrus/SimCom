package simcom;

import java.io.File;
import java.io.IOException;

import javafx.scene.image.Image;

import org.jgrapht.ext.*;

import static simcom.GraphCatalogPersistence.readFromFile;
import static simcom.GraphCatalogPersistence.writeToFile;

class GraphCatalogUtility {
    private static File catalogFile = new File(System.getProperty("user.home") + File.separator + "catalog.bin");

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
    private static CustomGraph loadGraphFromDotFile(File filepath) {
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

    static boolean importGraphFromFile(File file, GraphCatalog graphCatalog, Console console, boolean consoleMode) {
        boolean rv = false;
        CustomGraph graph = GraphCatalogUtility.loadGraphFromDotFile(file);
        if (graph != null) {
            String graphFilename = file.getName();
            graph.createHierarchicalStructure();
            if (graph.getComponentCount() == 1) {
                try {
                    Image image = graph.getImage();
                    if (image != null) {
                        int indexOf = graphCatalog.indexOf(graph);
                        if (indexOf == -1) {
                            GraphCatalogItem newGraphCatalogItem = new GraphCatalogItem(graph, image);
                            if (graphCatalog.add(newGraphCatalogItem)) {
                                writeToFile(catalogFile, graphCatalog);
                                console.println("Graph " + graph.getName() + " was added to to the catalog.", console.TEXT_ATTR_NORMAL);
                                rv = true;
                            } else {
                                if (consoleMode) {
                                    console.println("Cannot add graph " + graphFilename
                                            + " to the catalog. Collection was not changed.", console.TEXT_ATTR_ERROR);
                                }
                                else {
                                    Dialogs.cannotAddGraphCollectionErrorDialog();
                                }
                            }
                        } else {
                            String graphName = graphCatalog.getItems().get(indexOf).getGraph().getName();
                            if (consoleMode) {
                                console.println("Cannot add graph " + graphFilename
                                        + " to the catalog. The same one is already in the catalog ("
                                        + graphName + ").", console.TEXT_ATTR_ERROR);
                            }
                            else {
                                Dialogs.sameGraphInCatalogInformationDialog(graphName);
                            }
                        }
                    }
                } catch (IOException e) {
                    Dialogs.exceptionDialog(e);
                }
            }
            else {
                if (consoleMode) {
                    console.println("Cannot add graph "
                            + graphFilename + " to the catalog. It contains more than one component.", console.TEXT_ATTR_ERROR);
                }
                else {
                    Dialogs.cannotAddGraphComponentErrorDialog();
                }
            }
        }
        return (rv);
    }

    static boolean graphCatalogFileExists() {
        return catalogFile.exists() && !catalogFile.isDirectory();
    }

    static void deleteGraphCatalogFile() {
        if (graphCatalogFileExists()) {
            if (! catalogFile.delete()) {
                Dialogs.ioErrorDialog();
            }
        }
    }

    static GraphCatalog readGraphCatalogFile () {
        return readFromFile(catalogFile);
    }
}
