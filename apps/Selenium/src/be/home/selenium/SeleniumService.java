package be.home.selenium;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.utils.JSONUtils;
import be.home.model.json.AlbumInfo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Logger;

public class SeleniumService {
    public static final int WAIT = 10;

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

    public void sendKeysCheckStale(WebDriver driver, By locator, String keys){
        boolean exit=false;
        do {
            try {
                WebElement element = getNonStaleElement(driver, locator);
                element.sendKeys(keys);
                exit=true;
            } catch (StaleElementReferenceException ex) {
                System.out.println("sendKeysCheckStale. Retrying...");
            }
        }
        while(!exit);

    }

    public void sendKeysCheckStale2(WebDriver driver, By locator, String keys){
        boolean exit=false;
        do {
            try {
                WebElement element = getNonStaleElement(driver, locator);
                Actions actions = new Actions(driver);
                actions.moveToElement(element)
                        .click()
                        .sendKeys(keys+Keys.TAB)
                        .build()
                        .perform();
                exit=true;
            } catch (StaleElementReferenceException ex) {
                System.out.println("sendKeysCheckStale. Retrying...");
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
        boolean exit = false;
        do {
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
                WebElement combobox = wait.until(ExpectedConditions.elementToBeClickable(locator));
                combobox.clear();
                combobox.click();
                combobox.sendKeys(keys);
                // 3. PAUSE briefly to let the Oracle ADF AJAX call register the typed text
                try {
                    Thread.sleep(500);
                    // 500ms is usually the sweet spot for ADF auto-suggest drop-downs
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // 4. Use Actions class to cleanly execute the Enter hardware event
                new Actions(driver)
                        .moveToElement(combobox)
                        .sendKeys(Keys.ENTER)
                        .build()
                        .perform();
                wait.until(ExpectedConditions.textToBePresentInElementValue(locator, keys));
                WebElement element = driver.findElement(locator);
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
        // multiple attribute are seperatred with semicolon
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