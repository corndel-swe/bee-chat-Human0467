import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.websocket.WsMessageContext;

import java.rmi.server.ExportException;
import java.util.Map;

public class Message {

    private String recipientId;
    private String content;

    Message(WsMessageContext messageContext){
        contextToMessage( messageContext);
    }

    private void contextToMessage(WsMessageContext messageContext) {
        String messageAsString = messageContext.message();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, String> message = objectMapper.readValue(messageAsString, Map.class);
            this.recipientId = message.get("recipientId");
            this.content = message.get("content");
        } catch(Exception e){
            System.out.println("malformed message");
        }
    }

    public Boolean sentToAllUsers(){
        return recipientId.isEmpty();
    }

    public Map<String, String> mapMessage(){
        return Map.of("content", this.content, "recipientId", this.recipientId);
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
