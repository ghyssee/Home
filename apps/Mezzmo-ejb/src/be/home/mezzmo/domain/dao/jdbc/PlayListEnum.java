package be.home.mezzmo.domain.dao.jdbc;

import be.home.common.database.DatabaseColumn;

import java.util.*;

/**
 * Created by ghyssee on 14/12/2016.
 */
public enum PlayListEnum implements DatabaseColumn {
    ID("ID", "PRIMARYKEY"),
    NAME("Name", "FIELD"),
    TYPE("Type", "FIELD"),
    DESCRIPTION("Description", "FIELD"),
    PARENTID("ParentID", "FIELD"),
    AUTHOR("Author", "FIELD"),
    ICON ("Icon", "FIELD"),
    FILE ("File", "FIELD"),
    TRAVERSEFOLDER ("TraverseFolder", "FIELD"),
    FOLDERPATH ("FolderPath", "FIELD"),
    FILTER ("Filter", "FIELD"),
    DYNAMICTREETOKEN ("DynamicTreeToken", "FIELD"),
    RUNTIME ("Runtime", "FIELD"),
    STREAMNUM ("StreamNum", "FIELD"),
    ORDERBYCOLUMN ("OrderByColumn", "FIELD"),
    ORDERBYDIRECTION ("OrderByDirection", "FIELD"),
    LIMITBY ("LimitBy", "FIELD"),
    COMBINEAND ("CombineAnd", "FIELD"),
    LIMITTYPE ("LimitType", "FIELD"),
    PLAYLISTORDER ("PlaylistOrder", "FIELD"),
    MEDIATYPE ("MediaType", "FIELD"),
    THUMBNAILID ("ThumbnailID", "FIELD"),
    THUMBNAILAUTHOR ("ThumbnailAuthor", "FIELD"),
    CONTENTRATINGID ("ContentRatingID", "FIELD"),
    BACKDROPARTWORKID ("BackdropArtworkID", "FIELD"),
    DISPLAYTITLEFORMAT ("DisplayTitleFormat", "FIELD");

    public String columnName;
    public String type;

    PlayListEnum(String s, String t) {

        columnName = s;
        type = t;
    }

    public String getColumnName() {
        return columnName;
    }
    public String getType() {
        return type;
    }
    public String getColumnAndAlias() {
        return this + " AS " + columnName;
    }

}

