import javafx.application.Application;
 import javafx.stage.*;
 import javafx.scene.layout.*;
 import javafx.scene.Node;
 import javafx.scene.Scene;
 import javafx.scene.shape.*;
 import javafx.scene.paint.Color;
 import javafx.scene.Cursor;
 import javafx.scene.control.Button;
 import javafx.scene.control.Label;
 import javafx.scene.text.Font;
 import javafx.geometry.Pos;
 import java.util.Random;
 import java.util.HashMap;

public class Mastermind extends Application {
    // Game related
    Color[] colors;
    ColorButton[] current;
    int round = 0;
    static final int NUMBER_OF_ROWS = 12;
    static final int NUMBER_OF_COLUMNS = 4;
    static final Random r = new Random();
    static final Color nextColor = Color.THISTLE;
    static final Color[] colorPool = new Color[]{
        Color.SNOW, Color.YELLOW, Color.ORANGE, Color.SADDLEBROWN,
        Color.RED, Color.DARKMAGENTA, Color.BLUE, Color.GREEN
    };

    // GUI
    BorderPane root;
    GridPane grid;
    Scene scene;
    Stage stage;
    Button nextButton;
    double height;
    double circleRadius;

    public void start(Stage stage) {
        // Sizes
        int padding = NUMBER_OF_ROWS / 4;
        height = Screen.getPrimary().getVisualBounds().getHeight();
        circleRadius = (height / (NUMBER_OF_ROWS + padding)) / 2;

        this.stage = stage;
        colors = setColors();
        grid = opprettSpillebrett();

        nextButton = new Button("Next round");
        nextButton.setPrefWidth(230);
        nextButton.setDisable(true);
        nextButton.setOnAction(e -> nextRound());

        root = new BorderPane();
        root.setStyle("-fx-background-color: #483D8B");
        root.setCenter(grid);
        root.setBottom(new Pane(nextButton));

        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private GridPane opprettSpillebrett() {
        GridPane grid = new GridPane();
        current = new ColorButton[NUMBER_OF_COLUMNS];
        double width = circleRadius * 2 - 1;

        for (int i = 0; i < NUMBER_OF_ROWS; i++) {
            grid.add(new Rectangle(width, width, Color.PAPAYAWHIP), 0, i);
            for (int j = 1; j <= NUMBER_OF_COLUMNS; j++) {
                ColorButton cb = new ColorButton(current[j-1]);
                current[j-1] = cb;
                grid.add(cb, j, i);
            }
        }

        for (ColorButton cb : current) {
            cb.setFill(nextColor);
            cb.setClickable();
        }

        return grid;
    }

    private Color[] setColors() {
        Color[] colors = new Color[NUMBER_OF_COLUMNS];
        for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
            colors[i] = colorPool[r.nextInt(colorPool.length)];
        }
        return colors;
    }

    private void displayColors(ColorButton button) {
        VBox pane = new VBox();
        for (Color c : colorPool) {
            Rectangle r = new Rectangle(70, 70, c);
            r.setOnMouseClicked(e -> selectColor(button, c));
            pane.getChildren().add(r);
            setButtonlike(r);
        }
        root.setRight(pane);
        stage.sizeToScene();
    }

    private void selectColor(ColorButton button, Color c) {
        boolean ready = true;
        for (ColorButton cb : current) {
            if (!cb.clicked) ready = false;
        }
        if (ready) nextButton.setDisable(false);
        root.setRight(null);
        stage.sizeToScene();
        button.setFill(c);
    }

    private void nextRound() {
        StackPane pane = new StackPane(new Rectangle(45, 45, Color.PAPAYAWHIP));
        grid.add(pane, 0, NUMBER_OF_ROWS - ++round);
        int correct = evaluateRound(pane);

        // Prepare next generation
        for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
            current[i].setUnclickable();
            current[i] = current[i].next;
            if (current[i] == null) continue;
            current[i].setClickable();
            current[i].setFill(nextColor);
            setButtonlike(current[i]);
        }

        if (correct == NUMBER_OF_COLUMNS) victorious();
        else if (current[0] == null) failedGame();
        else nextButton.setDisable(true);
    }

    private void victorious() {
        Label text = new Label("CORRECT!");
        text.setFont(new Font("Arial", 35));
        text.setTextFill(Color.LIME);
        VBox pane = new VBox(text);
        pane.setAlignment(Pos.CENTER);
        root.setTop(pane);
        for (ColorButton cb : current) {
            if (cb == null) continue;
            cb.setUnclickable();
            cb.setFill(Color.BLACK);
        }
        nextButton.setText("Start new round");
        nextButton.setOnAction(e -> reset());
        stage.sizeToScene();
    }

    private void failedGame() {
        nextButton.setText("Try again");
        nextButton.setOnAction(e -> reset());

        // Fasit
        Label text = new Label("Incorrect! The colors are:");
        text.setFont(new Font("Arial", 20));
        text.setTextFill(Color.RED);
        HBox correctColors = new HBox();
        for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
            correctColors.getChildren().add(new Circle(circleRadius, colors[i]));
        }
        correctColors.setAlignment(Pos.CENTER);
        root.setTop(new VBox(text, correctColors));
        stage.sizeToScene();
    }

    private void reset() {
        round = 0;
        colors = setColors();
        grid = opprettSpillebrett();
        root.setCenter(grid);
        nextButton.setText("Next round");
        nextButton.setOnAction(e -> nextRound());
        root.setTop(null);
        stage.sizeToScene();
    }

    private int evaluateRound(StackPane stackpane) {
        int correct = 0;
        int almost = 0;
        HashMap<Color, Integer> seen = new HashMap<>();
        for (Color c : colorPool) seen.put(c, 0);

        // Identify correct colors
        for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
            Color color = (Color) current[i].getFill();
            if (color == colors[i]) {
                correct++;
                seen.put(color, seen.get(color) + 1);
            }
        }

        // Identify correct colors in incorrect places
        for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
            Color color = (Color) current[i].getFill();
            if (color != colors[i]) {
                int others = seen.get(color);
                for (int j = 0; j < NUMBER_OF_COLUMNS; j++) {
                    if (colors[j] == color && others-- == 0) {
                        almost++;
                        seen.put(color, seen.get(color) + 1);
                    }
                }
            }
        }

        // Display result on the stackpane
        GridPane results = new GridPane();
        stackpane.getChildren().add(results);
        for (int i = 0; i < correct + almost; i++) {
            Color c = i < correct ? Color.RED : Color.GOLD;
            results.add(new Circle(circleRadius / 2, c), i % 2, i / 2);
        }
        return correct;
    }

    private void setButtonlike(Node node) {
        node.setOnMouseEntered(e -> scene.setCursor(Cursor.HAND));
        node.setOnMouseExited(e -> scene.setCursor(Cursor.DEFAULT));
    }

    private class ColorButton extends Circle {
        ColorButton next;
        boolean clicked = false;

        public ColorButton(ColorButton next) {
            super(circleRadius, Color.BLACK);
            // System.out.println(circleRadius);
            this.next = next;
        }

        public void setClickable() {
            setOnMouseClicked(e -> {
                displayColors(this);
                clicked = true;
            });
            setButtonlike(this);
        }

        public void setUnclickable() {
            setOnMouseClicked(e -> {});
            setOnMouseEntered(e -> scene.setCursor(Cursor.DEFAULT));
        }
    }
}
