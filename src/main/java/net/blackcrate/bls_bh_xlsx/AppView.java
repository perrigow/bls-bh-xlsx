package net.blackcrate.bls_bh_xlsx;

import java.util.logging.Logger;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AppView {

    private static final Logger logger = Logger.getLogger(AppView.class.getName());

    public AppView() {
    }

    private VBox buildDragDropNode() {
        Label dragDropLabel = new Label("Drag&Drop Files or a Folder");
        dragDropLabel.setFocusTraversable(true);
        dragDropLabel.setId("dragDropLabel");
        
        Button browseFiles = new Button("Browse Files");
        browseFiles.getStyleClass().add("dragDropButton");
        //browseFiles.setOnMouseClicked();

        Button browseFolder = new Button("Browse Folders");
        browseFolder.getStyleClass().add("dragDropButton");
        //browseFolder.setOnMouseClicked();
        
        HBox dragDropButtons = new HBox(browseFiles, browseFolder);
        dragDropButtons.setId("dragDropButtons");
        
        VBox dragDrop = new VBox(dragDropLabel, dragDropButtons);
        dragDrop.setId("dragDrop");
        //dragDrop.setOnDragOver();
        //dragDrop.setOnDragDropped();

        return dragDrop;
    }

    private HBox buildSaveAsNode() {
        Button saveAsButton = new Button("Save as...");
        saveAsButton.setId("saveAsButton");
        saveAsButton.setDisable(true);
        //set disabled to false if files to process
        //saveAsButton.setOnMouseClicked()
        
        TextField saveAsText = new TextField();
        saveAsText.setId("saveAsText");
        saveAsText.setDisable(true);
        //update text if files to process
        
        HBox saveAs = new HBox(saveAsButton, saveAsText);
        saveAs.setId("saveAs");

        return saveAs;
    }

    private HBox buildProcessNode() {
        Label processNumFiles = new Label("Files to process: 0");
        processNumFiles.setId("processNumFiles");
        //update text if files to process

        Button processStart = new Button("Start!");
        processStart.setId("processStart");
        processStart.setDisable(true);
        //set disabled false if files to process

        HBox process = new HBox(processNumFiles, processStart);
        process.setId("process");

        return process;
    }

    public VBox buildRootNode() {
        VBox root = new VBox(buildDragDropNode(), buildSaveAsNode(), buildProcessNode());
        root.setId("root");

        return root;
    }
}
