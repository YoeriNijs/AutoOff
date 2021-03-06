package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {

    static final int APP_WIDTH = 800;
    static final int APP_HEIGHT = 600;
    private static final String APP_TITLE = "AutoOff";

    @Override
    public void start(Stage stage) {
        stage.setTitle(APP_TITLE);
        stage.setScene(new Scene(createGridPane(), APP_WIDTH, APP_HEIGHT));
        stage.show();
    }

    private GridPane createGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(new MainForm().getForm(), 1, 1);
        return gridPane;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
