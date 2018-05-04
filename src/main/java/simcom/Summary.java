package simcom;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Summary {
    private FileOutputStream summaryOutputStream;
    private ArrayList<CustomGraph> graphsForComparison;

    public Summary(ArrayList<CustomGraph> graphsForComparison) {
        this.graphsForComparison = graphsForComparison;
        try {
            summaryOutputStream = new FileOutputStream(AuxiliaryUtility.getSummaryHtmlPathname(), false);
        } catch (FileNotFoundException e) {
            Dialogs.exceptionDialog(e);
        }
    }

    private static void saveImageToFile(Image image, String name) {
        File outputFile = new File(AuxiliaryUtility.getImagesDirectory() + name + ".png");
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        try {
            ImageIO.write(bImage, "png", outputFile);
        } catch (IOException e) {
            Dialogs.exceptionDialog(e);
        }
    }

    void build() {
        // I. Opening HTML part ----------------------------------------------
        final String openingHtmlTemplate =
                "<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\">" +
                "<title>SimCom - Summary of calculated similarities of selected graphs</title>" +
                "<link rel=\"stylesheet\" href=\"styles/summary.css?v=1.0\"></head>" +
                "<body><table><tr id=\"headerCell\"><td colspan=\"%d\">" +
                "<div id=\"headerLine1\">Summary of calculated similarities of selected graphs</div>" +
                "<div id=\"headerLine2\">Created %s at %s</div></td></tr>%n";

        final Date date = new Date();
        final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        final int graphsNumber = graphsForComparison.size();
        final String openingHtmlPart = String.format(openingHtmlTemplate,
                graphsNumber + 1,
                dateFormat.format(date),
                timeFormat.format(date)
        );

        // II. Inner HTML part -----------------------------------------------
        StringBuilder innerHtmlPart = new StringBuilder(String.format("<tr id=\"headerCell\"><td></td>%n"));
        for (CustomGraph graph : graphsForComparison) {

            // Save graph picture to file
            try {
                saveImageToFile(graph.getImage(), graph.getName());
            } catch (IOException e) {
                Dialogs.exceptionDialog(e);
            }

            // Create cell of horizontal header (graphs)
            innerHtmlPart.append(String.format("<td><div><img src=\"%s\"></div><div>%s</div></td>%n",
                    "images" + File.separator + graph.getName() + ".png",
                    graph.getName()
            ));
        }
        innerHtmlPart.append(String.format("</tr>%n"));

        // III. Closing HTML part --------------------------------------------
        final String closingHtmlPart = "</table></body></html>";

        // Write all parts to output stream
        try {
            summaryOutputStream.write(openingHtmlPart.getBytes());
            summaryOutputStream.write(innerHtmlPart.toString().getBytes());
            summaryOutputStream.write(closingHtmlPart.getBytes());
            summaryOutputStream.close();
        } catch (IOException e) {
            Dialogs.exceptionDialog(e);
        }
    }
}
