package net.blackcrate.bls_bh_xlsx;

import java.io.File;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    static {
        try (InputStream loggingProperties = App.class.getClassLoader().getResourceAsStream("logging.properties")) {
            LogManager.getLogManager().readConfiguration(loggingProperties);
        } catch (Exception ex) {
            System.out.println("WARNING: Could not load logger properties: " + ex.getLocalizedMessage());
        }
    }

    private static final Logger logger = Logger.getLogger(App.class.getName());

    private final AppView appView;

    public App() {
        appView = new AppView();
    }   

    @Override
    public void start(Stage stage) throws Exception {
        VBox root = appView.buildRootNode();
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/css/app.css");

        stage.setTitle("BLS Bowler History to XLSX");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        if (args.length == 1) {
            File[] blsFiles = new File[] { new File(args[0]) };
            if (blsFiles[0].exists()) {
                if (blsFiles[0].isDirectory()) {
                    blsFiles = blsFiles[0].listFiles((File dir, String name) -> name.toLowerCase().endsWith(".pdf"));
                }
                AppModel appModel = new AppModel();
                appModel.parseBowlerHistory(blsFiles);
            } else {
                logger.severe("The file or directory does not exist, exiting...");
                System.exit(2);
            }
        } else {
            launch();
        }
    }
}
