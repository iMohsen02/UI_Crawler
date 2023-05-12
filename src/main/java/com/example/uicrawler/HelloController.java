package com.example.uicrawler;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class HelloController {

    @FXML
    private PieChart pieChart;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Button btnStartOrStop;
    @FXML
    private TextField txtSeedUrl;
    @FXML
    private TextField txtPageNum;
    @FXML
    private Label lblLogs;
    @FXML
    private Rectangle groupRectangle;
    @FXML
    private ProgressBar processBar;
    @FXML
    private Text lblReportNums;
    @FXML
    private Group groupButtons;
    @FXML
    private Group groupRecText;
    @FXML
    private Group groupBtns;
    @FXML
    private Button btnSetFilterOption;
    @FXML
    private Button btnClose;
    @FXML
    private Label lblInfo;
    @FXML
    private Label lblRes;
    @FXML
    private Label lblErrorUrl;
    @FXML
    private Label lblErrorNum;
    @FXML
    private TextArea txtAreaBlocker;

    Timeline resultTimeline;
    Crawler crawler;
    private LocalTime time;


    {


//        PrintStream ps = new PrintStream(new OutputStream() {
//            @Override
//            public void write(int b) throws IOException {
//                lblLogs.setText(lblLogs.getText() + Character.getName(b));
//            }
//        });
//
//        System.setOut(ps);
    }

    public void closeProgram() {
        clearRes();
        playGameProgress();
        if (crawler != null && !crawler.checkStop()) crawler.stopAsync().thenRunAsync(Platform::exit);
        else javafx.application.Platform.exit();
    }

    public void clickBtnStartStop() {

        if (btnStartOrStop.getText().equalsIgnoreCase("start crawler")) {

            translateAndScale(Duration.millis(1500), 0, 1);

            UrlBlocker blocker = new UrlBlocker();
            blocker.addBlock(txtAreaBlocker.getText().split("\n"));

            try {
                prepareCrawlerDataToStart(blocker);
                btnStartOrStop.setText("Stop Crawler");

            } catch (MalformedURLException malformedURLException) {
                lblErrorUrl.setVisible(true);
                return;
            } catch (IOException e) {
                lblLogs.setText(String.valueOf(e));
                return;
            } catch (NumberFormatException numberFormatException) {
                lblErrorNum.setVisible(true);
                return;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            lblInfo.setText("Crawling");
        } else {
            btnStartOrStop.setText("Start Crawler");
            lblInfo.setText("Waiting");
            playGameProgress();
            btnClose.setDisable(true);
            btnStartOrStop.setDisable(true);
            crawler.stopAsync().thenRun(() -> {
                System.out.println("start finish");
                btnStartOrStop.setDisable(false);
                btnClose.setDisable(false);
                System.out.println("start finish");

                reportsTimeline.stop();
                resultTimeline.stop();

                System.out.println("start finish");
                setFinish();
                System.out.println("end finish");
            });
        }
    }

    public void prepareResult() {
        FadeTransition f = new FadeTransition(Duration.seconds(0.5), groupRecText);
        f.setToValue(0);

        TranslateTransition transition = new TranslateTransition(Duration.seconds(1), groupBtns);
        transition.setToY(-330);
        transition.setCycleCount(1);

        new ParallelTransition(transition, f).play();

        lblRes.setVisible(true);
        reportsTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    lblRes.setText(crawler.getReport().toString().toLowerCase().replace("_", " "));
                }));
        reportsTimeline.setCycleCount(Timeline.INDEFINITE);
        reportsTimeline.play();
    }

    public void clearRes() {
        FadeTransition f = new FadeTransition(Duration.seconds(0.5), groupRecText);
        f.setByValue(1);

        TranslateTransition transition = new TranslateTransition(Duration.seconds(1), groupBtns);
        transition.setToY(0);
        transition.setCycleCount(1);

        new ParallelTransition(transition, f).play();
        lblRes.setVisible(false);
        if (reportsTimeline != null)
            reportsTimeline.pause();
    }

    private void prepareCrawlerDataToStart(UrlBlocker blocker) throws IOException, InterruptedException {
        playGameProgress();
        URL url = new URL(txtSeedUrl.getText());
        int pageNum = Integer.parseInt(txtPageNum.getText());

        crawler = new Crawler(url, pageNum, blocker);

        if (crawler.checkStart()) {
            Alert alert = askToStopedCrawler();

            if (alert.getResult() == ButtonType.YES) crawler.start();
            else crawler.clearData();
        }
        time = LocalTime.now();
        crawler.crawlAsync();
        setupReports();
        prepareResult();
    }

    @NotNull
    private Alert askToStopedCrawler() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "there is a stopped crawling do you want to continue it? if you press yes new crawling will be ignored otherwise new crawl process will be start", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Warning");
        alert.setHeaderText("There is a Stopped Crawler!");
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("alert.css").toExternalForm());
        dialogPane.getStyleClass().add("my-alert");
        alert.showAndWait();
        return alert;
    }

    Timeline playGameProgressTimeline;

    public void playGameProgress() {
        playGameProgressTimeline = new Timeline(
                new KeyFrame(Duration.millis(20), event -> {
                    // update the data
                    this.processBar.setProgress((this.processBar.getProgress() + 0.01) % 1.0);
                })
        );
        playGameProgressTimeline.setCycleCount(100);
        playGameProgressTimeline.play();
    }

    public void clickFilterOption() {

        Duration duration = Duration.millis(1500);
        if (groupButtons.getTranslateX() == 0) translateAndScale(duration, -500, 1.7);
        else translateAndScale(duration, 0, 1);
    }

    private void translateAndScale(Duration duration, int toX, double toXScale) {

        TranslateTransition transition = new TranslateTransition(duration, groupButtons);
        transition.setToX(toX);
        transition.setCycleCount(1);

        ScaleTransition scaleTransition = new ScaleTransition(duration, groupRectangle);
        scaleTransition.setToX(toXScale);

        new ParallelTransition(transition, scaleTransition).play();
    }

    public void setFinish() {
//        lblReportNums.setText("crawled done!. " + crawler.getMaxPage() + " pages crawled.");
        btnStartOrStop.setText("Start Crawler");
        lblReportNums.setText("Total time: " + time + " | " + LocalTime.now() + " min");
    }

    private Timeline reportsTimeline;

    public void setupReports() {
        pieChart.getData().removeAll(pieChart.getData());
        Arrays.stream(WebPage.Status.values())
                .map(state -> new PieChart.Data(state.name(), 0))
                .forEach(data -> pieChart.getData().add(data));


        // create a timeline to update the data every second
        reportsTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    // update the data
                    pieChart.getData().forEach(d -> d.setPieValue(crawler.getReport().getNumOf(d.getName())));
                    lblReportNums.setText(crawler.getReport().getCrawledPageNum() + "/" + crawler.getMaxPage() + "crawled. " + "Total time: " + java.time.Duration.between(time, LocalTime.now()).toSeconds() + " sec" + " -> " + java.time.Duration.between(time, LocalTime.now()).toMinutes() + " min");

                    processBar.setProgress(crawler.getReport().getCrawledPageNum() / (double) crawler.getMaxPage());

                    if (crawler.getReport().getCrawledPageNum() == crawler.getMaxPage())
                        System.out.println("Total time: " + java.time.Duration.between(time, LocalTime.now()).toSeconds() + " sec" + " -> " + java.time.Duration.between(time, LocalTime.now()).toMinutes() + " min");
                })
        );
        reportsTimeline.setCycleCount(Timeline.INDEFINITE);
        reportsTimeline.play();
    }

}

