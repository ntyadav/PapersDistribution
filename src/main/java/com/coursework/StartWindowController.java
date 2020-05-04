package com.coursework;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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
    public Stage currentStage;
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
        currentStage = stage;
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

    Workbook getWorkbookFromFile(File file) throws IOException, InvalidFormatException {
        if (getExtension(file).equals(".xlsx")) {
            try (Workbook workbook = new XSSFWorkbook(file)) {
                return workbook;
            }
        } else if (getExtension(file).equals(".xls")) {
            try (Workbook workbook = new HSSFWorkbook(POIFSFileSystem.create(file))) {
                return workbook;
            }
        }
        return null;
    }

    @FXML
    void handleExcelFileDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            if (isExcelFile(event.getDragboard().getFiles().get(0))) {
                event.acceptTransferModes(TransferMode.ANY);
            } else {
                event.acceptTransferModes(TransferMode.NONE);
            }
        }
    }

    @FXML
    void handleReviewersDragDropped(DragEvent event) {
        List<File> files = event.getDragboard().getFiles();
        File file = files.get(0);
        if (isExcelFile(file)) {
            setReviewersFile(file);
        }
    }

    @FXML
    void handlePapersDragDropped(DragEvent event) {
        List<File> files = event.getDragboard().getFiles();
        File file = files.get(0);
        if (isExcelFile(file)) {
            setPapersFile(file);
        }
    }

    boolean isExcelFile(File file) {
        String extension = getExtension(file);
        return extension.equals(".xlsx") || extension.equals(".xls");
    }

    @FXML
    void initialize() {
    }

    private String getExtension(File file) {
        String fileName = file.getName();
        String extension = "";
        int lastDotPosition = fileName.lastIndexOf('.');
        if (lastDotPosition > -1) {
            extension = fileName.substring(lastDotPosition);
        }
        return extension.toLowerCase();
    }

    @FXML
    private void distributeButtonClicked() {
        if (reviewersFile == null || papersFile == null) {
            showAlertWindow("Выберите файлы со списками!", null, Alert.AlertType.WARNING);
            return;
        }
        try {
            currentStage.hide();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("app_design/MainWindow.fxml"));
            loader.load();
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("assets/icon.png")));
            MainWindowController mainWindowController = loader.getController();
            stage.setOnCloseRequest((WindowEvent event) -> {
                currentStage.close();
            });
            mainWindowController.setMainWindowExceptionHandler(mainWindowExceptionHandler);
            mainWindowController.loadReviewersAndPapers(getWorkbookFromFile(reviewersFile), getWorkbookFromFile(papersFile));
            if (!mainWindowClosedIncorrect) {
                stage.showAndWait();
            }
        } catch (FileNotFoundException exception) {
            String title = null,
                    header = "Ошибка при попытке открытия Excel-файлов",
                    content = "Убедитесь, что файлы существуют и не открыты другими приложениями";
            showAlertWindow(header, content, Alert.AlertType.WARNING);
        } catch (Exception e) {
            e.printStackTrace();
            mainWindowExceptionHandler.run();
        } finally {
            mainWindowClosedIncorrect = false;
        }
    }

    private void showAlertWindow(String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(header);
        Label label = new Label(content);
        label.setWrapText(true);
        alert.getDialogPane().setContent(label);
        alert.showAndWait();
    }

    class MainWindowExceptionHandler implements Runnable {

        @Override
        public void run() {
            mainWindowClosedIncorrect = true;
            currentStage.show();
            String header = "Ошибка при работе с Excel-файлами",
                    content = "Убедитесь, что загруженные списки в Excel-файлах имеют правильный формат";
            showAlertWindow(header, content, Alert.AlertType.ERROR);
        }
    }
}
