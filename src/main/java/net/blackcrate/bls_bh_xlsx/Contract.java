package net.blackcrate.bls_bh_xlsx;

import java.io.File;
import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public interface Contract {
    interface View {
        // method to build the main scene in view
        Scene buildMainScene(Contract.View.ViewListener listener);

        // method to set save as text in view
        void setSaveAsText(String filepath);

        // method to set number of files in view
        void setProcessNumFiles(int numFiles);

        interface ViewListener {
            // method to handle drag over events in view
            EventHandler<DragEvent> dragOverHandler();

            // method to handle drag dropped events in view
            EventHandler<DragEvent> dragDroppedHandler();

            // method to handle browse files button click in view
            EventHandler<MouseEvent> browseFilesHandler();

            // method to handle browse folders button click in view
            EventHandler<MouseEvent> browseFoldersHandler();

            // method to handle save as button click in view
            EventHandler<MouseEvent> saveAsHandler();

            // method to handle start process button click in view
            EventHandler<MouseEvent> startProcessHandler();
        }

    }

    interface Model {
        // method to get BLS file count
        int getBlsFileCount();

        // method to get XLSX filepath
        File getXlsxFile();

        // method to set potential BLS files to process 
        void setBlsFiles(List<File> blsFiles);

        // methods to set XLSX filepath
        void setXlsxFile(File xlsxFile);
        void setXlsxFile(String xlsxFile);

        // method to parse the bowler histories
        void parseBowlerHistory(Contract.Model.onProcessUpdateListener listener);

        // nested interface to listen for progress updates
        interface onProcessUpdateListener {
            // method called when there is a progress update
            void onProgressUpdate(double progress, String status);
        }
    }

    interface Presenter {
        // method to show the scene
        void curtainUp(Stage stage);
    }
}
