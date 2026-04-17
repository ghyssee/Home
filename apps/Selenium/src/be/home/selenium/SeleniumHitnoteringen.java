package be.home.selenium;

import be.home.common.logging.LoggingConfiguration;

import be.home.common.utils.StringUtils;
import be.home.domain.model.service.MP3Service;
import be.home.model.json.AlbumInfo;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;


import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeleniumHitnoteringen extends SeleniumService {

    private static final Logger log = LoggingConfiguration.getMainLog(SeleniumDiscogs.class);

    public static void main(String[] args) {

        SeleniumHitnoteringen instance = new SeleniumHitnoteringen();
        try {
            instance.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException {

        AlbumInfo.Config configAlbum = initConfigAlbum();

        WebDriver driver = initDriver();

        //driver.get("https://www.discogs.com/release/31149296-Various-Now-Thats-What-I-Call-A-Summer-Party");
        driver.get("https://www.hitnoteringen.be/hitlijsten/vrt-radio-2-top-90-van-de-jaren-90/2024");

        getAlbumInfo(driver, configAlbum);
        getTracks(driver, configAlbum);
        printAlbumInfo(log, configAlbum);

        driver.quit();
        writeAlbumConfiguration(configAlbum);

    }

    public void getAlbumInfo(WebDriver driver, AlbumInfo.Config configAlbum) {

        try {

            WebElement element = driver.findElement(By.xpath("//section[starts-with(@id,'header')]//div[starts-with(@class,'container')]"));

            //JavascriptExecutor js = (JavascriptExecutor) driver;
            //js.executeScript("arguments[0].remove();", element);

            checkAlbumArtist(configAlbum, element);
            element.findElement(By.xpath(".//h1"));
            configAlbum.setAlbum(element.getText());


        } catch (NoSuchElementException ex) {
            throw new RuntimeException("Album info not found");
        }
    }


    public void checkAlbumArtist(AlbumInfo.Config configAlbum, WebElement element) {

        // it always is a list of top X with various artists
        configAlbum.setCompilation(false);
        configAlbum.setAlbumArtist(MP3Service.VARIOUS);
    }

    public void getTracks(WebDriver driver, AlbumInfo.Config albumConfig) {

        List<WebElement> trackList = driver.findElements(By.xpath("//li[@itemprop=track]"));
        List<AlbumInfo.Track> tracks = albumConfig.getTracks();
        log.info("nr of tracks found: " + trackList.size());
        for (WebElement track : trackList) {
            AlbumInfo.Track trackRec = new AlbumInfo().new Track();
            if (trackRec != null) {
                updateTrack(track, albumConfig, trackRec);
                updateArtist(track, albumConfig, trackRec);
                updateTitle(track, trackRec);
                updateExtraArtists(track, trackRec);
                tracks.add(trackRec);
            }
        }
    }

    public void updateTrack(WebElement songInfo, AlbumInfo.Config albumConfig, AlbumInfo.Track trackRec) {

        try {
            WebElement trackElement = songInfo.findElement(By.xpath(".//span[@class='position')]"));
            String trackInfo = trackElement.getText();
            trackRec.setTrack(trackInfo.trim());
        }
        catch (NoSuchElementException ex){
            // no track info found
            albumConfig.setCurrentTrack(albumConfig.getCurrentTrack()+1);
            trackRec.setTrack(String.valueOf(albumConfig.getCurrentTrack()));
        }
    }

    public void updateArtist(WebElement songInfo, AlbumInfo.Config albumConfig, AlbumInfo.Track trackRec) {

        try {
            WebElement trackElement = songInfo.findElement(By.xpath(".//span[@itemprop='byArtist')]"));
            trackRec.setArtist(albumConfig.getAlbumArtist());
        }
        catch (NoSuchElementException ex){
            // should never occur
            log.error("No artist info found for " + songInfo.getText());

        }
    }

    public String clearArtistTag(String artist){
        artist = artist.replaceAll("–", "");
        artist = artist.replaceAll(" ?\\([0-9]{1,3}\\)", "");
        artist = artist.replaceAll("\\*$", ""); // remove * at end of string
        artist = artist.replaceAll("\\* ", " "); // replace *<space> with <space>
        artist = artist.replaceAll("\\*, ?", ", "); // replace *, with ,
        artist = artist.trim();
        return artist;
    }

    public void updateTitle(WebElement songInfo, AlbumInfo.Track trackRec) {

        WebElement trackElement = songInfo.findElement(By.xpath(".//span[@itemprop='name')]"));
        trackRec.setTitle(trackElement.getText());
    }
}