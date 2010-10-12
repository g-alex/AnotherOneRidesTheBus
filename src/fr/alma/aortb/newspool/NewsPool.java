/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.alma.aortb.newspool;

import java.util.Properties;
import java.util.Set;

/**
 *
 * @author judu
 */
public class NewsPool {
   private final Set<String> topics;
   private final Properties properties;


   public NewsPool(Set<String> topics, Properties props) {
      this.topics = topics;
      this.properties = props;
   }

   public void readAndSend() {
      
   }
}
