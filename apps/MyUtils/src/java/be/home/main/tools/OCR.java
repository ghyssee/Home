package be.home.main.tools;

import be.home.common.enums.ImageFormat;
import be.home.common.model.windows.WindowAligner;
import be.home.common.model.windows.WindowTO;
import be.home.common.utils.DateUtils;
import be.home.common.utils.WinUtils;
import com.github.romankh3.image.comparison.ImageComparison;
import com.github.romankh3.image.comparison.ImageComparisonUtil;
import com.github.romankh3.image.comparison.model.ImageComparisonResult;
import com.github.romankh3.image.comparison.model.ImageComparisonState;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.MultiResolutionImage;
import java.io.File;
import java.io.IOException;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


public class OCR {

    public static final String CONFIG_PATH = WinUtils.getOneDrivePath() +
            File.separator + "Config" + File.separator + "Java" + File.separator + "tessdata";
    public static final String DATA_PATH = "C:\\My Data\\tessdata\\";

    public static void main(String[] args) throws AWTException, IOException, InterruptedException {
        //showOpenWindows();

        File fileA = new File("C:\\My Programs\\OneDrive\\Config\\Java\\tessdata\\DoubleSpeed.bmp");
        //System.out.println("Busy: " + checkIfStarted(fileA));
        //System.out.println("Start: " + checkIfNotStarted(fileA));


        if (true) {
            WindowTO window2 = findWindowByPartialTitle("LDPlayer");
        //Rectangle bounds = WindowAligner.getRobotCompatibleBounds(window2.getHandle());

            System.out.println(String.format("%-40.40s | X:%-4d Y:%-4d        | %-7d x %-7d",
                window2.getTitle(), window2.getBounds().x, window2.getBounds().y, window2.getBounds().width, window2.getBounds().height));
            activateWindow(window2.getHandle());
            Rectangle windowBounds = new Rectangle(110, 6, 473, 812);
            moveVisibleWindow(window2.getHandle(), 110, 6);
            window2 = findWindowByPartialTitle("LDPlayer");
            System.out.println(String.format("%-40.40s | X:%-4d Y:%-4d        | %-7d x %-7d",
                    window2.getTitle(), window2.getBounds().x, window2.getBounds().y, window2.getBounds().width, window2.getBounds().height));

            //makeScreenshot(window2.getBounds(), "CurrentWindow.png", ImageFormat.PNG);
            //makeScreenshot2(window2, "PlayStart.bmp", 437, 52, 32, 26);
            //makeScreenshot2(window2, "PlayStart2.bmp", 349, 41, 25, 22);
            //for (int i= 1; i <= 5; i++){
             //   makeScreenshot2(window2, "PlayBusy" + String.valueOf(i) + ".bmp", 437, 52, 32, 26);
             //   sleep(TimeUnit.MILLISECONDS, 200);
            //}

            Rectangle startButton = new Rectangle(441, 56, 24, 22);


            //makeScreenshot2(window2, "PlayStart.bmp", playButton2);
            //for (int i=1; i <= 5; i++){
            //    makeScreenshot2(window2, "PlayBusy" + String.valueOf(i) + ".bmp", playButton2);
            //    sleep(TimeUnit.MILLISECONDS, 200);

            //}
            //for (int i=1; i <= 10; i++){
            Rectangle replayButton = new Rectangle(120, 900, 100, 40);
            Rectangle fightButton = new Rectangle(345, 940, 85, 40);
            Rectangle energyButton = new Rectangle(68, 56, 60, 30);
            Rectangle heroecapButton = new Rectangle(145, 360, 270, 35);
            //makeScreenshot2(window2, "heroecap.bmp", heroecapButton);
            int wait = 1;

            if (true) {
                do {
                    mouseMoveRelativeToWindow(window2.getBounds(), 0, 0);
                    wait = processWindow(window2, startButton, replayButton, fightButton, energyButton, heroecapButton);
                    if (wait == -1){
                        break;
                    }
                    sleep(TimeUnit.SECONDS, wait);
                }
                while (true);
            }


        }

    }

    public static void moveVisibleWindow(HWND window2, int robotTargetX, int robotTargetY) {

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

    public static int processWindow (WindowTO window, Rectangle startButton, Rectangle replayButton, Rectangle fightButton, Rectangle energyButton, Rectangle heroecapButton) throws AWTException, IOException, InterruptedException {

        File dsFile = makeScreenshot2(window, "Start.bmp", startButton);
        //File dsFile = makeScreenshotRelativeToWindow(window.getBounds(), 350, 42, 25, 22, "DoubleSpeed.bmp", ImageFormat.BMP);
        int wait = 5;
        if (checkIfStarted(dsFile)){
            System.out.println("Busy");
            return 20;
        }
        else if (checkIfNotStarted(dsFile)) {
            System.out.println("Ready to start");
            System.out.println("Clicking on play button...");
            mouseClickRelativeToWindow(window.getBounds(), startButton);
            return 10;
        }
        else if (checkButton(window, replayButton, "REPLAY")) {

            System.out.println("Wait till all loot is loaded on the screen");
            sleep(TimeUnit.SECONDS, 10);
            Rectangle lootRect = new Rectangle(50, 400, 460, 250);
            int startX = lootRect.x + 50;
            int startY = lootRect.y + lootRect.height/2;
            int i=1;
            List<BufferedImage> images = new ArrayList<>();
            //mouseMoveRelativeToWindow(window.getBounds(), startX, startY);
            boolean compare = false;
            for (i=1; i <= 6; i++) {
                System.out.println("Dragging mouse to see all loot");
                //makeScreenshot2(window, "Loot"+ String.valueOf(i) + ".bmp", lootRect);
                BufferedImage image = getBufferedImage(window, lootRect);
                if (i > 1){
                    compare = compareImageLib(images.get(images.size()-1), image);
                    System.out.println("compare " + i + ": " + compare);
                    if (compare){
                        break;
                    }
                    else {
                        images.add(image);
                    }
                }
                else {
                    images.add(image);
                }
                mouseDrag(window.getBounds(), startX, startY, startX + 220, startY);
                //sleep(TimeUnit.SECONDS, 1);

            }
            String heroesTroopsFile = "HeroesTroops.bmp";
            Rectangle heroesRect = new Rectangle(50, 650, 480, 250);
            //File heroesFile = makeScreenshot2(window, heroesTroopsFile, heroesRect);
            BufferedImage heroesImage = getBufferedImage(window, heroesRect);
            images.add(heroesImage);
            File combinedLootFile = new File(DATA_PATH + "LootCombined. " + DateUtils.formatDate(new Date(), DateUtils.YYYYMMDDHHMMSS) + ".png");
            combineVertically(images, combinedLootFile);
            System.out.println("Loot file: " + combinedLootFile.getAbsolutePath());

            System.out.println("Press the replay button");
            mouseClickRelativeToWindow(window.getBounds(), replayButton);
            return 1;
        }
        else if (checkButton(window, heroecapButton, "HERO CAP REACHED!")) {
            System.out.println("HERO CAP REACHED");
            return -1;
        }
            else if (checkButton(window, fightButton, "FIGHT!")) {
            System.out.println("Press the fight button");
            String energy = getOCRText(window, energyButton);
            System.out.println("Energy: " + energy);
            int intEnergy = getEnergy(energy);
            System.out.println("Converted Energy: " + intEnergy);
            mouseClickRelativeToWindow(window.getBounds(), fightButton);
            return 5;
        }
        return wait;

    }

    public static int getEnergy(String energyText){
        int energy = -1;
        if (StringUtils.isNotBlank(energyText)){
            String energyArr[] = energyText.split("/");
            if (energyArr.length >= 2){
                try {
                    energy = Integer.valueOf(energyArr[0]);
                }
                catch (NumberFormatException ex){
                    System.err.println("Inccorect number: " + energyArr[0]);
                }
            }

        }
        return energy;
    }

    public static boolean checkAndClickButton(WindowTO window, Rectangle bounds, String buttonText) throws IOException {
        String text = getOCRText(window, bounds);
        boolean buttonFound = false;
        if (StringUtils.isNotBlank(text)) {
            if (text.trim().equalsIgnoreCase(buttonText)) {
                mouseClickRelativeToWindow(window.getBounds(), bounds);
                buttonFound = true;
            }
        }
        return buttonFound;
    }

    public static boolean checkButton(WindowTO window, Rectangle bounds, String buttonText) throws IOException {
        String text = getOCRText(window, bounds);
        boolean buttonFound = false;
        if (StringUtils.isNotBlank(text)) {
            if (text.trim().equalsIgnoreCase(buttonText)) {
                buttonFound = true;
            }
        }
        return buttonFound;
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

    public static boolean checkIfStarted(File dsButton)  {
        return compareImageSequence(dsButton, "PlayBusy", 5);
    }

    public static boolean  checkIfNotStarted(File dsButton)  {
        //File dsButton = makeScreenshotRelativeToWindow(window.getBounds(), 347, 43, 27, 20, "DoubleSpeedStart.bmp", ImageFormat.BMP);
        //File fileA = new File(CONFIG_PATH + File.separator + "DoubleSpeedStart.bmp");
        //compareImg(dsButton, fileA);
       // return compareImageLib(dsButton, fileA);
        return compareImageSequence(dsButton, "PlayStart", 1);

    }
    public static void activateWindow(HWND hwnd){
        User32.INSTANCE.ShowWindow(hwnd, 9);
        User32.INSTANCE.SetForegroundWindow(hwnd);
    }

    public static String getOCRText(WindowTO window, Rectangle bounds) throws IOException {
        String resultText = null;
        try {
            // 1. Definieer de coördinaten en grootte van het schermgedeelte (X, Y, Breedte, Hoogte)

            // 2. Maak een screenshot van het gedefinieerde gebied
            Robot robot = new Robot();
            BufferedImage screenCapture = getBufferedImage(window, bounds);

            // Definieer de bestandslocatie en naam
            File outputFile = new File("c:/My Data/tessdata/ocr.png");

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
            //System.out.println("--- Gevonden Tekst ---");
            //System.out.println(resultText.trim());

        } catch (TesseractException | AWTException e) {
            System.err.println("OCR Fout: " + e.getMessage());
        }
        return resultText;
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

    public static BufferedImage capturePhysicalScreen(Robot robot, Rectangle logicalRect) {
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

    public static File makeScreenshot2(WindowTO window, String filename, Rectangle bounds) throws AWTException, IOException {
        Robot robot = new Robot();
        File outputFile = new File(DATA_PATH + filename);
        File test = new File(DATA_PATH + "test.BMP");
        BufferedImage fullPhysicalWindow = capturePhysicalScreen(robot, window.getBounds());
        ImageIO.write(fullPhysicalWindow, "BMP", test);
        BufferedImage actualCrop = fullPhysicalWindow.getSubimage(bounds.x, bounds.y, bounds.width, bounds.height);
        ImageIO.write(actualCrop, "BMP", outputFile);
        return outputFile;

    }

    public static BufferedImage getBufferedImage(WindowTO window,  Rectangle bounds) throws AWTException, IOException {
        Robot robot = new Robot();
        BufferedImage fullPhysicalWindow = capturePhysicalScreen(robot, window.getBounds());
        BufferedImage actualCrop = fullPhysicalWindow.getSubimage(bounds.x, bounds.y, bounds.width, bounds.height);
        return actualCrop;

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

    public static boolean compareImageLib(File fileA, File fileB) {

        boolean compare = false;
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
        imageComparison.setAllowingPercentOfDifferentPixels(4);

        // Kwalificeer individuele pixels als gelijk als het kleurverschil binnen 15% valt (handig voor compressie)
        imageComparison.setPixelToleranceLevel(0.10);

        // Groepeer afwijkende pixels pas in een rode marker als de foutzone groter is dan 4x4 pixels
        imageComparison.setMinimalRectangleSize(4);

        // 4. Voer de vergelijking uit
        ImageComparisonResult result = imageComparison.compareImages();

        // 5. Assert dat de status MATCH is
        // Als dit faalt, staat er een visuele diff klaar in de gedefinieerde bestemming met rode kaders
        //System.out.println("Match: " + result.getImageComparisonState());
        //System.out.println("Percent: " + result.getDifferencePercent());
        if (result.getImageComparisonState().equals(ImageComparisonState.MATCH)
        ){
            compare = true;
        }
        return compare;

    }

    public static boolean compareImageLib(BufferedImage imageA, BufferedImage imageB) {

        boolean compare = false;


        // 2. Initialiseer de vergelijking
        ImageComparison imageComparison = new ImageComparison(imageA, imageB);

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

        if (result.getImageComparisonState().equals(ImageComparisonState.MATCH)
        ){
            compare = true;
        }
        return compare;

    }

    public static boolean compareImageSequence(File fileA, String fileName, int limit) {

        boolean compare = false;
        for (int i=1; i <= limit; i++){
            File fileB = new File(CONFIG_PATH + File.separator + fileName + String.valueOf(i) + ".bmp");
            if (fileB.exists()) {
                compare = compareImageLib(fileA, fileB);
                if (compare) {
                    System.out.println("Match found with " + fileB.getName());
                    break;
                }
            }
            else {
                System.err.println("File to compare with not found: 0" + fileB.getAbsolutePath());
            }
        }
        return compare;
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

    public static void mouseClickRelativeToWindow(Rectangle bounds, Rectangle button){

        // left + top: coordinants of the window
        // x + y : position relative to the window

        try {
            // Instantiate the Robot class
            Robot robot = new Robot();

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
            robotX = getRandomNumber(robotX, robotX+10);
            robotY = getRandomNumber(robotY-2, robotY+2);

            // Move the mouse pointer to the specific (X, Y) coordinates
            robot.mouseMove(robotX, robotY);

            // Press the left mouse button
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);

            // Optional delay between press and release to simulate a realistic click
            robot.delay(getRandomNumber(40,80));

            // Release the left mouse button
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

            System.out.println("Successfully clicked at position: (" + robotX + ", " + robotY + ")");

        } catch (AWTException e) {
            System.err.println("The platform configuration does not allow low-level input control.");
            e.printStackTrace();
        }
    }

    enum MOUSE {
        MOVE,
        CLICK
    }

    public static int getRandomNumber(int min, int max){

        // Generates a random number between 5 (inclusive) and 11 (exclusive) -> [5, 10]
        int randomNum = ThreadLocalRandom.current().nextInt(min, max);

        // Generates a random number where BOTH min and max are inclusive -> [5, 11]
        int randomNumInclusive = ThreadLocalRandom.current().nextInt(min, max + 1);
        System.out.println("randomNumInclusive:" + randomNumInclusive);
        return randomNumInclusive;
    }

    public static void sleep(TimeUnit timeUnit, int value){
        try {
            timeUnit.sleep(value);
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

    public static void mouseMoveRelativeToWindow(Rectangle windowBounds, int x, int y) {
        mouseRelativeToWindow(windowBounds, MOUSE.MOVE, x, y);
    }

    public static void mouseClickRelativeToWindow(Rectangle windowBounds, int x, int y) {
        mouseRelativeToWindow(windowBounds, MOUSE.CLICK, x, y);
    }

    public static void mouseRelativeToWindow(Rectangle windowBounds, MOUSE mouse, int x, int y){

        // left + top: coordinants of the window
        // x + y : position relative to the window

        try {
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

            if (mouse.equals(MOUSE.CLICK)){
                // Press the left mouse button
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);

                // Optional delay between press and release to simulate a realistic click
                robot.delay(getRandomNumber(40,80));

                // Release the left mouse button
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            }

            System.out.println("Successfully clicked/moved at position: (" + robotX + ", " + robotY + ")");

        } catch (AWTException e) {
            System.err.println("The platform configuration does not allow low-level input control.");
            e.printStackTrace();
        }
    }

    public static void mouseDrag(Rectangle windowBounds, int startX, int startY, int endX, int endY) throws AWTException, InterruptedException {

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
    public static void combineHorizontally(List<BufferedImage> images, File outputFile) throws IOException {
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
    public static void combineVertically(List<BufferedImage> images, File outputFile) throws IOException {
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
}