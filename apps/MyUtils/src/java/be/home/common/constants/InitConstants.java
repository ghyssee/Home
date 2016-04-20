package be.home.common.constants;

public interface InitConstants {

  public static final String INIFILE = "/projects/PICMGT/data/picmgt.ini";
  
  public static final String SECTION_MYDOCS = "mydocs";
  public static final String SECTION_LOG4J = "log4j";
  public static final String SECTION_MAKEDVD = "dvdmaker";
  public static final String SECTION_FILEMONITOR = "FileMonitor";
  public static final String SECTION_IMAGEINFO = "ImageInfo";

  public static final String MYDOCS_SOURCEFOLDER = "sourceFolder";
  public static final String MYDOCS_DESTFOLDER = "destFolder";
  public static final String MYDOCS_DESTFOLDERPATTERN = "destFolderPattern";
  public static final String MYDOCS_STARTCOUNTER = "startCounter";
  public static final String MYDOCS_STARTCOUNTERPATTERN = "startCounterPattern";
  public static final String MYDOCS_MAXFILES_DIR = "maxFilesFolder";
  public static final String MYDOCS_MAX_FILESIZE = "maxFileSize";
  
  public static final String DVDMAKER_SOURCEFOLDER = "sourceFolder";
  public static final String DVDMAKER_DESTFOLDER = "destFolder";
  public static final String DVDMAKER_DESTFOLDERPATTERN = "destFolderPattern";
  public static final String DVDMAKER_STARTCOUNTER = "startCounter";
  public static final String DVDMAKER_STARTCOUNTERPATTERN = "startCounterPattern";
  public static final String DVDMAKER_MAXFILES_DIR = "maxFilesFolder";
  public static final String DVDMAKER_CAPACITY = "capacity";
  public static final String DVDMAKER_SIZE = "size";
  
  public static final String FILEMONITOR_SOURCEFOLDER = "sourceFolder";
  public static final String FILEMONITOR_DESTFOLDER = "destFolder";
  public static final String FILEMONITOR_CREATENEWFOLDER = "createNewFolder";
  
  public static final String IMAGEINFO_SOURCEFOLDER = "sourceFolder";
  public static final String IMAGEINFO_MAXNROFLINES = "maxNrOfLines";
  public static final String IMAGEINFO_CSVPATH = "csvPath";
  public static final String IMAGEINFO_CSVFILE = "csvFile";
  public static final String IMAGEINFO_CSVBADFILE = "csvBadFile";
  public static final String IMAGEINFO_COUNTERPATTTERN = "counterPattern";
  
  public static final String NBPRO_INI = "iniFile";
  public static final String NBPRO_THREAD = "Threads";
  public static final String NBPRO_BACKUPINI = "backupIni";
  public static final String NBPRO_CONNECTIONLIMIT = "ConnectionLimit";
  public static final String NBPRO_CONNECTIONS = "connections";
  public static final String NBPRO_ACTIVE = "Active";
  
  public static final String WEBSHOTS_SERIES = "series";
  public static final String WEBSHOTS_PREFIX = "prefix";
  public static final String WEBSHOTS_SUFFIX = "suffix";
  public static final String WEBSHOTS_CSV = "importCSV";
  public static final String WEBSHOTS_EXPORTFILE = "export";
  public static final String WEBSHOTS_RENAMEFILE = "rename";

  public static final String WEBSHOTS_URLFILE = "url";
  public static final String WEBSHOTS_TITLE = "title";
  public static final String WEBSHOTS_NAME = "name";
  
  public static final String PROXY = "proxy";
  public static final String PROXY_SERVER = "server";
  public static final String PROXY_PORT = "port";
  public static final String PROXY_USER = "user";
  public static final String PROXY_PASSWORD = "password";
    
  public static final String PARAM_INI = "inifile";
  public static final String PARAM_BATCH = "batch";
  
  public static final String LOGFILE = "logFile";

  public static final String WIKI_INPUT = "input";
  public static final String WIKI_OUTPUT = "output";
  public static final String WIKI_MAXAPPSFILE = "maxappsfile";
  public static final String WIKI_RESULTLOG = "wikiResultLog";

  public interface Movies {

    public interface Import {
      public static final int ID = 0;
      public static final int TITLE = 1;
      public static final int ALTERNATE_TITLE = 2;
      public static final int YEAR = 3;
      public static final int GENRE = 4;
      public static final int STOCK_PLACE = 5;
      public static final int IMDB_ID = 6;
      public static final int MAX_FIELDS = 7;
    }
  }}
