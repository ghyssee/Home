package be.home.gui.common;

import javafx.scene.control.Alert;

public class GUIUtils {

    public static void alert(String title, String message, Alert.AlertType alertType){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        /*
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                System.out.println("Pressed OK.");
            }
        });*/
    }

    public static void errorAlert(String title, String message){
        alert(title, message, Alert.AlertType.ERROR);
    }
}
