/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VoteSMA;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author claire
 */
public class OpinionMessage {
     private ACLMessage msg;
    private Opinion content;

    public OpinionMessage(ACLMessage msg) {
        try {
            this.msg = msg;
            Object o =msg.getContentObject();          
            content=  Opinion.class.cast(o);
        } catch (UnreadableException ex) {
            Logger.getLogger(OpinionMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ACLMessage getMsg() {
        return msg;
    }

    public void setMsg(ACLMessage msg) {
        this.msg = msg;
    }

    public Opinion getContent() {
        return content;
    }

    public void setContent(Opinion content) {
        this.content = content;
    }
}
