import io.javalin.websocket.WsContext;

import java.util.HashMap;
import java.util.Map;

public class Session {
    Map<String, User> connectedUsers = new HashMap<>();

    Session(){
    };

    public void addUser(User user){
        connectedUsers.put(user.getId(), user);
    }

    public void removeUser(User user){
        connectedUsers.remove(user.getId());
    }

    public int numberOfCurrentConnections(){
        return connectedUsers.size();
    }

    public void sendToAll(Message message){
        connectedUsers.forEach((id, connectedUser) -> connectedUser.receiveMessage(message));
    }

    public void sendToUser(Message message){
        String recipient = message.getRecipientId();
        User user = connectedUsers.get(recipient);
        user.receiveMessage(message);
    }
}
