package SimCom;

import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.paint.Color;

public class Console extends TextFlow {

    private static final int SIZE = 100;
    final int TEXT_ATTR_NORMAL = 1;
    final int TEXT_ATTR_RESULT = 2;
    final int TEXT_ATTR_ERROR = 3;

    private void addText(Text text) {
        this.getChildren().add(text);
        int n = this.getChildren().size() - SIZE;
        if (n >= 0) {
            this.getChildren().remove(0, n);
        }
    }

    void println(String text, int textAttr) {
        Text t = new Text(String.format("%s%n", text));
        switch (textAttr) {
            case TEXT_ATTR_NORMAL: t.setFill(Color.BLACK);
                break;
            case TEXT_ATTR_RESULT: t.setFill(Color.BLUE);
                break;
            case TEXT_ATTR_ERROR: t.setFill(Color.RED);
                break;
        }
        addText(t);
    }
}
