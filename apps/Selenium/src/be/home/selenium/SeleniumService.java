package be.home.selenium;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.logging.LoggingConfiguration;
import be.home.common.utils.JSONUtils;
import be.home.model.json.AlbumInfo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class SeleniumService {
    public static final int WAIT = 10;
    private static final Logger log = LoggingConfiguration.getMainLog(SeleniumService.class);

    public WebDriver initDriver(){
        // Firefox
        System.setProperty("webdriver.gecko.driver", "C:\\My Programs\\Browsers\\geckodriver.exe");
        Path pathBinary = Paths.get("C:\\My Programs\\Browsers\\FirefoxPortable\\App\\Firefox64\\firefox.exe");
        if (!Files.exists(pathBinary)){
            throw new RuntimeException(("Firefox executable not found: " + pathBinary.toString()));
        }
        FirefoxOptions options = new FirefoxOptions();
        options.setBinary(pathBinary);
        //String profilePath = "C:\\My Programs\\Browsers\\FirefoxPortable\\Data\\profile";
        //
        //String profilePath = "C:\\Users\\ghyssee\\AppData\\Roaming\\Mozilla\\Firefox\\Profiles\\jaq9jhkh.SeleniumUser";

        String profilePath = ("C:\\My Programs\\OneDrive\\Browsers\\Firefox\\profile\\Selenium");
        // 1. Load the existing profile containing the DRM components
        FirefoxProfile profile = new FirefoxProfile(new File(profilePath));
        // 2. Set strict preferences to force DRM stability
        profile.setPreference("media.eme.enabled", true);
        profile.setPreference("media.gmp-widevinecdm.enabled", true);
        profile.setPreference("media.gmp-widevinecdm.visible", true);
        profile.setPreference("media.gmp-widevinecdm.autoupdate", true);
        File extensionPath = new File("C:\\My Programs\\OneDrive\\Browsers\\Firefox\\extension\\eric_ext.xpi");
        profile.addExtension(extensionPath);

        // Fix for headless mode or background audio playback if needed
        profile.setPreference("media.autoplay.default", 0); // 0 = Allow autoplay

        // 3. Configure Firefox Options
        options.setProfile(profile);


        WebDriver driver = new FirefoxDriver(options);
        // Chrome
        //System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");

        return driver;
    }

    public boolean hasClass(WebElement element, String className) {
        String classes = element.getAttribute("class");
        for (String c : classes.split(" ")) {
            if (c.equals(className)) {
                return true;
            }
        }

        return false;
    }

    public void printAlbumInfo(org.apache.logging.log4j.Logger log, AlbumInfo.Config configAlbum) {
        log.info(configAlbum.toCustomString());
        for (AlbumInfo.Track track: configAlbum.getTracks()){
            log.info(track.toCustomString());
        }
    }

    public AlbumInfo.Config initConfigAlbum(){
        AlbumInfo info = new AlbumInfo();
        AlbumInfo.Config configAlbum = info.new Config();
        return configAlbum;
    }

    public void writeAlbumConfiguration(AlbumInfo.Config configAlbum) throws IOException {
        JSONUtils.writeJsonFile(configAlbum, Setup.getInstance().getFullPath(Constants.Path.PROCESS) + File.separator + "Album.json");
    }

    public String getText(WebElement element){
        String text = "";
        if (!element.isDisplayed()){
            text = element.getAttribute("innerText");
        }
        else {
            text = element.getText();
            if (StringUtils.isBlank(text)){
                text = element.getAttribute("textContent");
            }
        }
        return text;
    }

    public File makeScreenshot(WebDriver driver, String destination){
        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        String filename = makeUniqueFileName("c:\\my data\\tmp\\" + destination, ".png");
        File dstFile = new File( filename);
        try {
            FileUtils.copyFile(scrFile, dstFile);
        } catch (IOException e) {
            System.out.println("There was a problem with creating screenshot to file " + dstFile.getAbsolutePath());
        }
        return dstFile;

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

    public WebElement waitForElement (WebDriver driver, By locator, String comment) {
        return waitForElement(driver, locator, 2, WAIT, true, comment);
    }

    public WebElement waitForElement (WebDriver driver, By locator, int polling, int timeout, boolean exitIfNotFound, String comment) {

        // Wait until everything is loaded
        WebElement elementRet = null;
        boolean error = false;
        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(Duration.ofSeconds(timeout))
                .pollingEvery(Duration.ofSeconds(polling))
                .ignoring(NoSuchElementException.class);

        try {

            WebElement element = wait.until(new Function<WebDriver, WebElement>() {
                public WebElement apply(WebDriver driver) {
                    System.out.println("Waiting for " + comment + " with Anchor " + locator.toString());
                    WebElement element = null;
                    element = driver.findElement(locator);
                    return element;
                }
            });
            if (element == null) {
                error = true;
            }
            else {
                elementRet = element;
            }

        }
        catch (TimeoutException ex) {
            error = true;
        }
        if (error) {
            if (exitIfNotFound) {
                throw new RuntimeException(comment + ": " + "css Anchor not found: " + locator.toString());
            }
        }
        return elementRet;
    }

    public WebElement getNonStaleElement(WebDriver driver, By locator){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement element = wait.until(
                ExpectedConditions.refreshed(
                                ExpectedConditions.elementToBeClickable(locator)
                )
        );
        return element;
    }



    public void sendKeysNoWait(WebDriver driver, By locator, String keys){
        boolean exit=false;
        do {
            try {
                WebElement element = driver.findElement(locator);
                element.sendKeys(keys);
                exit=true;
            } catch (StaleElementReferenceException ex) {
                System.out.println("sendKeys Stale. Retrying...");
            }
        }
        while(!exit);

    }
    public void waitForADFBackgroundProcess(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until(wd -> {
            try {
                return (Boolean) ((JavascriptExecutor) wd).executeScript(
                        "return typeof AdfPage !== 'undefined' && " +
                                "AdfPage.PAGE !== undefined && " +
                                "AdfPage.PAGE.getLookAndFeel() !== null && " +
                                "AdfPage.PAGE.isSynchronizedWithServer();"
                );
            } catch (Exception e) {
                // Als ADF tijdens het typen de DOM reset, kan er een tijdelijke JS-error ontstaan.
                // Door false te returnen, blijft Selenium proberen tot de timeout.
                return false;
            }
        });
    }

    public void sendKeys(WebDriver driver, By locator, String keys){
        boolean exit=false;
        do {
            try {
                WebElement element = getNonStaleElement(driver, locator);
                element.clear();
                element.sendKeys(keys);
                exit=true;
            } catch (StaleElementReferenceException ex) {
                System.out.println("sendKeys Stale. Retrying...");
            }
        }
        while(!exit);
    }

    public void fillADFCombobox(WebDriver driver, By locator, By optionLocator, String keys, String comment) {
        String value = "";
        int counter = 0;
        do {
            sendKeys(driver, locator, keys);
            sleep(driver, 1, "Test");
            clickScreen(driver, optionLocator, 10, SeleniumOracleFusion.CLICK_TYPE.ELEMENT, comment);
            WebElement inputBox = driver.findElement(locator);
            value = inputBox.getAttribute("value");
            counter++;
            if (counter > 1){
                log.info("Retrying " + comment);
            }
        }
        while (StringUtils.isBlank(value) && counter < 5);
    }

    public void clickScreen (WebDriver driver, By locator, int timeout, SeleniumOracleFusion.CLICK_TYPE clickType, String comment) {

        boolean exit = false;
        do {
            WebElement element = waitForElementClickable(driver, locator, timeout, comment);
            try {
                switch (clickType) {
                    case ACTION:
                        Actions actions = new Actions(driver);
                        actions.moveToElement(element).click().perform();
                        break;
                    default:
                        element.click();
                        break;
                }
                exit = true;
            } catch (ElementClickInterceptedException ex) {
                //another element is obscuring the element to be clicked
                sleep(driver, 1, "ElementClickInterceptedException");
                log.info("ElementClickInterceptedException: Retrying to click");
            }
            catch (ElementNotInteractableException ex) {
                log.info("ElementNotInteractableException: Retrying to click");
            }
            catch (StaleElementReferenceException ex2) {
                log.info("StaleElementReferenceException: Retrying to click");
            }
        }
        while (!exit);
    }
    public void clickScreen (WebElement webElement, By locator, String comment) {
        // Wait until everything is loaded
        WebElement elementRet = null;

        Wait<WebElement> wait = new FluentWait<WebElement>(webElement)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofSeconds(WAIT))
                .ignoring(NoSuchElementException.class);

        try {

            WebElement element2 = wait.until(new Function<WebElement, WebElement>() {
                public WebElement apply(WebElement element3) {
                    System.out.println("Waiting for " + comment + " with Anchor " + locator.toString());
                    return element3.findElement(locator);
                }
            });
            if (element2 == null) {
                throw new RuntimeException(comment + "Invalid anchor: " + locator.toString());
            }
            else {
                element2.click();
            }

        }
        catch (TimeoutException ex) {
            throw new RuntimeException(comment + ": " + "css Anchor not found: " + locator.toString());
        }
    }

    public void sendKeysADF(WebDriver driver, By locator, String keys){
        boolean exit = false;
        do {
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
                element.clear();
                element.click();
                element.sendKeys(keys);
                // 3. PAUSE briefly to let the Oracle ADF AJAX call register the typed text
                try {
                    Thread.sleep(500);
                    // 500ms is usually the sweet spot for ADF auto-suggest drop-downs
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // 4. Use Actions class to cleanly execute the Enter hardware event
                new Actions(driver)
                        .moveToElement(element)
                        .sendKeys(Keys.ENTER)
                        .build()
                        .perform();
                wait.until(ExpectedConditions.textToBePresentInElementValue(locator, keys));
                element = driver.findElement(locator);
                String value = element.getAttribute("value");
                System.out.println("Value: " + value);
                waitForADFBackgroundProcess(driver);
                exit = true;
            }
            catch (ElementClickInterceptedException ex) {
                System.out.println("ElementClickInterceptedException while sending keys. Retrying...");
            }
            catch (StaleElementReferenceException ex) {
                System.out.println("StaleElementReferenceException while sending keys. Retrying...");
            }
            catch (TimeoutException ex) {
                System.out.println("TimeoutException while sending keys. Retrying...");
            }
        }
        while (!exit);

    }

    // Maak een JavaScript-functie die door de closed shadow-host zoekt
    public WebElement getElementInClosedShadow(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Vervang 'span.xuu' door de exacte CSS-selector van de tag die de shadow-root BEZIT (de host)
        String script =
                "return document.querySelector('span.xuu')" +
                        "?.shadowRoot?.querySelector('input[id*=\\'::content\\']') || " +
                        "document.querySelector('input[id*=\\'::content\\']');"; // Fallback

        return (WebElement) js.executeScript(script);
    }

    public void sendKeysKeyboardADF(WebDriver driver, By locator, String keys){
        boolean exit = false;
        do {
            try {
                WebElement element = driver.findElement(locator);
                // Maak het veld leeg met de Actions class
                Actions actions = new Actions(driver);
                actions.click(element)
                        .keyDown(Keys.CONTROL)
                        .sendKeys("a")
                        .keyUp(Keys.CONTROL)
                        .sendKeys(Keys.BACK_SPACE)
                        .perform();
                new Actions(driver)
                        .moveToElement(element)
                        .sendKeys(keys + Keys.ENTER)
                        .build()
                        .perform();
                // 3. PAUSE briefly to let the Oracle ADF AJAX call register the typed text
                try {
                    Thread.sleep(500);
                    // 500ms is usually the sweet spot for ADF auto-suggest drop-downs
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // 4. Use Actions class to cleanly execute the Enter hardware event
                new Actions(driver)
                        .moveToElement(element)
                        .sendKeys(Keys.ENTER)
                        .build()
                        .perform();
                element = driver.findElement(locator);
                String value = element.getAttribute("value");
                System.out.println("Value: " + value);
                waitForADFBackgroundProcess(driver);
                exit = true;
            }
            catch (ElementClickInterceptedException ex) {
                System.out.println("ElementClickInterceptedException while sending keys. Retrying...");
            }
            catch (StaleElementReferenceException ex) {
                System.out.println("StaleElementReferenceException while sending keys. Retrying...");
            }
            catch (TimeoutException ex) {
                System.out.println("TimeoutException while sending keys. Retrying...");
            }
        }
        while (!exit);

    }



    public WebElement waitForNotBlank(WebDriver driver2, By locator, String attribute){
        // multiple attributes are separated with semicolon
        // wait till one of them is not blank
        boolean exit = false;
        String[] splitter = attribute.split(";");
        WebElement element = null;
        do {
            try {
                Wait<WebDriver> wait = new FluentWait<WebDriver>(driver2)
                        .withTimeout(Duration.ofSeconds(2))
                        .pollingEvery(Duration.ofSeconds(1))
                        .ignoring(NoSuchElementException.class);
                element = wait.until(new Function<WebDriver, WebElement>() {
                    public WebElement apply(WebDriver driver) {
                        WebElement elementToTest = driver.findElement(locator);
                        for (String attribute : splitter){
                            String value = elementToTest.getAttribute(attribute);
                            if (StringUtils.isNotBlank(value)){
                                System.out.println("waitForNotBlank: " + value);
                                return elementToTest;
                            }
                        }
                        return null;
                    }
                });
                exit = true;
            } catch (StaleElementReferenceException ex) {
                System.out.println("Stale element. Retrying...");
            }
        }
        while (!exit);
        return element;
    }
    public void select(WebDriver driver, By locator, String visibleText){
        boolean exit=false;
        do {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement element = wait.until(
                    ExpectedConditions.refreshed(
                            ExpectedConditions.visibilityOfElementLocated(locator)
                    )
            );
            Select dropdown = new Select(element);
            try {
                dropdown.selectByVisibleText(visibleText);
                exit = true;
            }
            catch (ElementClickInterceptedException ex){
                System.out.println("Select ElementClickInterceptedException. Retrying...");
                // retry
            }
        }
        while (!exit);
    }

    public WebElement waitForElementClickable(WebDriver driver, By locator, String comment){

        return waitForElementClickable(driver, locator, 5, comment);
    }


    public WebElement waitForElementClickable(WebDriver driver, By locator, int timeout, String comment){
        WebElement element = null;
        boolean exit = false;
        do {
            try {
                element = waitForElement(driver, locator, comment);
                Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
                wait.until(ExpectedConditions.elementToBeClickable(locator));
                exit = true;
            } catch (StaleElementReferenceException ex) {
                System.err.println("Element with anchor " + locator.toString() + " is stale. Retrying...");
            }
        }
        while (!exit);
        return element;

    }
    public WebElement waitForElementInvisible(WebDriver driver, By locator, String comment){
        WebElement element = waitForElement(driver, locator, comment);
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("AFModalGlassPane")));
        return element;

    }
    public WebDriver waitForFrame(WebDriver driver, String id, String xpathSelector) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        WebDriver frame = wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("fullscreen-app-host"));
        boolean exit = false;
        do {
            try {
                waitForElement(driver,By.xpath(xpathSelector), "iFrame loaded" );
                driver.findElement(By.xpath(xpathSelector));
                exit = true;
                System.out.println("frame loaded");

            } catch (NoSuchWindowException ex) {
                // frame not fully loaded
                System.out.println("frame not loaded. Retrying...");
                driver.switchTo().defaultContent();
                driver.switchTo().frame(id);

            }
        }
        while (!exit);

        return frame;

    }
    public WebElement waitForElementUntilValueNotEmpty (WebDriver driver, By locator, String comment) {
        int count = 0;
        boolean exit = false;
        WebElement element;
        do {
            element = waitForElement(driver, locator, comment);
            String value = element.getAttribute("value");
            if (StringUtils.isNotBlank(value)){
//            if (StringUtils.isNotBlank(value) || StringUtils.isNotBlank(getText(element))){
                exit = true;
                break;
            }
            else {
                sleep(driver, 1, "waitForElementUntilValueNotEmpty");
            }
            count++;
        }
        while (count < 5);
        if (!exit){
            makeScreenshot(driver, "waitForElementUntilValueNotEmpty");
            throw new RuntimeException(comment + " Element should not be empty with anchor: " + locator.toString() );
        }
        return element;

    }

    public WebElement waitForElementUntilTextNotEmpty (WebDriver driver, By locator, String comment) {
        int count = 0;
        boolean exit = false;
        WebElement element;
        do {
            element = waitForElement(driver, locator, comment);
            String value = element.getAttribute("value");
            if (StringUtils.isNotBlank(getText(element))){
                exit = true;
                break;
            }
            else {
                sleep(driver, 1, "waitForElementUntilValueNotEmpty");
            }
            count++;
        }
        while (count < 5);
        if (!exit){
            throw new RuntimeException(comment + "Element should not be empty with anchor: " + locator.toString() );
        }
        return element;

    }
    public void getTrackCd(List<Integer> tracknumbersPerCd, int trackNr, AlbumInfo.Track track){
        int cd = 1;
        int maxRange=0;
        int minRange=0;
        for (Integer limit : tracknumbersPerCd){
            maxRange+=limit.intValue();
            if (trackNr <=  maxRange){
                System.out.println("cd: " + cd);
                System.out.println("trackNr: " + (trackNr-minRange));
                track.setTrack(String.valueOf(trackNr-minRange));
                track.setCd(String.valueOf(cd));
                break;
            }
            cd++;
            minRange+=limit.intValue();
        }

    }

    public void sleep(WebDriver driver, int seconds, String errorMessage){
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            makeScreenshot(driver, errorMessage);
        }
    }
}