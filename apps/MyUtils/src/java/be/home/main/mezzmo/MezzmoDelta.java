package be.home.main.mezzmo;

import be.home.common.dao.jdbc.Databases;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.main.BatchJobV2;
import be.home.common.model.TransferObject;
import be.home.domain.model.ArtistSongItem;
import be.home.domain.model.MP3Helper;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.model.eric.MezzmoFileTO;
import be.home.mezzmo.domain.service.EricServiceImpl;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by ghyssee on 20/02/2015.
 */

public class MezzmoDelta extends BatchJobV2 {

    private static final Logger log = getMainLog(be.home.main.mezzmo.MezzmoDelta.class);
    private MezzmoServiceImpl impl = MezzmoServiceImpl.getInstance(Databases.MEZZMOV1);
    private MezzmoServiceImpl impl2 = MezzmoServiceImpl.getInstance(Databases.MEZZMOV2);
    private EricServiceImpl ericServiceImpl = EricServiceImpl.getInstance();

    public static void main(String args[]) {

        MezzmoDelta instance = new MezzmoDelta();
        instance.myInit();

    }

    private void myInit(){
        SQLiteJDBC.initialize();
        //MGOFileAlbumCompositeTO comp2 = impl.findFileById(175435);
        //System.out.println(comp2.getFileTO().getFile());
        MP3Helper mp3Helper = MP3Helper.getInstance();
        mp3Helper.disableLogging();
        TransferObject to = new TransferObject();
            do {
                List<MGOFileAlbumCompositeTO> list = getMezzmoV2Instance().getAllMP3Files(to);
                log.info("Index = " + to.getIndex());
                try {
                    checkFile(list, mp3Helper);
                } catch (SQLException e) {
                    e.printStackTrace();
                    break;
                }
            }
            while (!to.isEndOfList());
    }

    private void checkFile(List<MGOFileAlbumCompositeTO> list, MP3Helper mp3Helper) throws SQLException {
        for (MGOFileAlbumCompositeTO comp : list){
            if (comp.getFileTO().getId() == 17116){
            try {
                MezzmoServiceImpl tmp = getMezzmoV1Instance();
                tmp.findFileById(comp.getFileTO().getId());
                checkArtist(comp);

            }
            catch (IncorrectResultSizeDataAccessException ex){
                log.info("Not Found: " + comp.getFileTO().getId());
                insertMezzmoFile(comp, "NEW");

            }
        }
        }

    }

    private void checkArtist(MGOFileAlbumCompositeTO comp) throws SQLException {
        String prettifiedArtist = MP3Helper.getInstance().prettifyArtist(comp.getFileArtistTO().getArtist());
        String prettifiedTitle = MP3Helper.getInstance().prettifySong(comp.getFileTO().getTitle());
        ArtistSongItem artistItem = MP3Helper.getInstance().prettifyRuleArtistSong(prettifiedArtist, prettifiedTitle, false);
        if (artistItem.isMatched()) {
            insertMezzmoFile(comp, "ARTISTSONG");
        }
    }


    private void insertMezzmoFile(MGOFileAlbumCompositeTO comp, String status) throws SQLException {
        MezzmoFileTO mezzmoFile = new MezzmoFileTO();
        mezzmoFile.setId(comp.getFileTO().getId());
        mezzmoFile.setArtistId(comp.getFileArtistTO().getID());
        mezzmoFile.setArtistName((comp.getFileArtistTO().getArtist()));
        mezzmoFile.setStatus(status);
        ericServiceImpl.insertMezzmoFile(mezzmoFile);

    }

    private MezzmoServiceImpl getMezzmoV1Instance(){
        return impl;
    }

    private MezzmoServiceImpl getMezzmoV2Instance(){
        return impl2;
    }

    private EricServiceImpl geteEricServiceInstance(){
        return ericServiceImpl;
    }

    @Override
    public void run() {

    }
}
