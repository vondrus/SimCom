package simcom;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;
import javafx.collections.ListChangeListener;
import javafx.application.Platform;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class MainFormController implements Initializable {

    private CustomGraph leftGraph;

    private CustomGraph rightGraph;

    private GraphCatalog graphCatalog;

    private File catalogFile;

    private String lastOpenDirectory;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab graphsTab;

    @FXML
    private Tab consoleTab;

    @FXML
    private StackPane leftStackPane;

    @FXML
    private StackPane rightStackPane;

    @FXML
    private ImageView leftStackPaneImageView;

    @FXML
    private ImageView rightStackPaneImageView;

    @FXML
    private Label leftInfoLabel;

    @FXML
    private Label rightInfoLabel;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Console console;

    @FXML
    private void menuItemQuitOnAction() {
        if (Dialogs.quitConfirmationDialog())
            Platform.exit();
    }

    @FXML
    private void menuItemAboutOnAction() {
        Dialogs.aboutInformationDialog();
    }

    @FXML
    private void leftStackPaneOnMouseClicked() {
        if (graphCatalog.size() > 0)
            new CatalogForm(this, CatalogForm.LEFT_SIDE_CLICK_MODE);
        else
            Dialogs.catalogIsEmptyInformationDialog();
    }

    @FXML
    private void rightStackPaneOnMouseClicked() {
        if (graphCatalog.size() > 0)
            new CatalogForm(this, CatalogForm.RIGHT_SIDE_CLICK_MODE);
        else
            Dialogs.catalogIsEmptyInformationDialog();
    }

    @SuppressWarnings("Duplicates")
    void setLeftSideGraph(GraphCatalogItem item) {
        if (item != null) {
            CustomGraph graph = item.getGraph();
            Image image = item.getImage();
            leftStackPaneImageView.setImage(image);
            if ((image.getWidth() > leftStackPane.getWidth()) || (image.getHeight() > leftStackPane.getHeight())) {
                leftStackPaneImageView.setFitWidth(leftStackPane.getWidth());
                leftStackPaneImageView.setFitHeight(leftStackPane.getHeight());
            } else {
                leftStackPaneImageView.setFitWidth(0);
                leftStackPaneImageView.setFitHeight(0);
            }
            leftInfoLabel.setText(createInfoLabelText(graph));
            console.println("Graph " + graph.getName() + " was loaded from the catalog to the left pane.", console.TEXT_ATTR_NORMAL);
        }
        else {
            leftStackPaneImageView.setPreserveRatio(true);
            leftStackPaneImageView.setSmooth(true);
            leftStackPaneImageView.setCache(true);
            leftStackPaneImageView.setFitWidth(0);
            leftStackPaneImageView.setFitHeight(0);
            leftStackPaneImageView.setImage(new Image("/images/imageClickHere.png"));
            leftInfoLabel.setText("");
        }
    }

    @SuppressWarnings("Duplicates")
    void setRightSideGraph(GraphCatalogItem item) {
        if (item != null) {
            CustomGraph graph = item.getGraph();
            Image image = item.getImage();
            rightStackPaneImageView.setImage(image);
            if ((image.getWidth() > rightStackPane.getWidth()) || (image.getHeight() > rightStackPane.getHeight())) {
                rightStackPaneImageView.setFitWidth(rightStackPane.getWidth());
                rightStackPaneImageView.setFitHeight(rightStackPane.getHeight());
            } else {
                rightStackPaneImageView.setFitWidth(0);
                rightStackPaneImageView.setFitHeight(0);
            }
            rightInfoLabel.setText(createInfoLabelText(graph));
            console.println("Graph " + graph.getName() + " was loaded from the catalog to the right pane.", console.TEXT_ATTR_NORMAL);
        }
        else {
            rightStackPaneImageView.setPreserveRatio(true);
            rightStackPaneImageView.setSmooth(true);
            rightStackPaneImageView.setCache(true);
            rightStackPaneImageView.setFitWidth(0);
            rightStackPaneImageView.setFitHeight(0);
            rightStackPaneImageView.setImage(new Image("/images/imageClickHere.png"));
            rightInfoLabel.setText("");
        }
    }

    private String createInfoLabelText(CustomGraph graph) {
        String name = graph.getName();
        int vertices = graph.vertexSet().size();
        int edges = graph.edgeSet().size();
        int depth = graph.getDepth();
        return "Name: " + name + ", vertices: " + vertices + ", edges: " + edges + ", depth: " + depth;
    }

    private void checkCatalogFile() {
        catalogFile = new File(GlobalConstants.CATALOG_FILE_PATH);
        if (catalogFile.exists()) {
            GraphCatalogPersistence catalogReader = new GraphCatalogPersistence();
            graphCatalog = catalogReader.readFromFile(catalogFile);
            setDisableMenuItemDeleteContentOfCatalog(graphCatalog.size() == 0);
            setDisableMenuItemShowContentOfCatalog(graphCatalog.size() == 0);
            setDisableMenuItemSummaryOfComparisons(graphCatalog.size() == 0);
        }
        else {
            graphCatalog = new GraphCatalog();
            setDisableMenuItemDeleteContentOfCatalog(true);
            setDisableMenuItemShowContentOfCatalog(true);
            setDisableMenuItemSummaryOfComparisons(true);
            console.println("Catalog file not found! Catalog will be empty.", console.TEXT_ATTR_ERROR);
            tabPane.getSelectionModel().select(consoleTab);
        }
    }

    CustomGraph getLeftGraph() {
        return leftGraph;
    }

    void setLeftGraph(CustomGraph leftGraph) {
        this.leftGraph = leftGraph;
    }

    CustomGraph getRightGraph() {
        return rightGraph;
    }

    void setRightGraph(CustomGraph rightGraph) {
        this.rightGraph = rightGraph;
    }

    GraphCatalog getGraphCatalog() {
        return graphCatalog;
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
                                catalogWriter.writeToFile(new File(GlobalConstants.CATALOG_FILE_PATH), graphCatalog);
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
        File selectedFile = fileChooser.showOpenDialog(anchorPane.getScene().getWindow());
        if (selectedFile != null) {
            String absPath = selectedFile.getAbsolutePath();
            lastOpenDirectory = absPath.substring(0, absPath.lastIndexOf(File.separator));
            if (importGraphFromFile(selectedFile, false)) {
                tabPane.getSelectionModel().select(consoleTab);
            }
        }
        setDisableMenuItemDeleteContentOfCatalog(graphCatalog.size() == 0);
        setDisableMenuItemShowContentOfCatalog(graphCatalog.size() == 0);
        setDisableMenuItemSummaryOfComparisons(graphCatalog.size() == 0);
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
        final File selectedDirectory = directoryChooser.showDialog(anchorPane.getScene().getWindow());
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
        setDisableMenuItemDeleteContentOfCatalog(graphCatalog.size() == 0);
        setDisableMenuItemShowContentOfCatalog(graphCatalog.size() == 0);
        setDisableMenuItemSummaryOfComparisons(graphCatalog.size() == 0);
    }

    @FXML
    private MenuItem menuItemShowContentOfCatalog;

    private void setDisableMenuItemShowContentOfCatalog(boolean disable) {
        menuItemShowContentOfCatalog.setDisable(disable);
    }

    @FXML
    private void menuItemShowContentOfCatalogOnAction() {
        new CatalogForm(this, CatalogForm.MENU_ITEM_MODE);
    }

    @FXML
    private MenuItem menuItemDeleteContentOfCatalog;

    private void setDisableMenuItemDeleteContentOfCatalog(boolean disable) {
        menuItemDeleteContentOfCatalog.setDisable(disable);
    }

    @FXML
    private void menuItemDeleteContentOfCatalogOnAction() {
        if (Dialogs.deleteContentOfCatalogConfirmationDialog()) {
            graphCatalog = new GraphCatalog();

            setLeftGraph(null);
            setLeftSideGraph(null);

            setRightGraph(null);
            setRightSideGraph(null);

            if (catalogFile.exists()) {
                if (! catalogFile.delete()) {
                    Dialogs.ioErrorDialog();
                }
            }

            tabPane.getSelectionModel().select(consoleTab);
            console.println("Content of the catalog was successfully deleted.", console.TEXT_ATTR_NORMAL);
            setDisableMenuItemDeleteContentOfCatalog(true);
            setDisableMenuItemShowContentOfCatalog(true);
            setDisableMenuItemSummaryOfComparisons(true);
        }
    }

    @FXML
    private MenuItem menuItemCompareGraphs;

    void setDisableMenuItemCompareGraphs(boolean disable) {
        menuItemCompareGraphs.setDisable(disable);
    }

    @FXML
    private void menuItemCompareGraphsOnAction() {
        if ((leftGraph != null) && (rightGraph != null)) {

            // Get focus to console tab
            tabPane.getSelectionModel().select(consoleTab);

            // Calculate simhashes and evaluate similarity
            SimilarityMeasure2 similarityMeasure2 = new SimilarityMeasure2(leftGraph, rightGraph);
            similarityMeasure2.makeSimHashTables();
            similarityMeasure2.evaluateSimilarity();

            // Show results
            console.println(similarityMeasure2.getResultString(), console.TEXT_ATTR_RESULT);
        }
        else {
            // Get focus to graphs tab.
            tabPane.getSelectionModel().select(graphsTab);

            Dialogs.missingGraphToCompareErrorDialog();
        }
    }

    @FXML
    private MenuItem menuItemSummaryOfComparisons;

    private void setDisableMenuItemSummaryOfComparisons(boolean disable) {
        menuItemSummaryOfComparisons.setDisable(disable);
    }

    @FXML
    private void menuItemSummaryOfComparisonsOnAction() {
        // Get focus to console tab.
        tabPane.getSelectionModel().select(consoleTab);

        // Todo: Complete code
        System.out.println("Hierarchical structure:");
        this.getLeftGraph().printHierarchicalStructure();
        System.out.println();
    }

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        console.getChildren().addListener((ListChangeListener<Node>)
                ((change) -> {
                    console.layout();
                    scrollPane.layout();
                    scrollPane.setVvalue(1.0f);
                }));

        setLeftSideGraph(null);
        setRightSideGraph(null);

        checkCatalogFile();

        setDisableMenuItemCompareGraphs(true);

        console.println("Ready.", console.TEXT_ATTR_NORMAL);
    }
}
