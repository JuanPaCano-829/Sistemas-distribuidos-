package Client;

import UI.GameWindow;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import jakarta.jms.MessageListener;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import javax.swing.SwingUtilities;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

//Vincula el suscriptor con la interfaz gráfica del juego.
public class MonsterSubscriberActiveMQ implements MessageListener {

    private static final String URL = ActiveMQConnection.DEFAULT_BROKER_URL; // URL del broker
    private String monsterTopic;
    private String systemTopic;

    private final GameWindow window; // referencia a la ventana principal
    private Connection connection; // conexión JMS
    private Session session; // sesión JMS
    private MessageConsumer monsterConsumer; // consumidor de posiciones
    private MessageConsumer systemConsumer; // consumidor de mensajes del sistema
    private boolean started; // evita iniciar dos veces el listener

    public MonsterSubscriberActiveMQ(GameWindow window) {
        this.window = window; // guarda la referencia de la ventana
        this.started = false; // al inicio todavía no escucha
    }

    //Configura la conexión JMS y activa la recepción de mensajes.
    public void startListening(String monsterTopic, String systemTopic) {
        if (started) return;
        this.monsterTopic = monsterTopic;
        this.systemTopic = systemTopic;

        ConnectionFactory factory = new ActiveMQConnectionFactory(URL);
        try {
            connection = factory.createConnection();
            connection.start();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // Usa las variables en lugar de constantes:
            Destination monsterDestination = session.createTopic(this.monsterTopic);
            Destination systemDestination = session.createTopic(this.systemTopic);

            monsterConsumer = session.createConsumer(monsterDestination); // crea consumidor del topic de monstruos
            systemConsumer = session.createConsumer(systemDestination); // crea consumidor del topic del sistema

            monsterConsumer.setMessageListener(this); // asigna este objeto como listener
            systemConsumer.setMessageListener(this); // asigna este objeto como listener

            started = true; // marca el listener como iniciado
            System.out.println("JMS subscriber started."); // mensaje de confirmación

        } catch (JMSException e) {
            System.err.println("JMS subscriber error: " + e.getMessage()); // muestra error de JMS
        }
    }
    //Cierra de forma segura todos los recursos de ActiveMQ.
    public void stopListening() {
        if (!started) return;

        try {
            if (monsterConsumer != null) monsterConsumer.close();
            if (systemConsumer != null) systemConsumer.close();
            if (session != null) session.close();
            if (connection != null) connection.close();

            started = false;
            System.out.println("JMS subscriber stopped.");
        } catch (JMSException e) {
            System.err.println("Error stopping JMS: " + e.getMessage());
        }
    }
    //Procesa mensajes entrantes y actualiza la interfaz de usuario.
    @Override
    public void onMessage(Message message) {
        try {
            if (!(message instanceof TextMessage textMessage)) return; // ignora mensajes que no sean texto

            String text = textMessage.getText(); // obtiene el contenido del mensaje

            if (text.startsWith("WINNER:") || text.equals("GAME_START") || text.startsWith("PLAYERS:")) {
                SwingUtilities.invokeLater(() -> window.handleSystemMessage(text)); // actualiza Swing en el hilo correcto
                return; // termina el procesamiento del mensaje
            }

            int position = Integer.parseInt(text); // convierte la posición recibida a entero
            SwingUtilities.invokeLater(() -> window.handleMonsterPosition(position)); // actualiza la UI con la nueva posición

        } catch (JMSException | NumberFormatException e) {
            System.err.println("JMS message error: " + e.getMessage()); // muestra error al procesar mensaje
        }
    }
}