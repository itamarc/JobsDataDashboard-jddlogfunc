package itamarc.jddlog;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;

public class SQSLogHandler implements RequestHandler<SQSEvent, Void> {
    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        MongoDBLogger logger = new MongoDBLogger();
        for (SQSMessage msg : event.getRecords()) {
            logger.saveLogMessage(new String(msg.getBody()));
        }
        return null;
    }
}
