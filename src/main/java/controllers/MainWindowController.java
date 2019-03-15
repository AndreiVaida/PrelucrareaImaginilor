package controllers;

import converters.ImageConverter;
import domain.RGBImage;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.Lab1Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainWindowController {
    @FXML private AnchorPane mainAnchorPane;
    @FXML private ImageView toEditImageView;
    @FXML private ImageView editedImageView;
    private Image originalImage;
    private Image toEditImage;
    private Image editedImage;
    private final Lab1Service lab1Service;

    public MainWindowController() {
        lab1Service = new Lab1Service();
    }

    @FXML
    private void initialize() {
        loadDefaultImage();
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
    public void increaseLuminosity(final ActionEvent actionEvent) {
        final RGBImage rgbImage = ImageConverter.bufferedImageToRgbImage(toEditImage);
        lab1Service.increaseLuminosity(rgbImage);
        editedImage = ImageConverter.rgbImageToImage(rgbImage);
        editedImageView.setImage(editedImage);
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
}
