package net.blackcrate.bls_bh_xlsx;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javafx.application.Application;
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

    private final Contract.View appView;
    private final Contract.Model appModel;
    private final Contract.Presenter appPresenter;

    public App() {
        appView = new AppView();
        appModel = new AppModel();
        appPresenter = new AppPresenter(appView, appModel);
    }   

    @Override
    public void start(Stage stage) throws Exception {
        appPresenter.curtainUp(stage);
    }

    public static void main(String[] args) {
        if (args.length == 1) {
            logger.info("Run command line processing");
            List<File> blsFiles = new ArrayList<>();
            for (String arg : args) {
                File blsFile = new File(arg);
                if (blsFile.exists()) {
                    if (blsFile.isDirectory()) {
                        blsFiles.addAll(Arrays.asList(
                            blsFile.listFiles((File dir, String name) -> name.toLowerCase().endsWith(".pdf"))
                        ));
                        continue;
                    }
                    if (blsFile.getName().toLowerCase().endsWith(".pdf")) {
                        blsFiles.add(blsFile);
                    }
                    AppModel appModel = new AppModel();
                    appModel.setBlsFiles(blsFiles);
                    appModel.parseBowlerHistory(null);
                }
            }        
        } else {
            logger.info("Launch GUI processing");
            launch();
        }
    }
}
