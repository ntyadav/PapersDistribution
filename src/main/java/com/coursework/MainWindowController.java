package com.coursework;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedList;

public class MainWindowController {

    @FXML
    ScrollPane scrollPane;
    @FXML
    ListView<TextFlow> distributionListView;
    @FXML
    Label promptLabel;
    @FXML
    Stage thisStage;
    @FXML
    Button exportButton;


    BlacklistController blacklistController;

    Distribution distribution;
    Paper movingPaper = null;
    Paper selectedPaper = null;

    private Runnable mainWindowExceptionHandler;

    public void setMainWindowExceptionHandler(Runnable mainWindowExceptionHandler) {
        this.mainWindowExceptionHandler = mainWindowExceptionHandler;
    }

    public void loadReviewersAndPapers(Workbook reviewersWorkbook, Workbook papersWorkbook) {
        try {
            distribution = new Distribution();
            if (!distribution.loadReviewersFromExcelFile(reviewersWorkbook) ||
                    !distribution.loadPapersFromExcelFile(papersWorkbook)) {
                exceptionHandle();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            exceptionHandle();
        }
    }

    public void setUp(Stage startWindowStage, Stage mainWindowStage) {
        thisStage = mainWindowStage;
        mainWindowStage.setMinWidth(700);
        mainWindowStage.setMinHeight(360);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("app_design/Blacklist.fxml"));
            Parent root = loader.load();
            Stage blacklistStage = new Stage();
            blacklistStage.setScene(new Scene(root));
            blacklistController = loader.getController();
            blacklistController.setUp(blacklistStage);
            blacklistStage.setOnHidden((WindowEvent event) -> {
                mainWindowStage.toFront();
            });
        } catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
        mainWindowStage.setOnCloseRequest((WindowEvent event) -> {
            startWindowStage.close();
            blacklistController.getStage().close();
        });
    }

    @FXML
    private synchronized void distributeButtonClicked() {
        if (!distribution.hungarianAlgorithmDistribution()) {
            exceptionHandle();
            return;
        }
        drawDistributionList();
        exportButton.setDisable(false);
    }

    @FXML
    private void exportButtonClicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter1 = new FileChooser.
                ExtensionFilter("Excel file .xlsx", "*.xlsx");
        FileChooser.ExtensionFilter extFilter2 = new FileChooser.
                ExtensionFilter("Excel file .xls", "*.xls");
        fileChooser.getExtensionFilters().add(extFilter1);
        fileChooser.getExtensionFilters().add(extFilter2);
        File file = fileChooser.showSaveDialog(thisStage);

        try {
            if (file != null) {
                Workbook workbook;
                final String author = "Distribution app";
                if (AuxiliaryControllerMethods.getExtension(file).equals(".xlsx")) {
                    workbook = new XSSFWorkbook();
                    POIXMLProperties xmlProps = ((XSSFWorkbook) workbook).getProperties();
                    POIXMLProperties.CoreProperties coreProps = xmlProps.getCoreProperties();
                    coreProps.setCreator(author);
                } else {
                    workbook = new HSSFWorkbook();
                }
                Sheet sheet = workbook.createSheet("Distribution");
                int i = 0;
                LinkedList<Reviewer> emptyReviewers = new LinkedList<>();
                for (Reviewer reviewer : distribution.getReviewers()) {
                    if (reviewer.getStudentPapers().isEmpty()) {
                        emptyReviewers.add(reviewer);
                        continue;
                    }
                        Row row = sheet.createRow(i++);
                        reviewer.printToRow(row);
                        for (Paper paper : reviewer.getStudentPapers()) {
                            row = sheet.createRow(i++);
                            paper.printToRow(row);
                        }
                        sheet.createRow(i++);
                }
                for (Reviewer reviewer : emptyReviewers) {
                    Row row = sheet.createRow(i++);
                    reviewer.printToRow(row);
                    sheet.createRow(i++);
                }
                FileOutputStream outputStream = new FileOutputStream(file.getPath());
                workbook.write(outputStream);
                workbook.close();
            }
        } catch (Exception exception) {
            AuxiliaryControllerMethods.showAlertWindow(
                    "Ошибка при попытки сохранить Excel-файл", "", Alert.AlertType.ERROR);
            exception.printStackTrace();
        }
    }

    @FXML
    private void drawDistributionList() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem moveItem = new MenuItem("Переместить");
        MenuItem blackListItem = new MenuItem("Черный список");
        contextMenu.getItems().addAll(moveItem, blackListItem);
        moveItem.setOnAction((ActionEvent event) -> {
            movingPaper = selectedPaper;
            selectedPaper = null;
            promptLabel.setVisible(true);
        });
        blackListItem.setOnAction((ActionEvent event) -> {
            promptLabel.setVisible(false);
            movingPaper = null;
            blacklistController.draw(selectedPaper, distribution);
            selectedPaper = null;
        });
        distributionListView.getItems().clear();
        for (var reviewer : distribution.getReviewers()) {
            TextFlow reviewerTextFlow = createDistributionListElement(reviewer.toString(), true);
            reviewerTextFlow.setOnMouseClicked((MouseEvent mouseEvent) -> {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    if (mouseEvent.getClickCount() == 2 && movingPaper != null) {
                        reviewer.addPaper(movingPaper);
                        movingPaper = null;
                        drawDistributionList();
                        promptLabel.setVisible(false);
                    }
                }
            });
            distributionListView.getItems().add(reviewerTextFlow);
            for (var paper : reviewer.getStudentPapers()) {
                TextFlow paperTextFlow = createDistributionListElement(paper.toString(), false);
                paperTextFlow.setOnContextMenuRequested((ContextMenuEvent event) -> {
                    selectedPaper = paper;
                    contextMenu.show(paperTextFlow, event.getScreenX(), event.getScreenY());
                    promptLabel.setVisible(false);
                    movingPaper = null;
                });
                distributionListView.getItems().add(paperTextFlow);
            }
        }
    }

    private void exceptionHandle() {
        if (distributionListView.getScene() != null) {
            distributionListView.getScene().getWindow().hide();
        }
        mainWindowExceptionHandler.run();
    }

    @FXML
    private TextFlow createDistributionListElement(String text, boolean isReviewer) {
        final double reviewerTextLeftPadding = 25;
        final double reviewerTopPadding = 10;
        final double paperTextLeftPadding = 50;
        final double textRightPadding = 15;
        Insets insets;
        if (isReviewer) {
            insets = new Insets(reviewerTopPadding, textRightPadding, 0, reviewerTextLeftPadding);
        } else {
            insets = new Insets(0, textRightPadding, 0, paperTextLeftPadding);
        }
        Label label = new Label(text);
        label.setFont(new Font(18));
        TextFlow textFlow = new TextFlow();
        textFlow.getChildren().add(label);
        label.prefWidthProperty().bind(distributionListView.widthProperty().subtract(insets.getLeft() + insets.getRight() + 50));
        if (isReviewer) {
            label.setStyle("-fx-font-weight: bold; -fx-font-size: 18");
        }
        textFlow.setPadding(insets);
        return textFlow;

    }

}
