package itamarc.jddlog;

import java.util.Date;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;


public class MongoDBLogger {
    private static MongoClient mongoClient = null;

    public void saveLogMessage(String messageBody) {
        System.out.println(messageBody);
        MongoClient client = getMongoClient();
        MongoDatabase database = client.getDatabase("jobsdatadb");
        MongoCollection<Document> collection = database.getCollection("jddlog");
        try {
            Document msg = Document.parse(messageBody);
            collection.insertOne(msg);
        } catch (Exception e) {
            System.err.println("Document insertion failed");
            System.err.println(e);
            Document msg = buildErrorDoc(e, messageBody);
            collection.insertOne(msg);
        }
    }

    private Document buildErrorDoc(Exception e, String msg) {
        Document doc = new Document("brokenMessage", msg);
        doc.append("time", new Date());
        doc.append("Exception", e.getClass().getName());
        doc.append("ExceptionMsg", e.getMessage());
        doc.append("ExceptionTrace", e.toString());
        doc.append("origin", "MongoDBLogger");
        doc.append("level", "ERROR");
        return doc;
    }

    private static MongoClient getMongoClient() {
        try {
            if (mongoClient == null) {
                String mongoDbUri = System.getenv("JDD_MONGODBURI");
                mongoClient = MongoClients.create(mongoDbUri);
            }
        } catch (Exception e) {
            System.err.println("Connection establishment failed");
            System.err.println(e);
        }
        return mongoClient;
    }
}
