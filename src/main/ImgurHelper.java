package main;

import javafx.concurrent.Task;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

class ImgurHelper extends Task<Void>{

    private final List<String> links;
    private final String dir;

    ImgurHelper(List<String> links, String dir){
        this.links = links;
        this.dir = dir;
    }

    @Override
    protected Void call(){
        int maxSize = links.size();
        updateMessage("Progress 0/" + maxSize);
        for (int i = 0; i < maxSize; i++) {
            String link = links.get(i);
            try {
                FileUtils.copyURLToFile(new URL(link), new File(dir + "\\" + FilenameUtils.getName(link)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            updateProgress(i+1, maxSize);
            updateMessage("Progress " + i+1 + "/" + maxSize);
        }
        return null;
    }

    public static JsonArray crawl(String link){
        try{
            HttpURLConnection con = (HttpURLConnection)new URL("https://api.imgur.com/3/album/" + link.substring(link.lastIndexOf("/") + 1) + "/images").openConnection();
            con.setRequestProperty ("Authorization", Files.readAllLines(Paths.get("apiKey.dat")).get(0));
            JsonReader jr = Json.createReader(new InputStreamReader(con.getInputStream()));
            JsonObject json = jr.readObject();
            jr.close();
            return json.getJsonArray("data");
        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }
}