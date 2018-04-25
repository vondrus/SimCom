package simcom;

import java.io.File;
import java.net.URL;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;
import javafx.collections.ListChangeListener;
import javafx.application.Platform;
import simcom.EditDistanceSimilarity.EditDistanceSimilarity;
import simcom.SimhashSimilarity.SimhashSimilarity;

import javax.swing.filechooser.FileNameExtensionFilter;


public class MainFormController implements Initializable {
    private ArrayList<CustomGraph> graphsToCompare = new ArrayList<>();

    // remove
    private CustomGraph leftGraph;
    // remove
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
    private TilePane tilePane;

    @FXML
    private ScrollPane consoleScrollPane;

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

    private void setAvailabilityOfCatalogMenuItems() {
        setDisableMenuItemDeleteContentOfCatalog(graphCatalog.size() == 0);
        setDisableMenuItemShowContentOfCatalog(graphCatalog.size() == 0);
        setDisableMenuItemAddGraphsToCompare(graphCatalog.size() == 0);
    }

    private void checkCatalogFile() {
        catalogFile = new File(GlobalConstants.CATALOG_FILE_PATH);
        if (catalogFile.exists()) {
            GraphCatalogPersistence catalogReader = new GraphCatalogPersistence();
            graphCatalog = catalogReader.readFromFile(catalogFile);
            setAvailabilityOfCatalogMenuItems();
        }
        else {
            graphCatalog = new GraphCatalog();
            setDisableMenuItemDeleteContentOfCatalog(true);
            setDisableMenuItemShowContentOfCatalog(true);
            setDisableMenuItemAddGraphsToCompare(true);
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
        setAvailabilityOfCatalogMenuItems();
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
        setAvailabilityOfCatalogMenuItems();
    }

    @FXML
    private MenuItem menuItemShowContentOfCatalog;

    private void setDisableMenuItemShowContentOfCatalog(boolean disable) {
        menuItemShowContentOfCatalog.setDisable(disable);
    }

    @FXML
    private void menuItemShowContentOfCatalogOnAction() {
        new CatalogForm(this);
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
            setDisableMenuItemAddGraphsToCompare(true);
        }
    }

    @FXML
    private void tilePaneOnMouseClicked() {
        menuItemAddGraphsToCompareOnAction();
    }

    @FXML
    private MenuItem menuItemRemoveGraphsFromCompare;

    private void setDisableMenuItemRemoveGraphsFromCompare(boolean disable) {
        menuItemRemoveGraphsFromCompare.setDisable(disable);
    }

    @FXML
    private void menuItemRemoveGraphsFromCompareOnAction() {
        // TODO
    }

    @FXML
    private MenuItem menuItemAddGraphsToCompare;

    private void setDisableMenuItemAddGraphsToCompare(boolean disable) {
        menuItemAddGraphsToCompare.setDisable(disable);
    }

    @FXML
    private void menuItemAddGraphsToCompareOnAction() {
        if (graphCatalog.size() > 0)
            new CatalogForm(this);
        else
            Dialogs.catalogIsEmptyInformationDialog();
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
    }

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Moves grip on vertical scroll bar to the bottom
        console.getChildren().addListener((ListChangeListener<Node>)
                ((change) -> {
                    console.layout();
                    consoleScrollPane.layout();
                    consoleScrollPane.setVvalue(1.0f);
                }));

        //setLeftSideGraph(null);
        //setRightSideGraph(null);

        checkCatalogFile();

        setDisableMenuItemCompareGraphs(true);

        console.println("Ready.", console.TEXT_ATTR_NORMAL);

        for (int i = 0; i < 100; i++) {
            console.println(Integer.toString(i), console.TEXT_ATTR_NORMAL);
        }


        /* Tests */
        Image tux = new Image("file:/home/vondrus/tux.jpg");

        ImageView imageView1 = new ImageView(tux);
        imageView1.setPreserveRatio(true);
        imageView1.setSmooth(true);
        imageView1.setCacheHint(CacheHint.SCALE);
        imageView1.setFitWidth(200);
        StackPane stackPane1 = new StackPane();
        stackPane1.getChildren().add(imageView1);

        ImageView imageView2 = new ImageView(tux);
        imageView2.setPreserveRatio(true);
        imageView2.setSmooth(true);
        imageView2.setCacheHint(CacheHint.SCALE);
        imageView2.setFitWidth(200);
        StackPane stackPane2 = new StackPane();
        stackPane2.getChildren().add(imageView2);

        tilePane.getChildren().add(stackPane1);
        tilePane.getChildren().add(stackPane2);

    }
}
