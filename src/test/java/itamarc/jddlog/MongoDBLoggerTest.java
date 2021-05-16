package itamarc.jddlog;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import java.util.Date;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Collections;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
 
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * This test file connects with MongoDB and inserts a document there.
 * It needs a config file to point to the database with right credentials.
 * 
 * Example of the jddconfig.json file:
 * <code>
{
    "JDD_MONGODBURI": "mongodb+srv://<username>:<password>@<cluster>.<host>.mongodb.net/myFirstDatabase?retryWrites=true&w=majority"
}
 * </code>
 */
public class MongoDBLoggerTest {
    @Test
    public void mongoDBLoggerTest() {
        readConfigToEnvironment();
        MongoDBLogger logger = new MongoDBLogger();
        // 2021-05-06T22:07:00.000+00:00
        String date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(new Date());
        String messageBody = "{\n" + "    \"time\": {\"$date\": \"" + date + "\"}\n"
                + "    \"origin\": \"MongoDBLoggerTest\"\n" + "    \"message\": \"Run test\"\n"
                + "    \"level\": \"INFO\"\n" + "}";
        logger.saveLogMessage(messageBody);
        assertTrue(true);
    }

    @Test
    public void mongoDBLoggerBrokenbodyTest() {
        readConfigToEnvironment();
        MongoDBLogger logger = new MongoDBLogger();
        // 2021-05-06T22:07:00.000+00:00
        String date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(new Date());
        String messageBody = "\"time\": {\"$date\": \"" + date + "\"}\n" // Body with missing "{"
                + "    \"origin\": \"MongoDBLoggerTest\"\n" + "    \"message\": \"Run test\"\n"
                + "    \"level\": \"INFO\"\n" + "}";
        logger.saveLogMessage(messageBody);
        assertTrue(true);
    }

    // Ugly hack code from StackOverflow to be used only for testing purposes
    @SuppressWarnings("unchecked")
    protected void setEnv(Map<String, String> newenv) throws Exception {
        try {
            Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
            Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
            theEnvironmentField.setAccessible(true);
            Map<String, String> env = (Map<String, String>)theEnvironmentField.get(null);
            env.putAll(newenv);
            Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
            theCaseInsensitiveEnvironmentField.setAccessible(true);
            Map<String, String> cienv = (Map<String, String>)theCaseInsensitiveEnvironmentField.get(null);
            cienv.putAll(newenv);
        } catch (NoSuchFieldException e) {
            Class[] classes = Collections.class.getDeclaredClasses();
            Map<String, String> env = System.getenv();
            for(Class cl : classes) {
                if("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
                    Field field = cl.getDeclaredField("m");
                    field.setAccessible(true);
                    Object obj = field.get(env);
                    Map<String, String> map = (Map<String, String>) obj;
                    map.clear();
                    map.putAll(newenv);
                }
            }
        }
    }

    // Read config from jddconfig.json file to avoid hardcoding the configuration
    private void readConfigToEnvironment() {
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();
        //try (FileReader reader = new FileReader("jddconfig.json")) {
        try {
            String config = loadJsonFromResource("jddconfig.json");
            //Read JSON file
            Object obj = jsonParser.parse(config);
 
            JSONObject jddconfig = (JSONObject) obj;
            System.out.println(jddconfig);
            
            //Get MONGOURI
            String uri = (String) jddconfig.get("JDD_MONGODBURI");
            Map<String, String> confMap = new HashMap<String, String>();
            confMap.put("JDD_MONGODBURI", uri);
            setEnv(confMap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This way to load the config file is needed to work in Maven
    private String loadJsonFromResource(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        String ret = "";

        try (InputStream inputStream = classLoader.getResourceAsStream(fileName);
            InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(streamReader)) {

            String line;
            StringBuffer buf = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                buf.append(line);
            }
            ret = buf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
