/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.alma.aortb.parser;

/**
 *
 * @author indy
 */
public class DepecheMode {

    public static String parseId(String message) {
        return message.split(":")[0];
    }

    public static String parseDepeche(String message) {
        return message.split(":")[1];
    }

    public static String buildMessage(String id, String depeche) {
        return id + ":" + depeche;
    }
}
