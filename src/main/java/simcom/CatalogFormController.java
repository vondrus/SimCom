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
    private boolean mainFormInitSide;

    @FXML
    private TilePane tilePane1;

    private void buttonsOnClickAction(ActionEvent event, int buttonIndex) {
        GraphCatalog graphCatalog = mainFormController.getGraphCatalog();
        GraphCatalogItem item = graphCatalog.getItems().get(buttonIndex);
        CustomGraph graph = item.getGraph();

        if (mainFormInitSide) {
            CustomGraph rightGraph = mainFormController.getRightGraph();
            if ((rightGraph == null) || (! rightGraph.getName().equals(graph.getName()))) {
                mainFormController.setRightGraph(graph);
                mainFormController.setRightSideGraph(item);
                ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
            }
            else {
                Dialogs.graphIsLoadedInformationDialog();
            }
        }
        else {
            CustomGraph leftGraph = mainFormController.getLeftGraph();
            if ((leftGraph == null) || (! leftGraph.getName().equals(graph.getName()))) {
                mainFormController.setLeftGraph(graph);
                mainFormController.setLeftSideGraph(item);
                ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
            }
            else {
                Dialogs.graphIsLoadedInformationDialog();
            }
        }
    }

    void postInitialize(MainFormController mainFormController, boolean mainFormInitSide) {
        this.mainFormController = mainFormController;
        this.mainFormInitSide = mainFormInitSide;

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
            tilePane1.getChildren().add(buttons[i]);
        }
    }

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle rb) { }
}
