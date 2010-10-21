/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.alma.aortb.editorinchief;

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
      try {
         if (!(msg instanceof TextMessage)) {
            //TODO: log
            return;
         }
         TextMessage tmsg = (TextMessage) msg;
         System.out.println(tmsg.getText());
      } catch (JMSException ex) {
         Logger.getLogger(EdToChiefListener.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

}
