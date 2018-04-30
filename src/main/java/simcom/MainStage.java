package simcom;

import java.io.File;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.application.Platform;
import javafx.application.Application;

public class MainStage extends Application {

    @Override
    public void start(final Stage primaryStage) throws Exception {
        File dotExecFile = new File("/usr/bin/dot");
        if (dotExecFile.exists()) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scenes/MainStage.fxml"));
            Parent root = fxmlLoader.load();

            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("SimCom - Similarity Comparator");

            primaryStage.setOnCloseRequest((WindowEvent event) -> {
                event.consume();
                if (Dialogs.quitConfirmationDialog())
                    Platform.exit();
            });

            primaryStage.setResizable(AuxiliaryUtility.isResizableStage());
            primaryStage.show();
        }
        else
            Dialogs.dotExecutableNotFoundErrorDialog();
    }

    public static void main(String[] args) {
        AuxiliaryUtility.parseCommandLineParameters(args);
        launch(args);
    }
}
