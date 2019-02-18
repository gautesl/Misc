import javafx.application.Application;
 import javafx.stage.*;
 import javafx.stage.FileChooser.ExtensionFilter;
 import javafx.scene.Scene;
 import javafx.geometry.Pos;
 import javafx.scene.Node;
 import javafx.scene.Cursor;
 import javafx.scene.layout.*;
 import javafx.scene.paint.Paint;
 import javafx.scene.paint.Color;
 import javafx.scene.shape.Rectangle;
 import javafx.scene.shape.StrokeType;
 import javafx.scene.text.TextAlignment;
 import javafx.scene.control.TextField;
 import javafx.scene.control.Button;
 import javafx.scene.control.Label;
 import javafx.scene.control.Control;
 import javafx.scene.control.RadioButton;
 import javafx.scene.control.ToggleGroup;
 import javafx.scene.layout.Region;
 import javafx.geometry.Rectangle2D;
 import javafx.geometry.Insets;
 import javafx.scene.input.KeyEvent;
 import javafx.scene.input.KeyCode;
 import java.io.File;
 import java.util.Scanner;
 import java.io.FileNotFoundException;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Set;
 import java.util.Collections;
 import java.util.Random;
 import java.util.ArrayList;
 import javafx.util.Duration;
 import javafx.scene.image.Image;
 import javafx.scene.image.ImageView;
 import javafx.scene.paint.ImagePattern;
 import javafx.animation.Timeline;
 import javafx.animation.KeyFrame;
 import javafx.animation.Animation;
 import javafx.animation.Animation.Status;
 import javafx.concurrent.Task;
 import javafx.application.Platform;
 import javafx.scene.text.Font;

public class Sjakklokke extends Application {
    Stage stage;
    Scene scene;
    BorderPane root, innerRoot;
    Thread klokke;
    public static final int MAX_TID = 6 * 60;
    public static final int MAX_TID_HURTIG = 10 * 60;
    public static final int TID_PER_TREKK = 5;
    public int tid = MAX_TID;
    public int tid2 = MAX_TID;
    int player = 0;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        root = new BorderPane();

        Button normalButton = new Button("Vanlig sjakk");
        normalButton.setPrefWidth(500);
        Button speedButton = new Button("Hurtigsjakk");
        speedButton.setPrefWidth(500);

        normalButton.setOnAction(e -> root.setCenter(normalChess()));
        speedButton.setOnAction(e -> root.setCenter(speedChess()));
        root.setTop(new HBox(normalButton, speedButton));
        root.setLeft(new ImageView(new Image("white_rook.jpg", 200, 550, false, true)));
        root.setRight(new ImageView(new Image("black_rook.jpg", 200, 550, false, true)));
        root.setCenter(normalChess());

        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private BorderPane normalChess() {
        if (klokke != null) klokke.interrupt();
        tid = MAX_TID;

        Label t = new Label("6:00");
        t.setTextFill(Color.BLACK);
        t.setFont(new Font("Arial", 300));

        Button nyTur = new Button("Ny runde");
        nyTur.setMinHeight(200);
        nyTur.setMinWidth(600);
        nyTur.setStyle("-fx-font: 22 arial; -fx-base: #b6e7c9;");

        nyTur.setOnAction(e -> {
            tid = MAX_TID;
            t.setTextFill(Color.BLACK);
            updateTime(t, tid);
        });

        innerRoot = new BorderPane();
        innerRoot.setCenter(new Pane(nyTur));
        innerRoot.setTop(new Pane(t));

        Task<Integer> task = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                while (!isCancelled()) {
                    Platform.runLater(() -> updateTime(t, tid));
                    tid--;

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {break;}
                }

                return 0;
            }
        };

        klokke = new Thread(task);
        klokke.start();
        stage.setOnCloseRequest(e -> klokke.interrupt());

        return innerRoot;
    }

    private BorderPane speedChess() {
        if (klokke != null) klokke.interrupt();
        tid = MAX_TID_HURTIG;
        tid2 = MAX_TID_HURTIG;
        player = 0;

        Label t1 = new Label();
        t1.setFont(new Font("Arial", 150));
        t1.setTextFill(Color.DARKGREEN);
        updateTime(t1, tid);
        Label t2 = new Label();
        t2.setTextFill(Color.BLACK);
        t2.setFont(new Font("Arial", 150));
        VBox top = new VBox(t1, t2);
        updateTime(t2, tid2);
        top.setAlignment(Pos.CENTER);

        Button nyTur = new Button("Ny runde");
        nyTur.setMinHeight(200);
        nyTur.setMinWidth(600);
        nyTur.setStyle("-fx-font: 22 arial; -fx-base: #b6e7c9;");

        nyTur.setOnAction(e -> {
            if (player % 2 == 0) {
                tid += TID_PER_TREKK;
                updateTime(t1, tid);
                t2.setTextFill(Color.DARKGREEN);
                t1.setTextFill(Color.BLACK);
            }
            else {
                tid2 += TID_PER_TREKK;
                updateTime(t2, tid2);
                t1.setTextFill(Color.DARKGREEN);
                t2.setTextFill(Color.BLACK);
            }
            player++;
        });

        innerRoot = new BorderPane();
        innerRoot.setCenter(new Pane(nyTur));
        innerRoot.setTop(top);

        Task<Integer> task = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                while (!isCancelled()) {
                    if (player % 2 == 0) {
                        Platform.runLater(() -> updateTime(t1, tid));
                        tid--;
                    }
                    else {
                        Platform.runLater(() -> updateTime(t2, tid2));
                        tid2--;
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {break;}
                }

                return 0;
            }
        };

        klokke = new Thread(task);
        klokke.start();
        stage.setOnCloseRequest(e -> klokke.interrupt());

        return innerRoot;
    }

    public void updateTime(Label t, int tid) {
        if (tid < 0) {
            t.setTextFill(Color.RED);
            t.setText("------");
        }
        else {
            t.setText(String.format("%d:%02d", tid / 60, tid % 60));
        }
    }
}
