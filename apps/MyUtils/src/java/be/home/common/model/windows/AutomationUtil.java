package be.home.common.model.windows;

import be.home.common.enums.ImageFormat;
import com.github.romankh3.image.comparison.ImageComparison;
import com.github.romankh3.image.comparison.ImageComparisonUtil;
import com.github.romankh3.image.comparison.model.ImageComparisonResult;
import com.github.romankh3.image.comparison.model.ImageComparisonState;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.MultiResolutionImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AutomationUtil{

        public String DATA_PATH = "C:\\My Data\\tessdata\\";

        public String getDatapath(){
            return DATA_PATH;
        }

        public AutomationUtil(String dataPath){
            DATA_PATH = dataPath;
        }

        public AutomationUtil(){
        }

        public static File makeScreenshot(Rectangle bounds, String filename, ImageFormat format) throws AWTException {
            Robot robot = new Robot();

            BufferedImage screenshot = robot.createScreenCapture(bounds);


            // Definieer de bestandslocatie en naam
            File outputFile = new File(filename);
            // Schrijf het bestand weg naar de harde schijf
            try {
                ImageIO.write(screenshot, format.name(), outputFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return outputFile;
        }

        public WindowTO findWindowByPartialTitle(String partialTitle) {
            // Shared container to hold the result window handle
            WindowTO window = new WindowTO();
            final WinDef.HWND[] foundWindow = { null };

            // Define the callback function that runs for every open window
            boolean b = User32.INSTANCE.EnumWindows(new WinUser.WNDENUMPROC() {
                @Override
                public boolean callback(WinDef.HWND hwnd, Pointer data) {
                    // Determine the length of the window title
                    int titleLength = User32.INSTANCE.GetWindowTextLength(hwnd);
                    if (titleLength == 0) {
                        return true; // No title, skip and continue searching
                    }

                    // Buffer to hold the extracted title string
                    char[] buffer = new char[titleLength + 1];
                    User32.INSTANCE.GetWindowText(hwnd, buffer, titleLength + 1);
                    String windowTitle = Native.toString(buffer);

                    // Perform the partial match check (case-insensitive option)
                    if (windowTitle.toLowerCase().startsWith(partialTitle.toLowerCase())) {
                        foundWindow[0] = hwnd; // Found it! Store the handle.
                        window.setHandle(hwnd);
                        char[] windowText = new char[512];
                        // Haal de titel van het venster op
                        User32.INSTANCE.GetWindowText(hwnd, windowText, 512);
                        window.setTitle(Native.toString(windowText));
                        window.setBounds(WindowAligner.getRobotCompatibleBounds(hwnd));
                        return false;          // Return false to stop enumerating windows
                    }

                    return true; // Return true to keep searching next windows
                }
            }, null);

            return window;
        }

        public BufferedImage capturePhysicalScreen(Robot robot, Rectangle logicalRect) {
            // 1. Capture both the scaled (logical) and unscaled (physical) image variants
            MultiResolutionImage mrImage = robot.createMultiResolutionScreenCapture(logicalRect);

            // 2. Get all resolution variants (index 0 = scaled logical, last index = native physical)
            List<Image> variants = mrImage.getResolutionVariants();
            Image highestRes = variants.get(variants.size() - 1);

            // 3. Return as BufferedImage (already a BufferedImage in OpenJDK/Oracle JDK on Windows)
            if (highestRes instanceof BufferedImage) {
                return (BufferedImage) highestRes;
            }

            // Fallback conversion if a custom GraphicsEnvironment returns a generic Image
            BufferedImage buffered = new BufferedImage(
                    highestRes.getWidth(null),
                    highestRes.getHeight(null),
                    BufferedImage.TYPE_INT_RGB
            );
            buffered.getGraphics().drawImage(highestRes, 0, 0, null);
            return buffered;
        }

        public BufferedImage getBufferedImage(WindowTO window, Rectangle bounds) throws AWTException, IOException {
            Robot robot = new Robot();
            BufferedImage fullPhysicalWindow = capturePhysicalScreen(robot, window.getBounds());
            BufferedImage actualCrop = fullPhysicalWindow.getSubimage(bounds.x, bounds.y, bounds.width, bounds.height);
            return actualCrop;

        }

        public void moveVisibleWindow(WinDef.HWND window2, int robotTargetX, int robotTargetY) {

            final int DWMWA_EXTENDED_FRAME_BOUNDS = 9;

            // 1. Convert Robot (DPI-scaled) coordinates back to physical screen pixels
            GraphicsConfiguration gc = GraphicsEnvironment
                    .getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice()
                    .getDefaultConfiguration();
            double scaleX = gc.getDefaultTransform().getScaleX();
            double scaleY = gc.getDefaultTransform().getScaleY();

            int physicalTargetX = (int) Math.round(robotTargetX * scaleX);
            int physicalTargetY = (int) Math.round(robotTargetY * scaleY);

            // 2. Measure the invisible Windows 11 shadow border on this specific window
            WinDef.RECT windowRect = new WinDef.RECT();
            WinDef.RECT visibleRect = new WinDef.RECT();

            User32.INSTANCE.GetWindowRect(window2, windowRect);
            WindowAligner.Dwmapi.INSTANCE.DwmGetWindowAttribute(
                    window2,
                    DWMWA_EXTENDED_FRAME_BOUNDS,
                    visibleRect,
                    visibleRect.size()
            );
            visibleRect.read();

            // Calculate the exact invisible border thickness (usually ~7px on left, 0px on top)
            int borderOffsetX = visibleRect.left - windowRect.left;
            int borderOffsetY = visibleRect.top - windowRect.top;

            // 3. Move the window so its visible top-left corner lands at (110, 6)
            User32.INSTANCE.SetWindowPos(
                    window2,
                    null,
                    physicalTargetX - borderOffsetX,
                    physicalTargetY - borderOffsetY,
                    0,
                    0,
                    User32.SWP_NOSIZE | User32.SWP_NOZORDER
            );
        }

        public void activateWindow(WinDef.HWND hwnd){
            User32.INSTANCE.ShowWindow(hwnd, 9);
            User32.INSTANCE.SetForegroundWindow(hwnd);
        }

        public File makeScreenshotWithinWindow(WindowTO window, String filename, Rectangle bounds) throws AWTException, IOException {
            Robot robot = new Robot();
            File outputFile = new File(getDatapath() + filename);
            File test = new File(getDatapath() + "test.BMP");
            AutomationUtil automationUtil = new AutomationUtil();
            BufferedImage fullPhysicalWindow = automationUtil.capturePhysicalScreen(robot, window.getBounds());
            ImageIO.write(fullPhysicalWindow, "BMP", test);
            BufferedImage actualCrop = fullPhysicalWindow.getSubimage(bounds.x, bounds.y, bounds.width, bounds.height);
            ImageIO.write(actualCrop, "BMP", outputFile);
            return outputFile;

        }

        public int getRandomNumber(int min, int max){

            // Generates a random number between min (inclusive) and max (exclusive)
            //int randomNum = ThreadLocalRandom.current().nextInt(min, max);

            // Generates a random number where BOTH min and max are inclusive
            int randomNumInclusive = ThreadLocalRandom.current().nextInt(min, max + 1);
            return randomNumInclusive;
        }

        public void mouseMoveRelativeToWindow(Rectangle windowBounds, int x, int y) throws AWTException {
            mouseRelativeToWindow(windowBounds, MOUSE_TYPE.MOVE, x, y);
        }

        public void mouseClickRelativeToWindow(Rectangle windowBounds, int x, int y) throws AWTException {
            mouseRelativeToWindow(windowBounds, MOUSE_TYPE.CLICK, x, y);
        }

        public void mouseMoveRelativeToWindow(Rectangle windowBounds, Rectangle bounds) throws AWTException {
            double physicalX = bounds.x + (bounds.width / 2.0);
            double physicalY = bounds.y + (bounds.height / 2.0);
            mouseMoveRelativeToWindow(windowBounds, (int) Math.round(physicalX), (int) Math.round(physicalY));
        }

        public void mouseClickRelativeToWindow(Rectangle windowBounds, Rectangle bounds) throws AWTException {
            double physicalX = bounds.x + (bounds.width / 2.0);
            double physicalY = bounds.y + (bounds.height / 2.0);
            mouseClickRelativeToWindow(windowBounds, (int) Math.round(physicalX), (int) Math.round(physicalY));
        }

        public void mouseRelativeToWindow(Rectangle windowBounds, MOUSE_TYPE mouse, int x, int y) throws AWTException {

            // left + top: coordinants of the window
            // x + y : position relative to the window


            // Instantiate the Robot class
            Robot robot = new Robot();

            // 1. Get the physical center
            double physicalX = windowBounds.x + x;
            double physicalY = windowBounds.y + y;

            // 2. Get Windows 11 scaling factor (1.25 on your system)
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            AffineTransform transform = gd.getDefaultConfiguration().getDefaultTransform();
            double scaleX = transform.getScaleX();
            double scaleY = transform.getScaleY();

            // 3. Convert to Java Robot coordinates
            int robotX = (int) Math.round(physicalX / scaleX);
            int robotY = (int) Math.round(physicalY / scaleY);

            // Move the mouse pointer to the specific (X, Y) coordinates
            robot.mouseMove(robotX, robotY);

            if (mouse.equals(MOUSE_TYPE.CLICK)){
                // Press the left mouse button
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);

                // Optional delay between press and release to simulate a realistic click
                robot.delay(getRandomNumber(40,80));

                // Release the left mouse button
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            }

            System.out.println("Successfully clicked/moved at position: (" + robotX + ", " + robotY + ")");

        }

        public void mouseDrag(Rectangle windowBounds, int startX, int startY, int endX, int endY) throws AWTException, InterruptedException {

            // Instantiate the Robot class
            Robot robot = new Robot();

            // 1. Get the physical center
            double physicalX = windowBounds.x + startX;
            double physicalY = windowBounds.y + startY;

            double physicalEndX = windowBounds.x + endX;
            double physicalEndY = windowBounds.y + endY;

            // 2. Get Windows 11 scaling factor (1.25 on your system)
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            AffineTransform transform = gd.getDefaultConfiguration().getDefaultTransform();
            double scaleX = transform.getScaleX();
            double scaleY = transform.getScaleY();

            // 3. Convert to Java Robot coordinates
            int robotX = (int) Math.round(physicalX / scaleX);
            int robotY = (int) Math.round(physicalY / scaleY);
            int robotEndX = (int) Math.round(physicalEndX / scaleX);
            int robotEndY = (int) Math.round(physicalEndY / scaleY);
            //robot.mouseMove(startX, startY);
            robot.mouseMove(robotX, robotY);

            robot.delay(200);

            // 2. Houd de linkermuisknop ingedrukt
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.delay(100);

            // 3. Sleep de muis in kleine stapjes naar het eindpunt
            int steps = 50;
            for (int i = 1; i <= steps; i++) {
                int currentX = robotX + ((robotEndX - robotX) * i) / steps;
                int currentY = robotY + ((robotEndY - robotY) * i) / steps;
                robot.mouseMove(currentX, currentY);
                robot.delay(10); // Korte pauze voor een vloeiende beweging
            }
            System.out.println("Drag Mouse start position: (" + robotX + ", " + robotY + ")");
            System.out.println("Drag Mouse End position: (" + robotEndX + ", " + robotEndY + ")");

            // 4. Laat de linkermuisknop weer los
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        }

        /*
        public void mouseClickRelativeToWindow(Rectangle bounds, Rectangle button) throws AWTException {

            // left + top: coordinants of the window
            // x + y : position relative to the window

            // Instantiate the Robot class
            Robot robot = new Robot();
            AutomationUtil au = new AutomationUtil();

            // 1. Get the physical center
            double physicalX = bounds.x + button.x + (button.width / 2.0);   // 563.0
            double physicalY = bounds.y + button.y + (button.height / 2.0);  // 73.0

            // 2. Get Windows 11 scaling factor (1.25 on your system)
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            AffineTransform transform = gd.getDefaultConfiguration().getDefaultTransform();
            double scaleX = transform.getScaleX();
            double scaleY = transform.getScaleY();

            // 3. Convert to Java Robot coordinates
            int robotX = (int) Math.round(physicalX / scaleX); // 450
            int robotY = (int) Math.round(physicalY / scaleY); // 58
            robotX = au.getRandomNumber(robotX, robotX+10);
            robotY = au.getRandomNumber(robotY-2, robotY+2);

            // Move the mouse pointer to the specific (X, Y) coordinates
            robot.mouseMove(robotX, robotY);

            // Press the left mouse button
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);

            // Optional delay between press and release to simulate a realistic click
            robot.delay(au.getRandomNumber(40,80));

            // Release the left mouse button
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

            System.out.println("Successfully clicked at position: (" + robotX + ", " + robotY + ")");
        }
        */

    public boolean compareImageLib(File fileA, File fileB) {

        boolean compare = false;
        // 1. Laad de verwachte (baseline) en actuele afbeeldingen
        BufferedImage expected = ImageComparisonUtil.readImageFromResources(fileA.getAbsolutePath());
        BufferedImage actual = ImageComparisonUtil.readImageFromResources(fileB.getAbsolutePath());
        return compareImageLib(expected, actual, true);
    }

    public boolean compareImageLib(BufferedImage imageA, BufferedImage imageB, boolean visualDiff) {

        boolean compare = false;
        // Locatie waar de visuele 'diff' (met rode vierkanten) moet worden opgeslagen bij een mismatch
        File resultDestination = new File(getDatapath() + "visual-diff.png");


        // 2. Initialiseer de vergelijking
        ImageComparison imageComparison;
        if (visualDiff){
            imageComparison = new ImageComparison(imageA, imageB, resultDestination);
        }
        else {
            imageComparison = new ImageComparison(imageA, imageB);
        }


        // 3. Configureer perceptuele tolerantie
        //imageComparison.setThreshold(2);

        // Optioneel: Sta toe dat een klein percentage van de totale pixels compleet mag afwijken
        // (handig voor een knipperende cursor of een dynamisch element)
        imageComparison.setAllowingPercentOfDifferentPixels(4);

        // Kwalificeer individuele pixels als gelijk als het kleurverschil binnen 15% valt (handig voor compressie)
        imageComparison.setPixelToleranceLevel(0.10);

        // Groepeer afwijkende pixels pas in een rode marker als de foutzone groter is dan 4x4 pixels
        imageComparison.setMinimalRectangleSize(4);

        // 4. Voer de vergelijking uit
        ImageComparisonResult result = imageComparison.compareImages();
        //System.out.println("Match: " + result.getImageComparisonState());
        //System.out.println("Percent: " + result.getDifferencePercent());
        if (result.getImageComparisonState().equals(ImageComparisonState.MATCH)
        ){
            compare = true;
        }
        return compare;
    }


    public boolean compareImageLib(BufferedImage imageA, BufferedImage imageB) {
        return compareImageLib(imageA, imageB, false);
    }



    public void combineHorizontally(List<BufferedImage> images, File outputFile) throws IOException {
        if (images == null || images.isEmpty()) return;

        int singleWidth = images.get(0).getWidth();
        int singleHeight = images.get(0).getHeight();
        int totalWidth = singleWidth * images.size();

        // Create the canvas (use TYPE_INT_ARGB to support transparency)
        BufferedImage combined = new BufferedImage(totalWidth, singleHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combined.createGraphics();

        for (int i = 0; i < images.size(); i++) {
            g.drawImage(images.get(i), i * singleWidth, 0, null);
        }
        g.dispose();

        ImageIO.write(combined, "PNG", outputFile);
    }
    public void combineVertically(List<BufferedImage> images, File outputFile) throws IOException {
        if (images == null || images.isEmpty()) return;

        int singleWidth = images.get(0).getWidth();
        int singleHeight = images.get(0).getHeight();
        int totalHeight = singleHeight * images.size();

        BufferedImage combined = new BufferedImage(singleWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combined.createGraphics();

        for (int i = 0; i < images.size(); i++) {
            g.drawImage(images.get(i), 0, i * singleHeight, null);
        }
        g.dispose();

        ImageIO.write(combined, "PNG", outputFile);
    }

    public void alertBox(String title, String message, int messageType){
        /* message types:
            JOptionPane.INFORMATION_MESSAGE
        JOptionPane.WARNING_MESSAGE
        JOptionPane.ERROR_MESSAGE
        JOptionPane.QUESTION_MESSAGE
        JOptionPane.PLAIN_MESSAGE
         */
        JOptionPane.showMessageDialog(
                null,                                 // Parent component (null centers it on screen)
                message, // Message body
                title,                 // Window title
                messageType           // Icon type
        );

    }

    }
