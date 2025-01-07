import io.javalin.Javalin;
import io.javalin.websocket.WsContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        Javalin app = Javalin.create(javalinConfig -> {
            // Modifying the WebSocketServletFactory to set the socket timeout to 120 seconds
            javalinConfig.jetty.modifyWebSocketServletFactory(jettyWebSocketServletFactory ->
                    jettyWebSocketServletFactory.setIdleTimeout(Duration.ofSeconds(120))
            );
        });

        Map<String, WsContext> currentConnectedUsers = new HashMap<>();
        Session session = new Session();

        app.ws("/", wsConfig -> {

            wsConfig.onConnect((connectContext) -> {
                User user = new User(connectContext);
                session.addUser(user);
                System.out.println("Connected: " + user.getId());
                System.out.println(session.numberOfCurrentConnections());
            });

            wsConfig.onMessage((messageContext) -> {
                System.out.println("Message: " + messageContext.sessionId());

                Message message = new Message(messageContext);
                if(message.sentToAllUsers()){
                    session.sendToAll(message);
                } else {
                    session.sendToUser(message);
                    String recipient = message.getRecipientId();
                    WsContext user = currentConnectedUsers.get(recipient);
                    user.send(message.mapMessage());
                }
            });

            wsConfig.onClose((closeContext) -> {
                System.out.println("Closed: " + closeContext.sessionId());
                User user = new User(closeContext);
                session.removeUser(user);
                System.out.println(currentConnectedUsers.size());
            });

            wsConfig.onError((errorContext) -> {
                System.out.println("Error: " + errorContext.sessionId());
            });

        });

        app.start(5001);
    }

}