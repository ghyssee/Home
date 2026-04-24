package be.home.selenium;

import be.home.common.logging.LoggingConfiguration;

import be.home.domain.model.service.MP3Service;
import be.home.model.json.AlbumInfo;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;


import java.io.IOException;
import java.util.List;


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

        //https://www.hitnoteringen.be/hitlijsten/topradio-heroes-van-de-zeroes-top-400/2023
        //https://www.hitnoteringen.be/hitlijsten/vrt-radio-2-top-90-van-de-jaren-90/2024

        driver.get("https://www.hitnoteringen.be/hitlijsten/topradio-heroes-van-de-zeroes-top-400/2023");

        getAlbumInfo(driver, configAlbum);
        getTracks(driver, configAlbum);
        printAlbumInfo(log, configAlbum);

        driver.quit();
        writeAlbumConfiguration(configAlbum);

    }

    public void getAlbumInfo(WebDriver driver, AlbumInfo.Config configAlbum) {

        try {

            WebElement element = driver.findElement(By.xpath("//section[starts-with(@id,'header')]//div[starts-with(@class,'container')]"));

            checkAlbumArtist(configAlbum, element);
            WebElement albumElement = element.findElement(By.xpath(".//h1"));
            configAlbum.setAlbum(albumElement.getText());


        } catch (NoSuchElementException ex) {
            throw new RuntimeException("Album info not found");
        }
    }


    public void checkAlbumArtist(AlbumInfo.Config configAlbum, WebElement element) {

        // it always is a list of top X songs with various artists
        configAlbum.setCompilation(false);
        configAlbum.setAlbumArtist(MP3Service.VARIOUS);
    }

    public void getTracks(WebDriver driver, AlbumInfo.Config albumConfig) {

        List<WebElement> trackList = driver.findElements(By.xpath("//li[@itemprop='track']"));
        List<AlbumInfo.Track> tracks = albumConfig.getTracks();
        log.info("nr of tracks found: " + trackList.size());
        for (WebElement track : trackList) {
            AlbumInfo.Track trackRec = new AlbumInfo().new Track();
            if (trackRec != null) {
                updateTrack(track, albumConfig, trackRec);
                updateArtist(track, albumConfig, trackRec);
                updateTitle(track, trackRec);
                tracks.add(trackRec);
            }
        }
    }

    public void updateTrack(WebElement songInfo, AlbumInfo.Config albumConfig, AlbumInfo.Track trackRec) {

        try {
            WebElement trackElement = songInfo.findElement(By.xpath(".//span[starts-with(@class,'position')]"));
            String trackInfo = getText(trackElement);
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
            WebElement trackElement = songInfo.findElement(By.xpath(".//span[@itemprop='byArtist']"));
            trackRec.setArtist(getText(trackElement));
        }
        catch (NoSuchElementException ex){
            // should never occur
            log.error("No artist info found for " + songInfo.getText());

        }
    }


    public void updateTitle(WebElement songInfo, AlbumInfo.Track trackRec) {

        WebElement trackElement = songInfo.findElement(By.xpath(".//span[@itemprop='name']"));
        trackRec.setTitle(getText(trackElement));
    }
}