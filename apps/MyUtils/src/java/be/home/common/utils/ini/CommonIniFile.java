package be.home.common.utils.ini;

import java.io.IOException;
import org.ini4j.Ini;

public interface CommonIniFile {

	public void read() throws IOException;

	public Ini.Section get(String section);

	public String get(String section, String key);

	public void update(String section, String key, String value);

	public void commit() throws IOException;

}
