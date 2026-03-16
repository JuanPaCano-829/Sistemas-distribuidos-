package Server;

import Model.GameState;
import Model.Player;
import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class MonsterPublisherActiveMQ {
    private static final String URL = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static final String TOPIC_NAME = "MONSTER_UPDATES";
    private static final String SYSTEM_TOPIC = "GAME_SYSTEM";

    private final GameState gameState; // usa el GameState compartido

    public MonsterPublisherActiveMQ(GameState gameStateCompartido) {
        this.gameState = gameStateCompartido;
    }

    public void startPublishing() {
        ConnectionFactory factory = new ActiveMQConnectionFactory(URL);

        try (Connection connection = factory.createConnection();
             Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {

            connection.start();
            Destination monsterDest = session.createTopic(TOPIC_NAME);
            Destination systemDest = session.createTopic(SYSTEM_TOPIC);

            try (MessageProducer producer = session.createProducer(null)) {
                publishLoop(session, producer, monsterDest, systemDest);
            }

        } catch (JMSException | InterruptedException e) {
            System.err.println("Communication error: " + e.getMessage());
        }
    }

    private void publishLoop(Session session, MessageProducer producer, Destination monsterDest, Destination systemDest)
            throws JMSException, InterruptedException {
        sendMessage(session, producer, systemDest, "GAME_START"); // avisa que empezó el juego

        while (true) {

            if (!gameState.hayGanador()) {
                int pos = gameState.iniciarNuevaRonda(); // usa el monstruo del GameState
                sendMessage(session, producer, monsterDest, String.valueOf(pos)); // publica posición del monstruo
                System.out.println("Published monster position: " + pos);
            }

            if (gameState.hayGanador()) {
                String nombreGanador = gameState.obtenerGanador(); // obtiene ganador
                sendMessage(session, producer, systemDest, "WINNER:" + nombreGanador); // publica ganador
                System.out.println("Game Over! Winner: " + nombreGanador);

                Thread.sleep(5000); // pausa para que los jugadores vean el resultado
                gameState.reiniciarPartida(); // reinicia la partida
                sendMessage(session, producer, systemDest, "GAME_START"); // avisa nuevo juego
                System.out.println("Game restarted automatically.");
            }

            Thread.sleep(1500); // tiempo entre apariciones del monstruo
        }
    }

    private void sendMessage(Session session, MessageProducer producer, Destination destination, String text)
            throws JMSException {
        TextMessage message = session.createTextMessage(text);
        producer.send(destination, message);
    }
}