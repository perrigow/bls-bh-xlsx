package net.blackcrate.bls_bh_xlsx;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AppView implements Contract.View {

    private static final Logger logger = Logger.getLogger(AppView.class.getName());

    private Button saveAsButton;
    private TextField saveAsText;

    private Button processStart;
    private Label processNumFiles;

    public AppView() {
        logger.info("AppView object initialized");
    }

    private VBox buildDragDropNode(ViewListener listener) {
        logger.fine("Building drag and drop node");

        Label dragDropLabel = new Label("Drag&Drop Files or a Folder");
        dragDropLabel.setFocusTraversable(true);
        dragDropLabel.setId("dragDropLabel");
        
        Button browseFiles = new Button("Browse Files");
        browseFiles.getStyleClass().add("dragDropButton");
        browseFiles.setOnMouseClicked(listener.browseFilesHandler());

        Button browseFolder = new Button("Browse Folders");
        browseFolder.getStyleClass().add("dragDropButton");
        browseFolder.setOnMouseClicked(listener.browseFoldersHandler());
        
        HBox dragDropButtons = new HBox(browseFiles, browseFolder);
        dragDropButtons.setId("dragDropButtons");
        
        VBox dragDrop = new VBox(dragDropLabel, dragDropButtons);
        dragDrop.setId("dragDrop");
        dragDrop.setOnDragOver(listener.dragOverHandler());
        dragDrop.setOnDragDropped(listener.dragDroppedHandler());

        return dragDrop;
    }

    private HBox buildSaveAsNode(ViewListener listener) {
        logger.fine("Building save as node");

        saveAsButton = new Button("Save as...");
        saveAsButton.setId("saveAsButton");
        saveAsButton.setDisable(true);
        saveAsButton.setOnMouseClicked(listener.saveAsHandler());
        
        saveAsText = new TextField();
        saveAsText.setId("saveAsText");
        saveAsText.setDisable(true);
        
        HBox saveAs = new HBox(saveAsButton, saveAsText);
        saveAs.setId("saveAs");

        return saveAs;
    }

    private HBox buildProcessNode(ViewListener listener) {
        logger.fine("Building process node");

        processNumFiles = new Label("Files to process: 0");
        processNumFiles.setId("processNumFiles");

        processStart = new Button("Start!");
        processStart.setId("processStart");
        processStart.setDisable(true);
        processStart.setOnMouseClicked(listener.startProcessHandler());

        HBox process = new HBox(processNumFiles, processStart);
        process.setId("process");

        return process;
    }

    @Override
    public void setSaveAsText(String filepath) {
        logger.log(Level.INFO, "Setting save as text to: {0}", filepath);
        saveAsText.setText(filepath);
    }

    @Override
    public void setProcessNumFiles(int numFiles) {
        boolean buttonsDisabled = numFiles <= 0;

        logger.log(Level.INFO, "Setting buttons disabled: {0}", buttonsDisabled);
        saveAsButton.setDisable(buttonsDisabled);
        processStart.setDisable(buttonsDisabled);

        logger.log(Level.INFO, "Setting files to process: {0}", numFiles);
        processNumFiles.setText("Files to process: " + numFiles);
    }

    @Override
    public Scene buildMainScene(ViewListener listener) {
        logger.info("Building main scene");

        VBox root = new VBox(
            buildDragDropNode(listener),
            buildSaveAsNode(listener),
            buildProcessNode(listener)
        );
        root.setId("root");

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/css/app.css");

        return scene;
    }
}
