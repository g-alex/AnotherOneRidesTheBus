/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.alma.aortb.newspool;

import fr.alma.aortb.Main;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author judu
 */
public class NewsPool {

   private static final Set<String> warKeywords;

   private static final Set<String> wowKeywords;

   private final Session session;

   private final Context context;

   private final Properties properties;

   private Integer currId;

   static {
      warKeywords = new HashSet<String>();
      try {
         BufferedReader reader = new BufferedReader(new InputStreamReader(Main.class.getClassLoader().getResourceAsStream("war.txt")));
         String str;
         while ((str = reader.readLine()) != null) {
            warKeywords.add(str);
         }
      } catch (IOException ex) {
         Logger.getLogger(NewsPool.class.getName()).log(Level.SEVERE, null, ex);
      }

      wowKeywords = new HashSet<String>();
      try {
         BufferedReader reader = new BufferedReader(new InputStreamReader(Main.class.getClassLoader().getResourceAsStream("wow.txt")));
         String str;
         while ((str = reader.readLine()) != null) {
            wowKeywords.add(str);
         }
      } catch (IOException ex) {
         Logger.getLogger(NewsPool.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   public NewsPool() throws NamingException, JMSException {
      this.properties = Main.props;
      this.context = new InitialContext(this.properties);
      this.currId = 0;
      ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");
      Connection connection = (Connection) factory.createConnection();

      this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      connection.start();

   }

   public void readAndSend(Reader reader) {
      Logger.getLogger(getClass().getCanonicalName()).info("Reading news");


      BufferedReader br = new BufferedReader(reader);

      Map<Integer,String> news = new HashMap<Integer, String>();
      List<Integer> newsIds = new ArrayList<Integer>();


      try {
         String str = null;
         while ((str = br.readLine()) != null) {
            news.put(this.currId, str);
            ++currId;
         }
      } catch (IOException e) {
         return;
      }

      sendToChief(newsIds);
      sendToEditors(news);

   }//MIAOU!!

   private void sendToChief(List<Integer> newsIds) {
      try {
         Destination chiefDest = (Queue) context.lookup(properties.getProperty("aortb.pooltochief"));
         MessageProducer producer = session.createProducer(chiefDest);
         for (Integer id : newsIds) {
            try {
               TextMessage message = session.createTextMessage();
               message.setText(id.toString());
               Logger.getLogger(getClass().getCanonicalName()).log(Level.INFO, "Send ID {0} to chief", id.toString());
               producer.send(message);
            } catch (JMSException ex) {
               Logger.getLogger(NewsPool.class.getName()).log(Level.SEVERE, null, ex);
            }
         }
      } catch (JMSException ex) {
         Logger.getLogger(NewsPool.class.getName()).log(Level.SEVERE, null, ex);
      } catch (NamingException ex) {
         Logger.getLogger(NewsPool.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   private void sendToEditors(Map<Integer,String> news) {


      Boolean isWar = Boolean.FALSE;
      Boolean isWow = Boolean.FALSE;

      try {
         Destination warDestination = (Topic) context.lookup(properties.getProperty("aortb.topic.war"));
         Destination wowDestination = (Topic) context.lookup(properties.getProperty("aortb.topic.wow"));
         MessageProducer warProd = session.createProducer(warDestination);
         MessageProducer wowProd = session.createProducer(wowDestination);


         for (Integer nid : news.keySet()) {
            String aNews = news.get(nid);
            for (String keyword : wowKeywords) {
               if (aNews.contains(keyword)) {
                  isWow = Boolean.TRUE;
                  sendNews(wowProd, nid, aNews);
                  break;
               }
            }
            if (!isWow) {
               for (String keyword : warKeywords) {
                  if (aNews.contains(keyword)) {
                     isWar = Boolean.TRUE;
                     sendNews(warProd, nid, aNews);
                     break;
                  }
               }
            }
         }
      } catch (JMSException ex) {
         Logger.getLogger(NewsPool.class.getName()).log(Level.SEVERE, null, ex);
      } catch (NamingException ex) {
         Logger.getLogger(NewsPool.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   private void sendNews(MessageProducer producer, Integer nid, String aNews) {
      try {
         MapMessage msg = session.createMapMessage();
         msg.setInt(properties.getProperty("aortb.field.id"), nid);
         msg.setString(properties.getProperty("aortb.field.content"), aNews);
         Logger.getLogger(getClass().getCanonicalName()).log(Level.INFO, "Send news with ID {0} to editors", nid.toString());
         producer.send(msg);
      } catch (JMSException ex) {
         Logger.getLogger(NewsPool.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
}
