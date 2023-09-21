package slapp.editor;

import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EditorAlerts {

    public static void showSimpleAlert(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(header);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(EditorMain.mainStage);
        alert.showAndWait();
    }
}
