/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.alma.aortb.editor;

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
        ed.log("Receive from NewsPool", (MapMessage) msg);
        MapMessage mmsg = (MapMessage) msg;
        ed.sendToChief(mmsg);
    }
}
