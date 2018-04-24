package main;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.List;

class ProgressWindow extends Stage {

    private final ImgurHelper downloader;

    ProgressWindow(List<String> links, String dir){
        super(StageStyle.UTILITY);
        setTitle("Progress");
        BorderPane rootWindow = new BorderPane();
        VBox contentWindow = new VBox();
        contentWindow.setSpacing(10);
        contentWindow.setPadding(new Insets(10));
        Label progressText = new Label("Current progress: 0/" + links.size());
        downloader = new ImgurHelper(links, dir);
        progressText.textProperty().bind(downloader.messageProperty());
        ProgressBar bar = new ProgressBar(0);
        bar.progressProperty().bind(downloader.progressProperty());
        contentWindow.getChildren().addAll(progressText, bar);
        rootWindow.setCenter(contentWindow);
        setScene(new Scene(rootWindow));
    }

    public void start(){
        show();
        new Thread(downloader).start();
    }


}
