import io.javalin.Javalin;
import io.javalin.websocket.WsContext;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        Javalin app = Javalin.create(javalinConfig -> {
            // Modifying the WebSocketServletFactory to set the socket timeout to 120 seconds
            javalinConfig.jetty.modifyWebSocketServletFactory(jettyWebSocketServletFactory ->
                    jettyWebSocketServletFactory.setIdleTimeout(Duration.ofSeconds(120))
            );
        });

        List<WsContext> CurrentConnectedUsers = new ArrayList<>();

        app.ws("/", wsConfig -> {

            wsConfig.onConnect((connectContext) -> {
                System.out.println("Connected: " + connectContext.sessionId());
                CurrentConnectedUsers.add(connectContext);
                System.out.println(CurrentConnectedUsers.size());
            });

            wsConfig.onMessage((messageContext) -> {
                System.out.println("Message: " + messageContext.sessionId());

                Message message = new Message(messageContext);
                if(message.sentToAllUsers()){
                    for (WsContext connectedUser : CurrentConnectedUsers) {
                        connectedUser.send(message.mapMessage());
                    }
                }
            });

            wsConfig.onClose((closeContext) -> {
                System.out.println("Closed: " + closeContext.sessionId());
                CurrentConnectedUsers.remove(closeContext);
                System.out.println(CurrentConnectedUsers.size());
            });

            wsConfig.onError((errorContext) -> {
                System.out.println("Error: " + errorContext.sessionId());
            });

        });

        app.start(5001);
    }

}