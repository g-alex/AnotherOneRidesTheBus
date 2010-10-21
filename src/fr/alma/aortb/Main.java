/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.alma.aortb;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

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
      
   }

   public Properties getProps() {
      return props;
   }
}
