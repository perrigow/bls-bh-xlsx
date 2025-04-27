package net.blackcrate.bls_bh_xlsx;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AppView implements Contract.View {

    private static final Logger logger = Logger.getLogger(AppView.class.getName());

    private Button saveAsButton;
    private TextField saveAsText;

    private Button processStart;
    private Label processNumFiles;

    private Dialog<Boolean> progressDialog;
    private ProgressBar progressBar;
    private Label progressPercent;
    private Label progressStatus;

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
    public void showProgressDialog(Stage stage) {
        progressBar = new ProgressBar();
        progressPercent = new Label("0%");
        progressStatus = new Label("Warming up...");
        progressStatus.setId("progressStatus");

        HBox progressBarNode = new HBox(5);
        progressBarNode.setId("progressBarNode");
        progressBarNode.getChildren().addAll(progressBar, progressPercent);

        VBox progress = new VBox();
        progress.setId("progress");
        progress.getChildren().addAll(progressBarNode, progressStatus);

        progressDialog = new Dialog<>();
        DialogPane dp = progressDialog.getDialogPane();
        dp.getChildren().clear();
        dp.setId("processingProgress");
        dp.setContent(progress);

        progressDialog.setTitle("Processing progress");
        progressDialog.setHeaderText(null);
        progressDialog.initStyle(StageStyle.UNDECORATED);
        progressDialog.initOwner(stage);
        progressDialog.show();
    }

    @Override
    public void updateProgressDialog(double progress, String status) {
        if (progress >= 0.01) {
            progressBar.setProgress(progress);
        }
        progressPercent.setText((int) Math.floor(progress*100)+"%");
        progressStatus.setText(status);
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.setResult(Boolean.TRUE);
            progressDialog.close();
            progressDialog = null;
        }
    }

    @Override
    public void showProcessingComplete(Stage stage) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().setId("processingComplete");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.setContentText("BLS Bowler History stats converted to Excel (XLSX)!");
        dialog.setTitle("Processing Complete");
        dialog.setHeaderText(null);
        dialog.initOwner(stage);
        dialog.showAndWait();
    }
}
