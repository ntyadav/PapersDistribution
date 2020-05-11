package com.coursework;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class StartWindowController {

    private final MainWindowExceptionHandler mainWindowExceptionHandler = new MainWindowExceptionHandler();
    @FXML
    public Stage thisStage;
    boolean mainWindowClosedIncorrect = false;
    @FXML
    private File papersFile, reviewersFile;
    @FXML
    private Text reviewersFileNameLabel;
    @FXML
    private Text papersFileNameLabel;
    @FXML
    private StackPane reviewersStackPane;
    @FXML
    private StackPane papersStackPane;

    @FXML
    public void setPapersFile(File papersFile) {
        this.papersFile = papersFile;
        papersFileNameLabel.setText(papersFile.getName());
    }

    @FXML
    public void setReviewersFile(File reviewersFile) {
        this.reviewersFile = reviewersFile;
        reviewersFileNameLabel.setText(reviewersFile.getName());
    }

    @FXML
    public void setUp(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        thisStage = stage;
        reviewersStackPane.setOnMouseClicked((e) -> {
            fileChooser.setTitle("Открытие списка рецензентов");
            File file = fileChooser.showOpenDialog(stage);
            if (file != null && isExcelFile(file)) {
                setReviewersFile(file);
            }
        });
        papersStackPane.setOnMouseClicked((e) -> {
            fileChooser.setTitle("Открытие списка работ студентов");
            File file = fileChooser.showOpenDialog(stage);
            if (file != null && isExcelFile(file)) {
                setPapersFile(file);
            }
        });
    }

    @FXML
    void initialize() {
    }

    private Workbook getWorkbookFromFile(File file) throws IOException, InvalidFormatException {
        if (AuxiliaryControllerMethods.getExtension(file).equals(".xlsx")) {
            try (Workbook workbook = new XSSFWorkbook(file)) {
                return workbook;
            }
        } else if (AuxiliaryControllerMethods.getExtension(file).equals(".xls")) {
            try (Workbook workbook = new HSSFWorkbook(POIFSFileSystem.create(file))) {
                return workbook;
            }
        }
        return null;
    }

    @FXML
    private void handleExcelFileDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            if (isExcelFile(event.getDragboard().getFiles().get(0))) {
                event.acceptTransferModes(TransferMode.ANY);
            } else {
                event.acceptTransferModes(TransferMode.NONE);
            }
        }
    }

    @FXML
    private void handleReviewersDragDropped(DragEvent event) {
        List<File> files = event.getDragboard().getFiles();
        File file = files.get(0);
        if (isExcelFile(file)) {
            setReviewersFile(file);
        }
    }

    @FXML
    private void handlePapersDragDropped(DragEvent event) {
        List<File> files = event.getDragboard().getFiles();
        File file = files.get(0);
        if (isExcelFile(file)) {
            setPapersFile(file);
        }
    }

    private boolean isExcelFile(File file) {
        String extension = AuxiliaryControllerMethods.getExtension(file);
        return extension.equals(".xlsx") || extension.equals(".xls");
    }

    @FXML
    private void loadButtonClicked() {
        if (reviewersFile == null || papersFile == null) {
            AuxiliaryControllerMethods.showAlertWindow("Выберите файлы со списками!", null, Alert.AlertType.WARNING);
            return;
        }
        try {
            thisStage.hide();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("app_design/MainWindow.fxml"));
            loader.load();
            Parent root = loader.getRoot();
            Stage mainWindowStage = new Stage();
            mainWindowStage.setTitle("Распределение работ студентов");
            mainWindowStage.setScene(new Scene(root));
            mainWindowStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("assets/icon.png")));
            MainWindowController mainWindowController = loader.getController();
            mainWindowController.setMainWindowExceptionHandler(mainWindowExceptionHandler);
            mainWindowController.loadReviewersAndPapers(getWorkbookFromFile(reviewersFile), getWorkbookFromFile(papersFile));
            mainWindowController.setUp(thisStage, mainWindowStage);
            if (!mainWindowClosedIncorrect) {
                mainWindowStage.showAndWait();
            }
        } catch (FileNotFoundException exception) {
            thisStage.show();
            String title = null,
                    header = "Ошибка при попытке открытия Excel-файлов",
                    content = "Убедитесь, что файлы существуют и не открыты другими приложениями";
            AuxiliaryControllerMethods.showAlertWindow(header, content, Alert.AlertType.WARNING);
        } catch (Exception e) {
            e.printStackTrace();
            mainWindowExceptionHandler.run();
        } finally {
            mainWindowClosedIncorrect = false;
        }
    }

    class MainWindowExceptionHandler implements Runnable {
        @Override
        public void run() {
            mainWindowClosedIncorrect = true;
            thisStage.show();
            String header = "Ошибка при работе с Excel-файлами",
                    content = "Убедитесь, что загруженные списки в Excel-файлах имеют правильный формат";
            AuxiliaryControllerMethods.showAlertWindow(header, content, Alert.AlertType.ERROR);
        }
    }
}
