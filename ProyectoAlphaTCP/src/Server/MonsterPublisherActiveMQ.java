package Server;

import Model.GameState;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.MessageProducer;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class MonsterPublisherActiveMQ {

    private static final String URL = ActiveMQConnection.DEFAULT_BROKER_URL; // URL del broker
    public static final String MONSTER_TOPIC = "MONSTER_UPDATES";
    public static final String SYSTEM_TOPIC = "GAME_SYSTEM";

    private final GameState gameState; // referencia al estado compartido del juego

    public MonsterPublisherActiveMQ(GameState gameState) {
        this.gameState = gameState; // guarda el estado compartido
    }

    public void startPublishing() {
        ConnectionFactory factory = new ActiveMQConnectionFactory(URL); // crea la fábrica JMS

        try (
                Connection connection = factory.createConnection(); // crea la conexión
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE) // crea una sesión simple
        ) {
            connection.start(); // inicia la conexión con el broker

            Destination monsterDestination = session.createTopic(MONSTER_TOPIC); // crea el topic del monstruo
            Destination systemDestination = session.createTopic(SYSTEM_TOPIC); // crea el topic del sistema

            try (MessageProducer producer = session.createProducer(null)) {
                publishLoop(session, producer, monsterDestination, systemDestination); // entra al ciclo principal
            }

        } catch (JMSException | InterruptedException e) {
            System.err.println("Publisher error: " + e.getMessage()); // muestra error de comunicación
        }
    }

    private void publishLoop(Session session, MessageProducer producer, Destination monsterDestination, Destination systemDestination)
            throws JMSException, InterruptedException {

        while (true) {
            sendMessage(session, producer, systemDestination, "PLAYERS:" + gameState.getConnectedPlayersMessage()); // publica continuamente la lista actual de conectados

            if (!gameState.isGameStarted()) {
                Thread.sleep(1000); // si la partida no ha iniciado, solo espera en el lobby
                continue; // vuelve al inicio del ciclo sin publicar monstruos
            }

            if (!gameState.hasWinner()) {
                sendMessage(session, producer, systemDestination, "GAME_START"); // avisa a todos que la partida comenzó
                int position = gameState.startNewRound(); // inicia una nueva ronda
                sendMessage(session, producer, monsterDestination, String.valueOf(position)); // publica la posición del monstruo
                System.out.println("Published monster position: " + position); // imprime la posición publicada
            }

            Thread.sleep(800); // espera antes de revisar la siguiente ronda

            if (gameState.hasWinner()) {
                String winner = gameState.getWinner();
                sendMessage(session, producer, systemDestination, "WINNER:" + winner);
                System.out.println("Winner published: " + winner);

                Thread.sleep(5000);
                gameState.resetMatch();
                sendMessage(session, producer, systemDestination, "PLAYERS:" + gameState.getConnectedPlayersMessage());
                System.out.println("Game automatically restarted.");
            }
        }
    }

    private void sendMessage(Session session, MessageProducer producer, Destination destination, String text) throws JMSException {
        TextMessage message = session.createTextMessage(text);
        producer.send(destination, message); // envía el mensaje al topic indicado
    }
}