package be.home.main.mezzmo;

import be.home.common.constants.Constants;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.main.BatchJobV2;
import be.home.common.model.TransferObject;
import be.home.common.utils.JSONUtils;
import be.home.domain.model.MP3Helper;
import be.home.mezzmo.domain.model.AlbumError;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.model.MGOFileAlbumTO;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import be.home.model.ConfigTO;
import com.mpatric.mp3agic.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.List;

public class MP3TagChecker extends BatchJobV2{

    public static MezzmoServiceImpl mezzmoService = null;

    public static ConfigTO.Config config;
    private static final Logger log = getMainLog(MP3TagChecker.class);
    public List <AlbumError> albumErrors = (List<AlbumError>) JSONUtils.openJSONWithCode(Constants.JSON.ALBUMERRORS, List.class);

    public static void main(String args[]) {

        MP3TagChecker instance = new MP3TagChecker();
        try {
            config = instance.init();
            SQLiteJDBC.initialize(workingDir);
            instance.run();
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        final String batchJob = "MP3TagChecker";

        export("", "MezzmoDB.PlayCount.V12.csv");

    }

    public void export(String base, String fileName){


        TransferObject to = new TransferObject();
        //base = ""
        MGOFileAlbumTO albumTO = new MGOFileAlbumTO();
        albumTO.setName("100x Winter 2015");
        List<MGOFileAlbumCompositeTO> listAlbums = getMezzmoService().getAlbums(albumTO, new TransferObject());
        for (MGOFileAlbumCompositeTO comp : listAlbums){
            System.out.println("AlbumID: " + comp.getFileAlbumTO().getId());
            System.out.println("Album: " + comp.getFileAlbumTO().getName());
            processAlbum(comp);
        }
    }

    private void processAlbum(MGOFileAlbumCompositeTO comp){
        MGOFileAlbumCompositeTO search = new MGOFileAlbumCompositeTO();
        search.getFileAlbumTO().setId(comp.getFileAlbumTO().getId());
        List<MGOFileAlbumCompositeTO> list = getMezzmoService().findSongsByAlbum(search);
        System.out.println("Album: " + comp.getFileAlbumTO().getName());
        int maxDisc = getMaxDisc(list);
        System.out.println("Max Disc: " + maxDisc);
        for (MGOFileAlbumCompositeTO item : list){
            System.out.println("Track: " + item.getFileTO().getTrack());
            System.out.println("Artist: " + item.getFileArtistTO().getArtist());
            System.out.println("Title: " + item.getFileTO().getTitle());
            processSong(item, list.size(), maxDisc);
        }

    }

    private void processSong(MGOFileAlbumCompositeTO comp, int nrOfTracks, int maxDisc){
        File file = new File(comp.getFileTO().getFile());
        if (file.exists()){
            checkMP3Info(comp, file, nrOfTracks, maxDisc);
        }
        else {
            log.warn("File Not Found: " + file.getAbsolutePath());
            AlbumError error = new AlbumError(comp.getFileTO().getId(), comp.getFileTO().getFile(),
                    "FILE", "File Not Found", "");

        }

    }

    private void checkMP3Info(MGOFileAlbumCompositeTO comp, File file, int nrOfTracks, int maxDisc){
        Mp3FileExt mp3file = null;
        try {
            mp3file = new Mp3FileExt(file.getAbsolutePath());
            ID3v2 id3v2Tag;
            if (mp3file.hasId3v2Tag()) {
                id3v2Tag = mp3file.getId3v2Tag();
                //checkTrack(id3v2Tag.getTrack(), comp.getFileTO().getTrack(), 0);
                checkTrack(id3v2Tag.getTrack(), comp.getFileTO().getTrack(), nrOfTracks, maxDisc);
                checkArtist(id3v2Tag.getArtist());
                checkTitle(id3v2Tag.getTitle());

                System.out.println("Track: " + id3v2Tag.getTrack());
                System.out.println(StringUtils.repeat('=', 100));
                System.out.println("Artist: " + id3v2Tag.getArtist());
                System.out.println(StringUtils.repeat('=', 100));
                System.out.println("Title: " + id3v2Tag.getTitle());
                System.out.println(StringUtils.repeat('=', 100));
            }
            else {
                log.warn("No id3v2Tag Info found for file: " + file.getAbsolutePath());
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedTagException e) {
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
        }
    }

    private int getMaxDisc(List<MGOFileAlbumCompositeTO> list){
        int maxDisc = 0;
        for (MGOFileAlbumCompositeTO comp : list){
            maxDisc = Math.max(maxDisc, comp.getFileTO().getDisc());
        }
        return maxDisc;
    }

    private boolean checkTrack(String mp3Track, Integer mezzmoTrack, int nrOfTracks, int nrOfCds){
        //System.out.println("Track: " + id3v2Tag.getTrack());
        boolean ok = true;
        int lengthTrack = String.valueOf(nrOfTracks).length() + String.valueOf(nrOfCds).length();
        String mezzmoTr = StringUtils.leftPad(mezzmoTrack.toString(), lengthTrack, '0');
        if (!mezzmoTr.equals(mp3Track)){
            log.warn("Track does not match: " + "mp3: " + mp3Track + " / Mezzmo: " + mezzmoTr);
            ok = false;
        }
        return ok;
    }

    private boolean checkArtist(String mp3Artist){
        String artist = MP3Helper.getInstance().prettifyArtist(mp3Artist);
        boolean ok = true;
        if (!artist.equals(mp3Artist)){
            log.warn("Artist does not match: " + "mp3: " + mp3Artist + " / Formatted: " + artist);
            ok = false;
        }
        return ok;
    }

    private boolean checkTitle(String mp3Title){
        boolean ok = true;
        String title = MP3Helper.getInstance().prettifySong(mp3Title);
        if (!title.equals(mp3Title)){
            log.warn("Title does not match: " + "mp3: " + mp3Title + " / Formatted: " + title);
            ok = false;
        }
        return ok;
    }

    public static MezzmoServiceImpl getMezzmoService(){

        if (mezzmoService == null) {
            return MezzmoServiceImpl.getInstance();
        }
        return mezzmoService;
    }
}

