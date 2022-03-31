package be.home.mezzmo.domain.bo;

import be.home.mezzmo.domain.dao.jdbc.MezzmoAlbumArtistDAOImpl;
import be.home.mezzmo.domain.model.MGOAlbumArtistTO;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.model.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Gebruiker on 12/03/2017.
 */
public class AlbumArtistBO {
    private static final Logger log = LogManager.getLogger();

    public MGOAlbumArtistTO findAlbumArtist(MGOAlbumArtistTO albumArtist){

        return getMezzmoAlbumArtistDAO().findAlbumArtist(albumArtist);
    }

    public MGOAlbumArtistTO findAlbumArtistById(MGOAlbumArtistTO artist){
        return getMezzmoAlbumArtistDAO().findAlbumArtistById(artist);
    }

    public int updateAlbumArtist(MGOAlbumArtistTO albumArtist){
        return getMezzmoAlbumArtistDAO().updateAlbumArtist(albumArtist);
    }


    private int findAndUpdate(MGOFileAlbumCompositeTO comp) {
        MGOAlbumArtistTO albumArtist = findAlbumArtist(comp.getAlbumArtistTO());
        albumArtist.setName(comp.getAlbumArtistTO().getName());
        updateAlbumArtist(albumArtist);
        MGOAlbumArtistTO oldAlbumArtist = comp.getAlbumArtistTO();
        comp.setAlbumArtistTO(albumArtist);
        int nr = updateLinkFileAlbumArtist2(comp);
        checkAlbumArtistLinked(oldAlbumArtist);
        return nr;
    }

    public int updateLinkFileAlbumArtist2(MGOFileAlbumCompositeTO comp){
        return getMezzmoAlbumArtistDAO().updateLinkFileAlbumArtist2(comp);
    }


    public int update (MGOFileAlbumCompositeTO comp) throws SQLException {
        int nr = 0;
        MGOAlbumArtistTO albumArtist = null;
        try {
            albumArtist = findAlbumArtistById(comp.getAlbumArtistTO());
            if (albumArtist.getName().toUpperCase().equals(comp.getAlbumArtistTO().getName().toUpperCase())){
                try {
                    // Major Lazer Feat. Justin Bieber & Mø is Not The Same As
                    // Major Lazer Feat. Justin Bieber & MØ
                    // look up the artist just to be sure it does not exist
                    nr = findAndUpdate(comp);
                }
                catch (EmptyResultDataAccessException e){
                    // artist is case insensitive equal, example Jojo == JoJo
                    // look up the artist by name and update the artist (+link) and it is ok
                    log.info("Album Artist is case insensitive equal: " + comp.getAlbumArtistTO().getName());
                    nr = updateAlbumArtist(comp.getAlbumArtistTO());
                }

            }
            else {
                try {
                    // artist is different, and exist
                    // update artist to be sure it is case sensitive correct
                    // check if there are other songs linked to the old artist, if not, delete it
                    log.info("Artist is different, but exist already: " + comp.getAlbumArtistTO().getName());
                    nr = findAndUpdate(comp);
                }
                catch (EmptyResultDataAccessException e){
                    // artist is different, and does not exist already, example Jojo Ft. Dodo <==> Jojo Feat. Dodo
                    // insert new artist and link it to the file
                    // maybe check if there are other songs linked to the old artist, if not, delete it
                    // this situation is only for examples like: Major Lazer Feat. Justin Bieber & MØ
                    // MØ and Mø is not case insensitive the same for SQLite, this will create a new artist for this
                    // situation
                    Long albumArtistId = insertAlbumArtist(comp.getAlbumArtistTO());
                    if (albumArtistId != null){
                        log.info("New Album Artist created: " + comp.getAlbumArtistTO().getName() + "/Id: " + albumArtistId);
                        MGOAlbumArtistTO oldAlbumArtist = new MGOAlbumArtistTO();
                        oldAlbumArtist.setId(comp.getAlbumArtistTO().getId());
                        comp.getAlbumArtistTO().setId(albumArtistId);
                        nr = updateLinkFileAlbumArtist2(comp);
                        checkAlbumArtistLinked(oldAlbumArtist);
                    }
                    else {
                        // if insert failed nr will be empty and tag info of the song will not be updated
                        log.error("Insert Album Artist failed: " + comp.getAlbumArtistTO().getName());
                    }
                }
            }
        }
        catch (EmptyResultDataAccessException e){
            // artist not found By Id
            // this should never occur
            log.error("Find Album Artist By Id failed: " + comp.getAlbumArtistTO().getName() + " Id: " + comp.getAlbumArtistTO().getId());
            nr = 0;
        }
        return nr;
    }

    public List <MGOFileAlbumCompositeTO> findLinkedAlbumArtist(MGOAlbumArtistTO albumArtist){
        return getMezzmoAlbumArtistDAO().findLinkedAlbumArtist(albumArtist);
    }


    private void checkAlbumArtistLinked(MGOAlbumArtistTO albumArtist) {

        List<MGOFileAlbumCompositeTO> list = findLinkedAlbumArtist(albumArtist);
        if (list == null || list.size() == 0) {
            // nothing linked to it, safe to delete the link + the artist
            int del = deleteAlbumArtist(albumArtist);
            log.info("Nr Of Album Artists Deleted: " + del + " /Id: " + albumArtist.getId());
        }
    }


    public Result updateLinkFileArtist(MGOAlbumArtistTO albumArtist, Long newId){
        return getMezzmoAlbumArtistDAO().updateLinkFileAlbumArtist(albumArtist, newId);
    }


    public int deleteAlbumArtist(MGOAlbumArtistTO albumArtist){

        return getMezzmoAlbumArtistDAO().deleteAlbumArtist(albumArtist);
    }

    public Long insertAlbumArtist(MGOAlbumArtistTO albumArtist){
        return getMezzmoAlbumArtistDAO().insertAlbumArtist(albumArtist);
    }


    public MezzmoAlbumArtistDAOImpl getMezzmoAlbumArtistDAO(){
        return new MezzmoAlbumArtistDAOImpl();
    }


}
