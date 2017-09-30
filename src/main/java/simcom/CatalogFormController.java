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
    private MainFormController mainFormController;
    private int mainFormInitMode;

    @FXML
    private TilePane tilePane;

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

    private void buttonsOnClickAction(ActionEvent event, int buttonIndex) {

        switch (mainFormInitMode) {
            case CatalogForm.MENU_ITEM_MODE:
                switch (Dialogs.sideOfGraphInsertionConfirmationDialog()) {
                    case 2:
                        insertToLeftSide(event, buttonIndex);
                        break;
                    case 3:
                        insertToRightSide(event, buttonIndex);
                        break;
                }
                break;

            case CatalogForm.LEFT_SIDE_CLICK_MODE:
                insertToLeftSide(event, buttonIndex);
                break;

            case CatalogForm.RIGHT_SIDE_CLICK_MODE:
                insertToRightSide(event, buttonIndex);
                break;
        }
    }

    void postInitialize(MainFormController mainFormController, int mainFormInitMode) {
        this.mainFormController = mainFormController;
        this.mainFormInitMode = mainFormInitMode;

        Button[] buttons = new Button[mainFormController.getGraphCatalog().size()];
        for(int i = 0; i < buttons.length; i++){
            buttons[i] = new Button();

            ImageView imageView = new ImageView(mainFormController.getGraphCatalog().getItems().get(i).getImage());
            imageView.setFitWidth(250);
            imageView.setFitHeight(250);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setCache(true);

            Label title = new Label(' ' + mainFormController.getGraphCatalog().getItems().get(i).getGraph().getName());
            StackPane.setAlignment(title, Pos.TOP_LEFT);
            StackPane stackPane = new StackPane();
            stackPane.getChildren().addAll(imageView, title);

            buttons[i].setGraphic(stackPane);

            final int j = i;
            buttons[i].setOnAction(event -> buttonsOnClickAction(event, j));
            tilePane.getChildren().add(buttons[i]);
        }
    }

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle rb) { }
}
