/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.alma.aortb.editor;

import fr.alma.aortb.Main;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author indy
 */
public class EditorManager {

    private static List<Editor> edPool;
    private static EditorManager instance;
    private static Session session;
    private static Context context;
    private static Properties properties;

    public static EditorManager getInstance() {
        if (instance == null) {
            try {
                instance = new EditorManager();
            } catch (JMSException ex) {
                Logger.getLogger(EditorManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NamingException ex) {
                Logger.getLogger(EditorManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return instance;
    }

    private EditorManager() throws NamingException, JMSException {
        edPool = new ArrayList<Editor>();

        properties = Main.getInstance().getProps();
        context = new InitialContext(properties);
        ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");
        Connection connection = (Connection) factory.createConnection();

        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        connection.start();
    }

    public static void pushEditor(String topic) {
        try {
            edPool.add(new Editor((Topic) context.lookup(topic)));
        } catch (JMSException ex) {
            Logger.getLogger(EditorManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(EditorManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
