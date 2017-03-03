package be.home.common.navigation;

import java.util.*;

/**
 * Created by ghyssee on 2/03/2017.
 */
public class NavigationBar {

    Map<Integer, NavigationItem> nav = new HashMap();
    Integer level = 0;

    public NavigationBar(){
    }

    public Integer addLevel(){
        return level++;
    }

    public Integer getLevel(){
        return level;
    }
    public void add(NavigationItem navItem){
        level++;
        navItem.setLevel(level);
        nav.put(level, navItem);
    }

    public void add(Integer level, NavigationItem navItem){
        navItem.setLevel(level);
        nav.put(level, navItem);
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
