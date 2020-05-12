package com.coursework;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;



public class Main extends Application {

    @FXML
    public static void main(String[] args) {
        launch(args);
    }

    @FXML
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("app_design/StartWindow.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Распределение работ конкурса НИРС");
        primaryStage.setScene(new Scene(root, 700, 360));
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("assets/icon.png")));
        primaryStage.show();
        StartWindowController startWindowController = loader.getController();
        startWindowController.setUp(primaryStage);
        //CCS.printTree(null, null);
    }
}