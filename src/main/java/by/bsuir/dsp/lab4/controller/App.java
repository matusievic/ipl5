package by.bsuir.dsp.lab4.controller;

import by.bsuir.dsp.lab4.image.ImageService;
import by.bsuir.dsp.lab4.perceptron.Network;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class App extends Application {
    // learning params
    @FXML
    private TextField aField;
    @FXML
    private TextField errorField;
    @FXML
    private Button learnButton;
    @FXML
    private Label iterationsLabel;

    // noise generator params
    @FXML
    private ComboBox<String> fileBox;
    @FXML
    private Button recognizeButton;
    @FXML
    private ImageView inputImage;
    @FXML
    private ImageView resultImage;

    // class labels
    @FXML
    private Label firstClassPercentLabel;
    @FXML
    private Label secondClassPercentLabel;
    @FXML
    private Label thirdClassPercentLabel;
    @FXML
    private Label fourthClassPercentLabel;
    @FXML
    private Label fifthClassPercentLabel;
    @FXML
    private ImageView firstImage;
    @FXML
    private ImageView secondImage;
    @FXML
    private ImageView thirdImage;
    @FXML
    private ImageView fourthImage;
    @FXML
    private ImageView fifthImage;

    // non-UI fields
    private int[][][] patterns;
    private BufferedImage input;
    private Network network;

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);

        String fxmlFile = "/fxml/app.fxml";
        Parent root = loader.load(getClass().getResourceAsStream(fxmlFile));

        primaryStage.setTitle("lab-5");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        firstImage.setImage(SwingFXUtils.toFXImage(ImageService.maximize(ImageIO.read(App.class.getResourceAsStream("/pattern/1.png")), 20), null));
        secondImage.setImage(SwingFXUtils.toFXImage(ImageService.maximize(ImageIO.read(App.class.getResourceAsStream("/pattern/2.png")), 20), null));
        thirdImage.setImage(SwingFXUtils.toFXImage(ImageService.maximize(ImageIO.read(App.class.getResourceAsStream("/pattern/3.png")), 20), null));
        fourthImage.setImage(SwingFXUtils.toFXImage(ImageService.maximize(ImageIO.read(App.class.getResourceAsStream("/pattern/4.png")), 20), null));
        fifthImage.setImage(SwingFXUtils.toFXImage(ImageService.maximize(ImageIO.read(App.class.getResourceAsStream("/pattern/5.png")), 20), null));

        int[][][] patterns = new int[5][][];
        for (int k = 0; k < 5; k++) {
            patterns[k] = ImageService.imageToMap(ImageIO.read(App.class.getResourceAsStream("/pattern/" + (k + 1) + ".png")));

        }

        learnButton.setOnAction(e -> {
            int iterCount = 0;
            this.network = new Network(36, 5);
            double a = Double.parseDouble(aField.getText());
            double error = Double.parseDouble(errorField.getText());
            double[][] right = {
                    {1.0, 0.0, 0.0, 0.0, 0.0},
                    {0.0, 1.0, 0.0, 0.0, 0.0},
                    {0.0, 0.0, 1.0, 0.0, 0.0},
                    {0.0, 0.0, 0.0, 1.0, 0.0},
                    {0.0, 0.0, 0.0, 0.0, 1.0}
            };

            Random r = new Random();
            List<Integer> used = new ArrayList<>(5);
            for (int i = 0; i < 15625; i++) {
                int ind;
                do {
                    ind = r.nextInt(5);
                } while (used.contains(ind));
                used.add(ind);
                iterCount += this.network.learn(patterns[ind], right[ind], a, error);
                if (i % 4 == 0) {
                    used.clear();
                }
            }
            iterationsLabel.setText(String.valueOf(iterCount));
        });


        File images = new File(getClass().getResource("/noise").getPath());
        fileBox.getItems().addAll(Arrays.stream(images.listFiles()).map(File::getName).sorted().toArray(String[]::new));
        fileBox.setOnAction(event -> {
            try {
                this.input = ImageIO.read(getClass().getResourceAsStream("/noise/" + fileBox.getValue()));
                Image value = SwingFXUtils.toFXImage(ImageService.maximize(input, 20), null);
                inputImage.setImage(value);
            } catch (IOException ignored) {
            }
        });

        recognizeButton.setOnAction(event -> {
            int result = this.network.recognize(ImageService.imageToMap(input));
            resultImage.setImage(SwingFXUtils.toFXImage(ImageService.maximize(ImageService.mapToImage(patterns[result]), 20), null));
            double[] output = network.getOutput();
            firstClassPercentLabel.setText(String.format("%.2f", (output[0])));
            secondClassPercentLabel.setText(String.format("%.2f", (output[1])));
            thirdClassPercentLabel.setText(String.format("%.2f", (output[2])));
            fourthClassPercentLabel.setText(String.format("%.2f", (output[3])));
            fifthClassPercentLabel.setText(String.format("%.2f", (output[4])));
        });
    }
}
