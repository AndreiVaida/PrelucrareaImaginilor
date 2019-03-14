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

    public void openFileChooser(ActionEvent actionEvent) throws IOException {
        final Stage stage = (Stage) mainAnchorPane.getScene().getWindow();
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an image");
        final File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            loadImage(file);
        }
    }

    private void loadImage(final File file) throws IOException {
        final BufferedImage bufferedImage = ImageIO.read(file);
        originalImage = SwingFXUtils.toFXImage(bufferedImage, null);
        toEditImage = originalImage;
        toEditImageView.setImage(toEditImage);
    }

    private void loadDefaultImage() {
        final File file = new File("D:\\Proiecte\\IntelliJ IDEA\\PI\\Laborator\\src\\main\\resources\\images\\Teatrul (low res).jpg");
        try {
            loadImage(file);
        } catch (IOException e) {
            e.printStackTrace();
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
}
