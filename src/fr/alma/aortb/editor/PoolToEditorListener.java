/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.alma.aortb.editor;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 *
 * @author indy
 */
class PoolToEditorListener implements MessageListener {

    private Editor ed;

    PoolToEditorListener(Editor ed) {
        this.ed = ed;
    }

    public void onMessage(Message msg) {
        if (!(msg instanceof MapMessage)) {
            return;
        }
        MapMessage mmsg=(MapMessage) msg;
        Properties properties=ed.getProps();
        try {
            Logger.getLogger("fr.alma.aortb.editor.Editor").log(Level.INFO, "Receive from NewsPool {0} with id {1}", new Object[]{mmsg.getString(properties.getProperty("aortb.field.content")), mmsg.getInt(properties.getProperty("aortb.field.id"))});
        } catch (JMSException ex) {
            Logger.getLogger(PoolToEditorListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        ed.sendToChief(mmsg);
    }
}
