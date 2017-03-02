package be.home.common.navigation;

import java.util.*;

/**
 * Created by ghyssee on 2/03/2017.
 */
public class NavigationBar {

    Map<Integer, NavigationItem> nav = new HashMap();

    public NavigationBar(){
    }

    public void add(NavigationItem navItem){
        nav.put(navItem.level, navItem);
    }

    public List getSortedList(){
        List list = new ArrayList(nav.values());
        return list;
    }

    public List getSortedList(int level){
        Collection <NavigationItem> values = nav.values();
        List <NavigationItem> list = new ArrayList();
        for (NavigationItem item : values){
            if (item.getLevel() <= level){
                list.add(item);
            }
        }
        return list;
    }
}
