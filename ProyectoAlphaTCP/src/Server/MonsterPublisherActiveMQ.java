package Server;
import Model.Monster;
import Model.GameState;
import Model.Player;
import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import Model.GameState;
import Model.Player;
import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class MonsterPublisherActiveMQ {
    private static final String URL = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static final String TOPIC_NAME = "MONSTER_UPDATES";
    private static final String SYSTEM_TOPIC = "GAME_SYSTEM";
<<<<<<< HEAD
    private final GameState gameState; // usa el GameState compartido

    public MonsterPublisherActiveMQ(GameState gameStateCompartido) {
        this.gameState = gameStateCompartido;
    }

    public void startPublishing() {
        ConnectionFactory factory = new ActiveMQConnectionFactory(URL);
=======

    // Persistence: GameState keeps track of players even if they disconnect from JMS
    private final GameState gameState = new GameState();
    private final Monster monster = new Monster();

    public void startPublishing() {
        ConnectionFactory factory = new ActiveMQConnectionFactory(URL);

>>>>>>> 520fbdab6b513d72dedfebe94605c49b3b937dbf
        try (Connection connection = factory.createConnection();
             Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {

            connection.start();
            Destination monsterDest = session.createTopic(TOPIC_NAME);
            Destination systemDest = session.createTopic(SYSTEM_TOPIC);

<<<<<<< HEAD
            try (MessageProducer producer = session.createProducer(null)) {
                publishLoop(session, producer, monsterDest, systemDest);
            }

=======
            // Using a null-destination producer allows sending to multiple topics
            try (MessageProducer producer = session.createProducer(null)) {
                publishLoop(session, producer, monsterDest, systemDest);
            }
>>>>>>> 520fbdab6b513d72dedfebe94605c49b3b937dbf
        } catch (JMSException | InterruptedException e) {
            System.err.println("Communication error: " + e.getMessage());
        }
    }

<<<<<<< HEAD
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
=======
    private void publishLoop(Session session, MessageProducer producer, Destination monsterDest, Destination systemDest) throws JMSException, InterruptedException
    {
        while (true) {

            //Check for Winner (GameState logic)
            Player winner = gameState.checkWinner();
            if (winner != null) {
                // Notify all players who won
                sendMessage(session, producer, systemDest, "WINNER:" + winner.getName());
                System.out.println("Game Over! Winner: " + winner.getName());

                // 3. Automatic Restart logic
                Thread.sleep(5000); // 5-second pause so players see the result
                gameState.resetScores();
                sendMessage(session, producer, systemDest, "GAME_START");
                System.out.println("Game restarted automatically.");
            }

            //Publish Monster Position
            int pos = monster.generarNuevaPosicion();
            sendMessage(session, producer, monsterDest, String.valueOf(pos));
            System.out.println("Published monster position: " + pos);


            // Game tick rate
            Thread.sleep(1500);
        }
    }

    private void sendMessage(Session session, MessageProducer producer, Destination destination, String text) throws JMSException
    {
        TextMessage message = session.createTextMessage(text);
        producer.send(destination, message);
    }

    public static void main(String[] args) {
        new MonsterPublisherActiveMQ().startPublishing();
    }
>>>>>>> 520fbdab6b513d72dedfebe94605c49b3b937dbf
}