package net.blackcrate.bls_bh_xlsx;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class AppPresenter implements Contract.Presenter, Contract.View.ViewListener, Contract.Model.onProgressUpdateListener {

    private static final Logger logger = Logger.getLogger(AppPresenter.class.getName());

    private Stage stage;

    private final Contract.View appView;
    private final Contract.Model appModel;

    public AppPresenter(Contract.View appView, Contract.Model appModel) {
        logger.info("AppPresenter object initialized");
        this.appView = appView;
        this.appModel = appModel;
    }

    private List<File> getDirectoryFiles(File directory) {
        return Arrays.asList(directory.listFiles(
            (File dir, String name) -> name.toLowerCase().endsWith(".pdf")
        ));
    }

    private void updateModelView(List<File> selectedFiles) {
        if (selectedFiles == null || selectedFiles.isEmpty()) {
            logger.warning("No files to process");
            return;
        }

        List<File> blsFiles = new ArrayList<>();
        for (File file : selectedFiles) {
            if (file.isDirectory()) {
                blsFiles.addAll(getDirectoryFiles(file));
                continue;
            }
            if (file.getName().toLowerCase().endsWith(".pdf")) {
                blsFiles.add(file);
            }
        }
        appModel.setBlsFiles(blsFiles);
        appView.setSaveAsText(appModel.getXlsxFile().toString());
        appView.setProcessNumFiles(appModel.getBlsFileCount());
    }

    @Override
    public EventHandler<DragEvent> dragOverHandler() {
        return (DragEvent event) -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.ANY);
            }
        };
    }

    @Override
    public EventHandler<DragEvent> dragDroppedHandler() {
        return (DragEvent event) -> {
            updateModelView(event.getDragboard().getFiles());
        };
    }

    @Override
    public EventHandler<MouseEvent> browseFilesHandler() {
        return (MouseEvent event) -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Choose BLS PDFs");
            chooser.setSelectedExtensionFilter(new ExtensionFilter("PDF Documents (*.pdf)", "*.pdf"));
            updateModelView(chooser.showOpenMultipleDialog(stage));
        };
    }

    @Override
    public EventHandler<MouseEvent> browseFoldersHandler() {
        return (MouseEvent event) -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Choose BLS PDFs Folder");
            updateModelView(List.of(chooser.showDialog(stage)));
        };
    }

    @Override
    public EventHandler<MouseEvent> saveAsHandler() {
        return (MouseEvent event) -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save BLS XLSX");
            File file = chooser.showSaveDialog(stage);
            if (file != null) {
                appModel.setXlsxFile(file);
            }
        };
    }

    @Override
    public EventHandler<MouseEvent> startProcessHandler() {
        return (MouseEvent event) -> {
            // add code to show progress and update progress
            appModel.parseBowlerHistory(this);
        };
    }

    @Override
    public void onProgressUpdate(double progress, String status) {
        logger.log(Level.INFO, "Progress updated to: {0}%", progress * 100);
        logger.log(Level.INFO, "Progress status: {0}", status);
    }

    @Override
    public void curtainUp(Stage stage) {
        this.stage = stage;

        Scene scene = appView.buildMainScene(this);

        stage.setTitle("BLS Bowler History to XLSX");
        //stage.getIcons().add(new Image(("images/logo/logo.png")));
        stage.setResizable(false);
        stage.setScene(scene);

        logger.info("Curtain up and show the stage");
        stage.show();
    }
}
