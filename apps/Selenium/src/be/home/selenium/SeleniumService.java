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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class SeleniumService {

    public WebDriver initDriver(){
        // Firefox
        System.setProperty("webdriver.gecko.driver", "C:\\My Programs\\Firefox\\geckodriver.exe");
        Path pathBinary = Paths.get("C:\\My Programs\\Firefox\\FirefoxPortable\\App\\Firefox\\firefox.exe");
        if (!Files.exists(pathBinary)){
            throw new RuntimeException(("Firefox executable not found: " + pathBinary.toString()));
        }
        FirefoxOptions options = new FirefoxOptions();
        options.setBinary(pathBinary);
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
        for (AlbumInfo.Track track:   configAlbum.getTracks()){
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
