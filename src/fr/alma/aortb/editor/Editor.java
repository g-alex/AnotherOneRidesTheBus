/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.alma.aortb.editor;

import fr.alma.aortb.Main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Destination;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author indy
 */
public class Editor {
    private final Context context;
    private final Properties properties;

    public Editor() throws NamingException{
        this.properties=Main.getInstance().getProps();
        this.context=new InitialContext(this.properties);
    }

    public void readAndSend(Reader reader) {
        BufferedReader br = new BufferedReader(reader);
        List<String> news = new ArrayList<String>();
        try {
            String str = null;
            while ((str = br.readLine()) != null) {
                news.add(str);
            }
        } catch (IOException ex) {
            Logger.getLogger(Editor.class.getName()).log(Level.SEVERE, null, ex);
        }
        sendToChief(news);
    }

    private void sendToChief(List<String> news) {
        try {
            Destination chiefDest = (Queue) context.lookup(properties.getProperty("aortb.edtochief"));
        } catch (NamingException ex) {
            Logger.getLogger(Editor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
