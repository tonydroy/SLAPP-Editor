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

import javafx.scene.Scene;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import slapp.editor.EditorMain;

import java.io.IOException;
import slapp.editor.EditorAlerts;

public class MediaViewer {

    Player player;
    Stage stage;



    public void play(String file, double width, double height) {
        try {
            player = new Player(file);
        }
        catch (IOException | SecurityException e) {
            EditorAlerts.showSimpleAlert("Cannot Connect", "Cannot connect to www.slappservices.net.");
            return;
        }

        Scene scene = new Scene(player, width, height, Color.BLACK);

        stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("SLAPP Video Help");
        stage.initModality(Modality.NONE);
        stage.getIcons().addAll(EditorMain.icons);
        stage.initOwner(EditorMain.mainStage);
        stage.setX(EditorMain.mainStage.getX() + EditorMain.mainStage.getWidth());
        stage.setY(EditorMain.mainStage.getY() + 200);

        MediaView viewer = player.getView();
        viewer.fitWidthProperty().bind(stage.widthProperty() );
        viewer.fitHeightProperty().bind(stage.heightProperty());

        stage.setOnCloseRequest(e -> {
            stopPlay();
        });

        stage.show();
    }

    public void stopPlay() {
        if (player != null) player.getPlayer().stop();
        if (stage != null) stage.close();
    }





}
