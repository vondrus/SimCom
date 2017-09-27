package simcom;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;
import javafx.collections.ListChangeListener;

import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.ResourceBundle;


public class MainFormController implements Initializable {

    private CustomGraph leftGraph;

    private CustomGraph rightGraph;

    private GraphCatalog graphCatalog;

    private File catalogFile;

    private String lastOpenDirectory;

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
            System.exit(0);
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
        int levels = graph.getVerticesLevels();
        return "Name: " + name + ", vertices: " + vertices + ", edges: " + edges + ", levels: " + levels;
    }

    void checkCatalogFile() {
        catalogFile = new File(GlobalConstants.CATALOG_FILE_PATH);
        if (catalogFile.exists()) {
            GraphCatalogPersistence catalogReader = new GraphCatalogPersistence();
            graphCatalog = catalogReader.readFromFile(catalogFile);
            menuItemDeleteContentOfCatalog.setDisable(graphCatalog.size() == 0);
            menuItemShowContentOfCatalog.setDisable(graphCatalog.size() == 0);
        }
        else {
            Dialogs.catalogNotFoundWarningDialog();
            graphCatalog = new GraphCatalog();
            menuItemDeleteContentOfCatalog.setDisable(true);
            menuItemShowContentOfCatalog.setDisable(true);
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
        CustomGraph graph = GraphUtils.loadGraphFromDotFile(file);
        if (graph != null) {
            String graphFilename = file.getName();
            if (GraphUtils.evaluateGraph(graph)) {
                try {
                    Image image = GraphUtils.createGraphImage(graph);
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
        File selectedFile = fileChooser.showOpenDialog(leftStackPane.getScene().getWindow());
        if (selectedFile != null) {
            String absPath = selectedFile.getAbsolutePath();
            lastOpenDirectory = absPath.substring(0, absPath.lastIndexOf(File.separator));
            if (importGraphFromFile(selectedFile, false)) {
                tabPane.getSelectionModel().select(consoleTab);
            }
        }
        menuItemDeleteContentOfCatalog.setDisable(graphCatalog.size() == 0);
        menuItemShowContentOfCatalog.setDisable(graphCatalog.size() == 0);
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
        final File selectedDirectory = directoryChooser.showDialog(leftStackPane.getScene().getWindow());
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
        menuItemDeleteContentOfCatalog.setDisable(graphCatalog.size() == 0);
        menuItemShowContentOfCatalog.setDisable(graphCatalog.size() == 0);
    }

    @FXML
    private MenuItem menuItemShowContentOfCatalog;

    @FXML
    private void menuItemShowContentOfCatalogOnAction() {
        new CatalogForm(this, CatalogForm.MENU_ITEM_MODE);
    }

    @FXML
    private MenuItem menuItemDeleteContentOfCatalog;

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
            menuItemDeleteContentOfCatalog.setDisable(true);
            menuItemShowContentOfCatalog.setDisable(true);
        }
    }

    private void evaluateSimilarity(Set<CustomHash> simhash1, Set<CustomHash> simhash2) {
        long congruence = 0;

        for (CustomHash hash1 : simhash1) {
            console.println(hash1.getName(), console.TEXT_ATTR_RESULT);

            for (CustomHash hash2 : simhash2) {
                if (hash2.getName().equals(hash1.getName())) {
                    congruence = hash1.getHash() ^ hash2.getHash();
                    console.println("\tCongruence (" + Integer.toString(hash1.getLength()) + "bit): "
                            + Long.toBinaryString(congruence), console.TEXT_ATTR_RESULT);
                    break;
                }
            }

            int hammingDistance = 0;
            long mask = 1;
            for (int i = 0; i < hash1.getLength(); i++) {
                if ((congruence & mask) != 0)
                    hammingDistance++;
                mask = mask << 1;
            }

            console.println("\tHamming distance (" + Integer.toString(hash1.getLength()) + "bit): "
                    + Long.toString(hammingDistance), console.TEXT_ATTR_RESULT);
            console.println("\tSimilarity: " + (1 - (hammingDistance / (float) hash1.getLength())), console.TEXT_ATTR_RESULT);
            console.println();
        }
    }

    @FXML
    private void menuItemCompareGraphsOnAction() {
        if ((leftGraph != null) && (rightGraph != null)) {
            // Get focus to console tab.
            tabPane.getSelectionModel().select(consoleTab);

            console.println();

            console.println("Left graph (" + leftGraph.getName() + ") simhash", console.TEXT_ATTR_RESULT);
            final Set<CustomHash> leftGraphSimhash = leftGraph.getSimhash();
            for (CustomHash hash : leftGraphSimhash) {
                console.println('\t' + hash.getName() + " (" + hash.getLength() + "bit): " + Long.toBinaryString(hash.getHash())
                        + ", " + Long.toHexString(hash.getHash()), console.TEXT_ATTR_RESULT);
            }
            console.println();

            console.println("Right graph (" + rightGraph.getName() + ") simhash", console.TEXT_ATTR_RESULT);
            final Set<CustomHash> rightGraphSimhash = rightGraph.getSimhash();
            for (CustomHash hash : rightGraphSimhash) {
                console.println('\t' + hash.getName() + " (" + hash.getLength() + "bit): " + Long.toBinaryString(hash.getHash())
                        + ", " + Long.toHexString(hash.getHash()), console.TEXT_ATTR_RESULT);
            }
            console.println();

            evaluateSimilarity(leftGraphSimhash, rightGraphSimhash);
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
        setLeftSideGraph(null);
        setRightSideGraph(null);

        console.getChildren().addListener((ListChangeListener<Node>)
                ((change) -> {
                    console.layout();
                    scrollPane.layout();
                    scrollPane.setVvalue(1.0f);
                }));

        console.println("Ready.", console.TEXT_ATTR_NORMAL);
    }
}
