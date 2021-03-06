package services;

import converters.ImageConverter;
import domain.WhiteBlackImage;
import domain.Outline;
import domain.Skeleton;
import javafx.scene.paint.Color;
import observer.ChangePixelEvent;
import observer.Event;
import observer.Observable;
import observer.Observer;
import observer.SkeletonFinishedEvent;

import java.util.HashSet;
import java.util.Set;

public class Lab3Service implements Observable {
    private final Set<Observer> observers;
    private final Color outlineColor;

    public Lab3Service() {
        observers = new HashSet<>();
        outlineColor = Color.rgb(255, 0, 0);
    }

    public Outline identifyOutline(final WhiteBlackImage whiteBlackImage) {
        final int height = whiteBlackImage.getHeight();
        final int width = whiteBlackImage.getWidth();
        final Outline outline = new Outline(height, width);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                final boolean[][] neighborPixels = getNeighborPixels(whiteBlackImage.getMatrix(), 3, i, j);
                outline.setPixel(i, j, pixelIsOnOutline(neighborPixels));
            }
        }
        return outline;
    }

    /**
     * @param neighborPixels - 3x3 matrix
     * @return true if the centered pixel is white and any of its neighbors (only Left, Right, Up, Down) is black
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

    /**
     * @return the matrix[matrixSize][matrixSize] around the pixel from [line,column] from the given image (the pixel is in the middle of the returned matrix).
     * If the pixel is on the edge of the image, the non-existent pixels are set to 0.
     */
    public int[][] getNeighborPixels(final int[][] image, final int matrixSize, final int line, final int column) {
        final int[][] neighborMatrix = new int[matrixSize][matrixSize];

        // build the neighbor matrix
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                final int imgI = line + i - (matrixSize-1)/2;
                final int imgJ = column + j - (matrixSize-1)/2;

                if (imgI < 0 || imgJ < 0 || imgI >= image.length || imgJ >= image[0].length) {
                    neighborMatrix[i][j] = 0;
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
        if (x + 1 < outline.getWidth() && outline.getMatrix()[y][x + 1]) {
            goTroughOutlineThenRemoveIt(outline, y, x + 1, millisToSleep);
            return;
        }
        if (y + 1 < outline.getHeight() && x + 1 < outline.getWidth() && outline.getMatrix()[y + 1][x + 1]) {
            goTroughOutlineThenRemoveIt(outline, y + 1, x + 1, millisToSleep);
            return;
        }
        // search bottom for outline
        if (y + 1 < outline.getHeight() && outline.getMatrix()[y + 1][x]) {
            goTroughOutlineThenRemoveIt(outline, y + 1, x, millisToSleep);
            return;
        }
        if (y + 1 < outline.getHeight() && x - 1 >= 0 && outline.getMatrix()[y + 1][x - 1]) {
            goTroughOutlineThenRemoveIt(outline, y + 1, x - 1, millisToSleep);
            return;
        }
        // search left for outline
        if (x - 1 >= 0 && outline.getMatrix()[y][x - 1]) {
            goTroughOutlineThenRemoveIt(outline, y, x - 1, millisToSleep);
            return;
        }
        if (y - 1 >= 0 && x - 1 >= 0 && outline.getMatrix()[y - 1][x - 1]) {
            goTroughOutlineThenRemoveIt(outline, y - 1, x - 1, millisToSleep);
            return;
        }
        // search above for outline
        if (y - 1 >= 0 && outline.getMatrix()[y - 1][x]) {
            goTroughOutlineThenRemoveIt(outline, y - 1, x, millisToSleep);
            return;
        }
        if (y - 1 >= 0 && x + 1 < outline.getWidth() && outline.getMatrix()[y - 1][x + 1]) {
            goTroughOutlineThenRemoveIt(outline, y - 1, x + 1, millisToSleep);
            return;
        }
    }

    @Override
    public void notifyObservers(final Event event) {
        for (Observer observer : observers) {
            observer.notifyOnEvent(event);
        }
    }

    @Override
    public void addObserver(final Observer observer) {
        this.observers.add(observer);
    }

    public Skeleton identifySkeleton(final WhiteBlackImage whiteBlackImage) {
        final int height = whiteBlackImage.getHeight();
        final int width = whiteBlackImage.getWidth();
        final Skeleton skeleton = new Skeleton(ImageConverter.blackWhiteImageToGreyscaleImage(whiteBlackImage).getMatrix());
        boolean changed = true;

        while (changed) {
            changed = false;
            final Skeleton skeletonCopy = new Skeleton(skeleton.getMatrix());

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (skeleton.getMatrix()[i][j] == 0) {
                        continue;
                    }

                    final int[][] neighbors = getNeighborPixels(skeleton.getMatrix(), 3, i, j);
                    final int minNeighbor = getMinNeighbor_of4(neighbors) + 1;

                    if (minNeighbor > skeleton.getMatrix()[i][j]) {
                        changed = true;
                        skeletonCopy.setPixel(i, j, minNeighbor);

                        if (minNeighbor > skeleton.getMaxHeight()) {
                            skeleton.setMaxHeight(minNeighbor);
                        }
                    }
                }
            }
            skeleton.setMatrix(skeletonCopy.getMatrix());

            if (maxHeightIs1pxNarrow(skeleton)) {
                break;
            }
        }

        return skeleton;
    }

    public Skeleton identifySkeleton_Animate(final WhiteBlackImage whiteBlackImage, final int millisToSleep) {
        final int height = whiteBlackImage.getHeight();
        final int width = whiteBlackImage.getWidth();
        final Skeleton skeleton = new Skeleton(ImageConverter.blackWhiteImageToGreyscaleImage(whiteBlackImage).getMatrix());
        boolean changed = true;

        while (changed) {
            changed = false;
            final Skeleton skeletonCopy = new Skeleton(skeleton.getMatrix());

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (skeleton.getMatrix()[i][j] == 0) {
                        continue;
                    }

                    final int[][] neighbors = getNeighborPixels(skeleton.getMatrix(), 3, i, j);
                    final int minNeighbor = getMinNeighbor_of4(neighbors) + 1;

                    if (minNeighbor > skeleton.getMatrix()[i][j]) {
                        changed = true;
                        skeletonCopy.setPixel(i, j, minNeighbor);

                        if (minNeighbor > skeleton.getMaxHeight()) {
                            skeleton.setMaxHeight(minNeighbor);
                        }
                    }
                }
            }
            skeleton.setMatrix(skeletonCopy.getMatrix());

            if (maxHeightIs1pxNarrow(skeleton)) {
                break;
            }

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    final int pixel = skeleton.getMatrix()[i][j];
                    if (pixel > 0) {
                        Color color = Color.rgb(255, 255, 255);
                        if (pixel == skeleton.getMaxHeight()) {
                            color = Color.rgb(0, 255, 0);
                        }
                        notifyObservers(new ChangePixelEvent(j, i, color));
                    }
                }
            }
            try {
                Thread.sleep(millisToSleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        notifyObservers(new SkeletonFinishedEvent(skeleton));
        return skeleton;
    }

    public Skeleton identifySkeletonV2_Animate(final WhiteBlackImage whiteBlackImage, final int millisToSleep) {
        final int height = whiteBlackImage.getHeight();
        final int width = whiteBlackImage.getWidth();
        final Skeleton skeleton = new Skeleton(ImageConverter.blackWhiteImageToGreyscaleImage(whiteBlackImage).getMatrix());
        boolean changed = true;

        while (changed) {
            changed = false;
            final Skeleton skeletonCopy = new Skeleton(skeleton.getMatrix());

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (skeleton.getMatrix()[i][j] == 0) {
                        continue;
                    }

                    final int[][] neighbors = getNeighborPixels(skeleton.getMatrix(), 3, i, j);
                    int minNeighbor = getMinNeighbor_of4(neighbors) + 1;
                    // use this if only for objects which looks like F
                    if (neighbors[1][1] == neighbors[0][0] + 1 || neighbors[1][1] == neighbors[0][2] + 1 || neighbors[1][1] == neighbors[2][0] + 1 || neighbors[1][1] == neighbors[2][2] + 1) {
                        minNeighbor = neighbors[1][1];
                    }
                    skeletonCopy.setPixel(i, j, minNeighbor);

                    if (minNeighbor > skeleton.getMatrix()[i][j]) {
                        changed = true;
                    }
                }
            }
            skeleton.setMatrix(skeletonCopy.getMatrix());

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    final int pixel = skeleton.getMatrix()[i][j];
                    if (pixel > 0) {
                        Color color = Color.rgb(255, 255, 255);
                        final int[][] neighbors = getNeighborPixels(skeleton.getMatrix(), 3, i, j);
                        final int maxNeighbor = getMaxNeighbor_of4(neighbors);

                        if (pixel >= maxNeighbor) {
                            color = Color.rgb(0, 255, 0);
                        }
                        notifyObservers(new ChangePixelEvent(j, i, color));
                    }
                }
            }
            try {
                Thread.sleep(millisToSleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        notifyObservers(new SkeletonFinishedEvent(skeleton));
        return skeleton;
    }

    private boolean maxHeightIs1pxNarrow(final Skeleton skeleton) {
        for (int i = 0; i < skeleton.getHeight(); i++) {
            for (int j = 0; j < skeleton.getWidth(); j++) {
                if (skeleton.getMatrix()[i][j] == skeleton.getMaxHeight()) {
                    final int[][] neighbors = getNeighborPixels(skeleton.getMatrix(), 3, i, j);
                    if (getNrOfIdenticalNeighbors(neighbors) < 2) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @param neighbors - 3x3 matrix
     * Consider only 4 neighbors: up, down, left, right
     */
    private int getMinNeighbor_of4(final int[][] neighbors) {
        int min = neighbors[0][1];
        if (neighbors[1][0] < min) {
            min = neighbors[1][0];
        }
        if (neighbors[1][2] < min) {
            min = neighbors[1][2];
        }
        if (neighbors[2][1] < min) {
            min = neighbors[2][1];
        }
        return min;
    }

    /**
     * @param neighbors - 3x3 matrix
     * Consider all 8 neighbors
     */
    private int getMinNeighbor_of8(final int[][] neighbors) {
        int min = getMinNeighbor_of4(neighbors);
        if (neighbors[0][0] < min) {
            min = neighbors[0][0];
        }
        if (neighbors[0][2] < min) {
            min = neighbors[0][2];
        }
        if (neighbors[2][0] < min) {
            min = neighbors[2][0];
        }
        if (neighbors[2][2] < min) {
            min = neighbors[2][2];
        }
        return min;
    }

    /**
     * @param neighbors - 3x3 matrix
     * Consider only 4 neighbors: up, down, left, right
     */
    public int getMaxNeighbor_of4(final int[][] neighbors) {
        int max = neighbors[0][1];
        if (neighbors[1][0] > max) {
            max = neighbors[1][0];
        }
        if (neighbors[1][2] > max) {
            max = neighbors[1][2];
        }
        if (neighbors[2][1] > max) {
            max = neighbors[2][1];
        }
        return max;
    }

    /**
     * @param neighbors - 3x3 matrix
     * Consider all 8 neighbors
     */
    private int getMaxNeighbor_of8(final int[][] neighbors) {
        int max = getMaxNeighbor_of4(neighbors);
        if (neighbors[0][0] > max) {
            max = neighbors[0][0];
        }
        if (neighbors[0][2] > max) {
            max = neighbors[0][2];
        }
        if (neighbors[2][0] > max) {
            max = neighbors[2][0];
        }
        if (neighbors[2][2] > max) {
            max = neighbors[2][2];
        }
        return max;
    }

    /**
     * @param neighbors - 3x3 matrix
     * Consider only 4 neighbors: up, down, left, right
     */
    private int getNrOfIdenticalNeighbors(final int[][] neighbors) {
        int nrOfIdenticalNeighbors = 0;
        final int pixel = neighbors[1][1];
        if (neighbors[0][1] == pixel) {
            nrOfIdenticalNeighbors++;
        }
        if (neighbors[2][1] == pixel) {
            nrOfIdenticalNeighbors++;
        }
        if (neighbors[1][0] == pixel) {
            nrOfIdenticalNeighbors++;
        }
        if (neighbors[1][2] == pixel) {
            nrOfIdenticalNeighbors++;
        }
        return nrOfIdenticalNeighbors;
    }

    /**
     * @param neighbors - 3x3 matrix
     * Consider all 8 neighbors
     */
    private int getNrOfIdenticalNeighbors(final boolean[][] neighbors) {
        int nrOfIdenticalNeighbors = 0;
        final boolean pixel = neighbors[1][1];
        // up
        if (neighbors[0][1] == pixel) {
            nrOfIdenticalNeighbors++;
        }
        if (neighbors[0][2] == pixel) {
            nrOfIdenticalNeighbors++;
        }
        // right
        if (neighbors[2][1] == pixel) {
            nrOfIdenticalNeighbors++;
        }
        if (neighbors[2][2] == pixel) {
            nrOfIdenticalNeighbors++;
        }
        // down
        if (neighbors[1][0] == pixel) {
            nrOfIdenticalNeighbors++;
        }
        if (neighbors[2][0] == pixel) {
            nrOfIdenticalNeighbors++;
        }
        // left
        if (neighbors[1][2] == pixel) {
            nrOfIdenticalNeighbors++;
        }
        if (neighbors[0][0] == pixel) {
            nrOfIdenticalNeighbors++;
        }
        return nrOfIdenticalNeighbors;
    }

    public void slimImage(final WhiteBlackImage whiteBlackImage) {
        boolean canSlim = true;
        while (canSlim) {
            canSlim = false;
            final WhiteBlackImage whiteBlackImageCopy = new WhiteBlackImage(whiteBlackImage);
            for (int i = 0; i < whiteBlackImage.getHeight(); i++) {
                for (int j = 0; j < whiteBlackImage.getWidth(); j++) {
                    if (!whiteBlackImage.getMatrix()[i][j]) {
                        continue;
                    }

                    final boolean[][] neighbors = getNeighborPixels(whiteBlackImage.getMatrix(), 3, i, j);
                    final int nrOfOneNeighbors = getNrOfIdenticalNeighbors(neighbors);
                    if (nrOfOneNeighbors > 2 && nrOfOneNeighbors <= 6) {
                        whiteBlackImageCopy.setPixel(i, j, false);
                        notifyObservers(new ChangePixelEvent(j, i, Color.rgb(0,0,0)));
                        canSlim = true;
                    }
                }
            }
            whiteBlackImage.setMatrix(whiteBlackImageCopy.getMatrix());
        }
    }

    public void slimImage_Animate(final WhiteBlackImage whiteBlackImage, final int millisToSleep) {
        new Thread(() -> {
            boolean canSlim = true;
            while (canSlim) {
                canSlim = false;
                final WhiteBlackImage whiteBlackImageCopy = new WhiteBlackImage(whiteBlackImage);
                for (int i = 0; i < whiteBlackImage.getHeight(); i++) {
                    for (int j = 0; j < whiteBlackImage.getWidth(); j++) {
                        if (!whiteBlackImage.getMatrix()[i][j]) {
                            continue;
                        }

                        final boolean[][] neighbors = getNeighborPixels(whiteBlackImage.getMatrix(), 3, i, j);
                        final int nrOfOneNeighbors = getNrOfIdenticalNeighbors(neighbors);
                        if (nrOfOneNeighbors > 2 && nrOfOneNeighbors <= 6) {
                            whiteBlackImageCopy.setPixel(i, j, false);
                            notifyObservers(new ChangePixelEvent(j, i, Color.rgb(0,0,0)));
                            canSlim = true;
                            // sleep
                            long start = System.nanoTime();
                            while(System.nanoTime() - start < millisToSleep * 5000);
                        }
                    }
                }
                whiteBlackImage.setMatrix(whiteBlackImageCopy.getMatrix());
            }
        }).start();
    }

    private int numberOfTransitions(int i, int j, int[][] matrix) {
        int[][] transitions = {{i-1,j},{i-1,j+1},{i,j+1},{i+1,j+1},{i+1,j},{i+1,j-1},{i,j-1},{i-1,j-1},{i-1,j}};
        int k = 0;
        int nr = 0;
        int pixel1, pixel2;
        while ( k < transitions.length - 1){
            pixel1 = matrix[transitions[k][0]][transitions[k][1]];
            pixel2 = matrix[transitions[k+1][0]][transitions[k+1][1]];

            if(pixel1 == 0 && pixel2 == 255)
                nr++;

            k++;
        }
        return  nr;
    }
}
