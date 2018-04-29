package simcom;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;
import javafx.collections.ListChangeListener;
import javafx.application.Platform;

import javax.swing.filechooser.FileNameExtensionFilter;


public class MainStageController implements Initializable {
    private static final String CATALOG_FILE_PATH = "catalog.bin";

    private ArrayList<CustomGraph> graphsForComparison = new ArrayList<>();
    private GraphCatalog graphCatalog;
    private File catalogFile;
    private String lastOpenDirectory;

    // FXML: Containers
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab consoleTab;
    @FXML
    private TilePane tilePane;
    @FXML
    private ScrollPane tilePaneScrollPane;
    @FXML
    private Console console;
    @FXML
    private ScrollPane consoleScrollPane;

    // FXML: Menu Items
    @FXML
    private MenuItem menuItemShowContentOfCatalog;
    @FXML
    private MenuItem menuItemDeleteContentOfCatalog;
    @FXML
    private MenuItem menuItemAddGraphsForComparison;
    @FXML
    private MenuItem menuItemRemoveAllGraphsFromComparison;
    @FXML
    private MenuItem menuItemCompareGraphs;


    // FXML: Menu items actions ----------------------------------------------------------------------------------------
    @FXML
    private void menuItemQuitOnAction() {
        if (Dialogs.quitConfirmationDialog())
            Platform.exit();
    }

    @FXML
    private void menuItemAboutOnAction() {
        Dialogs.aboutInformationDialog();
    }

    private boolean importGraphFromFile(File file, boolean consoleMode) {
        boolean rv = false;
        CustomGraph graph = GraphUtility.loadGraphFromDotFile(file);
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
                                GraphCatalogPersistence catalogWriter = new GraphCatalogPersistence();
                                catalogWriter.writeToFile(new File(CATALOG_FILE_PATH), graphCatalog);
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

    @FXML
    private void menuItemImportGraphFromFileOnAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open DOT File");
        if (lastOpenDirectory == null) {
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        } else {
            fileChooser.setInitialDirectory(new File(lastOpenDirectory));
        }
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("DOT Files", "*.dot", "*.gv"));
        File selectedFile = fileChooser.showOpenDialog(tilePane.getScene().getWindow());
        if (selectedFile != null) {
            String absPath = selectedFile.getAbsolutePath();
            lastOpenDirectory = absPath.substring(0, absPath.lastIndexOf(File.separator));
            if (importGraphFromFile(selectedFile, false)) {
                tabPane.getSelectionModel().select(consoleTab);
            }
        }
        setMenuItemsAvailability();
    }

    @FXML
    private void menuItemImportGraphsFromDirectoryOnAction() {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open directory with DOT files");
        if (lastOpenDirectory == null) {
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        } else {
            directoryChooser.setInitialDirectory(new File(lastOpenDirectory));
        }
        final File selectedDirectory = directoryChooser.showDialog(tilePane.getScene().getWindow());
        if (selectedDirectory != null) {
            lastOpenDirectory = selectedDirectory.getAbsolutePath();
            final FileNameExtensionFilter extensionFilter = new FileNameExtensionFilter("DOT files", "dot", "DOT");
            final File[] allFiles = selectedDirectory.listFiles();
            int processedFiles = 0;
            if (allFiles != null) {
                for (final File file : allFiles) {
                    if (extensionFilter.accept(file)) {
                        if (file.isFile()) {
                            importGraphFromFile(file, true);
                            processedFiles++;
                        }
                    }
                }
                tabPane.getSelectionModel().select(consoleTab);
                if (processedFiles == 0) {
                    Dialogs.noDotFileFoundInformationDialog(selectedDirectory.getAbsolutePath());
                }
            } else {
                Dialogs.ioErrorDialog();
            }
        }
        setMenuItemsAvailability();
    }

    @FXML
    private void menuItemShowContentOfCatalogOnAction() {
        if (graphCatalog.size() > 0) {
            new CatalogStage(graphCatalog, false);
        }
    }

    @FXML
    private void menuItemDeleteContentOfCatalogOnAction() {
        if (Dialogs.deleteContentOfCatalogConfirmationDialog()) {

            // Remove graphs prepared for comparison
            removeAllGraphsFromComparison();

            // Create a new graph catalog
            graphCatalog = new GraphCatalog();

            // Delete catalog file
            if (catalogFile.exists()) {
                if (! catalogFile.delete()) {
                    Dialogs.ioErrorDialog();
                }
            }

            // Set appropriate state of some menu items
            setMenuItemsAvailability();

            // Message to console
            tabPane.getSelectionModel().select(consoleTab);
            console.println("Content of the catalog was successfully deleted.", console.TEXT_ATTR_NORMAL);
        }
    }

    @FXML
    private void menuItemAddGraphsForComparisonOnAction() {
        final int IMAGE_VIEW_FIT_WIDTH = 600;
        final int IMAGE_VIEW_FIT_HEIGHT = 800;

        if (graphCatalog.size() > 0) {
            CatalogStage catalogStage = new CatalogStage(graphCatalog, true);
            boolean[] graphsSelectedForComparison = catalogStage.getResult();

            if (graphsSelectedForComparison != null) {
                for (int i = 0; i < graphsSelectedForComparison.length; i++) {
                    if (graphsSelectedForComparison[i]) {

                        GraphCatalogItem item = graphCatalog.getItems().get(i);
                        CustomGraph graph = item.getGraph();

                        if (! graphsForComparison.contains(graph)) {
                            // Add graph to comparison
                            graphsForComparison.add(graph);

                            // ImageView
                            Image image = item.getImage();
                            ImageView imageView = new ImageView(image);
                            imageView.setPreserveRatio(true);
                            imageView.setSmooth(true);
                            imageView.setCache(true);
                            imageView.setCacheHint(CacheHint.SCALE);
                            if ((image.getWidth() > IMAGE_VIEW_FIT_WIDTH) || (image.getHeight() > IMAGE_VIEW_FIT_HEIGHT)) {
                                imageView.setFitWidth(IMAGE_VIEW_FIT_WIDTH);
                                imageView.setFitHeight(IMAGE_VIEW_FIT_HEIGHT);
                            } else {
                                imageView.setFitWidth(0);
                                imageView.setFitHeight(0);
                            }

                            // StackPane
                            StackPane stackPane = new StackPane();
                            stackPane.setId("StackPane");
                            stackPane.getChildren().add(imageView);

                            // Label
                            Label label = new Label();
                            label.setId("GraphLabel");
                            label.setText(String.format("Name: %s, vertices: %s, edges: %s, depth: %s",
                                    graph.getName(),
                                    graph.vertexSet().size(),
                                    graph.edgeSet().size(),
                                    graph.getDepth()
                            ));

                            // VBox
                            VBox vBox = new VBox();
                            vBox.getChildren().add(stackPane);
                            vBox.getChildren().add(label);

                            // TilePane
                            tilePane.getChildren().add(vBox);

                            // Message to console
                            console.println("Graph " + graph.getName() + " was loaded from the catalog to the left pane.", console.TEXT_ATTR_NORMAL);

                            // Set appropriate state of some menu items
                            setMenuItemsAvailability();
                        }
                    }
                }
            }
        } else {
            Dialogs.catalogIsEmptyInformationDialog();
        }
    }

    @FXML
    private void menuItemRemoveAllGraphsFromComparisonOnAction() {
        if (Dialogs.removeAllGraphsFromComparisonConfirmationDialog()) {
            // Remove graphs prepared for comparison
            removeAllGraphsFromComparison();

            // Set appropriate state of some menu items
            setMenuItemsAvailability();
        }
    }

    @FXML
    private void menuItemCompareGraphsOnAction() {
        /*
        if ((leftGraph != null) && (rightGraph != null)) {

            // Get focus to console tab
            tabPane.getSelectionModel().select(consoleTab);

            // Method 1 - Evaluate similarity
            EditDistanceSimilarity editDistanceSimilarity = new EditDistanceSimilarity(leftGraph, rightGraph);
            editDistanceSimilarity.evaluateSimilarity();
            console.println(editDistanceSimilarity.getResultString(), console.TEXT_ATTR_RESULT);

            // Method 2 - Calculate simhashes and evaluate similarity
            SimhashSimilarity simhashSimilarity = new SimhashSimilarity(leftGraph, rightGraph);
            simhashSimilarity.evaluateSimilarity();
            console.println(simhashSimilarity.getResultString(), console.TEXT_ATTR_RESULT);
        }
        else {
            // Get focus to graphs tab.
            tabPane.getSelectionModel().select(graphsTab);

            Dialogs.missingGraphToCompareErrorDialog();
        }
        */
    }


    // Other methods ---------------------------------------------------------------------------------------------------

    private void removeAllGraphsFromComparison() {
        tilePane.getChildren().clear();
        graphsForComparison.clear();
    }

    private void disableMenuItemsAvailability() {
        menuItemShowContentOfCatalog.setDisable(true);
        menuItemDeleteContentOfCatalog.setDisable(true);
        menuItemAddGraphsForComparison.setDisable(true);
        menuItemRemoveAllGraphsFromComparison.setDisable(true);
        menuItemCompareGraphs.setDisable(true);
    }

    private void setMenuItemsAvailability() {
        if (graphCatalog.size() > 0) {
            // Catalog is not empty
            menuItemShowContentOfCatalog.setDisable(false);
            menuItemDeleteContentOfCatalog.setDisable(false);
            menuItemAddGraphsForComparison.setDisable(false);

            int graphsToCompareSize = graphsForComparison.size();
            menuItemRemoveAllGraphsFromComparison.setDisable(graphsToCompareSize < 1);
            menuItemCompareGraphs.setDisable(graphsToCompareSize < 2);
        } else {
            // Catalog is empty
            disableMenuItemsAvailability();
        }
    }


    // Initialization --------------------------------------------------------------------------------------------------

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Console tab: Moves grip on vertical scroll bar to the bottom
        console.getChildren().addListener((ListChangeListener<Node>)
                ((change) -> {
                    console.layout();
                    consoleScrollPane.layout();
                    consoleScrollPane.setVvalue(1.0f);
                }));

        // Graphs tab: Moves grip on vertical scroll bar to the bottom
        tilePane.getChildren().addListener((ListChangeListener<Node>)
                ((change) -> {
                    tilePane.layout();
                    tilePaneScrollPane.layout();
                    tilePaneScrollPane.setVvalue(1.0f);
                }));

        // Check existence of catalog file
        catalogFile = new File(CATALOG_FILE_PATH);
        if (catalogFile.exists()) {
            GraphCatalogPersistence catalogReader = new GraphCatalogPersistence();
            graphCatalog = catalogReader.readFromFile(catalogFile);
        } else {
            graphCatalog = new GraphCatalog();
            console.println("Catalog file not found! Catalog will be empty.", console.TEXT_ATTR_ERROR);
            tabPane.getSelectionModel().select(consoleTab);
        }

        // Set appropriate state of some menu items
        disableMenuItemsAvailability();
        setMenuItemsAvailability();

        // Let's go...
        console.println("Ready.", console.TEXT_ATTR_NORMAL);
    }

}
