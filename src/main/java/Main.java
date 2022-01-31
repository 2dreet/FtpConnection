import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class Main {

    static String password;
    static String username;
    static String host;
    static Integer port;
    static String path;
    static String fileName;
    static String protocol;

    public static Logger logger = Logger.getLogger("ftp-log");

    static Properties configProps = new Properties();

    public static void main(String[] args) {
        initConfig();
        FtpClient ftpClient = new FtpClient( password,  username,  host,  port,  path,  fileName,  protocol,  logger);
        ftpClient.getFile();
    }

    static void initConfig()
    {
        try(FileInputStream propsInput = new FileInputStream("config.ini"))
        {
            configProps.load(propsInput);

            host= configProps.getProperty("host");
            port = Integer.parseInt(configProps.getProperty("port"));
            username= configProps.getProperty("username");
            password = configProps.getProperty("password");
            protocol= configProps.getProperty("protocol");
            path= configProps.getProperty("path");
            fileName= configProps.getProperty("fileName");
        }
        catch(Exception e)
        {
            logger.severe(e.getMessage());
        }
    }
}
