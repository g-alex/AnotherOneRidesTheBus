/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.alma.aortb.newspool;

import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author judu
 */
public class NewsPool {

   private final Session session;
   private final Destination destination;

   public NewsPool(Set<String> topics, Properties props) throws NamingException, JMSException {
      String dest = "";
      Context context = new InitialContext(props);
      ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");
      Connection connection = (Connection) factory.createConnection();
      destination = (Destination) context.lookup(dest);
      this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      connection.start();

   }

   public void readAndSend() {

      //TODO: read part
      String text = "";
      try {
         MessageProducer sender = session.createProducer(destination);
         TextMessage message = session.createTextMessage();
         message.setText(text);
         sender.send(message);
      } catch (JMSException ex) {
         Logger.getLogger(NewsPool.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
}
