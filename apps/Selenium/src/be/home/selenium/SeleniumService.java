package be.home.selenium;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.utils.JSONUtils;
import be.home.common.utils.StringUtils;
import be.home.model.json.AlbumInfo;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class SeleniumService {

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
        }
        return text;
    }

}
