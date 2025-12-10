package com.khorshed.mybank.mybankapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main Application Class
 * Entry point for the My Bank application
 */
public class Main extends Application {

    private static Stage primaryStageObj;

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStageObj = primaryStage;

            // Load Dashboard FXML
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Dashboard.fxml"));

            // Create scene
            Scene scene = new Scene(root, 900, 650);

            // Load CSS stylesheet
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            // Set up primary stage
            primaryStage.setTitle("My Bank - Bank Account Management System");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Error starting application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Changes the current scene
     * @param fxml The FXML file name (without path)
     * @throws Exception if loading fails
     */
    public static void changeScene(String fxml) throws Exception {
        Parent root = FXMLLoader.load(Main.class.getResource("/fxml/" + fxml));
        Scene scene = new Scene(root, 900, 650);
        scene.getStylesheets().add(Main.class.getResource("/css/style.css").toExternalForm());
        primaryStageObj.setScene(scene);
    }

    /**
     * Main method
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
