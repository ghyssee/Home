package be.home.main.tools;

import be.home.common.enums.ImageFormat;
import be.home.common.model.windows.WindowAligner;
import be.home.common.model.windows.WindowTO;
import be.home.common.utils.WinUtils;
import com.github.romankh3.image.comparison.ImageComparison;
import com.github.romankh3.image.comparison.ImageComparisonUtil;
import com.github.romankh3.image.comparison.model.ImageComparisonResult;
import com.sun.jna.Pointer;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


public class OCR {

    public static final String CONFIG_PATH = WinUtils.getOneDrivePath() +
            File.separator + "Config" + File.separator + "Java" + File.separator + "tessdata";
    public static final String DATA_PATH = "C:\\My Data\\tessdata\\";

    public static void main(String[] args) {
        //showOpenWindows();
        File fileA = new File("C:\\My Programs\\OneDrive\\Config\\Java\\tessdata\\DoubleSpeedBusy.png");
        File fileB = new File("C:\\My Programs\\OneDrive\\Config\\Java\\tessdata\\DoubleSpeedStart.png");
        System.out.println("compare: " + compareImg(fileA, fileB));
        //compareImage3(fileA, fileB);
        if (false) {
            String title = "Total Commander";
            WindowTO window = findWindowByPartialTitle(title);
            if (window.getHandle() == null) {
                System.err.println("Window " + title + " not found");
            } else {
                System.out.println(String.format("%-40.40s | X:%-4d Y:%-4d        | %-7d x %-7d",
                        window.getTitle(), window.getBounds().x, window.getBounds().y, window.getBounds().width, window.getBounds().height));
                activateWindow(window.getHandle());
                mouseClickRelativeToWindow(window.getBounds(), 700, 200);
                try {
                    makeScreenshot(window.getBounds(), "TotalCommander.png", ImageFormat.PNG);
                    makeScreenshotRelativeToWindow(window.getBounds(), 21, 263, 131, 20, "TcmdTest.png", ImageFormat.PNG);
                    getOCRTextRelativeToWindow(window.getBounds(), 21, 263, 131, 20);
                } catch (AWTException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (true) {
            try {
                WindowTO window2 = findWindowByPartialTitle("LDPlayer");
            //Rectangle bounds = WindowAligner.getRobotCompatibleBounds(window2.getHandle());

                System.out.println(String.format("%-40.40s | X:%-4d Y:%-4d        | %-7d x %-7d",
                    window2.getTitle(), window2.getBounds().x, window2.getBounds().y, window2.getBounds().width, window2.getBounds().height));
                activateWindow(window2.getHandle());

                makeScreenshot(window2.getBounds(), "CurrentWindow.png", ImageFormat.PNG);
                // World Energy
                makeScreenshotRelativeToWindow(window2.getBounds(), 56, 48, 45, 15, "EnergyRel.png", ImageFormat.PNG);
                // Replay Button
                makeScreenshotRelativeToWindow(window2.getBounds(), 92, 722, 90, 30, "Replay.png", ImageFormat.PNG);
                // Double Speed Button
                File dsFile = makeScreenshotRelativeToWindow(window2.getBounds(), 350, 42, 25, 22, "DoubleSpeed.bmp", ImageFormat.BMP);
                //checkIfStarted(window2);
                checkIfNotStarted(window2, dsFile);
                for (int i=1; i<=5; i++){
                    TimeUnit.MILLISECONDS.sleep(100);
                    makeScreenshotRelativeToWindow(window2.getBounds(), 350, 42, 25, 22, "DoubleSpeedStart" + String.valueOf(i) + ").bmp", ImageFormat.BMP);
                }

                //getOCRText(222, 55, 40, 15);
                //getOCRTextRelativeToWindow(window2.getBounds(), 42, 15, 40, 15);
                //makeScreenshot(426, 48, 28, 20, "CheckStart.png");
            } catch (AWTException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }



        //System.out.println(compareImage(new File("C:\\My Data\\tessdata\\test.png"), new File("C:\\My Data\\tessdata\\test2.png")));
        //mouseClick(800,200, 340, 120);

        //getOCRText(100, 150, 400, 200);


    }

    public static boolean compareImg(File fileA, File fileB){
        int threshold = 50;
        try {
            BufferedImage imgA = ImageIO.read(fileA);
            BufferedImage imgB = ImageIO.read(fileB);

            if (imgA == null || imgB == null) return false;
            if (imgA.getWidth() != imgB.getWidth() || imgA.getHeight() != imgB.getHeight()) return false;

            int width = imgA.getWidth();

            int height = imgA.getHeight();
            boolean identicalWithinTolerance = true;

            // Loop door alle pixels
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgbA = imgA.getRGB(x, y);
                    int rgbB = imgB.getRGB(x, y);

                    // Haal de afzonderlijke RGB-waarden op
                    int rA = (rgbA >> 16) & 0xFF;
                    int gA = (rgbA >> 8) & 0xFF;
                    int bA = rgbA & 0xFF;

                    int rB = (rgbB >> 16) & 0xFF;
                    int gB = (rgbB >> 8) & 0xFF;
                    int bB = rgbB & 0xFF;

                    // Bereken het absolute verschil per kanaal
                    int diffR = Math.abs(rA - rB);
                    int diffG = Math.abs(gA - gB);
                    int diffB = Math.abs(bA - bB);


                    // Als het verschil op een kanaal groter is dan de drempel, zijn ze écht anders
                    if (diffR > threshold || diffG > threshold || diffB > threshold) {
                        System.out.println("diffR: " + diffR);
                        System.out.println("threshold: " + threshold);
                        identicalWithinTolerance = false;
                        // Je kunt hier eventueel direct 'return false;' doen voor maximale snelheid,
                        // tenzij je de hele afbeelding wilt scannen (bijv. om te highlighten).
                        System.out.println("Different Images");
                        return false;
                    }
                }
            }
            System.out.println("Images are the same");
            return identicalWithinTolerance;

        } catch (Exception e) {
            System.err.println("Fout bij vergelijken: " + e.getMessage());
            return false;
        }
    }

    public static void checkIfStarted(WindowTO window) throws AWTException {
        File dsButton = makeScreenshotRelativeToWindow(window.getBounds(), 347, 43, 27, 20, "DoubleSpeedBusy.bmp", ImageFormat.BMP);
        compareImg(dsButton, new File(CONFIG_PATH + File.separator + "DoubleSpeedBusy.bmp"));
        compareImageLib(dsButton, new File(CONFIG_PATH + File.separator + "DoubleSpeedBusy.png"));
    }

    public static void checkIfNotStarted(WindowTO window, File dsButton) throws AWTException {
        //File dsButton = makeScreenshotRelativeToWindow(window.getBounds(), 347, 43, 27, 20, "DoubleSpeedStart.bmp", ImageFormat.BMP);
        compareImg(dsButton, new File(CONFIG_PATH + File.separator + "DoubleSpeedStart.bmp"));
        compareImageLib(dsButton, new File(CONFIG_PATH + File.separator + "DoubleSpeedStart.bmp"));

    }
    public static void activateWindow(HWND hwnd){
        User32.INSTANCE.ShowWindow(hwnd, 9);
        User32.INSTANCE.SetForegroundWindow(hwnd);
    }
    public static String getOCRTextRelativeToWindow(Rectangle bounds, int left, int top, int width, int height){
        Rectangle screenRect = new Rectangle(bounds.x + left, bounds.y + top, width, height);
        return getOCRText(screenRect);
    }

    public static String getOCRText(Rectangle bounds){
        String resultText = null;
        try {
            // 1. Definieer de coördinaten en grootte van het schermgedeelte (X, Y, Breedte, Hoogte)

            // 2. Maak een screenshot van het gedefinieerde gebied
            Robot robot = new Robot();
            BufferedImage screenCapture = robot.createScreenCapture(bounds);

            // Definieer de bestandslocatie en naam
            File outputFile = new File("c:/My Data/tessdata/screenshot.png");

            // Schrijf het bestand weg naar de harde schijf
            try {
                ImageIO.write(screenCapture, "png", outputFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // 3. Initialiseer Tesseract OCR
            Tesseract tesseract = new Tesseract();

            // TIP: Geef het pad op naar uw 'tessdata' map (bevat language packs zoals eng.traineddata)
            // Download deze van de officiële Tesseract GitHub indien nodig
            tesseract.setDatapath(CONFIG_PATH);
            tesseract.setLanguage("nld"); // Gebruik "eng" voor Engels, "nld" voor Nederlands


            // 4. Voer de OCR uit direct op de BufferedImage in het geheugen
            resultText = tesseract.doOCR(screenCapture);

            // Output het resultaat
            System.out.println("--- Gevonden Tekst ---");
            System.out.println(resultText.trim());

        } catch (TesseractException | AWTException e) {
            System.err.println("OCR Fout: " + e.getMessage());
        }
        return resultText;
    }
    public static String getOCRText(int left, int top, int width, int height){
            // 1. Definieer de coördinaten en grootte van het schermgedeelte (X, Y, Breedte, Hoogte)
            Rectangle screenRect = new Rectangle(left, top, width, height);
            return getOCRText(screenRect);

    }

    public static File makeScreenshotRelativeToWindow(Rectangle bounds, int left, int top, int width, int height, String filename, ImageFormat format) throws AWTException{
        Rectangle screenRect = new Rectangle(bounds.x + left, bounds.y + top, width, height);
        return makeScreenshot(screenRect, filename, format);
    }
    public static File makeScreenshot(int left, int top, int width, int height, String filename, ImageFormat format) throws AWTException{
        Rectangle screenRect = new Rectangle(left, top, width, height);
        return makeScreenshot(screenRect, filename, format);
    }

    public static File makeScreenshot(Rectangle bounds, String filename, ImageFormat format) throws AWTException {
        Robot robot = new Robot();

        BufferedImage screenshot = robot.createScreenCapture(bounds);


        // Definieer de bestandslocatie en naam
        File outputFile = new File(DATA_PATH + filename);
        // Schrijf het bestand weg naar de harde schijf
        try {
            ImageIO.write(screenshot, format.name(), outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return outputFile;
    }

    public static WindowTO findWindowByPartialTitle(String partialTitle) {
        // Shared container to hold the result window handle
        WindowTO window = new WindowTO();
        final HWND[] foundWindow = { null };

        // Define the callback function that runs for every open window
        boolean b = User32.INSTANCE.EnumWindows(new WinUser.WNDENUMPROC() {
            @Override
            public boolean callback(HWND hwnd, Pointer data) {
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

    public static void compareImageLib(File fileA, File fileB) {

                // 1. Laad de verwachte (baseline) en actuele afbeeldingen
                BufferedImage expected = ImageComparisonUtil.readImageFromResources(fileA.getAbsolutePath());
                BufferedImage actual = ImageComparisonUtil.readImageFromResources(fileB.getAbsolutePath());

                // Locatie waar de visuele 'diff' (met rode vierkanten) moet worden opgeslagen bij een mismatch
                File resultDestination = new File("c:/My Data/tessdata/visual-diff.png");

                // 2. Initialiseer de vergelijking
                ImageComparison imageComparison = new ImageComparison(expected, actual, resultDestination);

                // 3. Configureer perceptuele tolerantie
                //imageComparison.setThreshold(2);

                // Optioneel: Sta toe dat een klein percentage van de totale pixels compleet mag afwijken
                // (handig voor een knipperende cursor of een dynamisch element)
                //imageComparison.setAllowingPercentOfDifferentPixels(1.0);

                // Kwalificeer individuele pixels als gelijk als het kleurverschil binnen 15% valt (handig voor compressie)
                imageComparison.setPixelToleranceLevel(0.10);

                // Groepeer afwijkende pixels pas in een rode marker als de foutzone groter is dan 4x4 pixels
                //imageComparison.setMinimalRectangleSize(16);

                // 4. Voer de vergelijking uit
                ImageComparisonResult result = imageComparison.compareImages();

                // 5. Assert dat de status MATCH is
                // Als dit faalt, staat er een visuele diff klaar in de gedefinieerde bestemming met rode kaders
        System.out.println("Match: " + result.getImageComparisonState());
        System.out.println("Percent: " + result.getDifferencePercent());
        //ImageComparisonState.MATCH

    }


    public static void mouseClick(int left, int top, int x, int y){

        // left + top: coordinants of the window
        // x + y : position relative to the window

        try {
            // Instantiate the Robot class
            Robot robot = new Robot();


            // Move the mouse pointer to the specific (X, Y) coordinates
            robot.mouseMove(left+x, top+y);

            // Press the left mouse button
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);

            // Optional delay between press and release to simulate a realistic click
            robot.delay(getRandomNumber(40,80));

            // Release the left mouse button
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

            System.out.println("Successfully clicked at position: (" + x + ", " + y + ")");

        } catch (AWTException e) {
            System.err.println("The platform configuration does not allow low-level input control.");
            e.printStackTrace();
        }
    }

    public static void mouseClickRelativeToWindow(Rectangle bounds, int x, int y){

        // left + top: coordinants of the window
        // x + y : position relative to the window

        try {
            // Instantiate the Robot class
            Robot robot = new Robot();

            int absX = bounds.x+x;
            int absY = bounds.y+y;


            // Move the mouse pointer to the specific (X, Y) coordinates
            robot.mouseMove(absX, absY);

            // Press the left mouse button
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);

            // Optional delay between press and release to simulate a realistic click
            robot.delay(getRandomNumber(40,80));

            // Release the left mouse button
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

            System.out.println("Successfully clicked at position: (" + absX + ", " + absY + ")");

        } catch (AWTException e) {
            System.err.println("The platform configuration does not allow low-level input control.");
            e.printStackTrace();
        }
    }
    public static int getRandomNumber(int min, int max){

        // Generates a random number between 5 (inclusive) and 11 (exclusive) -> [5, 10]
        int randomNum = ThreadLocalRandom.current().nextInt(min, max);

        // Generates a random number where BOTH min and max are inclusive -> [5, 11]
        int randomNumInclusive = ThreadLocalRandom.current().nextInt(min, max + 1);
        System.out.println("randomNumInclusive:" + randomNumInclusive);
        return randomNumInclusive;
    }

    public static void sleep(int seconds){
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
            System.err.println("Thread was interrupted!");
        }
    }

    public String makeUniqueFileName(String destination, String extension) {
        boolean exit;
        String filename;
        int count = 1;
        do {
            filename = destination + "." + StringUtils.leftPad(String.valueOf(count), 5, '0') + extension;
            File file = new File(filename);
            count++;
            exit = !file.exists();

        }
        while (!exit);
        return filename;
    }

}