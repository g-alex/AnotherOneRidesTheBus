/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.alma.aortb.editor;

import fr.alma.aortb.Main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
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
        this.properties = Main.getInstance().getProps();
        this.context = new InitialContext(this.properties);
        ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");
        Connection connection = (Connection) factory.createConnection();

        this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        connection.start();
    }

    public void readAndSend(Reader reader) {
        BufferedReader br = new BufferedReader(reader);
        List<String> news = new ArrayList<String>();
        try {
            String str = null;
            while ((str = br.readLine()) != null) {
                news.add(str);
            }
        } catch (IOException ex) {
            Logger.getLogger(Editor.class.getName()).log(Level.SEVERE, null, ex);
        }
        sendToChief(news);
    }

    private void sendToChief(List<String> news) {
        try {
            Destination chiefDest = (Queue) context.lookup(properties.getProperty("aortb.edtochief"));
            MessageProducer prod = session.createProducer(chiefDest);
            for(String str:news){
               TextMessage message = session.createTextMessage();
               message.setText(str);
               prod.send(message);
            }
        } catch (JMSException ex) {
            Logger.getLogger(Editor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(Editor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
