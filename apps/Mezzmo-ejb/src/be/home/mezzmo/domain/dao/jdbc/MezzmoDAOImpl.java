package be.home.mezzmo.domain.dao.jdbc;

import be.home.common.dao.jdbc.SQLiteUtils;
import be.home.common.database.sqlbuilder.Comparator;
import be.home.common.database.sqlbuilder.ConditionType;
import be.home.common.database.sqlbuilder.SQLBuilder;
import be.home.common.enums.MP3Tag;
import be.home.common.model.TransferObject;
import be.home.mezzmo.domain.dao.definition.MGOAlbumArtistColumns;
import be.home.mezzmo.domain.dao.definition.MGOFileAlbumColumns;
import be.home.mezzmo.domain.dao.definition.MGOFileColumns;
import be.home.mezzmo.domain.dao.definition.TablesEnum;
import be.home.mezzmo.domain.model.*;
import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by ghyssee on 9/02/2016.
 */
public class MezzmoDAOImpl extends MezzmoRowMappers {

    private static final Logger log = Logger.getLogger(MezzmoDAOImpl.class);

    public List<MGOFileTO> getFiles(MGOFileAlbumCompositeTO compSearchTO)
    {
        Object[] params = {compSearchTO.getFileTO().getFileTitle(), "%"};
        if (compSearchTO.getFileAlbumTO() != null && compSearchTO.getFileAlbumTO().getName() != null){
            params[1] = compSearchTO.getFileAlbumTO().getName();
        }
        List<MGOFileTO> list  = getInstance().getJDBCTemplate().query(FILE_SELECT_TITLE, new MezzmoRowMappers.FileRowMapper(), params);
        return list;
    }

    public int updatePlayCount(String fileTitle, String album, int playCount, java.util.Date dateLastPlayed) throws SQLException {

        Object[] params = {playCount, SQLiteUtils.convertDateToString(dateLastPlayed), fileTitle, playCount,
                           album == null ? "%" : album, fileTitle
                          };
        return getInstance().getJDBCTemplate().update(FILE_UPDATE_PLAYCOUNT, params);


    }

    public int synchronizePlayCount(Long fileID, int playCount, java.util.Date lastPlayed) throws SQLException {
        Object[] params = {playCount, SQLiteUtils.convertDateToString(lastPlayed), fileID};
        return getInstance().getJDBCTemplate().update(FILE_SYNC_PLAYCOUNT, params);
    }

    public List<MGOFileAlbumCompositeTO> getMP3FilesWithPlayCount(TransferObject to)
    {
        Object[] params = {to.getIndex(), to.getLimit()};
        List<MGOFileAlbumCompositeTO> list  = getInstance().getJDBCTemplate().query(FILE_PLAYCOUNT, new MezzmoRowMappers.FileAlbumPlayCountMapper(), params);
        if (list.size() == 0 || list.size() < to.getLimit()){
            to.setEndOfList(true);
        }
        to.addIndex(list.size());
        return list;
    }

    public List<MGOFileAlbumCompositeTO> getAlbums(MGOFileAlbumTO albumTO, TransferObject to)
    {
        List<MGOFileAlbumCompositeTO> list = null;
        Object[] params;
        if (albumTO == null) {
            params = new Object[] {"%"};
        }
        else {
            params = new Object[] {albumTO.getName()};
        }
        list = getInstance().getJDBCTemplate().query(LIST_ALBUMS_DEFAULT, new AlbumRowMapper(), params);
        return list;
    }

    public List<MGOFileAlbumCompositeTO> getAlbumsWithExcludeList(MGOFileAlbumTO albumTO, List <String> albumsToExclude,
                                                                  TransferObject to)
    {
        SQLBuilder query = ((SQLBuilder) SerializationUtils.clone(LIST_ALBUMS));
        List params = new ArrayList();
        if (albumTO == null) {
            params.add("%");
        }
        else {
            params.add(albumTO.getName());
        }
        for (String albumToExclude : albumsToExclude){
            query.addCondition(TablesEnum.MGOFileAlbum.alias(), MGOFileAlbumColumns.ALBUM, Comparator.NOT_LIKE);
            params.add(albumToExclude);
        }
        List<MGOFileAlbumCompositeTO> list = null;
        String sql = query.render();
        list = getInstance().getJDBCTemplate().query(sql, new AlbumRowMapper(), params.toArray());
        return list;
    }



    public List<MGOFileAlbumCompositeTO> getAlbumTracks(TransferObject to)
    {
        List<MGOFileAlbumCompositeTO> list  = getInstance().getJDBCTemplate().query(LIST_ALBUMS_TRACKS, new AlbumTrackRowMapper());
        return list;
    }

    public List<MGOFileAlbumCompositeTO> getTop20()
    {
        List<MGOFileAlbumCompositeTO> list  = getInstance().getJDBCTemplate().query(LIST_TOP20, new MezzmoRowMappers.FileAlbumRowMapper());
        return list;

    }

    public MGOFileTO findByFile(String file) {
        Object[] params = {file};
        MGOFileTO fileTO = (MGOFileTO) getInstance().getJDBCTemplate().queryForObject(FIND_BY_FILE, new FileRowMapper(), params);
        return fileTO;
    }

    public MGOFileTO findByTitleAndAlbum(MGOFileAlbumCompositeTO comp){
        Object[] params = {
                SQLiteUtils.getSearchField(comp.getFileTO().getTrack()),
                SQLiteUtils.getSearchField(comp.getFileArtistTO().getArtist()),
                SQLiteUtils.getSearchField(comp.getFileTO().getTitle()),
                SQLiteUtils.getSearchField(comp.getFileAlbumTO().getName())
        };
        MGOFileTO fileTO = (MGOFileTO) getInstance().getJDBCTemplate().queryForObject(FILE_FIND_TAGINFO_CRITERIA, new FileRowMapper(), params);
        return fileTO;
    }

    public List<MGOFileAlbumCompositeTO> findSongsByAlbum(MGOFileAlbumCompositeTO comp){
        Object[] params = {
                comp.getFileAlbumTO().getId() == 0 ? "%": comp.getFileAlbumTO().getId()
        };
        List<MGOFileAlbumCompositeTO> list = getInstance().getJDBCTemplate().query(FILE_FIND_TAGINFO_BY_ALBUMID, new SongsAlbumRowMapper(), params);
        return list;
    }

    public MGOFileTO findCoverArt(Long albumId){
        Object[] params = {albumId };
        MGOFileTO fileTO = (MGOFileTO) getInstance().getJDBCTemplate().queryForObject(FIND_COVER_ART, new FileRowMapper(), params);
        return fileTO;
    }

    public List<MGOFileAlbumCompositeTO> getCustomPlayListSongs(List <MGOFileAlbumCompositeTO> albums, int limit)
    {
        SQLBuilder query = ((SQLBuilder) SerializationUtils.clone(LIST_CUSTOM));

        List params = new ArrayList();
        for (MGOFileAlbumCompositeTO album : albums){
            //query
            query
                    .groupCondition()
                    .add(TablesEnum.MGOFile.alias(), MGOFileColumns.RANKING, Comparator.GREATER, null, ConditionType.AND)
                    .add(TablesEnum.MGOFile.alias(), MGOFileColumns.PLAYCOUNT, Comparator.LESS, 2, ConditionType.AND)
                    .add(TablesEnum.MGOFileAlbum.alias(), MGOFileAlbumColumns.ALBUM, Comparator.LIKE, null, ConditionType.AND)
                    .add(TablesEnum.MGOAlbumArtist.alias(), MGOAlbumArtistColumns.ALBUMARTIST, Comparator.LIKE, null, ConditionType.AND)
                    .close();
            params.add(0L);
            params.add(album.getFileAlbumTO().getName());
            String albumArtist = album.getAlbumArtistTO().getName();
            params.add(albumArtist == null ? "%" : albumArtist);
        }
        params.add(limit);

        List<MGOFileAlbumCompositeTO>  list = getInstance().getJDBCTemplate().query(query.render(), new FileAlbumPlayCountMapper(), params.toArray());
        return list;

    }

    public List<MGOFileAlbumCompositeTO> getSongsAlbum(Long albumId, Long albumArtistId)
    {
        String query = FIND_SONGS_ALBUM;
        Object[] params = {albumId, albumArtistId};

        List<MGOFileAlbumCompositeTO>  list = getInstance().getJDBCTemplate().query(query, new FileAlbumPlayCountMapper(), params);
        return list;

    }

    public List<MGOFileAlbumCompositeTO> getLastPlayed(int number)
    {
        String query = FIND_LAST_PLAYED;
        Object[] params = {number};
        List<MGOFileAlbumCompositeTO>  list = getInstance().getJDBCTemplate().query(query, new FileAlbumPlayCountMapper(), params);
        return list;

    }

    public int updateRanking(Long fileID, int ranking) throws SQLException {
        Object[] params = {ranking, fileID};
        return getInstance().getJDBCTemplate().update(FILE_UPDATE_RATING, params);
    }

    public List<MGOFileAlbumCompositeTO> getMaxDisc()
    {
        String query = MAX_DISC;
        List<MGOFileAlbumCompositeTO>  list = getInstance().getJDBCTemplate().query(query, new MaxDiscRowMapper());
        return list;

    }
    public int updateArtist(MGOFileArtistTO artist) {
        Object params[] = {artist.getArtist(), artist.getID()};
        int nr = getInstance().getJDBCTemplate().update(FILE_UPDATE_ARTIST, params);
        return nr;
    }

    public int updateSong(MGOFileAlbumCompositeTO comp, MP3Tag mp3Tag){
        Object[] params;
        int nr = 0;
        switch (mp3Tag) {
            case TITLE:
                params = new Object[] {comp.getFileTO().getTitle(),
                        comp.getFileTO().getSortTitle(),
                        comp.getFileTO().getId()};
                nr = getInstance().getJDBCTemplate().update(FILE_UPDATE_TITLE, params);
                break;
            case ARTIST:
                nr = updateArtist(comp.getFileArtistTO());
                break;
            case ALBUM:
                params = new Object[] {comp.getFileAlbumTO().getName(),
                        comp.getFileAlbumTO().getId()};
                nr = getInstance().getJDBCTemplate().update(FILE_UPDATE_ALBUM, params);
                break;
            case FILE:
                params = new Object[] {comp.getFileTO().getFile(),
                        comp.getFileTO().getFileTitle(),
                        comp.getFileTO().getId()};
                nr = getInstance().getJDBCTemplate().update(FILE_UPDATE_FILE, params);
                break;
            case FILETITLE:
                params = new Object[] {comp.getFileTO().getFileTitle(),
                        comp.getFileTO().getId()};
                nr = getInstance().getJDBCTemplate().update(FILE_UPDATE_FILETITLE, params);
                break;
            case TRACK:
                params = new Object[] {comp.getFileTO().getTrack(), comp.getFileTO().getId()};
                nr = getInstance().getJDBCTemplate().update(FILE_UPDATE_TRACK, params);
                break;
            case DISC:
                params = new Object[] {comp.getFileTO().getDisc(), comp.getFileTO().getId()};
                nr = getInstance().getJDBCTemplate().update(FILE_UPDATE_DISC, params);
                break;
            default:
                log.error("Unknown Type: " + mp3Tag.name());
                break;
        }
        return nr;
    }

    public MGOFileArtistTO findArtist(MGOFileArtistTO artist){
        Object[] params = {
                artist.getArtist()
                };
        MGOFileArtistTO fileArtistTO = (MGOFileArtistTO) getInstance().getJDBCTemplate().queryForObject(FIND_ARTIST, new ArtistRowMapper(), params);
        return fileArtistTO;
    }

    public MGOFileArtistTO findArtistById(MGOFileArtistTO artist){
        Object[] params = {
                artist.getID()
        };
        MGOFileArtistTO fileArtistTO = (MGOFileArtistTO) getInstance().getJDBCTemplate().queryForObject(FIND_ARTIST_BY_ID, new ArtistRowMapper(), params);
        return fileArtistTO;
    }

    public List <MGOFileAlbumCompositeTO> findLinkedArtist(MGOFileArtistTO artist){
        Object[] params = {
                artist.getID()
        };
        List <MGOFileAlbumCompositeTO> list = getInstance().getJDBCTemplate().query(FIND_LINKED_ARTIST, new FileArtistRowMapper(), params);
        return list;
    }

    public Result updateLinkFileArtist(MGOFileArtistTO artist, Long newId){
        Object[] params;
        Result result = new Result();
        params = new Object[] {newId, artist.getID()};
        result.setNr1(getInstance().getJDBCTemplate().update(UPDATE_LINK_FILE_ARTIST, params));
        return result;
    }

    public int updateLinkFileArtist2(MGOFileAlbumCompositeTO comp){
        Object[] params;
        params = new Object[] {comp.getFileArtistTO().getID(), comp.getFileTO().getId()};
        int nr = getInstance().getJDBCTemplate().update(UPDATE_LINK_FILE_ARTIST2, params);
        return nr;
    }

    public int deleteArtist(MGOFileArtistTO artist){
        Object[] params;
        params = new Object[] {artist.getID()};
        int nr = getInstance().getJDBCTemplate().update(DELETE_ARTIST, params);
        return nr;
    }

    public int deleteAlbum(MGOFileAlbumTO album){
        Object[] params;
        params = new Object[] {album.getId()};
        int nr = getInstance().getJDBCTemplate().update(DELETE_ALBUM, params);
        return nr;
    }

    public Integer insertAlbum(final MGOFileAlbumTO album){

        KeyHolder keyHolder = new GeneratedKeyHolder();
        getInstance().getJDBCTemplate().update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                        PreparedStatement pst =
                                con.prepareStatement(INSERT_ALBUM, new String[] {"id"});
                        pst.setString(1, album.getName());
                        return pst;
                    }
                },
                keyHolder);
        return (Integer)keyHolder.getKey();
    }

    public List <MGOFileAlbumCompositeTO> findLinkedAlbum(MGOFileAlbumTO album){
        Object[] params = {
                album.getId()
        };
        List <MGOFileAlbumCompositeTO> list = getInstance().getJDBCTemplate().query(FIND_LINKED_ALBUM, new SingleFileAlbumRowMapper(), params);
        return list;
    }

    public Integer insertArtist2(final MGOFileArtistTO artist){

        KeyHolder keyHolder = new GeneratedKeyHolder();
        getInstance().getJDBCTemplate().update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                        PreparedStatement pst =
                                con.prepareStatement(INSERT_ARTIST, new String[] {"id"});
                        pst.setString(1, artist.getArtist());
                        return pst;
                    }
                },
                keyHolder);
        return (Integer)keyHolder.getKey();
    }

    public Integer insertArtist(final MGOFileArtistTO artist){
        Object[] params = {artist.getArtist()};
        Integer key = insertJDBC(getInstance().getJDBCTemplate(), params, INSERT_ARTIST, "id");
        return key;
    }

    public int updateLinkFileAlbum(MGOFileAlbumCompositeTO comp, long newAlbumId){
        Object[] params = {newAlbumId, comp.getFileAlbumTO().getId(), comp.getFileTO().getId()};
        int nr = getInstance().getJDBCTemplate().update(UPDATE_LINK_FILE_ALBUM, params);
        return nr;
    }

}
