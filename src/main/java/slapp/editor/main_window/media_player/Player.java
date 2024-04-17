package slapp.editor.main_window.media_player;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import slapp.editor.EditorAlerts;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Player extends BorderPane {
    Media media;
    MediaPlayer player;
    MediaView view;
    Pane mpane;
    MediaBar bar;

    public Player(String file) throws SecurityException, IOException  { // Default constructor

        try {
            if (InetAddress.getByName("www.slappservices.net").isReachable(2000)) {
                media = new Media(file);
                System.out.println(media);
            }
        }
        catch (SecurityException | IOException e ) {
            throw e;
        }

        player = new MediaPlayer(media);
        view = new MediaView(player);
        mpane = new Pane();
        mpane.getChildren().add(view); // Calling the function getChildren



        // inorder to add the view
        setCenter(mpane);
        bar = new MediaBar(player); // Passing the player to MediaBar
        setBottom(bar); // Setting the MediaBar at bottom
        setStyle("-fx-background-color:#bfc2c7"); // Adding color to the mediabar


        player.setOnReady(new Runnable() {
            @Override
            public void run() {
                double seconds = media.getDuration().toSeconds();
                int hours = (int) (seconds/3600);
                int min = (int) ((seconds % 3600) / 60);
                int sec = (int) (seconds % 60);
                String timeString = String.format("%02d:%02d:%02d ", hours, min, sec);
                bar.getTotalTime().setText(timeString);
            }
        });


        player.play(); // Making the video play
    }

    public MediaView getView() {     return view;  }

    public MediaPlayer getPlayer() {
        return player;
    }
}
