package net.blackcrate.bls_bh_xlsx;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javafx.event.EventHandler;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class AppController {

    private static final Logger logger = Logger.getLogger(AppController.class.getName());

    public AppController() {
    }

    private List<File> getDirectoryFiles(File directory) {
        return Arrays.asList(directory.listFiles(
            (File dir, String name) -> name.toLowerCase().endsWith(".pdf")
        ));
    }

    public EventHandler<DragEvent> dragOver() {
        return new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (event.getDragboard().hasFiles()) {
                    event.acceptTransferModes(TransferMode.ANY);
                }
            }
        };
    }

    public EventHandler<DragEvent> dragDropped() {
        return new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                List<File> files = new ArrayList<>();
                for (File file : event.getDragboard().getFiles()) {
                    if (file.isDirectory()) {
                        files.addAll(getDirectoryFiles(file));
                        continue;
                    }
                    files.add(file);
                }
                if (!files.isEmpty()) {
                    // do something with files
                }
            }
        };
    }

    public EventHandler<MouseEvent> browseFiles(Stage stage) {
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Choose BLS PDFs");
                chooser.setSelectedExtensionFilter(new ExtensionFilter("PDF Documents (*.pdf)", "*.pdf"));
                List<File> files = chooser.showOpenMultipleDialog(stage);
                if (files != null) {
                    // do something with files
                }
            }
        };
    }

    public EventHandler<MouseEvent> browseFolders(Stage stage) {
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                DirectoryChooser chooser = new DirectoryChooser();
                chooser.setTitle("Choose BLS PDFs Folder");
                File directory = chooser.showDialog(stage);
                if (directory != null) {
                    List<File> files = getDirectoryFiles(directory);
                    if (!files.isEmpty()) {
                        // do something with files
                    }
                }
            }
        };
    }

    public EventHandler<MouseEvent> saveAs(Stage stage) {
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Save BLS XLSX");
                File file = chooser.showSaveDialog(stage);
                if (file != null) {
                    // do something with save file
                }
            }
        };
    }

    public EventHandler<MouseEvent> startProcess() {
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                logger.info("Start processing files");
            }
        };
    }
}
