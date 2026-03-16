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
    private static final String MONSTER_TOPIC = "MONSTER_UPDATES"; // topic de posiciones del monstruo
    private static final String SYSTEM_TOPIC = "GAME_SYSTEM"; // topic de eventos del sistema

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

            Thread.sleep(1500); // espera antes de revisar la siguiente ronda

            if (gameState.hasWinner()) {
                String winner = gameState.getWinner(); // obtiene al ganador actual
                sendMessage(session, producer, systemDestination, "WINNER:" + winner); // publica el ganador
                System.out.println("Winner published: " + winner); // imprime el ganador

                Thread.sleep(5000); // deja tiempo para que todos vean el resultado
                gameState.resetMatch(); // reinicia la partida y regresa al lobby
                sendMessage(session, producer, systemDestination, "PLAYERS:" + gameState.getConnectedPlayersMessage()); // publica otra vez la lista de jugadores
                System.out.println("Game restarted and returned to lobby."); // imprime el reinicio
            }
        }
    }

    private void sendMessage(Session session, MessageProducer producer, Destination destination, String text) throws JMSException {
        TextMessage message = session.createTextMessage(text); // crea un mensaje de texto
        producer.send(destination, message); // envía el mensaje al topic indicado
    }
}