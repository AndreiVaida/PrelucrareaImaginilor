package controllers;

import converters.ImageConverter;
import domain.RGBImage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.Lab1Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;

public class MainWindowController {
    @FXML private AnchorPane mainAnchorPane;
    @FXML private ImageView toEditImageView;
    @FXML private ImageView editedImageView;
    @FXML private Slider aSlider;
    @FXML private TextField aTextField;
    @FXML private Slider bSlider;
    @FXML private TextField bTextField;
    private final Lab1Service lab1Service;
    private Function<Void,Void> currentFilter;
    private Image originalImage;
    private Image toEditImage;
    private Image editedImage;
    private int a = 50;
    private int b = 200;

    public MainWindowController() {
        lab1Service = new Lab1Service();
    }

    @FXML
    private void initialize() {
        loadDefaultImage();
        aSlider.setValue(a);
        bSlider.setValue(b);
        aTextField.setText(String.valueOf(a));
        bTextField.setText(String.valueOf(b));

        aSlider.valueProperty().addListener((observableValue, previousValue, newValue) -> {
            a = newValue.intValue();
            aTextField.setText(String.valueOf(a));
            currentFilter.apply((Void) null);
        });
        bSlider.valueProperty().addListener((observableValue, previousValue, newValue) -> {
            b = newValue.intValue();
            bTextField.setText(String.valueOf(b));
            currentFilter.apply((Void) null);
        });
        aTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                aTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            a = ImageConverter.clamp(Integer.valueOf(aTextField.getText()));
            aTextField.setText(String.valueOf(a));
            aSlider.setValue(a);
        });
        bTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                bTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            b = ImageConverter.clamp(Integer.valueOf(bTextField.getText()));
            bTextField.setText(String.valueOf(b));
            bSlider.setValue(b);
        });
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
    }

    private void loadDefaultImage() {
        final File file = new File("./src/main/resources/images/Teatrul (low res).jpg");
        try {
            loadImage(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void loadImage(final ActionEvent actionEvent) throws IOException {
        final File file = openFileChooser();
        if (file != null) {
            loadImage(file);
        }
    }

    @FXML
    public void setEditedImageInToEditView(final MouseEvent mouseEvent) {
        toEditImage = editedImage;
        toEditImageView.setImage(toEditImage);
    }

    @FXML
    public void resetToOriginal(final ActionEvent actionEvent) {
        toEditImage = originalImage;
        toEditImageView.setImage(toEditImage);
    }

    @FXML
    public void exportImage(final ActionEvent actionEvent) throws IOException {
        final File file = saveFileChooser();
        final BufferedImage bi = SwingFXUtils.fromFXImage(editedImage, null);
        final BufferedImage bufferedImage = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        bufferedImage.getGraphics().drawImage(bi, 0, 0, null);
        ImageIO.write(bufferedImage, "jpg", file);
        System.out.println("Image saved: " + file.toString());
    }

    /* Curs 1 */
    @FXML
    public void increaseLuminosity(final ActionEvent actionEvent) {
        currentFilter = this::increaseLuminosity;
        increaseLuminosity((Void) null);
    }

    private Void increaseLuminosity(Void aVoid) {
        final RGBImage rgbImage = ImageConverter.bufferedImageToRgbImage(toEditImage);
        lab1Service.increaseLuminosity(rgbImage);
        editedImage = ImageConverter.rgbImageToImage(rgbImage);
        editedImageView.setImage(editedImage);
        return null;
    }

    /* Curs 2 */
    @FXML
    public void increaseContrast(final ActionEvent actionEvent) {
        currentFilter = this::increaseContrast;
        increaseContrast((Void) null);
    }

    private Void increaseContrast(Void aVoid) {
        final RGBImage rgbImage = ImageConverter.bufferedImageToRgbImage(toEditImage);
        lab1Service.increaseContrast(rgbImage, a, b);
        editedImage = ImageConverter.rgbImageToImage(rgbImage);
        editedImageView.setImage(editedImage);
        return null;
    }
}
