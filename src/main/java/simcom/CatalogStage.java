package simcom;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

class CatalogStage extends Stage {
    private CatalogStageController catalogStageController;

    CatalogStage(GraphCatalog graphCatalog, boolean selectable) {
        try {
            Stage catalogStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scenes/CatalogStage.fxml"));
            Parent root = fxmlLoader.load();
            this.catalogStageController = fxmlLoader.getController();
            this.catalogStageController.postInitialize(graphCatalog, selectable);
            catalogStage.setScene(new Scene(root));
            catalogStage.initModality(Modality.APPLICATION_MODAL);
            catalogStage.setTitle("Catalog of graphs (" + graphCatalog.size() + " items)");

            // Detect Esc key pressing
            catalogStage.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
                if (KeyCode.ESCAPE == event.getCode()) {
                    catalogStageController.clearResult();
                    catalogStage.close();
                }
            });

            // Detect close window event
            catalogStage.setOnCloseRequest((WindowEvent event) -> {
                catalogStageController.clearResult();
                catalogStage.close();
            });

            catalogStage.setResizable(AuxiliaryUtility.isResizableStage());
            catalogStage.showAndWait();
        } catch (IOException e) {
            Dialogs.exceptionDialog(e);
        }
    }

    boolean[] getResult() {
        return catalogStageController.getResult();
    }

}
