package controllers;

import converters.ImageConverter;
import domain.GreyscaleImage;
import domain.RGBImage;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.Lab1Service;
import services.Lab2Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;

public class MainWindowController {
    @FXML private AnchorPane mainAnchorPane;
    @FXML private ImageView toEditImageView;
    @FXML private ImageView editedImageView;
    @FXML private Label labelCurrentTransformationName;
    @FXML private HBox containerA;
    @FXML private HBox containerB;
    @FXML private Slider sliderA;
    @FXML private Slider sliderB;
    @FXML private Label labelASlider;
    @FXML private Label labelBSlider;
    @FXML private TextField textFieldA;
    @FXML private TextField textFieldB;
    private final Lab1Service lab1Service;
    private final Lab2Service lab2Service;
    private Function<Void,Void> currentFilter;
    private Image originalImage;
    private Image toEditImage;
    private Image editedImage;
    private int a = 50;
    private int b = 200;
    private boolean changedByUser = true;

    public MainWindowController() {
        lab1Service = new Lab1Service();
        lab2Service = new Lab2Service();
    }

    @FXML
    private void initialize() {
        loadDefaultImage();
        disableSliders();
        sliderA.setValue(a);
        sliderB.setValue(b);
        textFieldA.setText(String.valueOf(a));
        textFieldB.setText(String.valueOf(b));

        sliderA.valueProperty().addListener((observableValue, previousValue, newValue) -> {
            if (!changedByUser) {
                return;
            }
            a = newValue.intValue();
            textFieldA.setText(String.valueOf(a));
            currentFilter.apply(null);
        });
        sliderB.valueProperty().addListener((observableValue, previousValue, newValue) -> {
            if (!changedByUser) {
                return;
            }
            b = newValue.intValue();
            textFieldB.setText(String.valueOf(b));
            currentFilter.apply(null);
        });
    }

    public void changeATextFieldHandler(final KeyEvent keyEvent) {
        try {
            a = Integer.valueOf(textFieldA.getText());
            if (keyEvent.getCode() == KeyCode.ENTER) {
                sliderA.setValue(a);
            }
        } catch (NumberFormatException ignored) {}
    }

    public void changeBTextFieldHandler(final KeyEvent keyEvent) {
        try {
            b = Integer.valueOf(textFieldB.getText());
            if (keyEvent.getCode() == KeyCode.ENTER) {
                sliderB.setValue(b);
            }
        } catch (NumberFormatException ignored) {}
    }

    private File openFileChooser() {
        final Stage stage = (Stage) mainAnchorPane.getScene().getWindow();
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a JPEG image.");
        return fileChooser.showOpenDialog(stage);
    }

    private File saveFileChooser() {
        final Stage stage = (Stage) mainAnchorPane.getScene().getWindow();
        final FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JPEG image (*.jpg)", "*.jpg");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Save as");
        return fileChooser.showSaveDialog(stage);
    }

    private void loadImage(final File file) throws IOException {
        final BufferedImage bufferedImage = ImageIO.read(file);
        originalImage = SwingFXUtils.toFXImage(bufferedImage, null);
        toEditImage = originalImage;
        toEditImageView.setImage(toEditImage);
        editedImage = toEditImage;
        editedImageView.setImage(editedImage);
    }

    private void loadDefaultImage() {
        final File defaultImage = new File("./src/main/resources/images/Teatrul (low res).jpg");
        final File imageForNoiseReduction = new File("./src/main/resources/images/Talisman (low res).jpg");
        try {
            loadImage(imageForNoiseReduction);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void loadImageHandler(final ActionEvent actionEvent) throws IOException {
        final File file = openFileChooser();
        if (file != null) {
            loadImage(file);
        }
    }

    @FXML
    public void setEditedImageInToEditViewHandler(final MouseEvent mouseEvent) {
        toEditImage = editedImage;
        toEditImageView.setImage(toEditImage);
    }

    @FXML
    public void resetToOriginalHandler(final ActionEvent actionEvent) {
        toEditImage = originalImage;
        toEditImageView.setImage(toEditImage);
    }

    @FXML
    public void exportImageHandler(final ActionEvent actionEvent) throws IOException {
        final File file = saveFileChooser();
        final BufferedImage bi = SwingFXUtils.fromFXImage(editedImage, null);
        final BufferedImage bufferedImage = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        bufferedImage.getGraphics().drawImage(bi, 0, 0, null);
        ImageIO.write(bufferedImage, "jpg", file);
        System.out.println("Image saved: " + file.toString());
    }

    private void disableSliders() {
        containerA.setDisable(true);
        containerB.setDisable(true);
        containerA.setOpacity(0);
        containerB.setOpacity(0);
    }

    /* Curs 1 */
    @FXML
    public void convertToGreyscaleHandler(final ActionEvent actionEvent) {
        labelCurrentTransformationName.setText("Convert to grayscale");
        changedByUser = false;
        disableSliders();
        changedByUser = true;
        currentFilter = this::convertToGreyscale;
        currentFilter.apply(null);
    }

    private Void convertToGreyscale(Void ignored) {
        final RGBImage rgbImage = ImageConverter.bufferedImageToRgbImage(toEditImage);
        final GreyscaleImage greyscaleImage = lab1Service.convertToGrayscale(rgbImage);
        editedImage = ImageConverter.grayscaleImageToImage(greyscaleImage);
        editedImageView.setImage(editedImage);
        return null;
    }

    @FXML
    public void increaseLuminosityHandler(final ActionEvent actionEvent) {
        labelCurrentTransformationName.setText("C1. Increase luminosity");
        changedByUser = false;
        disableSliders();
        changedByUser = true;
        currentFilter = this::increaseLuminosity;
        currentFilter.apply(null);
    }

    private Void increaseLuminosity(Void ignored) {
        final RGBImage rgbImage = ImageConverter.bufferedImageToRgbImage(toEditImage);
        lab1Service.increaseLuminosity(rgbImage);
        editedImage = ImageConverter.rgbImageToImage(rgbImage);
        editedImageView.setImage(editedImage);
        return null;
    }

    /* Curs 2 */
    @FXML
    public void increaseContrastHandler(final ActionEvent actionEvent) {
        labelCurrentTransformationName.setText("C2. Increase contrast");
        changedByUser = false;
        disableSliders();
        changedByUser = true;
        currentFilter = this::increaseContrast;
        currentFilter.apply(null);
    }

    private Void increaseContrast(Void ignored) {
        final RGBImage rgbImage = ImageConverter.bufferedImageToRgbImage(toEditImage);
        lab1Service.increaseContrast(rgbImage, a, b);
        editedImage = ImageConverter.rgbImageToImage(rgbImage);
        editedImageView.setImage(editedImage);
        return null;
    }

    @FXML
    public void changeContrastHandler(final ActionEvent actionEvent) {
        labelCurrentTransformationName.setText("Change contrast");
        changedByUser = false;
        containerA.setDisable(false);
        containerB.setDisable(true);
        containerA.setOpacity(1);
        containerB.setOpacity(0);
        sliderA.setMin(-255);
        sliderA.setMax(255);
        a = 0;
        sliderA.setValue(a);
        sliderA.setMajorTickUnit(25);
        textFieldA.setText(String.valueOf(a));
        labelASlider.setText("contrast");
        changedByUser = true;
        currentFilter = this::changeContrast;
        currentFilter.apply(null);
    }

    private Void changeContrast(Void ignored) {
        final RGBImage rgbImage = ImageConverter.bufferedImageToRgbImage(toEditImage);
        lab1Service.changeContrast(rgbImage, a);
        editedImage = ImageConverter.rgbImageToImage(rgbImage);
        editedImageView.setImage(editedImage);
        return null;
    }

    /* Curs 3 */
    @FXML
    public void bitExtractionHandler(final ActionEvent actionEvent) {
        labelCurrentTransformationName.setText("C3. Bit extraction");
        changedByUser = false;
        containerA.setDisable(false);
        containerB.setDisable(true);
        containerA.setOpacity(1);
        containerB.setOpacity(0);
        sliderA.setMin(0);
        sliderA.setMax(7);
        a = 0;
        sliderA.setValue(a);
        sliderA.setMajorTickUnit(1);
        sliderA.setMinorTickCount(0);
        textFieldA.setText(String.valueOf(a));
        labelASlider.setText("bit");
        changedByUser = true;
        currentFilter = this::bitExtraction;
        currentFilter.apply(null);
    }

    private Void bitExtraction(final Void ignored) {
        final RGBImage rgbImage = ImageConverter.bufferedImageToRgbImage(toEditImage);
        final GreyscaleImage greyscaleImage = lab1Service.bitExtraction(rgbImage, a);
        editedImage = ImageConverter.grayscaleImageToImage(greyscaleImage);
        editedImageView.setImage(editedImage);
        return null;
    }

    /* Curs 4 */
    @FXML
    public void medianFilterHandler(final ActionEvent actionEvent) {
        labelCurrentTransformationName.setText("C4. a.3) Filtrare mediană");
        changedByUser = false;
        containerA.setDisable(false);
        containerB.setDisable(true);
        containerA.setOpacity(1);
        containerB.setOpacity(0);
        sliderA.setMin(2);
        sliderA.setMax(5);
        a = 3;
        sliderA.setValue(a);
        sliderA.setMajorTickUnit(1);
        sliderA.setMinorTickCount(0);
        textFieldA.setText(String.valueOf(a));
        labelASlider.setText("Matrix size");
        changedByUser = true;
        currentFilter = this::medianFilter;
        currentFilter.apply(null);
    }

    private Void medianFilter(Void ignored) {
        final RGBImage rgbImage = ImageConverter.bufferedImageToRgbImage(toEditImage);
        lab2Service.medianFilter(rgbImage, a);
        editedImage = ImageConverter.rgbImageToImage(rgbImage);
        editedImageView.setImage(editedImage);
        return null;
    }

    /* Curs 5 */
    @FXML
    public void invertContrastHandler(final ActionEvent actionEvent) {
    }

    @FXML
    public void pseudocolorImagehandler(final ActionEvent actionEvent) {
    }
}
