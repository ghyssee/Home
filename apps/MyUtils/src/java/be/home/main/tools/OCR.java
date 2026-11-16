package be.home.main.tools;

import be.home.common.enums.ImageFormat;
import be.home.common.model.windows.AutomationUtil;
import be.home.common.model.windows.MOUSE_TYPE;
import be.home.common.model.windows.WindowTO;
import be.home.common.utils.DateUtils;
import be.home.common.utils.WinUtils;
import be.home.test.main.A;
import com.github.romankh3.image.comparison.ImageComparison;
import com.github.romankh3.image.comparison.ImageComparisonUtil;
import com.github.romankh3.image.comparison.model.ImageComparisonResult;
import com.github.romankh3.image.comparison.model.ImageComparisonState;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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

    public static void main(String[] args) throws AWTException, IOException, InterruptedException {
        //showOpenWindows();

        File fileA = new File("C:\\My Programs\\OneDrive\\Config\\Java\\tessdata\\DoubleSpeed.bmp");
        //System.out.println("Busy: " + checkIfStarted(fileA));
        //System.out.println("Start: " + checkIfNotStarted(fileA));
        AutomationUtil au = new AutomationUtil();


        if (true) {
            //WindowTO window2 = au.findWindowByPartialTitle("LDPlayer");
            WindowTO window2 = au.findWindowByPartialTitle("test");
        //Rectangle bounds = WindowAligner.getRobotCompatibleBounds(window2.getHandle());

            System.out.println(String.format("%-40.40s | X:%-4d Y:%-4d        | %-7d x %-7d",
                window2.getTitle(), window2.getBounds().x, window2.getBounds().y, window2.getBounds().width, window2.getBounds().height));
            au.activateWindow(window2.getHandle());
            Rectangle windowBounds = new Rectangle(110, 6, 473, 812);
            au.moveVisibleWindow(window2.getHandle(), 110, 6);
            window2 = au.findWindowByPartialTitle("LDPlayer");
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
                    au.mouseMoveRelativeToWindow(window2.getBounds(), 0, 0);
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


    public static int processWindow (WindowTO window, Rectangle startButton, Rectangle replayButton, Rectangle fightButton, Rectangle energyButton, Rectangle heroecapButton) throws AWTException, IOException, InterruptedException {

        AutomationUtil au = new AutomationUtil();
        File dsFile = au.makeScreenshotWithinWindow(window, "Start.bmp", startButton);
        //File dsFile = makeScreenshotRelativeToWindow(window.getBounds(), 350, 42, 25, 22, "DoubleSpeed.bmp", ImageFormat.BMP);
        int wait = 5;
        if (checkIfStarted(dsFile)){
            System.out.println("Busy");
            return 20;
        }
        else if (checkIfNotStarted(dsFile)) {
            System.out.println("Ready to start");
            System.out.println("Clicking on play button...");
            au.mouseClickRelativeToWindow(window.getBounds(), startButton);
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
                BufferedImage image = au.getBufferedImage(window, lootRect);
                if (i > 1){
                    compare = au.compareImageLib(images.get(images.size()-1), image);
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
                au.mouseDrag(window.getBounds(), startX, startY, startX + 220, startY);
                //sleep(TimeUnit.SECONDS, 1);

            }
            String heroesTroopsFile = "HeroesTroops.bmp";
            Rectangle heroesRect = new Rectangle(50, 650, 480, 250);
            //File heroesFile = makeScreenshot2(window, heroesTroopsFile, heroesRect);
            BufferedImage heroesImage = au.getBufferedImage(window, heroesRect);
            images.add(heroesImage);
            File combinedLootFile = new File(au.getDatapath() + "LootCombined. " + DateUtils.formatDate(new Date(), DateUtils.YYYYMMDDHHMMSS) + ".png");
            au.combineVertically(images, combinedLootFile);
            System.out.println("Loot file: " + combinedLootFile.getAbsolutePath());

            System.out.println("Press the replay button");
            au.mouseClickRelativeToWindow(window.getBounds(), replayButton);
            return 1;
        }
        else if (checkButton(window, heroecapButton, "HERO CAP REACHED!")) {
            System.out.println("HERO CAP REACHED");
            return -1;
        }
            else if (checkButton(window, fightButton, "FIGHT!")) {
            System.out.println("Press the fight button");
            String energy = getOCRText(window, energyButton);
            System.out.println("OCR Energy: " + energy);
            int intEnergy = getEnergy(energy);
            System.out.println("Converted Energy: " + intEnergy);
            au.mouseClickRelativeToWindow(window.getBounds(), fightButton);
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
                    System.err.println("Incorrect number: " + energyArr[0]);
                }
            }

        }
        return energy;
    }

    public static boolean checkAndClickButton(WindowTO window, Rectangle bounds, String buttonText) throws IOException, AWTException {
        String text = getOCRText(window, bounds);
        boolean buttonFound = false;
        AutomationUtil au = new AutomationUtil();
        if (StringUtils.isNotBlank(text)) {
            if (text.trim().equalsIgnoreCase(buttonText)) {
                au.mouseClickRelativeToWindow(window.getBounds(), bounds);
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
        return compareImageSequence(dsButton, "PlayStart", 1);

    }

    public static String getOCRText(WindowTO window, Rectangle bounds) throws IOException {
        String resultText = null;
        AutomationUtil au = new AutomationUtil();
        try {
            // 1. Definieer de coördinaten en grootte van het schermgedeelte (X, Y, Breedte, Hoogte)

            BufferedImage screenCapture = au.getBufferedImage(window, bounds);

            // Definieer de bestandslocatie en naam
            File outputFile = new File(au.getDatapath() + "ocr.png");

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
            tesseract.setLanguage("eng"); // Gebruik "eng" voor Engels, "nld" voor Nederlands


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


    public static boolean compareImageSequence(File fileA, String fileName, int limit) {

        boolean compare = false;
        AutomationUtil au = new AutomationUtil();
        for (int i=1; i <= limit; i++){
            File fileB = new File(CONFIG_PATH + File.separator + fileName + String.valueOf(i) + ".bmp");
            if (fileB.exists()) {
                compare = au.compareImageLib(fileA, fileB);
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
            robot.delay(new AutomationUtil().getRandomNumber(40,80));

            // Release the left mouse button
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

            System.out.println("Successfully clicked at position: (" + x + ", " + y + ")");

        } catch (AWTException e) {
            System.err.println("The platform configuration does not allow low-level input control.");
            e.printStackTrace();
        }
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


}