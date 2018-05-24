package SimCom;

import java.nio.file.Files;
import java.nio.file.Paths;

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

        // Is detected operating system supported?
        if (AuxiliaryUtility.isOSTypeSupported()) {

            // Does the DOT application exist?
            if (AuxiliaryUtility.dotExecutableExists()) {

                // Try to create temporary directories
                if (AuxiliaryUtility.makeTemporaryDirectories()) {

                    // Copy CSS file from resources to temporary directory
                    Files.copy(getClass().getResourceAsStream("/styles/" + AuxiliaryUtility.getSummaryCssFilename()),
                            Paths.get(AuxiliaryUtility.getStylesDirectory() + AuxiliaryUtility.getSummaryCssFilename()));

                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scenes/MainStage.fxml"));
                    Parent root = fxmlLoader.load();

                    primaryStage.setScene(new Scene(root));
                    primaryStage.setTitle(AuxiliaryUtility.getApplicationName());

                    primaryStage.setOnCloseRequest((WindowEvent event) -> {
                        event.consume();
                        if (Dialogs.quitConfirmationDialog())
                            Platform.exit();
                    });

                    primaryStage.setResizable(AuxiliaryUtility.isResizableStage());
                    primaryStage.show();
                } else {
                    Dialogs.canNotMakeTemporaryDirectories();
                }
            } else {
                Dialogs.dotExecutableNotFoundErrorDialog();
            }
        } else {
            Dialogs.notSupportedOSType();
        }
    }

    public static void main(String[] args) {
        AuxiliaryUtility.parseCommandLineParameters(args);
        launch(args);
        AuxiliaryUtility.deleteTemporaryDirectories();
    }
}
