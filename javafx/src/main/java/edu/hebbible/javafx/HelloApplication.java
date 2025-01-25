package edu.hebbible.javafx;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle("Hello!");

        TextField b = new TextField();
        Label l = new Label("");

        EventHandler<ActionEvent> event = e -> {
            String response;
            try {
                HttpClient client = HttpClient.newHttpClient();
                java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                        // .GET()
                        .uri(URI.create("http://localhost:9000/psukim/" + b.getText()))
                        .header("Content-Type", "application/json")
                        .build();
                response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString()).body();
            }
            catch (Exception ex) {
                response = ex.toString();
            }
            l.setText(response);
        };
        b.setOnAction(event);

        StackPane r = new StackPane();

        r.getChildren().add(b);
        r.getChildren().add(l);

        Scene sc = new Scene(r, 200, 200);
        stage.setScene(sc);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}