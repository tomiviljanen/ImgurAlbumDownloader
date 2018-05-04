package main;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.json.JsonArray;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
            Button chooseDir = new Button("Choose folder");
            TextField dirText = new TextField();
            chooseDir.setOnAction(e -> {
                    File file = new DirectoryChooser().showDialog(primaryStage);
                    if(file != null)dirText.setText(file.getAbsolutePath());
            });
            dir.getChildren().addAll(chooseDir, dirText);

            link.setSpacing(10);
            TextField linkText = new TextField();
            HBox.setHgrow(linkText, Priority.ALWAYS);
            linkText.textProperty().addListener(e -> {
                if(!linkText.getText().contains("imgur.com/")){
                    download.setDisable(true);
                    linkText.setStyle("-fx-background-color: red, -fx-background;");
                }else{
                    download.setDisable(false);
                    linkText.setStyle("-fx-background-color: green, -fx-background;");
                }
            });
            link.getChildren().addAll(new Label("Link:"), linkText);

            download.setDisable(true);
            download.setSpacing(10);
            Button chooseImages = new Button("Choose images");
            chooseImages.setOnAction(e -> new ImagePanel(linkText.getText(), dirText.getText()));
            Button downloadAll = new Button("Download all");
            downloadAll.setOnAction(e -> {
                List<String> links = new ArrayList<>();
                JsonArray jsonArray = ImgurHelper.crawl(linkText.getText());
                int size = jsonArray != null ? jsonArray.size() : 0;
                for(int i = 0; i< size; i++) links.add(jsonArray.getJsonObject(i).getString("link"));
                download(links, dirText.getText());
            });
            download.getChildren().addAll(chooseImages, downloadAll);
          content.getChildren().addAll(dir, link, download);
          Scene scene = new Scene(content);
          scene.getStylesheets().add("main/theme.css");
          primaryStage.setScene(scene);
          primaryStage.setTitle("Imgur Album Downloader");
          primaryStage.show();
    }

    public static void download(List<String> links, String dir){
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.setTitle("Progress");
        VBox content = new VBox();
        content.setSpacing(10);
        content.setPadding(new Insets(10));
        Label progressText = new Label();
        ImgurHelper downloader = new ImgurHelper(links, dir);
        progressText.textProperty().bind(downloader.messageProperty());
        ProgressBar bar = new ProgressBar(0);
        bar.progressProperty().bind(downloader.progressProperty());
        content.getChildren().addAll(progressText, bar);
        stage.setScene(new Scene(content));
        stage.show();
        new Thread(downloader).start();
    }

}