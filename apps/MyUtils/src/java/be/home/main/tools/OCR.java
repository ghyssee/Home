package be.home.main.tools;

import be.home.common.model.windows.AutomationUtil;
import be.home.common.model.windows.WindowTO;
import be.home.common.utils.DateUtils;
import be.home.common.utils.WinUtils;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class OCR {

    public static JFrame frame;
    public static JPanel feedPanel;
    public static JScrollPane scrollPane;
    private static boolean isRunning = true;
    public static JTextArea textArea = new JTextArea();;
    public static final String CONFIG_PATH = WinUtils.getOneDrivePath() +
            File.separator + "Config" + File.separator + "Java" + File.separator + "tessdata";
    public static AtomicInteger TOTAL_RECRUITS = new AtomicInteger(0);

    public static void main(String[] args) throws AWTException, IOException, InterruptedException {

        AutomationUtil au = new AutomationUtil();
        //WindowTO window = au.findWindowByPartialTitle("Total Commander");
        //au.activateWindow(window.getHandle());
        //au.mouseMoveRelativeToWindow(window.getBounds(), 0, 0);
        //au.mouseClickRelativeToWindow(window.getBounds(), new Rectangle(1, 1, 0, 0));
        initializeFrame();
        addPostToFeed(new Post("", "", "C:\\My Programs\\OneDrive\\Config\\Java\\tessdata\\tesststart.png"), true);
        sleep(TimeUnit.SECONDS, 2);
        startBatch();
    }

    static class Post {
        private final String author;
        private final String content;
        private final String imageUrl;

        public Post(String author, String content, String imageUrl) {
            this.author = author;
            this.content = content;
            this.imageUrl = imageUrl;
        }

        public String getAuthor() { return author; }
        public String getContent() { return content; }
        public String getImageUrl() { return imageUrl; }
    }

    public static void startBatch() throws AWTException, IOException, InterruptedException {

        AutomationUtil au = new AutomationUtil();

        if (true) {
            //showFeed("Started at " + DateUtils.formatDate(new Date(), DateUtils.DD_MM_YYYY_HH_MM_SS));
            addPostToFeed(new Post("Start", "Start time: " + DateUtils.formatDate(new Date(), DateUtils.DD_MM_YYYY_HH_MM_SS), ""), true);
            WindowTO window2 = au.findWindowByPartialTitle("LDPlayer");
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
            //for (int i=1; i <= 5; i++){
            //    makeScreenshot2(window2, "PlayBusy" + String.valueOf(i) + ".bmp", playButton2);
            //    sleep(TimeUnit.MILLISECONDS, 200);

            //}
            //for (int i=1; i <= 10; i++){
            Rectangle replayButton = new Rectangle(120, 900, 100, 40);
            Rectangle fightButton = new Rectangle(345, 940, 85, 40);
            Rectangle energyButton = new Rectangle(68, 56, 60, 30);
            Rectangle heroCapButton = new Rectangle(145, 360, 270, 35);
            Rectangle recruitButton = new Rectangle(390, 310, 40, 20);
            //System.out.println(getOCRText(window2, recruitButton));

            au.makeScreenshotWithinWindow(window2, "recruitRect.bmp", recruitButton);
            //makeScreenshot2(window2, "heroecap.bmp", heroecapButton);
            int wait = 1;

            if (true) {
                do {
                    au.mouseMoveRelativeToWindow(window2.getBounds(), 0, 0);
                    wait = processWindow(window2, startButton, replayButton, fightButton, energyButton, heroCapButton, recruitButton);
                    if (wait == -1 || !isRunning){
                        break;
                    }
                    sleep(TimeUnit.SECONDS, wait);
                }
                while (isRunning);
            }


        }
        closeFeed();

    }


    public static int processWindow (WindowTO window, Rectangle startButton, Rectangle replayButton, Rectangle fightButton, Rectangle energyButton, Rectangle heroecapButton, Rectangle recruitButton) throws AWTException, IOException, InterruptedException {

        AutomationUtil au = new AutomationUtil();
        File dsFile = au.makeScreenshotWithinWindow(window, "Start.bmp", startButton);
        //File dsFile = makeScreenshotRelativeToWindow(window.getBounds(), 350, 42, 25, 22, "DoubleSpeed.bmp", ImageFormat.BMP);
        int wait = 5;
        if (checkIfStarted(dsFile)){
            //logInfo("Busy");
            addPostToFeed(new Post("Busy", "Fight is still ongoing...", ""), true);
            return 20;
        }
        else if (checkIfNotStarted(dsFile)) {
            //logInfo("Ready to start");
            //logInfo("Clicking on play button...");
            addPostToFeed(new Post("Start", "Clicking on play button..", ""), true);
            au.mouseClickRelativeToWindow(window.getBounds(), startButton);
            return 10;
        }
        else if (checkButton(window, replayButton, "REPLAY")) {
            TOTAL_RECRUITS.addAndGet(processRecruits(window, recruitButton));
            //logInfo("Total recruits: " + TOTAL_RECRUITS.get());
            //logInfo("Wait till all loot is loaded on the screen");
            addPostToFeed(new Post("Recruits", "Wait till all loot is loaded on the screen. Total recruits: " + TOTAL_RECRUITS.get(), ""), true);
            sleep(TimeUnit.SECONDS, 10);
            Rectangle lootRect = new Rectangle(50, 400, 460, 250);
            int startX = lootRect.x + 50;
            int startY = lootRect.y + lootRect.height/2;
            int i=1;
            List<BufferedImage> images = new ArrayList<>();
            //mouseMoveRelativeToWindow(window.getBounds(), startX, startY);
            boolean compare = false;
            for (i=1; i <= 6; i++) {
                //ogInfo("Dragging mouse to see all loot");
                addPostToFeed(new Post("Loot", "Dragging mouse to see all loot" + TOTAL_RECRUITS.get(), ""), true);
                //makeScreenshot2(window, "Loot"+ String.valueOf(i) + ".bmp", lootRect);
                BufferedImage image = au.getBufferedImage(window, lootRect);
                if (i > 1){
                    compare = au.compareImageLib(images.get(images.size()-1), image);
                    addPostToFeed(new Post("Compare Loot", "compare " + i + ": " + compare, ""), true);
                    //logInfo("compare " + i + ": " + compare);
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
                Rectangle dragRect = au.mouseDrag(window.getBounds(), startX, startY, startX + 220, startY);

                addPostToFeed(new Post("MouseDrag", "Drag Mouse start position: (" + dragRect.x + ", " + dragRect.y + ")" , ""), true);
                addPostToFeed(new Post("MouseDrag", "Drag Mouse End position: (" + dragRect.width + ", " + dragRect.height + ")" , ""), true);
                //sleep(TimeUnit.SECONDS, 1);

            }
            String heroesTroopsFile = "HeroesTroops.bmp";
            Rectangle heroesRect = new Rectangle(50, 650, 480, 250);
            //File heroesFile = makeScreenshot2(window, heroesTroopsFile, heroesRect);
            BufferedImage heroesImage = au.getBufferedImage(window, heroesRect);
            images.add(heroesImage);
            File combinedLootFile = new File(au.getDatapath() + "LootCombined. " + DateUtils.formatDate(new Date(), DateUtils.YYYYMMDDHHMMSS) + ".png");
            au.combineVertically(images, combinedLootFile);
            //logInfo("Loot file: " + combinedLootFile.getAbsolutePath());
            addPostToFeed(new Post("File", "Loot file: " + combinedLootFile.getAbsolutePath(), ""), true);

            if (TOTAL_RECRUITS.get() > 330){
                au.alertBox("E&P", "Recruits are full", JOptionPane.INFORMATION_MESSAGE);
                return -1;
            }

            //System.out.println("Press the replay button");
            addPostToFeed(new Post("Start", "Press the replay button", ""), true);
            au.mouseClickRelativeToWindow(window.getBounds(), replayButton);
            return 1;
        }
        else if (checkButton(window, heroecapButton, "HERO CAP REACHED!")) {
            //logInfo("HERO CAP REACHED");
            addPostToFeed(new Post("Heroes", "Hero Cap Reached", ""), true);
            au.alertBox("E&P", "Hero Cap Reached", JOptionPane.INFORMATION_MESSAGE);
            return -1;
        }
        else if (checkButton(window, fightButton, "FIGHT!")) {
            //logInfo("Press the fight button");
            addPostToFeed(new Post("Fight", "Ready to fight", ""), true);
            String energy = getOCRText(window, energyButton);
            //logInfo("OCR Energy: " + energy);
            addPostToFeed(new Post("Energy", "OCR Energy: " + energy, ""), true);
            if (StringUtils.isNotBlank(energy)){
                int intEnergy = getEnergy(energy);
                if (intEnergy == -1){
                    //logInfo("There was a problem getting the energy amount!");
                    addPostToFeed(new Post("Problem", "There was a problem getting the energy amount", ""), true);
                }
                else {
                    //logInfo("Converted Energy: " + intEnergy);
                    addPostToFeed(new Post("Energy", "Converted Energy: " + intEnergy, ""), true);

                    if (intEnergy < 3) {
                        wait = 120;
                        //logInfo("Not Enough Energy: " + intEnergy + ". Waiting " + wait + " seconds");
                        addPostToFeed(new Post("Energy", "Not Enough Energy: " + intEnergy + ". Waiting " + wait + " seconds", ""), true);
                        // force mouseclick so that game keeps running
                        //au.mouseClickRelativeToWindow(window.getBounds(), new Rectangle(1, 1, 0, 0));
                        return wait;
                    }
                }
            }
            else {
                addPostToFeed(new Post("Energy", "There was a problem getting the energy amount!!!", ""), true);
                //logInfo("There was a problem getting the energy amount!!!");
            }
            au.mouseClickRelativeToWindow(window.getBounds(), fightButton);
            return 5;
        }
        return wait;

    }

    public static int processRecruits(WindowTO window, Rectangle recruitRect) throws IOException {
        String recruits = getOCRText(window, recruitRect);
        //logInfo("Getting number of recruits: " + recruits);
        addPostToFeed(new Post("Recruits", "Getting number of recruits: " + recruits, ""), true);
        int totalRecruits = 0;
        if (StringUtils.isNotBlank(recruits)){
            // replace all chars except 0-9
            recruits = recruits.replaceAll("[^0-9 ]", "");
            try {
                totalRecruits = Integer.valueOf(recruits);
                addPostToFeed(new Post("Recruits", "Total recruits: " + totalRecruits, ""), true);
                //logInfo("recruits: " + totalRecruits);
            }
            catch (NumberFormatException ex){
                addPostToFeed(new Post("Recruits", "Recruits conversion problem: " + recruits, ""), true);
                //logInfo("Recruits conversion problem: " + recruits);
            }
        }
        return totalRecruits;
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
                    addPostToFeed(new Post("Energy", "Incorrect number: " + energyArr[0], ""), true);
                    //logInfo("Incorrect number: " + energyArr[0]);
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
            addPostToFeed(new Post("OCR","OCR Fout: " + e.getMessage(),""), true);
            //System.err.println("OCR Fout: " + e.getMessage());
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
                    //logInfo("Match found with " + fileB.getName());
                    addPostToFeed(new Post("Compare","Match found with " + fileB.getName(), ""), true);
                    break;
                }
            }
            else {
                //logInfo("File to compare with not found: " + fileB.getAbsolutePath());
                addPostToFeed(new Post("Compare","File to compare with not found: " + fileB.getAbsolutePath(), ""), true);
            }
        }
        return compare;
    }


    public static void mouseClick(int left, int top, int x, int y) throws AWTException {

        // left + top: coordinants of the window
        // x + y : position relative to the window


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

        //logInfo("Successfully clicked at position: (" + x + ", " + y + ")");
        addPostToFeed(new Post("Mouse","Successfully clicked at position: (" + x + ", " + y + ")", ""), true);

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

    public static void logInfoOld(String message){
        System.out.println(message);
        showFeedOld(message);

    }

    public static void initializeFrame(){
            // 1. Only create the window and textArea if they don't exist yet
            if (frame == null) {
                frame = new JFrame("Log Viewer");
                //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

                frame.setSize(600, 400);

                // Main vertical feed container
                feedPanel = new JPanel();
                feedPanel.setLayout(new BoxLayout(feedPanel, BoxLayout.Y_AXIS));
                feedPanel.setBackground(new Color(240, 242, 245));
                feedPanel.setBorder(new EmptyBorder(10, 10, 10, 10));


                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        int choice = JOptionPane.showConfirmDialog(
                                frame,
                                "Are you sure you want to exit?",
                                "Confirm Exit",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE
                        );

                        if (choice == JOptionPane.YES_OPTION) {
                            isRunning = false;
                            frame.dispose();
                        }
                        // If NO_OPTION or CLOSED_OPTION (clicking 'X' on dialog), do nothing
                    }
                });

                //Post[] posts = {
                 //       new Post("", "Exploring the mountains today!", ""),
                 //       new Post("Sam", "Morning coffee setup.", "")
                //};

                //for (Post post : posts) {
                //    feedPanel.add(createPostCard(post));
                //    feedPanel.add(Box.createRigidArea(new Dimension(0, 12))); // Spacing
                //}

                //for (Post post : posts) {
                //    feedPanel.add(createPostCard(post));
                //    feedPanel.add(Box.createRigidArea(new Dimension(0, 12)));
               // }
                scrollPane = new JScrollPane(feedPanel);
                scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smooth scrolling
                frame.add(scrollPane);


                // Calculate the right edge of the usable screen area
                Rectangle screenBounds = GraphicsEnvironment
                        .getLocalGraphicsEnvironment()
                        .getMaximumWindowBounds();

                int x = screenBounds.x + screenBounds.width - frame.getWidth();
                int y = screenBounds.y + (screenBounds.height - frame.getHeight()) / 2; // Vertically centered

                frame.setLocation(x, y);
                frame.setVisible(true);
            }
    }

    private static JPanel createPostCard(Post post) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(12, 12, 12, 12));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (StringUtils.isNotBlank(post.getAuthor())){
            JLabel authorLabel = new JLabel(post.getAuthor());
            //authorLabel.setPreferredSize(new Dimension(authorLabel.getWidth(), 10));
            authorLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            card.add(authorLabel);
        }

        if (StringUtils.isNotBlank(post.getContent())){
            JLabel textLabel = new JLabel("<html><body style='width: 380px;'>" + post.getContent() + "</body></html>");
            textLabel.setBorder(new EmptyBorder(5, 0, 8, 0));
            card.add(textLabel);
        }
        //textLabel.setPreferredSize(new Dimension(textLabel.getWidth(), 12));
        if (StringUtils.isNotBlank(post.getImageUrl())) {
            JLabel imageLabel = new JLabel("Loading image...");


        // Load image in the background so the UI stays responsive

            new SwingWorker<ImageIcon, Void>() {
                @Override
                protected ImageIcon doInBackground() throws Exception {
                    if (StringUtils.isNotBlank(post.getImageUrl())){
                        File path = new File(post.getImageUrl());
                        BufferedImage img = ImageIO.read(path);
                        //Image scaled = img.getScaledInstance(400, -1, Image.SCALE_SMOOTH);
                        Image image = img.getScaledInstance(img.getWidth(), img.getHeight(), Image.SCALE_SMOOTH);
                        return new ImageIcon(image);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        imageLabel.setText("<html><body style='width: 380px;'>");
                        imageLabel.setIcon(get());
                    } catch (Exception e) {
                        imageLabel.setText("[Failed to load image]");
                    }
                }
            }.execute();
            card.add(imageLabel);
        }


        return card;
    }


    public static void showFeedOld(String message) {
        initializeFrame();
        SwingUtilities.invokeLater(() -> {
            // Append the incoming message every time showFeed is called

            textArea.append(message + "\n");
            textArea.setCaretPosition(textArea.getDocument().getLength());
        });
    }

    public static void closeFeed() {
        SwingUtilities.invokeLater(() -> {
            if (frame != null) {
                frame.dispose();   // Closes the window and releases UI resources
                frame = null;      // Reset so showFeed() can create a fresh one later
                textArea = null;
            }
        });
    }

    private static void addPostToFeed(Post post, boolean addToTop) {
        JPanel card = createPostCard(post);
        Component spacer = Box.createRigidArea(new Dimension(0, 12));

        if (addToTop) {
            feedPanel.add(spacer, 0);
            feedPanel.add(card, 0); // Insert at index 0 (top of the feed)
        } else {
            feedPanel.add(card);    // Append to the bottom of the feed
            feedPanel.add(spacer);
        }

        // Tell Swing to recalculate layout and redraw the new components
        feedPanel.revalidate();
        feedPanel.repaint();

        // Scroll to the top if added at the top, or bottom if appended
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(addToTop ? 0 : vertical.getMaximum());
        });
    }
}