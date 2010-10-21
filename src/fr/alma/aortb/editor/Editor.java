/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.alma.aortb.editor;

import fr.alma.aortb.Main;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author indy
 */
public class Editor {

    private final Context context;
    private final Properties properties;
    private final Session session;
    private final Topic topic;

    public Editor(Topic topic) throws NamingException, JMSException {
        this.topic = topic;
        this.properties = Main.props;
        this.context = new InitialContext(this.properties);
        ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");
        Connection connection = (Connection) factory.createConnection();

        this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        connection.start();
    }

    public void readAndSend() {
        try {
            MessageConsumer consumer = session.createConsumer(topic);
            MessageListener listener = new PoolToEditorListener(this);
            consumer.setMessageListener(listener);
        } catch (JMSException ex) {
            Logger.getLogger(Editor.class.getName()).log(Level.SEVERE, null, ex);
        }


    }


    /*package*/ void sendToChief(Message msg) {
        if (!(msg instanceof MapMessage)) {
            return;
        } else {
            MapMessage mmsg = (MapMessage) msg;
            try {
                Destination chiefDest = (Queue) context.lookup(properties.getProperty("aortb.edtochief"));
                MessageProducer prod = session.createProducer(chiefDest);

                prod.send(mmsg);
                log("Send to ChiefEditor", mmsg);
            } catch (JMSException ex) {
                Logger.getLogger(Editor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NamingException ex) {
                Logger.getLogger(Editor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /*package*/ void log(String head, MapMessage msg) {
        try {
            Logger.getLogger("fr.alma.aortb.editor.Editor").log(Level.INFO, "{0} {1} with id {2}", new Object[]{head, msg.getString(properties.getProperty("aortb.field.content")), msg.getInt(properties.getProperty("aortb.field.id"))});
        } catch (JMSException ex) {
            Logger.getLogger(Editor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
