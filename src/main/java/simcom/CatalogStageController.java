package simcom;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class CatalogStageController implements Initializable {
    @FXML
    private Label label;
    @FXML
    private TilePane tilePane;
    @FXML
    private Button defaultButton;

    private boolean selectable;
    private boolean[] graphsSelectedToCompare;

    boolean[] getResult() {
        return graphsSelectedToCompare;
    }

    void clearResult() {
        graphsSelectedToCompare = null;
    }

    @FXML
    private void defaultButtonOnAction() {
        ((Stage)(tilePane.getScene().getWindow())).close();
    }

    private void buttonsOnClickAction(int index, StackPane stackPane) {
        if (selectable) {
            if (graphsSelectedToCompare[index]) {
                stackPane.setStyle("-fx-border-color: white");
                graphsSelectedToCompare[index] = false;
            } else {
                stackPane.setStyle("-fx-border-color: cyan");
                graphsSelectedToCompare[index] = true;
            }
        }
    }

    void postInitialize(GraphCatalog graphCatalog, boolean selectable) {
        final int IMAGE_VIEW_FIT_WIDTH = 280;
        final int IMAGE_VIEW_FIT_HEIGHT = 280;

        this.graphsSelectedToCompare = new boolean[graphCatalog.size()];
        this.selectable = selectable;

        if (selectable) {
            label.setText("Mark selected graphs by mouse click and then press the button.");
            defaultButton.setText("Add for comparison");
        } else {
            label.setText("To select graphs for comparison choose item Graph in main menu.");
            defaultButton.setText("OK");
        }

        Button[] buttons = new Button[graphsSelectedToCompare.length];
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new Button();

            ImageView imageView = new ImageView(graphCatalog.getItems().get(i).getImage());
            imageView.setFitWidth(IMAGE_VIEW_FIT_WIDTH);
            imageView.setFitHeight(IMAGE_VIEW_FIT_HEIGHT);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setCache(true);
            imageView.setCacheHint(CacheHint.SCALE);


            Label title = new Label(' ' + graphCatalog.getItems().get(i).getGraph().getName());
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

    @Override
    public void initialize(URL url, ResourceBundle rb) { }
}
