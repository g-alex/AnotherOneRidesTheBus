/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.alma.aortb;

import fr.alma.aortb.editor.Editor;
import fr.alma.aortb.editorinchief.EditorInChief;
import fr.alma.aortb.newspool.NewsPool;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.naming.NamingException;

/**
 *
 * @author judu
 */
public class Main {

   private Properties props;

   private static Main instance;

   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {
      getInstance().go();
   }

   private Main() {
      try {
         props = new Properties();
         props.load(getClass().getClassLoader().getResourceAsStream("jndi.properties"));
      } catch (IOException ex) {
         Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   public static Main getInstance() {
      if (instance == null) {
         instance = new Main();
      }
      return instance;
   }

   public void go() {
      {
         FileReader fr = null;
         try {
            EditorInChief eic = EditorInChief.getInstance();
            eic.run();
            NewsPool np = new NewsPool();
            String path = askForFile();
            fr = new FileReader(path);

            np.readAndSend(fr);

         } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
         } catch (NamingException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
         } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
         } finally {
            try {
               fr.close();
            } catch (IOException ex) {
               Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
         }
      }
   }

   public Properties getProps() {
      return props;
   }

   private String askForFile() {
      try {
         System.out.println("Please give the path of the news file.");
         BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
         return inFromUser.readLine();
      } catch (IOException ex) {
         Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
         return null;
      }
   }
}
