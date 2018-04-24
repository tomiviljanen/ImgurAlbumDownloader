package main;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import javax.json.JsonArray;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Frame extends Application {

    public static void main(String args[]) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage){
        VBox content = new VBox();
        content.setPadding(new Insets(10));
        content.setSpacing(10);
        HBox dir = new HBox();
        HBox download = new HBox();
        HBox link = new HBox();
            dir.setSpacing(10);
            Button chooseDir = new Button("Choose directory");
            TextField dirText = new TextField();
            chooseDir.setOnAction(e -> {
                    DirectoryChooser dc = new DirectoryChooser();
                    dc.setTitle("Choose Directory");
                    File file = dc.showDialog(primaryStage);
                    if(file != null)dirText.setText(file.getAbsolutePath());
            });
            dir.getChildren().addAll(chooseDir, dirText);

            link.setSpacing(10);
            TextField linkText = new TextField();
            HBox.setHgrow(linkText, Priority.ALWAYS);
            linkText.textProperty().addListener(e -> download.setDisable(!linkText.getText().contains("imgur.com/")));
            link.getChildren().addAll(new Label("Link:"), linkText);

            download.setDisable(true);
            download.setSpacing(10);
            Button chooseImages = new Button("Choose images");
            chooseImages.setOnAction(e -> new ImagePanel(linkText.getText(), dirText.getText()));
            Button downloadAll = new Button("Download all");
            downloadAll.setOnAction(e -> {
                List<String> links = new ArrayList<>();
                JsonArray jsonArray = ImgurHelper.crawl(linkText.getText());
                for(int i = 0; i< Objects.requireNonNull(jsonArray).size(); i++){
                    links.add(jsonArray.getJsonObject(i).getString("link"));
                }
                ProgressWindow progress = new ProgressWindow(links, dirText.getText());
                progress.start();
            });
            download.getChildren().addAll(chooseImages, downloadAll);
          content.getChildren().addAll(dir, link, download);
          primaryStage.setScene(new Scene(content));
          primaryStage.setTitle("Imgur Album Downloader");
          primaryStage.show();
    }
}