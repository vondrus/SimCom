package simcom;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.fxml.FXMLLoader;

import java.io.IOException;


class CatalogForm {

    CatalogForm(MainFormController mainFormController, boolean mainFormInitSide) {
        try {
            Stage catalogStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scenes/CatalogForm.fxml"));
            Parent root = fxmlLoader.load();
            CatalogFormController catalogFormController = fxmlLoader.getController();
            catalogFormController.postInitialize(mainFormController, mainFormInitSide);
            catalogStage.setScene(new Scene(root));
            catalogStage.initModality(Modality.APPLICATION_MODAL);
            catalogStage.setTitle(GlobalConstants.CATALOG_STAGE_TITLE);
            catalogStage.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
                if (KeyCode.ESCAPE == event.getCode()) {
                    catalogStage.close();
                }
            });
            catalogStage.show();
        } catch (IOException e) {
            Dialogs.exceptionDialog(e);
        }
    }
}
