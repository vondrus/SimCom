package SimCom;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import javafx.scene.image.Image;
import javafx.embed.swing.SwingFXUtils;
import SimCom.SimhashSimilarity.HashAlgorithm;

public class Summary {
    private final int MAX_RESIZE_IMAGE_WIDTH = 192;
    private final int MAX_RESIZE_IMAGE_HEIGHT = 120;

    private ArrayList<CustomGraph> graphsForComparison;
    private ArrayList<Double> editDistanceResults;
    private ArrayList<Double> simhashResults;
    private FileOutputStream summaryOutputStream;
    private Map<String, String> imageResize;

    public Summary(ArrayList<CustomGraph> graphsForComparison, ArrayList<Double> editDistanceResults, ArrayList<Double> simhashResults) {
        this.editDistanceResults = editDistanceResults;
        this.simhashResults = simhashResults;
        this.graphsForComparison = graphsForComparison;
        this.imageResize = new HashMap<>();
        try {
            summaryOutputStream = new FileOutputStream(AuxiliaryUtility.getSummaryHtmlPathname(), false);
        } catch (FileNotFoundException e) {
            Dialogs.exceptionDialog(e);
        }
    }

    private void saveImageToFile(Image image, String name) {
        final String ID_RESIZE_IMAGE_WIDTH = "id=\"resWidth\"";
        final String ID_RESIZE_IMAGE_HEIGHT = "id=\"resHeight\"";

        File outputFile = new File(AuxiliaryUtility.getImagesDirectory() + name + ".png");
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);

        // Notice graph picture resize type to map
        final int difWidth = bImage.getWidth() - MAX_RESIZE_IMAGE_WIDTH;
        final int difHeight = bImage.getHeight() - MAX_RESIZE_IMAGE_HEIGHT;

        if ((difWidth <= 0) && (difHeight <= 0)) {
            imageResize.put(name, "");
        }
        else if (difWidth >= difHeight) {
            imageResize.put(name, ID_RESIZE_IMAGE_WIDTH);
        }
        else {
            imageResize.put(name, ID_RESIZE_IMAGE_HEIGHT);
        }

        // Save image to file
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
                "<body><table><tr id=\"headerCell1\"><td colspan=\"%d\">" +
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
        StringBuilder innerHtmlPart = new StringBuilder(String.format("<tr id=\"headerCell2\"><td></td>%n"));
        for (CustomGraph graph : graphsForComparison) {

            // Save graph picture to file
            try {
                saveImageToFile(graph.getImage(), graph.getName());
            } catch (IOException e) {
                Dialogs.exceptionDialog(e);
            }

            // Create cell of horizontal header (graphs)
            innerHtmlPart.append(String.format(
                    "<td width=\"%d\" height=\"%d\"><div>" +
                    "<img %s src=\"%s\" /></div><div>%s</div></td>%n",
                    MAX_RESIZE_IMAGE_WIDTH,
                    MAX_RESIZE_IMAGE_HEIGHT,
                    imageResize.get(graph.getName()),
                    "images" + File.separator + graph.getName() + ".png",
                    graph.getName()
            ));

        }
        innerHtmlPart.append(String.format("</tr>%n"));

        // Make body of the summary (row by row)
        HashAlgorithm[] hashAlgorithms = HashAlgorithm.values();
        int numberOfSimhashHashAlgorithms = hashAlgorithms.length;
        double[] meanOfSimhashDifferences = new double[numberOfSimhashHashAlgorithms];
        int resultIndex = 0;

        for (CustomGraph graph1 : graphsForComparison) {

            // Graph picture at first column
            innerHtmlPart.append(String.format(
                    "<tr id=\"headerCell2\">" +
                    "<td width=\"%d\" height=\"%d\"><div>" +
                    "<img %s src=\"%s\" /></div><div>%s</div></td>%n",
                    MAX_RESIZE_IMAGE_WIDTH,
                    MAX_RESIZE_IMAGE_HEIGHT,
                    imageResize.get(graph1.getName()),
                    "images" + File.separator + graph1.getName() + ".png",
                    graph1.getName()
            ));

            // Values of similarities
            for (CustomGraph ignored : graphsForComparison) {
                innerHtmlPart.append("<td>");

                // Edit distance method
                double editDistanceResult = editDistanceResults.get(resultIndex);
                innerHtmlPart.append(String.format(
                                "<div>" +
                                "<div id=\"resultCell\">%.4f</div>" +
                                "<div id=\"resultCell\"></div>" +
                                "</div>",
                        editDistanceResult
                ));

                // Simhash method
                for (int i = 0; i < numberOfSimhashHashAlgorithms; i++) {
                    double simhashResult = simhashResults.get(resultIndex * numberOfSimhashHashAlgorithms + i);
                    double resultsDifference = simhashResult - editDistanceResult;
                    meanOfSimhashDifferences[i] += resultsDifference;
                    innerHtmlPart.append(String.format(
                                    "<div>" +
                                    "<div id=\"resultCell\" style=\"color: %s\">%.4f</div>" +
                                    "<div id=\"resultCell\">(%+.4f)</div>" +
                                    "</div>",
                            hashAlgorithms[i].getSummaryColor(),
                            simhashResult,
                            resultsDifference
                    ));
                }

                resultIndex++;
                innerHtmlPart.append("</td>");
            }
            innerHtmlPart.append(String.format("</tr>%n"));
        }

        // III. Closing HTML part --------------------------------------------
        StringBuilder closingHtmlPart = new StringBuilder(String.format(
                "<tr id=\"legendRow\"><td>Legend:</td><td align=\"left\" colspan=\"%d\">",
                graphsNumber
        ));

        closingHtmlPart.append("<div><div id=\"legendCell\">Edit Distance method</div></div>");

        for (int i = 0; i < numberOfSimhashHashAlgorithms; i++) {
            closingHtmlPart.append(String.format(
                            "<div>" +
                            "<div id=\"legendCell\">SimHash method; hash algorithm: </div>" +
                            "<div id=\"legendCell\" style=\"color: %s\">%s; </div>" +
                            "<div id=\"legendCell\">mean of differences: %+.4f</div>" +
                            "</div>",
                    HashAlgorithm.values()[i].getSummaryColor(),
                    HashAlgorithm.values()[i].getName(),
                    meanOfSimhashDifferences[i] / (graphsNumber * graphsNumber)
            ));
        }

        closingHtmlPart.append(String.format("</td></tr></table><p>HTTP User-Agent: %s</p></body></html>",
                AuxiliaryUtility.getHttpUserAgent()
        ));

        // Write all parts to output stream
        try {
            summaryOutputStream.write(openingHtmlPart.getBytes());
            summaryOutputStream.write(innerHtmlPart.toString().getBytes());
            summaryOutputStream.write(closingHtmlPart.toString().getBytes());
            summaryOutputStream.close();
        } catch (IOException e) {
            Dialogs.exceptionDialog(e);
        }
    }
}
