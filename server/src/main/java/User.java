import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;

public class User {

    private String id;
    private WsContext context;

    User(WsContext context){
        this.id = context.sessionId();
        this.context = context;
    }

    public String getId() {
        return id;
    }

    public WsContext getContext() {
        return context;
    }

    void receiveMessage(Message message){
        context.send(message.mapMessage());
    }
}
