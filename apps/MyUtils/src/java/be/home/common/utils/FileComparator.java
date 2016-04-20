package be.home.common.utils;

import java.util.*;
import java.io.File;

public class FileComparator implements Comparator<File> {

  public int compare(File o1, File o2) {
      File f1, f2;
      f1 = (File) o1;
      f2 = (File) o2;
      return f1.lastModified() < f2.lastModified() ? -1 :
             f1.lastModified() == f2.lastModified() ? 0 : 1;
  }

}
