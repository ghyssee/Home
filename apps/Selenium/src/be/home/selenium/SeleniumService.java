package be.home.selenium;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.utils.JSONUtils;
import be.home.model.json.AlbumInfo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.bidi.browsingcontext.Locator;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
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
    public static final int WAIT = 1;

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

    public By getBy(String selector, SELECTOR selectorType) {
        By byObj = null;
        switch (selectorType){
            case CSS:
                byObj = By.cssSelector(selector);
                break;
            case XPATH:
                byObj = By.xpath(selector);
                break;
            case ID:
                byObj =By.id(selector);
                break;
        }
        return byObj;
    }


    public WebElement waitForElement (WebDriver driver, String css, SELECTOR selectorType, String comment) {

        // Wait until everything is loaded
        WebElement elementRet = null;
        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofSeconds(WAIT))
                .ignoring(NoSuchElementException.class);

        try {

            WebElement element = wait.until(new Function<WebDriver, WebElement>() {
                public WebElement apply(WebDriver driver) {
                    System.out.println("Waiting for " + comment + " with Anchor " + css);
                    WebElement element = null;
                    element = driver.findElement(getBy(css, selectorType));
                    return element;
                }
            });
            if (element == null) {
                throw new RuntimeException(comment + "Invalid Selector type: " + selectorType.name());
            }
            else {
                elementRet = element;
            }

        }
        catch (TimeoutException ex) {
            throw new RuntimeException(comment + ": " + "css Anchor not found: " + css);
        }
        return elementRet;
    }

    public WebElement waitForElement (WebDriver driver, By locator, String comment) {

        // Wait until everything is loaded
        WebElement elementRet = null;
        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofSeconds(WAIT))
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
                throw new RuntimeException(comment + "Element not found with anchor: " + locator.toString());
            }
            else {
                elementRet = element;
            }

        }
        catch (TimeoutException ex) {
            throw new RuntimeException(comment + ": " + "css Anchor not found: " + locator.toString());
        }
        return elementRet;
    }

    enum SELECTOR {
        CSS, XPATH, ID;
    }

    public WebElement getNonStaleElement(WebDriver driver, By locator){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement element = wait.until(
                ExpectedConditions.refreshed(
                        ExpectedConditions.visibilityOfElementLocated(locator)
                )
        );
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