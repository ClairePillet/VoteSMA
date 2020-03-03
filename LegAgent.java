/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VoteSMA;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JFrame;

/**
 *
 * @author claire
 */
public class LegAgent extends Agent {

    HashMap<AID, Opinion> opinionVoter;// opinion des influ
    int nbVoter;
    Opinion o;
     Opinion oP;
    int evaCount = 0;
    int lastChangeCount = 0;
    MajorityVote mvP;
    boolean term;
    boolean lastW;
    int switchMaj = 0;

    public void setup() {
        Object[] args = getArguments();
        nbVoter = (int) args[1];
        o = new Opinion((int) args[0]);

        opinionVoter = new HashMap<AID, Opinion>();

        addBehaviour(new Routine());
    }

    public class Routine extends CyclicBehaviour {

        public void action() {
            getMessage();
        }

        synchronized void sendMsg(String Content, int Performative, AID reciver) {
            ACLMessage msg = new ACLMessage(Performative);
            msg.setContent(Content);
            msg.addReceiver(reciver);
            myAgent.send(msg);
        }

        synchronized public void getMessage() {
            ACLMessage msgR = receive();
            while (msgR != null) {
                int performative = msgR.getPerformative();
                if (performative == ACLMessage.PROPAGATE) {
                    if (msgR.getContent() != null) {
                        OpinionMessage msgContent = new OpinionMessage(msgR);
                        opinionVoter.put(msgR.getSender(), msgContent.getContent());
                    }
                }
                if (opinionVoter.size() == nbVoter) {
                    evaluation();
                    opinionVoter.clear();
                }
                msgR = receive();
            }
        }

        public void evaluation() {

            MajorityVote mv = new MajorityVote(opinionVoter, nbVoter, o);
            o = mv.updateOMajority();
            evaCount++;
            if (mvP != null) {
                if ((mvP.equals(mv))) {
                    lastChangeCount++;
                    System.out.println(evaCount + " ol " + mv);
                    if (lastChangeCount > 40) {                   
                        term = true;
                        SendStop();
                    }
                } else {
                    if(o!=oP){
                        switchMaj++;
                    }                   
                    System.out.println(evaCount + " op " + mv);
                    lastChangeCount = 0;
                }
                if (evaCount > 100) {
                    term = false;
                    System.out.println(evaCount + " ol " + mv);
                    SendStop();
                }
            }
            oP=o;
            mvP = mv;
        }

        public void SendStop() {
            Iterator it = opinionVoter.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry ps = (Map.Entry) it.next();
                AID key = (AID) ps.getKey();
                sendMsg("END", ACLMessage.INFORM, key);

            }
            System.exit(0);
            doDelete();
        }
    }
}
