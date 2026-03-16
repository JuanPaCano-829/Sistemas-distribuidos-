package Client;
// Código para la clase MonsterSubscriberActiveMQ
import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class MonsterSubscriberActiveMQ implements MessageListener {

    private static final String URL = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static final String TOPIC_NAME = "MONSTER_UPDATES";
    private static final String SYSTEM_TOPIC = "GAME_SYSTEM";

    public void startListening() {
        ConnectionFactory factory = new ActiveMQConnectionFactory(URL);

        try {
            Connection connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination monsterDest = session.createTopic(TOPIC_NAME);
            MessageConsumer monsterConsumer = session.createConsumer(monsterDest);
            monsterConsumer.setMessageListener(this);

            Destination systemDest = session.createTopic(SYSTEM_TOPIC);
            MessageConsumer systemConsumer = session.createConsumer(systemDest);
            systemConsumer.setMessageListener(this);

            System.out.println("Listening for game updates and system alerts...");

            // Note: We do not close the connection here because the listener needs to remain active.

        } catch (JMSException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String text = textMessage.getText();

                if (text.startsWith("WINNER:") || text.equals("GAME_START")) {
                    System.out.println("\n[SYSTEM ALERT]: " + text);
                    // Trigger GUI reset or show winner dialog
                } else {
                    System.out.println("Monster appeared at position: " + text);
                    // Trigger GUI to draw the monster at parsed Integer.parseInt(text)
                }
            }
        } catch (JMSException e) {
            System.err.println("Error reading message: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new MonsterSubscriberActiveMQ().startListening();
    }
}