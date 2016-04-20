package be.home.mezzmo.domain.service;

import be.home.mezzmo.domain.bo.MezzmoBO;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.model.MGOFileTO;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by ghyssee on 13/04/2016.
 */
public class MezzmoServiceImpl {


    public int updatePlayCount(String fileID, String album, int playCount, java.util.Date dateLastPlayed) throws SQLException {
        MezzmoBO bo = new MezzmoBO();
        return bo.updatePlayCount(fileID, album, playCount, dateLastPlayed);
    }

    public List<MGOFileTO> findMP3Files(MGOFileAlbumCompositeTO compSearchTO){
        MezzmoBO bo = new MezzmoBO();
        return bo.findMP3Files(compSearchTO);

    }


    }
