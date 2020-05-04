package com.coursework;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextFlow;
import org.apache.poi.ss.usermodel.Workbook;

public class MainWindowController {

    @FXML
    ScrollPane scrollPane;
    @FXML
    ListView<TextFlow> distributionListView;
    @FXML
    Label promptLabel;

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
            exceptionHandle();
        }
    }

    @FXML
    void distributeButtonClicked() {
        if (!distribution.hungarianAlgorithmDistribution()) {
            exceptionHandle();
            return;
        }
        drawDistributionList();
    }

    @FXML
    void drawDistributionList() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem moveItem = new MenuItem("Переместить");
        MenuItem blackListItem = new MenuItem("Черный список");
        contextMenu.getItems().addAll(moveItem, blackListItem);
        moveItem.setOnAction((ActionEvent event) -> {
            movingPaper = selectedPaper;
            promptLabel.setVisible(true);
        });
        distributionListView.getItems().clear();
        for (var reviewer : distribution.getReviewers()) {
            TextFlow reviewerTextFlow = createDistributionListElement(reviewer.toString(), true);
            reviewerTextFlow.setOnMouseClicked((MouseEvent mouseEvent) -> {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    if (mouseEvent.getClickCount() == 2 && movingPaper != null) {
                        reviewer.addPaper(movingPaper);
                        movingPaper = null;
                        selectedPaper = null;
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
                });
                distributionListView.getItems().add(paperTextFlow);
            }
        }
    }

    private void exceptionHandle() {
        distributionListView.getScene().getWindow().hide();
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
