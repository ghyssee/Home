package be.home.selenium;

import be.home.common.logging.LoggingConfiguration;

import be.home.domain.model.service.MP3Service;
import be.home.model.json.AlbumInfo;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.WheelInput;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;


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
        driver.get("https://open.spotify.com/playlist/6jILXSPvAm0sSUBdoXRhud");


        getAlbumInfo(driver, configAlbum);
        getTracks(driver, configAlbum);
        List<Integer> trackNumbers = Arrays.asList(20,20,20,20);
        for (AlbumInfo.Track track : configAlbum.getTracks()){
            getTrackCd(trackNumbers, Integer.parseInt(track.getTrack()), track);
        }

        printAlbumInfo(log, configAlbum);

        driver.quit();
        writeAlbumConfiguration(configAlbum);

    }

    public void getAlbumInfo(WebDriver driver, AlbumInfo.Config configAlbum) {

        WebElement element = waitForElement(driver, "div[data-testid='topbar-content-wrapper'", SELECTOR.CSS, "Track List");
                //driver.findElement(By.cssSelector("div[data-testid='topbar-content-wrapper'"));

        String title = getText(element);
        configAlbum.setAlbum(title);
        configAlbum.setAlbumArtist(MP3Service.VARIOUS);
        configAlbum.setCompilation(true);
    }

    public void scrollDown(WebDriver driver){

        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebElement element = driver.findElement(By.cssSelector("div[class='os-scrollbar-handle'"));
        js.executeScript("arguments[0].scrollIntoView();", element);
        long lastHeight = (long) js.executeScript("return document.body.scrollHeight");
        System.out.println("lastHeight: " + lastHeight);
        while (true) {
            js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // Wait for new content to load
            long newHeight = (long) js.executeScript("return document.body.scrollHeight");
            if (newHeight == lastHeight) {
                break;
            }
        }

    }

    public void getTracks(WebDriver driver, AlbumInfo.Config albumConfig){

        //scrollDown(driver);
       // WebElement element = driver.findElement(By.cssSelector("section[data-testid='playlist-page'"));
        //element.click();
        //WebElement trackList = driver.findElement(By.cssSelector("div[data-testid='playlist-tracklist'"));
        //Actions actions = new Actions(driver);
        //actions.keyDown(Keys.CONTROL).sendKeys(Keys.END).perform(); // scroll down

        // you need to scroll down go get all tracks
        // unique identifier = aria-rowindex
        // use page down key to croll down
        // <div role="row" aria-rowindex="37" aria-selected="false">
        // <div data-testid="tracklist-row" class="AWIrTV2Lx3pp211jnrfX hcazXzVsKhkarfzRfTem" draggable="true" role="presentation">
        // <div class="scCF_FeUXBT6pV6u7Wlo" role="gridcell" aria-colindex="1">
        // <div class="nNpUCAvODwUD6NcNxI50">
        // <span class="e-10860-text encore-text-body-medium tZHpslRrHgBJfbAyXM2U" data-encore-id="text">36</span>
        // <button class="uguMZhYNPFPT0K39gHNI" aria-label="Domino van Lenie afspelen" tabindex="-1">
        // <svg data-encore-id="icon" role="img" aria-hidden="true" class="e-10860-icon FeDnOC5h1Q9iUMWg6oYl" viewBox="0 0 24 24">
        // <path d="m7.05 3.606 13.49 7.788a.7.7 0 0 1 0 1.212L7.05 20.394A.7.7 0 0 1 6 19.788V4.212a.7.7 0 0 1 1.05-.606"></path>
        // </svg></button></div></div><
        // div class="fwyEl7IpX7I7HZGGsomh" role="gridcell" aria-colindex="2"><img aria-hidden="false" draggable="false" loading="eager" src="https://i.scdn.co/image/ab67616d00004851403ae9b4650ac43394f94364" data-image-status="loaded" alt="" class="FwkSRC8nZx0oFeY2HJi2 BDOw_uxmzi8Yd04G9HLQ xMLEqLC2MvHjIHuD0xxf" style="border-radius: 4px;" width="40" height="40"><div class="sd_NBI5fI3_7uJv0bLwQ"><a draggable="false" class="k_CUvHNLoYcwpmUhV5jN" data-testid="internal-track-link" href="/track/62jHdjJuQKcwQFyn2qqU7T" tabindex="-1"><div class="e-10860-text encore-text-body-medium encore-internal-color-text-base k_CUvHNLoYcwpmUhV5jN standalone-ellipsis-one-line" data-encore-id="text" dir="auto">Domino</div></a><span class="e-10860-text encore-text-body-medium encore-internal-color-text-subdued rJU8vfEEKjCLvne6Z8QQ" data-encore-id="text"></span><span class="e-10860-text encore-text-body-small encore-internal-color-text-subdued h0Bl8QAoHwVgKLXrvjGE standalone-ellipsis-one-line" data-encore-id="text"><div class="e-10860-text encore-text-body-small" data-encore-id="text"><a draggable="true" dir="auto" href="/artist/2ln12LyfiK9UkmiYXOiuaF" tabindex="-1">Lenie</a></div></span></div></div><div class="Xn3SJClo6kZtw6iAZuKe" role="gridcell" aria-colindex="3"><span class="e-10860-text encore-text-body-small" data-encore-id="text"><a draggable="true" class="standalone-ellipsis-one-line" dir="auto" href="/album/0T5bkxRSeVR6yVhJpliWQv" tabindex="-1">Domino</a></span></div><div class="Xn3SJClo6kZtw6iAZuKe" role="gridcell" aria-colindex="4"><span class="e-10860-text encore-text-body-small encore-internal-color-text-subdued standalone-ellipsis-one-line" data-encore-id="text">17 okt 2025</span></div><div class="PGUsx7TTOQt2hFluVtHK" role="gridcell" aria-colindex="5"><button aria-checked="false" class="e-10860-legacy-button e-10860-legacy-button-tertiary e-10860-overflow-wrap-anywhere e-10860-button-tertiary--icon-only-small e-10860-button-tertiary--icon-only e-10860-button-tertiary--condensed e-10860-button-tertiary--text-subdued encore-internal-color-text-subdued rFLncrzPHwpKJ7KnPap8" aria-label="Toevoegen aan Nummers die je leuk vindt" data-encore-id="buttonTertiary" tabindex="-1"><span aria-hidden="true" class="e-10860-button__icon-wrapper"><svg data-encore-id="icon" role="img" aria-hidden="true" class="e-10860-icon" style="--encore-icon-height: var(--encore-graphic-size-decorative-smaller); --encore-icon-width: var(--encore-graphic-size-decorative-smaller);" viewBox="0 0 16 16"><path d="M8 1.5a6.5 6.5 0 1 0 0 13 6.5 6.5 0 0 0 0-13M0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8"></path><path d="M11.75 8a.75.75 0 0 1-.75.75H8.75V11a.75.75 0 0 1-1.5 0V8.75H5a.75.75 0 0 1 0-1.5h2.25V5a.75.75 0 0 1 1.5 0v2.25H11a.75.75 0 0 1 .75.75"></path></svg></span></button><div class="e-10860-text encore-text-body-small encore-internal-color-text-subdued buwCyhSufWYlHJ5_Wffd" data-encore-id="text">2:20</div><button aria-haspopup="menu" data-testid="more-button" class="e-10860-legacy-button e-10860-legacy-button-tertiary e-10860-overflow-wrap-anywhere e-10860-button-tertiary--icon-only-small e-10860-button-tertiary--icon-only e-10860-button-tertiary--condensed e-10860-button-tertiary--text-subdued encore-internal-color-text-subdued dXezRjWDksvHkq4M37C3" aria-label="Meer opties voor Domino van Lenie" data-encore-id="buttonTertiary" tabindex="-1"><span aria-hidden="true" class="e-10860-button__icon-wrapper"><svg data-encore-id="icon" role="img" aria-hidden="true" class="e-10860-icon" style="--encore-icon-height: var(--encore-graphic-size-decorative-smaller); --encore-icon-width: var(--encore-graphic-size-decorative-smaller);" viewBox="0 0 16 16"><path d="M3 8a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0m6.5 0a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0M16 8a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0"></path></svg></span></button></div></div></div>


        boolean exit = false;
        // colindex 1 = track
        // colindex 2 = artist + title
        // colindex 3 = album
        List<AlbumInfo.Track> tracks = albumConfig.getTracks();
        do {
            WebElement trackList = waitForElement(driver, By.cssSelector("div[data-testid='playlist-tracklist']"), "Track List");
                    //driver.findElement(By.cssSelector("div[data-testid='playlist-tracklist']"));

            List<WebElement> elements = trackList.findElements(By.cssSelector("div[data-testid='tracklist-row']"));
            log.info("nr of tracks found: " + elements.size());
            int oldSize = tracks.size();
            System.out.println("Old size: " + oldSize);
            for (WebElement trackElement : elements){
                AlbumInfo.Track trackRec = new AlbumInfo().new Track();
                WebElement trackNr = trackElement.findElement(By.cssSelector("div[aria-colindex='1']"));
                trackRec.setTrack(getText(trackNr));
                WebElement artist = trackElement.findElement(By.cssSelector("div[aria-colindex='2'] a[href^='/artist']"));
                trackRec.setArtist(getText(artist));
                WebElement title = trackElement.findElement(By.cssSelector("div[aria-colindex='2'] a[href^='/track']"));
                trackRec.setTitle(getText(title));
                addTrack(tracks, trackRec);
               //System.out.println(getText(trackElement));
            }
            int newSize = tracks.size();
            System.out.println("New size: " + newSize);
            if (oldSize == newSize){
                // no new track found. We reached end of list
                exit = true;
            }
            else {
                // jump to the last element we get from the list
                WebElement el = elements.get(elements.size()-1);
                JavascriptExecutor executor = (JavascriptExecutor) driver;
                executor.executeScript("arguments[0].scrollIntoView();", el);
                sleep(driver, 1, "Scroll");
            }
        }
        while (!exit);

    }

    public void addTrack(List<AlbumInfo.Track> tracks, AlbumInfo.Track trackRec){
        boolean found = false;
        for (AlbumInfo.Track track : tracks){
            if (track.getTrack().equalsIgnoreCase(trackRec.getTrack())){
               found = true;
               break;
            }
        }
        if (!found){
            tracks.add(trackRec);
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