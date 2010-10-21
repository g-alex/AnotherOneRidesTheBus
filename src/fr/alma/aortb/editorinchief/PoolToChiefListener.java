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
public class PoolToChiefListener implements MessageListener {

   public void onMessage(Message msg) {
      if (!(msg instanceof TextMessage)) {
         //TODO: log
         return;
      }
      
      try {

         TextMessage tmsg = (TextMessage) msg;
         Logger.getLogger(getClass().getCanonicalName()).log(Level.INFO, "got message from pool : {0}", tmsg.getText());
         Integer id = new Integer(Integer.parseInt(DepecheMode.parseId(tmsg.getText())));

         EditorInChief.getInstance().addID(id);
      } catch (JMSException ex) {
         Logger.getLogger(EdToChiefListener.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
}
