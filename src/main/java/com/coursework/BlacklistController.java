package com.coursework;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class BlacklistController {
    private final Background blacklistedBackground = new Background(
            new BackgroundFill(Color.rgb(220, 20, 60), CornerRadii.EMPTY, Insets.EMPTY));
    @FXML
    private ListView<TextFlow> blacklistListView;
    @FXML
    private TextFlow paperNameTextFlow;
    @FXML
    private Label paperNameTextLabel;
    @FXML
    private Stage thisStage;
    @FXML
    private ScrollPane scrollPane;

    public Stage getStage() {
        return thisStage;
    }


    public void draw(Paper paper, Distribution distribution) {
        paperNameTextLabel.setText(paper.toString());
        blacklistListView.getItems().clear();
        final Insets insets = new Insets(0, 0, 0, 30);
        for (var reviewer : distribution.getReviewers()) {
            TextFlow reviewerTextFlow = new TextFlow();
            Label label = new Label(reviewer.toString());
            //label.setPadding(insets);
            label.setFont(new Font(20));
            if (paper.getBlacklist().contains(reviewer)) {
                reviewerTextFlow.setBackground(blacklistedBackground);
            }
            blacklistListView.getItems().add(reviewerTextFlow);
            reviewerTextFlow.getChildren().add(label);
        }
        blacklistListView.setOnKeyPressed((KeyEvent keyEvent) -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                onReviewerClickedOrPressed(paper, distribution);
            }
        });
        blacklistListView.setOnMouseClicked((MouseEvent mouseEvent) -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() == 2) {
                    onReviewerClickedOrPressed(paper, distribution);
                }
            }
        });
        thisStage.show();
        paperNameTextLabel.setPadding(new Insets(
                paperNameTextFlow.getHeight() / 2 - paperNameTextLabel.getHeight() / 2, 0, 0, 0));
        for (var reviewerTextFlow : blacklistListView.getItems()) {
            Label label = (Label) reviewerTextFlow.getChildren().get(0);
            reviewerTextFlow.setPrefHeight(reviewerTextFlow.getHeight() * 1.5);
            label.setPadding(new Insets(
                    reviewerTextFlow.getPrefHeight() / 2 - label.getHeight() / 2, 0, 0, 30));
        }
        thisStage.toFront();
    }

    public void setUp(Stage blacklistStage) {
        paperNameTextLabel.setMaxWidth(paperNameTextFlow.getPrefWidth());
        paperNameTextLabel.setMaxHeight(paperNameTextFlow.getPrefHeight());
        blacklistStage.setTitle("Черный список рецензентов");
        blacklistStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("assets/icon.png")));
        blacklistStage.setResizable(false);
        thisStage = blacklistStage;
    }

    private void onReviewerClickedOrPressed(Paper paper, Distribution distribution) {
        var reviewerTextFlow = blacklistListView.getSelectionModel().getSelectedItem();
        Reviewer reviewer = distribution.getReviewers().get(blacklistListView.getItems().indexOf(reviewerTextFlow));
        if (paper.getBlacklist().contains(reviewer)) {
            paper.removeFromBlacklist(reviewer);
            reviewerTextFlow.setBackground(Background.EMPTY);
        } else {
            paper.addTobBlacklist(reviewer);
            reviewerTextFlow.setBackground(blacklistedBackground);
        }
    }
}
