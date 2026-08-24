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

public class SeleniumSpotify extends SeleniumService {

    private static final Logger log = LoggingConfiguration.getMainLog(SeleniumSpotify.class);

    public static void main(String[] args) {

        SeleniumSpotify instance = new SeleniumSpotify();
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
        driver.get("https://open.spotify.com/album/0vONlAiwIlplNVRfF3tVFc");


        getAlbumInfo(driver, configAlbum);
        //getTracks(driver, configAlbum);
        //printAlbumInfo(log, configAlbum);

        driver.quit();
        //writeAlbumConfiguration(configAlbum);

    }

    public void getAlbumInfo(WebDriver driver, AlbumInfo.Config configAlbum) {

        WebElement element = driver.findElement(By.cssSelector("span[data-testid='entityTitle'"));

        String title = getText(element);
        configAlbum.setAlbum(title);

        WebElement albumArtistElement = driver.findElement(By.cssSelector("figure[title][class]"));
        String albumArtist = albumArtistElement.getAttribute("title");
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