
package pl.rstepniewski.pool.util;

import java.io.IOException;
import java.util.Properties;

public final class PropertiesUtil {


    private PropertiesUtil(){}

    private static final Properties PROPERTIES =new Properties();

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try(var stream = PropertiesUtil.class.getClassLoader().getResourceAsStream("application.properties")){
            PROPERTIES.load(stream);
        }
        catch (IOException ex){
            throw new RuntimeException(ex);
        }
    }

    public static String getString(String key){
        return PROPERTIES.getProperty(key);
    }

    public static int getNumber(String key){
        String value = PROPERTIES.getProperty(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format for key: " + key, e);
        }
    }


}