package be.home.mezzmo.domain.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by ghyssee on 9/06/2016.
 */
public class MezzmoJavaBeanFactory {

    private static List<AlbumTO> albumList = new ArrayList<AlbumTO>();

    public static void setAlbumList(List<AlbumTO> albumList2) {
        albumList = albumList2;
    }

    public static Collection getAlbumList() {

        List<AlbumTO> albumList2 = new ArrayList<AlbumTO>();
        albumList2.add(new AlbumTO("TEST ALBUM", "TEST ARTIST"));
        albumList2.add(new AlbumTO("TEST ALBUM 2", "TEST ARTIST 2"));
        //getMezzmoService().getAlbums(to);

        return albumList2;
    }

    public static Collection getCompositeAlbumList() {

        List<MGOFileAlbumCompositeTO> list = new ArrayList<MGOFileAlbumCompositeTO>();
        MGOFileAlbumCompositeTO comp = new MGOFileAlbumCompositeTO();
        MGOFileTO fileTO = new MGOFileTO();
        MGOFileAlbumTO album = new MGOFileAlbumTO();
        album.setName("ALBUM NAME 1");
        MGOAlbumArtistTO albumArtist = new MGOAlbumArtistTO();
        albumArtist.setName("ALBUM ARTIST 1");
        comp.setFileTO(fileTO);
        comp.setFileAlbumTO(album);
        comp.setAlbumArtistTO(albumArtist);
        list.add(comp);
        //getMezzmoService().getAlbums(to);

        return list;
    }
}
