package be.home.selenium.common;

import be.home.common.exceptions.ApplicationException;
import be.home.common.model.FirefoxProfiles;
import be.home.common.model.FirefoxProfilesBO;
import org.openqa.selenium.firefox.*;
import org.openqa.selenium.remote.*;
import org.openqa.selenium.remote.http.W3CHttpCommandCodec;
import org.openqa.selenium.remote.http.W3CHttpResponseCodec;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.logging.Level;

public class FirefoxDriverSetup {

    public FirefoxProfiles.FirefoxInstance firefoxInstance;
    public String computerId;
    public String instanceId;

    public FirefoxDriverSetup(String computerId, String instanceId){
        this.computerId = computerId;
        this.instanceId = instanceId;
        FirefoxProfiles.Computer computer = FirefoxProfilesBO.getInstance().findComputer(computerId);
        this.firefoxInstance = FirefoxProfilesBO.getInstance().findInstance(computerId, instanceId);
    }

    public FirefoxDriver setupWebDriver() throws IOException {
        if (firefoxInstance == null){
            throw new ApplicationException("FirefoxInstance Is Empty");
        }
        else if (!Files.isDirectory(Paths.get(firefoxInstance.path))) {
            throw new ApplicationException("Path of firefox instance does not exist or is not valid: " + firefoxInstance.path);
        }
        File pathToBinary = new File(firefoxInstance.path + "\\App\\Firefox\\Firefox.exe");
        FirefoxProfile firefoxProfile = new FirefoxProfile(new File(firefoxInstance.path + "\\Data\\profile"));
        FirefoxDriver driver = getWebDriver(pathToBinary, firefoxProfile);
        SessionId sessionId = driver.getSessionId();
        HttpCommandExecutor ce = (HttpCommandExecutor) driver.getCommandExecutor();
        FirefoxProfiles.Session session = new FirefoxProfiles().new Session(sessionId.toString(), String.valueOf(ce.getAddressOfRemoteServer().getPort()));
        FirefoxProfilesBO.getInstance().saveSession(this.computerId, this.instanceId, session);
        firefoxInstance.session = session;
        System.out.println("Session id: " + firefoxInstance.session.id);
        System.out.println("Port: " + firefoxInstance.session.port);
        return driver;
    }

    public FirefoxDriver getWebDriver(File pathToBinary, FirefoxProfile firefoxProfile){
        FirefoxBinary ffBinary = new FirefoxBinary(pathToBinary);
        System.setProperty("webdriver.gecko.driver", "C:\\My Programs\\Firefox\\geckodriver.exe");
        FirefoxOptions options = new FirefoxOptions();
        options.setBinary(ffBinary);
        options.setProfile(firefoxProfile);

        System.setProperty("webdriver.gecko.driver", "C:\\My Programs\\Firefox\\geckodriver.exe");
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,"/php/test.logs");
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
        options.setLogLevel(FirefoxDriverLogLevel.INFO);
        FirefoxDriver driver = new FirefoxDriver(options);
        return driver;
    }

    public void closeWebDriver(RemoteWebDriver webDriver) throws IOException {
        FirefoxProfiles.Session session = new FirefoxProfiles().new Session(null, null);
        FirefoxProfilesBO.getInstance().saveSession(this.computerId, this.instanceId, session);
        webDriver.quit();

    }

    public RemoteWebDriver createDriverFromSession(final SessionId sessionId, URL command_executor){
        CommandExecutor executor = new HttpCommandExecutor(command_executor) {

            @Override
            public Response execute(Command command) throws IOException {
                Response response = null;
                if (command.getName() == "newSession") {
                    response = new Response();
                    response.setSessionId(sessionId.toString());
                    response.setStatus(0);
                    response.setValue(Collections.<String, String>emptyMap());

                    try {
                        Field commandCodec = null;
                        commandCodec = this.getClass().getSuperclass().getDeclaredField("commandCodec");
                        commandCodec.setAccessible(true);
                        commandCodec.set(this, new W3CHttpCommandCodec());

                        Field responseCodec = null;
                        responseCodec = this.getClass().getSuperclass().getDeclaredField("responseCodec");
                        responseCodec.setAccessible(true);
                        responseCodec.set(this, new W3CHttpResponseCodec());
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                } else {
                    response = super.execute(command);
                }
                return response;
            }
        };
        return new RemoteWebDriver(executor, new DesiredCapabilities());
    }

}
