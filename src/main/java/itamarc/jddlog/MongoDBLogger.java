package itamarc.jddlog;

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
        try {
            MongoDatabase database = client.getDatabase("jobsdatadb");
            MongoCollection<Document> collection = database.getCollection("jddlog");
            Document msg = Document.parse(messageBody);
            collection.insertOne(msg);
//            ObjectId id = collection.insertOne(msg).getInsertedId().asObjectId().getValue();
        } catch (Exception e) {
            System.out.println("Document insertion failed");
            System.out.println(e);
        }
    }
   
    private static MongoClient getMongoClient() {
        try {
            if (mongoClient == null) {
                String mongoDbUri = System.getenv("JDD_MONGODBURI");
//                System.out.println(mongoDbUri);
                mongoClient = MongoClients.create(mongoDbUri);
            }
        } catch (Exception e) {
            System.out.println("Connection establishment failed");
            System.out.println(e);
        }
        return mongoClient;
    }
}
