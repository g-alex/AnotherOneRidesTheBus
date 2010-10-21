/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.alma.aortb.editorinchief;

import fr.alma.aortb.Main;
import fr.alma.aortb.parser.DepecheMode;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 *
 * @author judu
 */
public class EdToChiefListener implements MessageListener {

    public void onMessage(Message msg) {

        if (!(msg instanceof MapMessage)) {
            //TODO: log
            return;
        }
        try {
            MapMessage mmsg = (MapMessage) msg;
            Properties properties = Main.getInstance().getProps();
            Integer id = mmsg.getInt(properties.getProperty("aortb.field.id"));
            if (EditorInChief.getInstance().hasID(id)) {
                EditorInChief.getInstance().removeID(id);
                System.out.println("News " + id + ": " + mmsg.getString(properties.getProperty("aortb.field.content")));
            } else {
                Logger.getAnonymousLogger().log(Level.INFO, "Got unidentified news");
            }
        } catch (JMSException ex) {
            Logger.getLogger(EdToChiefListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
