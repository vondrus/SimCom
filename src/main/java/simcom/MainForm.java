package simcom;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.WindowEvent;
import javafx.application.Application;

import java.io.File;


public class MainForm extends Application {

    @Override
    public void start(final Stage primaryStage) throws Exception {
        File dotExecFile = new File(GlobalConstants.DOT_EXEC_FILE_PATH);
        if (dotExecFile.exists()) {
            primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/scenes/MainForm.fxml"))));
            primaryStage.setTitle(GlobalConstants.PRIMARY_STAGE_TITLE);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(750);
            primaryStage.setOnCloseRequest((WindowEvent event) -> {
                event.consume();
                if (Dialogs.quitConfirmationDialog())
                    System.exit(0);
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
