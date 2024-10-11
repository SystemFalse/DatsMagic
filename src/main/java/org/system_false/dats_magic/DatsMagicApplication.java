package org.system_false.dats_magic;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DatsMagicApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("DatsMagic");
        stage.setWidth(800);
        stage.setHeight(600);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game-view.fxml"));
        stage.setScene(new Scene(loader.load()));
        DatsMagicController controller = loader.getController();
        controller.setStage(stage);
        stage.show();
    }

    public static void main(String[] args) {
        RequestManager.setToken(args[0]);
        RequestManager.setServer(args[1]);
        launch();
    }
}
