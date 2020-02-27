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
    int evaCount = 0;
    int lastChangeCount = 0;
    Opinion oP;

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
            System.out.println(getLocalName() + " send a msg" + reciver + " " + Content);
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
            oP = o;
            MajorityVote mv = new MajorityVote(opinionVoter, nbVoter, o);
            o = mv.updateOMajority();
            System.out.println(mv);
            evaCount++;
            if ((oP == o)) {
                lastChangeCount++;
                if (lastChangeCount > 4) {
                    SendStop();
                }
            } else {
                lastChangeCount = 0;
            }

         
            if (evaCount > 100) {
                SendStop();
            }
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
