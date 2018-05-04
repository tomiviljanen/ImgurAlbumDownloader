package main;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class ImagePanel {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 600;

   ImagePanel(String urlToCrawl, String dir){

       List<SelectableImage> images = new ArrayList<>();
       VBox root = new VBox();
        ScrollPane imagePane = new ScrollPane();
        imagePane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Choose images");
        VBox content = new VBox();
        stage.widthProperty().addListener((obs, oldVal, newVal) -> images.forEach(e -> e.view.setFitWidth(newVal.intValue() - 40)));

        JsonArray jsonArray = ImgurHelper.crawl(urlToCrawl);
        int size = jsonArray != null ? jsonArray.size() : 0;
        for(int i = 0; i< size; i++){
            SelectableImage image = new SelectableImage(jsonArray.getJsonObject(i));
            images.add(image);
            content.getChildren().add(image);
        }
        Button download = new Button("Download");
        download.setOnAction(e -> {
            List<String> links = images.stream().filter(image -> image.isSelected).map(image -> image.link).collect(Collectors.toList());
            Frame.download(links, dir);
        });
        download.setDefaultButton(true);
        download.setMaxWidth(Double.MAX_VALUE);
        imagePane.setContent(content);
        root.getChildren().addAll(imagePane, download);
        stage.setScene(new Scene(root, WIDTH, HEIGHT));
        stage.show();
    }

   class SelectableImage extends HBox{
       private boolean isSelected = true;
       private final String link;
       private final ImageView view;

       SelectableImage(JsonObject obj){
           super();
           link = obj.getString("link");
           view = new ImageView(new Image(obj.getBoolean("animated") ? link : link + "s"));
           view.setFitWidth(WIDTH - 15);
           view.setPreserveRatio(true);
           setStyle("-fx-border-width: 3; -fx-border-color: green");
           view.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
               isSelected ^= true;
               setStyle( "-fx-border-color: " + (isSelected ? "green;" : "TRANSPARENT;"));
           });
           getChildren().add(view);
       }
   }
}
