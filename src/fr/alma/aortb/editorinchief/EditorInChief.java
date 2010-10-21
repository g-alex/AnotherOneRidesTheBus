/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.alma.aortb.editorinchief;

import fr.alma.aortb.Main;
import java.util.HashSet;
import java.util.Set;
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
public class EditorInChief implements Runnable {

   static private EditorInChief instance;

   private Session session;

   private Context context;

   private Set<Integer> ids;

   private EditorInChief() {
      try {

         ids = new HashSet<Integer>();

         context = new InitialContext(Main.getInstance().getProps());
         ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");
         Connection connection = factory.createConnection();
         session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
         connection.start();
      } catch (JMSException ex) {
         Logger.getLogger(EditorInChief.class.getName()).log(Level.SEVERE, null, ex);
      } catch (NamingException ex) {
         Logger.getLogger(EditorInChief.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   private void listenToPool() {
      try {
         Destination dest = (Destination) context.lookup(Main.getInstance().getProps().getProperty("aortb.pooltochief"));
         MessageConsumer consumer = session.createConsumer(dest);
         MessageListener listener = new PoolToChiefListener();
         consumer.setMessageListener(listener);
      } catch (JMSException ex) {
         Logger.getLogger(EditorInChief.class.getName()).log(Level.SEVERE, null, ex);
      } catch (NamingException ex) {
         Logger.getLogger(EditorInChief.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   private void listenToEditors() {
      try {
         Destination dest = (Destination) context.lookup(Main.getInstance().getProps().getProperty("aortb.edtochief"));
         MessageConsumer consumer = session.createConsumer(dest);
         MessageListener listener = new EdToChiefListener();
         consumer.setMessageListener(listener);
      } catch (JMSException ex) {
         Logger.getLogger(EditorInChief.class.getName()).log(Level.SEVERE, null, ex);
      } catch (NamingException ex) {
         Logger.getLogger(EditorInChief.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   public static EditorInChief getInstance() {
      if (instance == null) {
         instance = new EditorInChief();
      }
      return instance;
   }

   public void addID(Integer id) {
      this.ids.add(id);
   }

   public Boolean hasID(Integer id) {
      return this.ids.contains(id);
   }

   public void removeID(Integer id) {
      this.ids.remove(id);
   }

   public void run() {
      listenToEditors();
      listenToPool();
   }
}
