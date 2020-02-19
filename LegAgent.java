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

/**
 *
 * @author claire
 */
public class LegAgent extends Agent {

    HashMap<AID, Opinion> opinionVoter;// opinion des influ
    int nbVoter;
    Opinion o;

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

        synchronized public void getMessage() {
            ACLMessage msgR = receive();
            while (msgR != null) {
                int performative = msgR.getPerformative();
                if (performative == ACLMessage.INFORM) {
                    if (msgR.getContent() != null) {
                        OpinionMessage msgContent = new OpinionMessage(msgR);
                        opinionVoter.put(msgR.getSender(), msgContent.getContent());
                    }
                }
                if(opinionVoter.size()==nbVoter){
                    System.out.println(opinionVoter.size());
                    evaluation();
                    opinionVoter.clear();
                }
                msgR = receive();
            }
        }
        public void evaluation(){
              MajorityVote mv= new MajorityVote(opinionVoter, nbVoter, o);
              o=mv.updateOMajority();
              System.out.println("Result : "+o);
        }
    }
}
