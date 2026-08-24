package be.home.selenium;

import be.home.common.logging.LoggingConfiguration;

import be.home.domain.model.service.MP3Service;
import be.home.model.json.AlbumInfo;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;


import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeleniumAmazonMusic extends SeleniumService {

    private static final Logger log = LoggingConfiguration.getMainLog(SeleniumAmazonMusic.class);

    public static void main(String[] args) {

        SeleniumAmazonMusic instance = new SeleniumAmazonMusic();
        try {
            instance.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException {

        AlbumInfo.Config configAlbum = initConfigAlbum();

        WebDriver driver = initDriver();

        //driver.get("https://music.amazon.de/albums/B0DQ6NX41T");
        driver.get("https://www.amazon.com/Pyrotechnik/dp/B0DM87G8W8");


        getAlbumInfo(driver, configAlbum);
        getTracks(driver, configAlbum);
        printAlbumInfo(log, configAlbum);

        driver.quit();
        writeAlbumConfiguration(configAlbum);

    }

    public void getAlbumInfo(WebDriver driver, AlbumInfo.Config configAlbum) {

        // Wait until everything is loaded
        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofSeconds(5))
                .ignoring(NoSuchElementException.class);

        WebElement element = wait.until(new Function<WebDriver, WebElement>() {
            public WebElement apply(WebDriver driver) {
                return driver.findElement(By.xpath("//music-detail-header[starts-with(@class,'hydrated')]"));
            }
        });

        SearchContext tx = element.getShadowRoot();
        // only css selectors can be used to look up into a shadow DOM
        String title = getText(tx.findElement(By.cssSelector("h1.null")));
        configAlbum.setAlbum(title);

        WebElement albumArtistElement = tx.findElement(By.cssSelector(".primary"));
        String albumArtist = getText(albumArtistElement);
        if (albumArtist.equalsIgnoreCase(MP3Service.VARIOUS)){
            configAlbum.setAlbumArtist(MP3Service.VARIOUS);
            configAlbum.setCompilation(true);
        }
        else {
            configAlbum.setAlbumArtist(albumArtist);
            configAlbum.setCompilation(true);
        }
    }

    public void getTracks(WebDriver driver, AlbumInfo.Config albumConfig){

        List<WebElement> elements = driver.findElements(By.xpath("//music-text-row[starts-with(@class,'hydrated')]"));
        List<AlbumInfo.Track> tracks = albumConfig.getTracks();
        log.info("nr of tracks found: " + elements.size());
        for (WebElement track : elements) {
            AlbumInfo.Track trackRec = getSongInfo(track, albumConfig);
            if (trackRec != null) {
                tracks.add(trackRec);
            }
        }
    }

    public AlbumInfo.Track getSongInfo(WebElement trackInfo, AlbumInfo.Config albumConfig){
        // col1 = title
        // col3 = artist
        // col4 = Length of track

        AlbumInfo.Track trackRec = new AlbumInfo().new Track();

        WebElement title = trackInfo.findElement(By.cssSelector("div.col1 > music-link.hydrated > a"));
        WebElement artist = trackInfo.findElement(By.cssSelector("div.col3"));
        WebElement trackNr = trackInfo.findElement(By.cssSelector("span.index-text"));

        trackRec.setTitle(getText(title));
        trackRec.setArtist(getText(artist));
        trackRec.setTrack(getText(trackNr));

        return trackRec;
    }
}