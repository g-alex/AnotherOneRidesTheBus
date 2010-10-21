/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.alma.aortb.editorinchief;

import fr.alma.aortb.Main;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author judu
 */
public class EditorInChief {

   static private EditorInChief instance;

   private EditorInChief() {
      try {
         Context context = new InitialContext(Main.getInstance().getProps());
         ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");
         Connection connection = factory.createConnection();
         Destination dest = (Destination) context.lookup(Main.getInstance().getProps().getProperty("aortb.pooltochief"));
         Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
         MessageConsumer consumer = session.createConsumer(dest);
         MessageListener edListener = (MessageListener) new EditorInChief();
         consumer.setMessageListener(edListener);

         connection.start();
      } catch (JMSException ex) {
         Logger.getLogger(EditorInChief.class.getName()).log(Level.SEVERE, null, ex);
      } catch (NamingException ex) {
         Logger.getLogger(EditorInChief.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   public EditorInChief getInstance() {
      if (instance == null) {
         instance = new EditorInChief();
      }
      return instance;
   }
}
