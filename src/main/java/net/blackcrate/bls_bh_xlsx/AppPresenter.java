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

public class AppPresenter implements Contract.Presenter, Contract.View.ViewListener, Contract.Model.onProcessUpdateListener {

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

    private void resetModelView() {
        appModel.setBlsFiles(new ArrayList<>());
        appView.setProcessNumFiles(0);
        appView.setSaveAsText("");
    }

    private void updateModelView(List<File> blsFiles) {
        if (blsFiles.isEmpty()) {
            logger.warning("Nothing to process");
            resetModelView();
            return;
        }

        appModel.setBlsFiles(blsFiles);
        appView.setSaveAsText(appModel.getXlsxFile().getName());
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
            List<File> selectedFiles = new ArrayList<>();
            for (File file : event.getDragboard().getFiles()) {
                if (file.isDirectory()) {
                    selectedFiles.addAll(getDirectoryFiles(file));
                    continue;
                }
                if (file.getName().toLowerCase().endsWith(".pdf")) {
                    selectedFiles.add(file);
                }
            }
            updateModelView(selectedFiles);
        };
    }

    @Override
    public EventHandler<MouseEvent> browseFilesHandler() {
        return (MouseEvent event) -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Choose BLS PDFs");
            chooser.getExtensionFilters().add(new ExtensionFilter("PDF Documents (*.pdf)", "*.pdf"));
            List<File> selectedFiles = chooser.showOpenMultipleDialog(stage);
            if (selectedFiles != null) {
                updateModelView(selectedFiles);
            }
        };
    }

    @Override
    public EventHandler<MouseEvent> browseFoldersHandler() {
        return (MouseEvent event) -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Choose BLS PDFs Folder");
            File directory = chooser.showDialog(stage);
            if (directory != null) {
                updateModelView(getDirectoryFiles(directory));
            }
        };
    }

    @Override
    public EventHandler<MouseEvent> saveAsHandler() {
        return (MouseEvent event) -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save BLS XLSX");
            chooser.setInitialDirectory(appModel.getXlsxFile().getParentFile());
            File xlsxFile = chooser.showSaveDialog(stage);
            if (xlsxFile != null) {
                appModel.setXlsxFile(xlsxFile);
                appView.setSaveAsText(xlsxFile.getName());
            }
        };
    }

    @Override
    public EventHandler<MouseEvent> startProcessHandler() {
        return (MouseEvent event) -> {
            // add code to show progress and update progress
            appModel.parseBowlerHistory(this);
            // alert to notify user processing has been completed
            resetModelView();
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
