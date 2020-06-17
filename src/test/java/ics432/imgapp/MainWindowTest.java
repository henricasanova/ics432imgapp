package ics432.imgapp;

import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.framework.junit5.Stop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("ALL")
@ExtendWith(ApplicationExtension.class)
class MainWindowTest {

    private MainWindow mw;
    private Stage stage;

    /**
     * Code to start the Application
     *
     * @param stage The stage
     */
    @Start
    void start(Stage stage) {

        MainWindow mw = new MainWindow(stage, 800, 400);
        stage.show();
        stage.toFront();
        this.stage = stage;
        this.mw = mw;
    }

    /**
     * Code to stop the Application
     */
    @Stop
    void stop() {
        stage.close();
    }

    /**
     * @param robot Will be injected by the test runner.
     */
    @Test
//    @Disabled
    void checkButtonsOnStartup(FxRobot robot) {

        // Quit Button
        Button quitButton = robot.lookup("#quitButton").queryButton();
        Assertions.assertThat(quitButton).hasText("Quit");
        Assertions.assertThat(quitButton).isEnabled();

        // AddFiles Button
        Button addFilesButton = robot.lookup("#addFilesButton").queryButton();
        Assertions.assertThat(addFilesButton).hasText("Add Image Files");
        Assertions.assertThat(addFilesButton).isEnabled();

        // CreateJob Button
        Button createJobButton = robot.lookup("#createJobButton").queryButton();
        Assertions.assertThat(createJobButton).hasText("Create Job");
        Assertions.assertThat(createJobButton).isDisabled();
    }

    /**
     * A test to check that the Quit button works
     *
     * @param robot Will be injected by the test runner.
     */
    @Test
//    @Disabled
    void checkQuitButton(FxRobot robot) {

        // Quit Button
        Button quitButton = robot.lookup("#quitButton").queryButton();

        robot.clickOn(quitButton);
        robot.sleep(1000);

        assert(!this.stage.isShowing());
    }

    /**
     * A test to check that when files are added, they do show up in the FileListWithViewPort
     *
     * This test uses reflection to access private methods.
     *
     * @param robot Will be injected by the test runner.
     */
    @Test
//    @Disabled
    void addFiles(FxRobot robot) {

        // Create a list of files
        List<Path> fileList = new ArrayList<>();

        String[] filenames = {"fish1.jpg", "fish2.jpg"};
        for (String f : filenames) {
            Path resourceDirectory = Paths.get("src", "test", "resources", f);
            String absolutePath = resourceDirectory.toFile().getAbsolutePath();
            fileList.add(Paths.get(absolutePath));
        }

        // Get the (private!) addFiles() method
        Method m = TestUtil.getAddFilesMethod(MainWindow.class);

        // Invoke it!
        try {
            m.invoke(mw, fileList);
        } catch (IllegalAccessException | InvocationTargetException e) {
            Assertions.fail(e.toString());
        }

        robot.sleep(1000);

        //  Check the fileListWithViewPort
        FileListWithViewPort flwvp = null;
        try {
            flwvp = (FileListWithViewPort) TestUtil.getFieldByName(MainWindow.class, "fileListWithViewPort").get(mw);
        } catch (IllegalAccessException e) {
            Assertions.fail(e.toString());
        }

        assert(flwvp.getNumFiles() == 2);

        ListView<Path> lv = null;
        try {
            lv = (ListView<Path>) TestUtil.getFieldByName(FileListWithViewPort.class, "availableFilesView").get(flwvp);
        } catch (IllegalAccessException e) {
            Assertions.fail(e.toString());
        }

        lv.getSelectionModel().selectFirst();
        robot.clickOn(lv);
        robot.sleep(1000);

        assert(flwvp.getSelection().size() == 1);
    }

    /**
     * A test to check that the Create Job button works as expected
     *
     * @param robot Will be injected by the test runner.
     */
    @Test
//    @Disabled
    void createJob(FxRobot robot) {

        // Create a list of files
        List<Path> fileList = new ArrayList<>();

        String[] filenames = {"fish1.jpg", "fish2.jpg"};
        for (String f : filenames) {
            Path resourceDirectory = Paths.get("src", "test", "resources", f);
            String absolutePath = resourceDirectory.toFile().getAbsolutePath();
            fileList.add(Paths.get(absolutePath));
        }

        // Get the (private!) addFiles() method
        Method m = TestUtil.getAddFilesMethod(MainWindow.class);

        // Invoke it!
        try {
            m.invoke(mw, fileList);
        } catch (IllegalAccessException | InvocationTargetException e) {
            Assertions.fail(e.toString());
        }

        Window mainWindow = Stage.getWindows().get(0);

        // Select the files
        FileListWithViewPort flwvp = null;
        try {
            flwvp = (FileListWithViewPort) TestUtil.getFieldByName(MainWindow.class, "fileListWithViewPort").get(mw);
        } catch (IllegalAccessException e) {
            Assertions.fail(e.toString());
        }

        ListView<Path> lv = null;
        try {
            lv = (ListView<Path>) TestUtil.getFieldByName(FileListWithViewPort.class, "availableFilesView").get(flwvp);
        } catch (IllegalAccessException e) {
            Assertions.fail(e.toString());
        }

        robot.clickOn(lv);
        robot.press(KeyCode.DOWN);
        robot.sleep(1000);
        lv.getSelectionModel().selectAll();
        robot.sleep(1000);

        // Click the button
        Button createJobButton = robot.lookup("#createJobButton").queryButton();

        robot.clickOn(createJobButton);

        robot.sleep(1000);

        // Check that the window has popped up, somehow
        assert(Stage.getWindows().size() == 2);
        assert(Stage.getWindows().get(0) == mainWindow);

        Window jobWindow = Stage.getWindows().get(1);
        assert (jobWindow instanceof JobWindow);

        // Check that the Main window's quit button is disabled
        robot.clickOn(mainWindow);
        Button quitButton = robot.lookup("#quitButton").queryButton();

        assert(quitButton.isDisabled());

        // Close the Job window
        JobWindow jw = (JobWindow) jobWindow;
        robot.clickOn(jw);
        Button closeButton = robot.lookup("#closeButton").queryButton();
        robot.clickOn(closeButton);

        assert(Stage.getWindows().size() == 1);

        // Check that the Main window's quit button is enabled
        assert(!quitButton.isDisabled());

    }
}
