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
        /*
        MezzmoFileTO myTO = new MezzmoFileTO();
        myTO.setId(3);
        myTO.setStatus("TEST");
        myTO.setArtistId(3);
        myTO.setArtistName("TESTARTIST");
        myTO.setNew(false);

        try {
            getEricServiceInstance().insertMezzmoFile(myTO);
        } catch (SQLException e) {
            e.printStackTrace();
        }*/

            do {
                List<MGOFileAlbumCompositeTO> list = getMezzmoV2Instance().getAllMP3Files(to);
                log.info("Index = " + to.getIndex());
                try {
                    checkFile(list);
                } catch (SQLException e) {
                    e.printStackTrace();
                    break;
                }
            }
            while (!to.isEndOfList());

    }

    private void checkFile(List<MGOFileAlbumCompositeTO> list) throws SQLException {
        for (MGOFileAlbumCompositeTO comp : list){
            //if (comp.getFileTO().getId() == 17116){
            boolean isNew = false;
            try {
                MezzmoServiceImpl tmp = getMezzmoV1Instance();
                tmp.findFileById(comp.getFileTO().getId());

            }
            catch (IncorrectResultSizeDataAccessException ex){
                log.info("Not Found: " + comp.getFileTO().getId());
                isNew = true;
            }
            checkArtist(comp, isNew);
        //}
        }

    }

    private void checkArtist(MGOFileAlbumCompositeTO comp, boolean isNew) throws SQLException {
        ArtistSongItem artistItem = MP3Helper.getInstance().formatSongObj(comp.getFileArtistTO().getArtist(), comp.getFileTO().getTitle(), false);
        if (artistItem.isMatched() || isNew) {
            insertMezzmoFile(comp, artistItem, isNew);
        }
    }


    private void insertMezzmoFile(MGOFileAlbumCompositeTO comp, ArtistSongItem artistSongItem, boolean isNew) throws SQLException {
        MezzmoFileTO mezzmoFile = new MezzmoFileTO();
        mezzmoFile.setId(comp.getFileTO().getId());
        mezzmoFile.setArtistId(comp.getFileArtistTO().getID());
        mezzmoFile.setArtistName((comp.getFileArtistTO().getArtist()));
        mezzmoFile.setStatus(artistSongItem.getRule() != null ? artistSongItem.getRule().name() : null);
        mezzmoFile.setNew(isNew);
        mezzmoFile.setRuleId(artistSongItem.getRuleId());
        mezzmoFile.setSong(comp.getFileTO().getTitle());
        mezzmoFile.setNewSong(artistSongItem.getSong());
        mezzmoFile.setNewArtist(artistSongItem.getArtist());
        getEricServiceInstance().insertMezzmoFile(mezzmoFile);

    }

    private MezzmoServiceImpl getMezzmoV1Instance(){
        return impl;
    }

    private MezzmoServiceImpl getMezzmoV2Instance(){
        return impl2;
    }

    private EricServiceImpl getEricServiceInstance(){
        return ericServiceImpl;
    }

    @Override
    public void run() {

    }
}
