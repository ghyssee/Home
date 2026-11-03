package be.home.main.tools;

import be.home.common.model.windows.WindowAligner;
import be.home.common.model.windows.WindowTO;
import be.home.common.utils.WinUtils;
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
        if (true) {
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
                    makeScreenshot(window.getBounds(), "TotalCommander.png");
                    makeScreenshotRelativeToWindow(window.getBounds(), 21, 263, 131, 20, "TcmdTest.png");
                    getOCRTextRelativeToWindow(window.getBounds(), 21, 263, 131, 20);
                } catch (AWTException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (false) {
            try {
                WindowTO window2 = findWindowByPartialTitle("LDPlayer");
            //Rectangle bounds = WindowAligner.getRobotCompatibleBounds(window2.getHandle());

                System.out.println(String.format("%-40.40s | X:%-4d Y:%-4d        | %-7d x %-7d",
                    window2.getTitle(), window2.getBounds().x, window2.getBounds().y, window2.getBounds().width, window2.getBounds().height));
                activateWindow(window2.getHandle());

                makeScreenshot(window2.getBounds(), "CurrentWindow.png");
                makeScreenshot(222, 55, 40, 15, "Energy.png");
                makeScreenshotRelativeToWindow(window2.getBounds(), 42, 40, 40, 15, "EnergyRel.png");
                // Get World Energy
                getOCRText(222, 55, 40, 15);
                getOCRTextRelativeToWindow(window2.getBounds(), 42, 15, 40, 15);
                makeScreenshot(426, 48, 28, 20, "CheckStart.png");
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }



        //System.out.println(compareImage(new File("C:\\My Data\\tessdata\\test.png"), new File("C:\\My Data\\tessdata\\test2.png")));
        //mouseClick(800,200, 340, 120);

        //getOCRText(100, 150, 400, 200);


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

    public static void makeScreenshotRelativeToWindow(Rectangle bounds, int left, int top, int width, int height, String filename) throws AWTException{
        Rectangle screenRect = new Rectangle(bounds.x + left, bounds.y + top, width, height);
        makeScreenshot(screenRect, filename);
    }
    public static void makeScreenshot(int left, int top, int width, int height, String filename) throws AWTException{
        Rectangle screenRect = new Rectangle(left, top, width, height);
        makeScreenshot(screenRect, filename);
    }

    public static void makeScreenshot(Rectangle bounds, String filename) throws AWTException {
        Robot robot = new Robot();

        BufferedImage screenshot = robot.createScreenCapture(bounds);


        // Definieer de bestandslocatie en naam
        File outputFile = new File(DATA_PATH + filename);
        // Schrijf het bestand weg naar de harde schijf
        try {
            ImageIO.write(screenshot, "png", outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public static void showOpenWindows(){

        final HWND[] foundWindow = { null };

        // Roep de Windows API aan om door alle open vensters te lopen
        User32.INSTANCE.EnumWindows((hwnd, ptr) -> {
            // Controleer of het venster daadwerkelijk zichtbaar is voor de gebruiker
            if (User32.INSTANCE.IsWindowVisible(hwnd)) {
                char[] windowText = new char[512];

                // Haal de titel van het venster op
                User32.INSTANCE.GetWindowText(hwnd, windowText, 512);
                String title = Native.toString(windowText);

                // Print de titel alleen als deze niet leeg is
                if (!title.isEmpty()) {

                    //WinUser.RECT rect = new WinUser.RECT();
                    //User32.INSTANCE.GetWindowRect(hwnd, rect);

                   Rectangle  bounds = WindowAligner.getRobotCompatibleBounds(hwnd);

                    // Bereken breedte en hoogte op basis van de randen
                    //int width = rect.right - rect.left;
                    //int height = rect.bottom - rect.top;

                    //int javaX = (int) (externX / scaleX);
                    //int javaY = (int) (externY / scaleY);

                    // Print de naam, X/Y startpositie en de dimensies
                    //System.out.println(String.format("%-40.40s | X:%-4d Y:%-4d        | %-7d x %-7d",
                    //        title, rect.left, rect.top, width, height));
                    System.out.println(String.format("%-40.40s | X:%-4d Y:%-4d        | %-7d x %-7d",
                            title, bounds.x, bounds.y, bounds.width, bounds.height));
                    if (title.startsWith("LDPlayer")){
                        User32.INSTANCE.ShowWindow(hwnd, 9);
                        // 2. Breng het venster naar de voorgrond en geef het focus
                        User32.INSTANCE.SetForegroundWindow(hwnd);
                        //makeScreenshot(rect.left, rect.top, width, height);
                        //makeScreenshot(rect.left-10, rect.top, width-150, height);
                    }

                }
            }
            return true; // Ga door naar het volgende venster
        }, null);
    }

    public static float compareImage(File fileA, File fileB) {

        float percentage = 0;
        try {
            // take buffer data from both image files //
            BufferedImage biA = ImageIO.read(fileA);
            DataBuffer dbA = biA.getData().getDataBuffer();
            int sizeA = dbA.getSize();
            BufferedImage biB = ImageIO.read(fileB);
            DataBuffer dbB = biB.getData().getDataBuffer();
            int sizeB = dbB.getSize();
            int count = 0;
            // compare data-buffer objects //
            if (sizeA == sizeB) {

                for (int i = 0; i < sizeA; i++) {

                    if (dbA.getElem(i) == dbB.getElem(i)) {
                        count = count + 1;
                    }

                }
                percentage = (count * 100) / sizeA;
            } else {
                System.out.println("Both the images are not of same size");
            }

        } catch (Exception e) {
            System.out.println("Failed to compare image files ...");
        }
        return percentage;
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