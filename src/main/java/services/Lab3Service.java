package services;

import domain.BlackWhiteImage;
import domain.Outline;
import javafx.scene.paint.Color;
import observer.ChangePixelEvent;
import observer.Observable;
import observer.Observer;

import java.util.HashSet;
import java.util.Set;

public class Lab3Service implements Observable {
    private final Set<Observer> observers;
    private final Color outlineColor;

    public Lab3Service() {
        observers = new HashSet<>();
        outlineColor = Color.rgb(255, 0, 0);
    }

    public Outline identifyOutline(final BlackWhiteImage blackWhiteImage) {
        final int height = blackWhiteImage.getHeight();
        final int width = blackWhiteImage.getWidth();
        final Outline outline = new Outline(height, width);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                final boolean[][] neighborPixels = getNeighborPixels(blackWhiteImage.getMatrix(), 3, i, j);
                outline.setPixel(i, j, pixelIsOnOutline(neighborPixels));
            }
        }
        return outline;
    }

    /**
     * @param neighborPixels - 3x3 matrix
     * @return true if the centered pixel is white and any its neighbors (only Left, Right, Up, Down) is black
     */
    private boolean pixelIsOnOutline(final boolean[][] neighborPixels) {
        if (!neighborPixels[1][1]) {
            return false;
        }
        return !neighborPixels[1][0] || !neighborPixels[1][2] || !neighborPixels[0][1] || !neighborPixels[2][1];
    }

    /**
     * @return the matrix[matrixSize][matrixSize] around the pixel from [line,column] from the given image (the pixel is in the middle of the returned matrix).
     * If the pixel is on the edge of the image, the non-existent pixels are set to 0 (false) - border.
     */
    private boolean[][] getNeighborPixels(final boolean[][] image, final int matrixSize, final int line, final int column) {
        final boolean[][] neighborMatrix = new boolean[matrixSize][matrixSize];

        // build the neighbor matrix
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                final int imgI = line + i - (matrixSize - 1) / 2;
                final int imgJ = column + j - (matrixSize - 1) / 2;

                if (imgI < 0 || imgJ < 0 || imgI >= image.length || imgJ >= image[0].length) {
                    neighborMatrix[i][j] = false;
                    continue;
                }
                neighborMatrix[i][j] = image[imgI][imgJ];
            }
        }

        return neighborMatrix;
    }

    public void animateOutline_Standard(final Outline outline, final int speed) {
        new Thread(() -> {
            final int height = outline.getHeight();
            final int width = outline.getWidth();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    final boolean outlinePixel = outline.getMatrix()[y][x];
                    if (outlinePixel) {
                        notifyObservers(new ChangePixelEvent(x, y, outlineColor));
                        try {
                            Thread.sleep(speed);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    public void animateOutline_LineByLine(final Outline outline, final int millisToSleep) {
        final Outline outlineCopy = new Outline(outline);
        new Thread(() -> {
            final int height = outlineCopy.getHeight();
            final int width = outlineCopy.getWidth();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    final boolean outlinePixel = outlineCopy.getMatrix()[y][x];
                    if (outlinePixel) {
                        goTroughOutlineThenRemoveIt(outlineCopy, y, x, millisToSleep);
                    }
                }
            }
        }).start();
    }

    /**
     * [y,x] - beginning of the outline
     * Go through the outline after reaching it's end. Priority direction: right -> down -> left -> up.
     * Notify observers at every outline pixel, then sleep millisToSleep.
     */
    private void goTroughOutlineThenRemoveIt(final Outline outline, final int y, final int x, final int millisToSleep) {
        // notify observers that at [y,x] is outline
        notifyObservers(new ChangePixelEvent(x, y, outlineColor));
        try {
            Thread.sleep(millisToSleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // delete [y,x] outline pixel
        outline.getMatrix()[y][x] = false;

        // search right for outline
        if (x+1 < outline.getWidth() && outline.getMatrix()[y][x+1]) {
            goTroughOutlineThenRemoveIt(outline, y, x+1, millisToSleep);
            return;
        }
        if (y+1 < outline.getHeight() && x+1 < outline.getWidth() && outline.getMatrix()[y+1][x+1]) {
            goTroughOutlineThenRemoveIt(outline, y+1, x+1, millisToSleep);
            return;
        }
        // search bottom for outline
        if (y+1 < outline.getHeight() && outline.getMatrix()[y+1][x]) {
            goTroughOutlineThenRemoveIt(outline, y+1, x, millisToSleep);
            return;
        }
        if (y+1 < outline.getHeight() && x-1 >= 0 && outline.getMatrix()[y+1][x-1]) {
            goTroughOutlineThenRemoveIt(outline, y+1, x-1, millisToSleep);
            return;
        }
        // search left for outline
        if (x-1 >= 0 && outline.getMatrix()[y][x-1]) {
            goTroughOutlineThenRemoveIt(outline, y, x-1, millisToSleep);
            return;
        }
        if (y-1 >= 0 && x-1 >= 0 && outline.getMatrix()[y-1][x-1]) {
            goTroughOutlineThenRemoveIt(outline, y-1, x-1, millisToSleep);
            return;
        }
        // search above for outline
        if (y-1 >= 0 && outline.getMatrix()[y-1][x]) {
            goTroughOutlineThenRemoveIt(outline, y-1, x, millisToSleep);
            return;
        }
        if (y-1 >= 0 && x+1 < outline.getWidth() && outline.getMatrix()[y-1][x+1]) {
            goTroughOutlineThenRemoveIt(outline, y-1, x+1, millisToSleep);
            return;
        }
    }

    @Override
    public void notifyObservers(final ChangePixelEvent changePixelEvent) {
        for (Observer observer : observers) {
            observer.notifyOnEvent(changePixelEvent);
        }
    }

    @Override
    public void addObserver(final Observer observer) {
        this.observers.add(observer);
    }
}
