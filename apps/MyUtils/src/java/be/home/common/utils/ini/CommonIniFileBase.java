package be.home.common.utils.ini;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Ini;

import java.util.Map;
import java.util.Set;

public class CommonIniFileBase implements CommonIniFile {

	private File iniFile;
	private static final Logger log = LogManager.getLogger(CommonIniFileBase.class);
	private Ini ini;

	public CommonIniFileBase(File iniFile) {
		this.iniFile = iniFile;
	}

	public void read() throws IOException {

		ini = new Ini();
		FileInputStream in = new FileInputStream(iniFile);
		try {
			ini.load(in);
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	public Ini.Section get(String section) {

		Ini.Section sec = (Ini.Section) ini.get(section);
		log.info(StringUtils.repeat('-', 50));
		log.info("Reading Section [" + section + "]");
		log.info(StringUtils.repeat('-', 80));
		Set keySet = sec.keySet();
		for (Object obj : keySet){
			String key = (String) obj;
			log.info("Key = " + key);
			String value = sec.fetch(key);
			log.info("Value = " + value);
			log.info(StringUtils.repeat('-', 50));
		}
		log.info("End Reading Section [" + section + "]");
		return sec;

	}

	public String get(String section, String key) {

		Ini.Section sec = (Ini.Section) ini.get(section);
		if (sec == null) {
			return null;
		}
		return (String) sec.fetch(key);

	}

	@SuppressWarnings("unchecked")
	public void update(String section, String key, String value) {

		Map sec = (Map) ini.get(section);
		sec.put(key, value);
	}

	public void commit() throws IOException {

		FileOutputStream out = new FileOutputStream(iniFile);
		try {
			ini.store(out);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	public File getIniFile() {
		return iniFile;
	}

	public Ini getIni() {
		return ini;
	}

}
