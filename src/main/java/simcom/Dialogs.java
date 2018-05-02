package simcom;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.io.PrintWriter;
import java.io.StringWriter;

class Dialogs {

    private static void genericSimpleDialog(Alert.AlertType alertType, String headerText, String contentText) {
        final Alert alert;

        switch (alertType) {
            case INFORMATION:
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                break;

            case CONFIRMATION:
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                break;

            case WARNING:
                alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                break;

            case ERROR:
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                break;

            default:
                alert = new Alert(Alert.AlertType.NONE);
                alert.setTitle("");
        }

        if (headerText != null)
            alert.setHeaderText(headerText);

        if (contentText != null)
            alert.setContentText(contentText);

        alert.showAndWait();
    }

    private static boolean genericConfirmationDialog(String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");

        if (headerText != null)
            alert.setHeaderText(headerText);

        if (contentText != null)
            alert.setContentText(contentText);

        return alert.showAndWait().orElse(null) == ButtonType.OK;
    }

    private static void genericInformationDialog(String headerText, String contentText) {
        genericSimpleDialog(Alert.AlertType.INFORMATION, headerText, contentText);
    }

    private static void genericWarningDialog(String headerText, String contentText) {
        genericSimpleDialog(Alert.AlertType.WARNING, headerText, contentText);
    }

    private static void genericErrorDialog(String headerText, String contentText) {
        genericSimpleDialog(Alert.AlertType.ERROR, headerText, contentText);
    }

    static void exceptionDialog(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception");
        alert.setHeaderText("Some problem occurred...");
        alert.setContentText(e.getMessage());

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);

        // Java bug, ugly hack thanks to user4746254 from stackoverflow.com
        alert.getDialogPane().expandedProperty().addListener((l) ->
                Platform.runLater(() -> {
                    alert.getDialogPane().requestLayout();
                    Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                    stage.sizeToScene();
                })
        );

        alert.showAndWait();
    }


/* ------------------------------------------------------------------------------------------------------------------ */


    static boolean quitConfirmationDialog() {
        return genericConfirmationDialog(null, "Are you sure you want to quit this program?");
    }

    static boolean deleteContentOfCatalogConfirmationDialog() {
        return genericConfirmationDialog(
                "Are you sure you want to delete content of catalog?",
                "All stored graphs will be lost!");
    }

    static boolean removeAllGraphsFromComparisonConfirmationDialog() {
        return genericConfirmationDialog(
                "Are you sure to remove graphs?",
                "All graphs prepared for comparison will be removed!");
    }

    static void aboutInformationDialog() {
        genericInformationDialog("SimCom - Similarity Comparator", String.format("%s%n%s%n%s",
                "Copyright \u00a9 2018 Petr Vondrus",
                "Czech Technical University in Prague",
                "Faculty of Electrical Engineering"
        ));
    }

    static void sameGraphInCatalogInformationDialog(String graphName) {
        genericInformationDialog("The same graph is already in the catalog.", "Graph name: " + graphName);
    }

    static void catalogIsEmptyInformationDialog() {
        genericInformationDialog("The catalog is empty for now.", null);
    }

    static void noDotFileFoundInformationDialog(String directory) {
        genericInformationDialog("No DOT file found in selected directory.", directory);
    }

    static void cannotAddGraphCollectionErrorDialog() {
        genericErrorDialog("Cannot add selected graph to the catalog.", "Collection was not changed.");
    }

    static void cannotAddGraphComponentErrorDialog() {
        genericErrorDialog("Cannot add selected graph to the catalog.", "Graph contains more than one component.");
    }

    static void dotExecutableNotFoundErrorDialog() {
        genericErrorDialog("DOT executable file not found!", null);
    }

    static void ioErrorDialog() {
        genericErrorDialog("Some I/O error occurs!", null);
    }
}
