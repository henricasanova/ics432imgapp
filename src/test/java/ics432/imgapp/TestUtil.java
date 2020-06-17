package ics432.imgapp;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import org.testfx.assertions.api.Assertions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;

/**
 * A helper class that implements static helper methods
 */
class TestUtil {

    /**
     * Method to get a reference to a field of the JobWindow class
     * by name (and to set that field to no longer being private
     * for testing purposes)
     *
     * @param name The field's name
     * @return The field
     */
    static Field getFieldByName(Class c, String name) {
        // Get the image in the view port
        Field theField = null;
        try {
            theField = c.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            Assertions.fail(e.toString());
        }
        theField.setAccessible(true);
        return theField;
    }

    /**
     * Method to get a reference to the "setTargetDir()" method of the
     * JobWindow class (and to set that method to no longer being
     * private for testing purposes)
     *
     * @return The method
     */
    static Method getSetTargetDirMethodOfJobWindowClass() {

        // Get a reference to the private method setTargetDir()
        Class[] cArg = new Class[1];
        cArg[0] = Path.class;
        Method m = null;
        try {
            m = JobWindow.class.getDeclaredMethod("setTargetDir", cArg);
            m.setAccessible(true);
        } catch (NoSuchMethodException e) {
            Assertions.fail(e.toString());
        }
        return m;
    }


    /**
     * Method to get a reference to the "addFiles()" method of the
     * MainWindow class (and to set that method to no longer being
     * private for testing purposes)
     *
     * @return The method
     */
    static Method getAddFilesMethod(Class c) {

        // Get a reference to the private method setTargetDir()
        Class[] cArg = new Class[1];
        cArg[0] = List.class;
        Method m = null;
        try {
            m = c.getDeclaredMethod("addFiles", cArg);
            m.setAccessible(true);
        } catch (NoSuchMethodException e) {
            Assertions.fail(e.toString());
        }
        return m;
    }

    /**
     * Helper method that compares two image files pixel-to-pixel
     * @param f1 the first image file
     * @param f2 the second image file
     * @return true if the two images are pixel-identical, false otherwise
     */
    static boolean checkPixels(Path f1, Path f2) {

        // Get the images
        Image img1 = null, img2 = null;
        try {
            img1 = new Image(f1.toUri().toURL().toString());
            img2 = new Image(f2.toUri().toURL().toString());
        } catch (MalformedURLException e) {
            org.junit.jupiter.api.Assertions.fail(e.toString());
        }

        return checkPixels(img1, img2);
    }

    /**
     * Helper method that compares two images pixel-to-pixel
     * @param img1 the first image
     * @param img2 the second image
     * @return true if the two images are pixel-identical, false otherwise
     */
    static boolean checkPixels(Image img1, Image img2) {

        if ((img1.getWidth() != img2.getWidth()) || (img1.getHeight() != img2.getHeight())) {
            return false;
        }

        // Perform a pixel-to-pixel comparison
        PixelReader pr1 = img1.getPixelReader();
        PixelReader pr2 = img2.getPixelReader();

        for (int x = 0; x  < img1.getWidth(); x++) {
            for (int y = 0; y < img1.getHeight(); y++) {
                int pix1 = pr1.getArgb(x,y);
                int pix2 = pr2.getArgb(x,y);
                if (pix1 != pix2) {
                    return false;
                }
            }
        }
        return true;
    }
}
