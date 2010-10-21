/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.alma.aortb.editorinchief;

import fr.alma.aortb.parser.DepecheMode;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 *
 * @author judu
 */
public class EdToChiefListener implements MessageListener {

   public void onMessage(Message msg) {

      if (!(msg instanceof TextMessage)) {
         //TODO: log
         return;
      }
      try {
         TextMessage tmsg = (TextMessage) msg;
         Integer id = new Integer(Integer.parseInt(DepecheMode.parseId(tmsg.getText())));

         if (EditorInChief.getInstance().hasID(id)) {
            EditorInChief.getInstance().removeID(id);
            System.out.println("News " + id + ": " + DepecheMode.parseDepeche(tmsg.getText()));
         } else {
            Logger.getAnonymousLogger().log(Level.INFO, "Got unidentified news");
         }
      } catch (JMSException ex) {
         Logger.getLogger(EdToChiefListener.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
}
