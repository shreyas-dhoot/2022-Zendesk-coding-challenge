import java.io.IOException;
import java.util.Properties;

public class SystemConfiguration {
    public static String getConfig(String fileName,String config) throws IOException {
        Properties properties = new Properties();
        properties.load(SystemConfiguration.class.getClassLoader().getResourceAsStream(fileName));
        return properties.getProperty(config);
    }
}
