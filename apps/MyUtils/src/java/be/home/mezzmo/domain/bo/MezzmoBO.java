package be.home.mezzmo.domain.bo;

import be.home.common.model.BusinessObject;
import be.home.common.model.TransferObject;
import be.home.mezzmo.domain.dao.jdbc.MezzmoDAOImpl;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.model.MGOFileTO;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by ghyssee on 13/04/2016.
 */
public class MezzmoBO extends BusinessObject {

    public MezzmoBO (){
    }


    public int updatePlayCount(String fileID, String album, int playCount, java.util.Date dateLastPlayed) throws SQLException {
        return getMezzmoDAO().updatePlayCount(fileID, album, playCount, dateLastPlayed);
    }

    public List<MGOFileTO> findMP3Files(MGOFileAlbumCompositeTO compSearchTO){
        List<MGOFileTO> list = getMezzmoDAO().getFiles(compSearchTO);
        return list;

    }

    public List<MGOFileTO> getFiles(MGOFileAlbumCompositeTO compSearchTO){
        return getMezzmoDAO().getFiles(compSearchTO);
    }

    public List<MGOFileAlbumCompositeTO> getMP3FilesWithPlayCount(TransferObject to){
        return getMezzmoDAO().getMP3FilesWithPlayCount(to);
    }

    public List<MGOFileAlbumCompositeTO> getAlbums(TransferObject to){
        return getMezzmoDAO().getAlbums(to);
    }

    public MezzmoDAOImpl getMezzmoDAO(){
        return new MezzmoDAOImpl();
    }

}
