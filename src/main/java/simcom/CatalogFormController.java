package simcom;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;


public class CatalogFormController implements Initializable {
    @FXML
    private TilePane tilePane;
    private MainFormController mainFormController;
    private boolean[] graphsSelectedToCompare;

    private void insertToLeftSide(ActionEvent event, int buttonIndex) {
        GraphCatalog graphCatalog = mainFormController.getGraphCatalog();
        GraphCatalogItem item = graphCatalog.getItems().get(buttonIndex);
        CustomGraph graph = item.getGraph();
        CustomGraph leftGraph = mainFormController.getLeftGraph();
        if ((leftGraph == null) || (! leftGraph.getName().equals(graph.getName()))) {
            mainFormController.setLeftGraph(graph);
            mainFormController.setLeftSideGraph(item);
            if (mainFormController.getRightGraph() != null) {
                mainFormController.setDisableMenuItemCompareGraphs(false);
            }
            ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
        }
        else {
            Dialogs.graphIsLoadedInformationDialog();
        }
    }

    private void insertToRightSide(ActionEvent event, int buttonIndex) {
        GraphCatalog graphCatalog = mainFormController.getGraphCatalog();
        GraphCatalogItem item = graphCatalog.getItems().get(buttonIndex);
        CustomGraph graph = item.getGraph();
        CustomGraph rightGraph = mainFormController.getRightGraph();
        if ((rightGraph == null) || (! rightGraph.getName().equals(graph.getName()))) {
            mainFormController.setRightGraph(graph);
            mainFormController.setRightSideGraph(item);
            if (mainFormController.getLeftGraph()!= null) {
                mainFormController.setDisableMenuItemCompareGraphs(false);
            }
            ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
        }
        else {
            Dialogs.graphIsLoadedInformationDialog();
        }
    }

    private void buttonsOnClickAction(int index, StackPane stackPane) {
        if (graphsSelectedToCompare[index]) {
            stackPane.setStyle("-fx-border-color: white");
            graphsSelectedToCompare[index] = false;
        } else {
            stackPane.setStyle("-fx-border-color: cyan");
            graphsSelectedToCompare[index] = true;
        }
    }

    void postInitialize(MainFormController mainFormController) {
        this.mainFormController = mainFormController;
        this.graphsSelectedToCompare = new boolean[mainFormController.getGraphCatalog().size()];

        Button[] buttons = new Button[graphsSelectedToCompare.length];
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new Button();

            ImageView imageView = new ImageView(mainFormController.getGraphCatalog().getItems().get(i).getImage());
            imageView.setFitWidth(280);
            imageView.setFitHeight(280);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setCache(true);

            Label title = new Label(' ' + mainFormController.getGraphCatalog().getItems().get(i).getGraph().getName());
            title.setStyle("-fx-padding: 4");
            StackPane.setAlignment(title, Pos.TOP_LEFT);
            StackPane stackPane = new StackPane();
            stackPane.getChildren().addAll(imageView, title);

            buttons[i].setGraphic(stackPane);
            buttons[i].setId("GraphTile");

            final int j = i;
            final StackPane sp = stackPane;
            buttons[i].setOnAction(event -> buttonsOnClickAction(j, sp));

            tilePane.getChildren().add(buttons[i]);
        }

    }

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle rb) { }
}
