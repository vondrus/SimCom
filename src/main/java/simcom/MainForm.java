package simcom;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.application.Platform;
import javafx.application.Application;

import java.io.File;


public class MainForm extends Application {

    @Override
    public void start(final Stage primaryStage) throws Exception {
        File dotExecFile = new File(GlobalConstants.DOT_EXEC_FILE_PATH);
        if (dotExecFile.exists()) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scenes/MainForm.fxml"));
            Parent root = fxmlLoader.load();

            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle(GlobalConstants.PRIMARY_STAGE_TITLE);
            primaryStage.setMinWidth(1200);
            primaryStage.setMinHeight(800);

            primaryStage.setOnCloseRequest((WindowEvent event) -> {
                event.consume();
                if (Dialogs.quitConfirmationDialog())
                    Platform.exit();
            });

            primaryStage.show();
        }
        else
            Dialogs.dotExecutableNotFoundErrorDialog();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
