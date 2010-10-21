/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.alma.aortb.newspool;

import fr.alma.aortb.Main;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
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
         BufferedReader reader = new BufferedReader(new FileReader("war.txt"));
         String str;
         while ((str = reader.readLine()) != null) {
            warKeywords.add(str);
         }
      } catch (IOException ex) {
         Logger.getLogger(NewsPool.class.getName()).log(Level.SEVERE, null, ex);
      }

      wowKeywords = new HashSet<String>();
      try {
         BufferedReader reader = new BufferedReader(new FileReader("wow.txt"));
         String str;
         while ((str = reader.readLine()) != null) {
            wowKeywords.add(str);
         }
      } catch (IOException ex) {
         Logger.getLogger(NewsPool.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   public NewsPool() throws NamingException, JMSException {
      this.properties = Main.getInstance().getProps();
      this.context = new InitialContext(this.properties);
      this.currId = 0;
      ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");
      Connection connection = (Connection) factory.createConnection();

      this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      connection.start();

   }

   public void readAndSend(Reader reader) {
      BufferedReader br = new BufferedReader(reader);

      List<String> news = new ArrayList<String>();
      List<Integer> newsIds = new ArrayList<Integer>();


      try {
         String str = null;
         while ((str = br.readLine()) != null) {
            news.add(this.currId + ":" + str);
            newsIds.add(currId);
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

   private void sendToEditors(List<String> news) {


      Boolean isWar = Boolean.FALSE;
      Boolean isWow = Boolean.FALSE;

      try {
         Destination warDestination = (Topic) context.lookup(properties.getProperty("aortb.topic.war"));
         Destination wowDestination = (Topic) context.lookup(properties.getProperty("aortb.topic.wow"));
         MessageProducer warProd = session.createProducer(warDestination);
         MessageProducer wowProd = session.createProducer(wowDestination);


         for (String aNews : news) {
            for (String keyword : wowKeywords) {
               if (aNews.contains(keyword)) {
                  isWow = Boolean.TRUE;
                  sendNews(wowProd, aNews);
                  break;
               }
            }
            if (!isWow) {
               for (String keyword : warKeywords) {
                  if (aNews.contains(keyword)) {
                     isWar = Boolean.TRUE;
                     sendNews(warProd, aNews);
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

   private void sendNews(MessageProducer wowProd, String aNews) {
      try {
         TextMessage msg = session.createTextMessage();
         msg.setText(aNews);
         wowProd.send(msg);
      } catch (JMSException ex) {
         Logger.getLogger(NewsPool.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
}
