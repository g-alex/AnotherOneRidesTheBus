/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.alma.aortb.newspool;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author judu
 */
public class NewsPool {

   private final String topics;

   private final String properties;

   public NewsPool(String topics, String props) {
      if(topics == null) {
         this.topics = "";
      } else {
         this.topics = topics;
      }
      try {
         String destination = "";
         this.properties = props;
         Context context = new InitialContext(props);
         ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");
         Connection connection = (Connection) factory.createConnection();
         Destination dest = (Destination) context.lookup(destination);
         Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
         MessageConsumer consumer = session.createConsumer(dest);
         connection.start();
      } catch (JMSException ex) {
         Logger.getLogger(NewsPool.class.getName()).log(Level.SEVERE, null, ex);
      } catch (NamingException ex) {
         Logger.getLogger(NewsPool.class.getName()).log(Level.SEVERE, null, ex);
      }


   }

   public void readAndSend() {
   }
}
