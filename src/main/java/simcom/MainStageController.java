package simcom;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.filechooser.FileNameExtensionFilter;

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

import simcom.SimhashSimilarity.SimhashSimilarity;
import simcom.EditDistanceSimilarity.EditDistanceSimilarity;

import static simcom.GraphCatalogUtility.*;

public class MainStageController implements Initializable {
    private ArrayList<CustomGraph> graphsForComparison = new ArrayList<>();
    private GraphCatalog graphCatalog;
    private String lastOpenDirectory;

    // FXML: Containers
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab graphsTab;
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
            if (importGraphFromFile(selectedFile, graphCatalog, console,false)) {
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
                            importGraphFromFile(file, graphCatalog, console, true);
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
            deleteGraphCatalogFile();

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
                tabPane.getSelectionModel().select(graphsTab);

                for (int i = 0; i < graphsSelectedForComparison.length; i++) {
                    if (graphsSelectedForComparison[i]) {

                        GraphCatalogItem item = graphCatalog.getItems().get(i);
                        CustomGraph graph = item.getGraph();
                        item.setSelected(true);

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

        // Get focus to console tab
        tabPane.getSelectionModel().select(consoleTab);

        // Make similarity matrix
        for (CustomGraph graph1 : graphsForComparison) {
            for (CustomGraph graph2 : graphsForComparison) {

                // Method 1 - Evaluate similarity
                EditDistanceSimilarity editDistanceSimilarity = new EditDistanceSimilarity(graph1, graph2);
                editDistanceSimilarity.evaluateSimilarity();
                console.println(editDistanceSimilarity.getResultString(), console.TEXT_ATTR_RESULT);

                // Method 2 - Calculate simhashes and evaluate similarity
                SimhashSimilarity simhashSimilarity = new SimhashSimilarity(graph1, graph2);
                simhashSimilarity.evaluateSimilarity();
                console.println(simhashSimilarity.getResultString(), console.TEXT_ATTR_RESULT);
            }
        }
    }


    // Other methods ---------------------------------------------------------------------------------------------------

    private void removeAllGraphsFromComparison() {
        tilePane.getChildren().clear();
        graphsForComparison.clear();
        for (GraphCatalogItem item : graphCatalog.getItems()) {
            item.setSelected(false);
        }
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
        if (graphCatalogFileExists()) {
            graphCatalog = readGraphCatalogFile();
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
