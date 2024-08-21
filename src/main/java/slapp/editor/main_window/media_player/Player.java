/*
Copyright (c) 2024 Tony Roy

This file is part of the Symbolic Logic APPlication (SLAPP).

SLAPP is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

SLAPP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with SLAPP.  If not, see
<https://www.gnu.org/licenses/>.
 */

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
