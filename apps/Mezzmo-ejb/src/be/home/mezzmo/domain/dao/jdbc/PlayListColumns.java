package be.home.mezzmo.domain.dao.jdbc;

import be.home.common.database.DatabaseColumn;
import be.home.common.database.FieldType;

/**
 * Created by ghyssee on 14/12/2016.
 */
public enum PlaylistColumns implements DatabaseColumn {
    ID("ID", FieldType.SEQUENCE),
    NAME("Name", FieldType.NORMAL),
    TYPE("Type", FieldType.NORMAL),
    DESCRIPTION("Description", FieldType.NORMAL),
    PARENTID("ParentID", FieldType.NORMAL),
    AUTHOR("Author", FieldType.NORMAL),
    ICON ("Icon", FieldType.NORMAL),
    FILE ("File", FieldType.NORMAL),
    TRAVERSEFOLDER ("TraverseFolder", FieldType.NORMAL),
    FOLDERPATH ("FolderPath", FieldType.NORMAL),
    FILTER ("Filter", FieldType.NORMAL),
    DYNAMICTREETOKEN ("DynamicTreeToken", FieldType.NORMAL),
    RUNTIME ("Runtime", FieldType.NORMAL),
    STREAMNUM ("StreamNum", FieldType.NORMAL),
    ORDERBYCOLUMN ("OrderByColumn", FieldType.NORMAL),
    ORDERBYDIRECTION ("OrderByDirection", FieldType.NORMAL),
    LIMITBY ("LimitBy", FieldType.NORMAL),
    COMBINEAND ("CombineAnd", FieldType.NORMAL),
    LIMITTYPE ("LimitType", FieldType.NORMAL),
    PLAYLISTORDER ("PlaylistOrder", FieldType.NORMAL),
    MEDIATYPE ("MediaType", FieldType.NORMAL),
    THUMBNAILID ("ThumbnailID", FieldType.NORMAL),
    THUMBNAILAUTHOR ("ThumbnailAuthor", FieldType.NORMAL),
    CONTENTRATINGID ("ContentRatingID", FieldType.NORMAL),
    BACKDROPARTWORKID ("BackdropArtworkID", FieldType.NORMAL),
    DISPLAYTITLEFORMAT ("DisplayTitleFormat", FieldType.NORMAL),
    PARENTNAME ("Name", FieldType.TRANSIENT);

    public String columnName;
    public FieldType fieldType;

    PlaylistColumns(String s, FieldType t) {

        columnName = s;
        fieldType = t;
    }

    public String getColumnName() {
        return columnName;
    }
    public FieldType getFieldType() {
        return fieldType;
    }
    public String getColumnAndAlias() {
        return this + " AS " + columnName;
    }

}

