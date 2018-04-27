package be.home.gui.ultratop;

import be.home.gui.common.GUIApplication;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;

public class UltratopGUI extends GUIApplication {

    public static void main(String[] args) {
        launch(args);
    }
    private static final Logger log = loggingConfiguration.getMainLog(UltratopGUI.class);

    @Override
    public void start(Stage primaryStage) {
        initRootLayout(primaryStage);
    }

    public void initRootLayout(Stage primaryStage)  {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            System.out.println(UltratopGUI.class.getResource("/Ultratop.fxml"));
            loader.setLocation(UltratopGUI.class.getResource("/Ultratop.fxml"));
            Pane rootLayout = loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setTitle("Ultratop List");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
