/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VoteSMA;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author claire
 */
public class VoterAgent extends Agent {

    int id;
    int timeNoSpeak;
    int DIFFUSIONTYPE;
    Graphe g;
    ArrayList<Integer> friend;//ceux qu il influ
    HashMap<AID, Opinion> influencer;// opinion des influ
    HashMap<AID, Integer> answerMe;// opinion des influ
    int nbInflu;
    Opinion o;
    boolean MyTurn = false;

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(VoterAgent.class);
    public void setup() {
        Object[] args = getArguments();
        id = (int) args[0];
        g = (Graphe) args[1];
        o = new Opinion((int) args[2]);
        DIFFUSIONTYPE = (int) args[3];
        friend = new ArrayList<>();
        Node myNode = g.getNode(id);
        myNode.getfriendNode().forEach((n) -> {
            friend.add(n.getId());
        });
        influencer = new HashMap<>();
        answerMe = new HashMap<>();
        influencer.put(getAID(), o);
        nbInflu = myNode.getInfluNode().size() + 1;//us
        timeNoSpeak = 0;
        addBehaviour(new Routine());
        addBehaviour(new Tick(this, 500));
        if (id == 0) {
            MyTurn = true;
        }
    }

    synchronized public void sendMsgWithContent(Object Content, int Performative, AID[] reciver, Agent myAgent, String strContent) throws IOException {
        ACLMessage msgSend = new ACLMessage(Performative);
        Date d = new Date();
        msgSend.setConversationId(d.getTime() + getLocalName());
        if (strContent.equalsIgnoreCase("")) {
            msgSend.setContentObject((Opinion) Content);
        } else {
            msgSend.setContent(strContent);
        }
        for (AID reciver1 : reciver) {
            msgSend.addReceiver(reciver1);
        }
        myAgent.send(msgSend);
    }

    public class Tick extends TickerBehaviour {

        public Tick(Agent a, long period) {
            super(a, period);
        }

        protected void onTick() {
            try {           
                sendMsgWithContent(o, ACLMessage.PROPAGATE, new AID[]{new AID("Legislateur", AID.ISLOCALNAME)}, myAgent, "");
            } catch (IOException ex) {
                logger.error(ex);
            }
        }

    }

    public class Routine extends CyclicBehaviour {

        public void onStart() {
            try {         
                sendMsgWithContent(o, ACLMessage.PROPAGATE, new AID[]{new AID("Legislateur", AID.ISLOCALNAME)}, myAgent, "");
            } catch (IOException ex) {
                 logger.error(ex);
            }
        }

        synchronized public void action() {
            getMessage();
            try {
                if (friend.size() > 0) {
                    diffusion(DIFFUSIONTYPE);
                }
            } catch (IOException ex) {
                 logger.error(ex);
            }

        }

        synchronized public void diffusion(int type) throws IOException {
            switch (type) {
                case 1://syncro                         
                    diffSync();
                    break;
                case 2:
                    diffNextId();
                    break;
                case 3:
                    //System.out.println("I talk"+id);
                    diffNeedToSpeak();
                    break;
                default:
                    diffNextFriend();
                    break;
            }
        }

        synchronized public void diffNextId() throws IOException {//          
            if (MyTurn == true) {
                AID[] tabAid = new AID[friend.size()];
                for (int i = 0; i < friend.size(); i++) {
                    tabAid[i] = new AID(String.valueOf(i), AID.ISLOCALNAME);
                }
                sendMsgWithContent(o, ACLMessage.PROPAGATE, tabAid, myAgent, "");
                int r = id + 1;
                AID rAid = new AID(String.valueOf(r), AID.ISLOCALNAME);
                sendMsgWithContent(o, ACLMessage.INFORM, new AID[]{rAid}, myAgent, "YourTurn");
                MyTurn = false;
            }
        }

        synchronized public void diffNextFriend() throws IOException {//          
            if (MyTurn == true) {
                AID[] tabAid = new AID[friend.size()];
                for (int i = 0; i < friend.size(); i++) {
                    tabAid[i] = new AID(String.valueOf(i), AID.ISLOCALNAME);
                }
                sendMsgWithContent(o, ACLMessage.PROPAGATE, tabAid, myAgent, "");
                Random random = new Random();
                int r = random.nextInt(friend.size());
                AID rAid = new AID(String.valueOf(r), AID.ISLOCALNAME);

                sendMsgWithContent(o, ACLMessage.INFORM, new AID[]{rAid}, myAgent, "YourTurn");

                MyTurn = false;
            }
        }

        synchronized public void diffSync() throws IOException {//rqndom
            Random random = new Random();
            int r = random.nextInt(friend.size());
            sendMsgWithContent(o, ACLMessage.PROPAGATE, new AID[]{new AID(String.valueOf(friend.get(r)), AID.ISLOCALNAME)}, myAgent, "");
        }

        synchronized public void diffNeedToSpeak() throws IOException {//rqndom
            try {
                if (MyTurn == true) {
                    timeNoSpeak=0;
                    AID[] tabAid = new AID[friend.size()];
                    for (int i = 0; i < friend.size(); i++) {
                        tabAid[i] = new AID(String.valueOf(i), AID.ISLOCALNAME);
                    }
                    sendMsgWithContent(o, ACLMessage.PROPAGATE, tabAid, myAgent, "");

                    
                    tabAid = new AID[g.numberVertex];
                  
                    for (int i=0 ; i<g.numberVertex;i++) {
                         tabAid[i] = new AID(String.valueOf(i), AID.ISLOCALNAME);
                         
                    }
                    sendMsgWithContent(o, ACLMessage.REQUEST, tabAid, myAgent, "WhoTime?");

                    MyTurn = false;
                }
            } catch (Exception e) {
                 logger.error(e);
            }
        }

        synchronized public void updateMemory(OpinionMessage msgR) {
            AID sender = msgR.getMsg().getSender();
            Opinion oSender = msgR.getContent();
            influencer.put(sender, oSender);
        }

        synchronized public void updateOMajority() {
            if (influencer.size() >= (nbInflu / 2)) {
                MajorityVote mv = new MajorityVote(influencer, nbInflu, o);
                String sop = String.valueOf(o);
                o = mv.updateOMajority();
            }
        }

        synchronized public void getMessage() {
            ACLMessage msgR = receive();
            while (msgR != null) {
                int performative = msgR.getPerformative();
                if (performative == ACLMessage.INFORM) {
                    if (msgR.getContent() != null) {
                        if (msgR.getContent().contains("YourTurn")) {
                         
                            MyTurn = true;
                        }
                        if (msgR.getContent().contains("END")) {
                            doDelete();
                        }
                    }
                }
                if (performative == ACLMessage.PROPAGATE) {
                    if (msgR.getContent() != null) {
                        updateMemory(new OpinionMessage(msgR));
                        updateOMajority();
                        timeNoSpeak++;
                    }
                }
                if (performative == ACLMessage.REQUEST) {
            
                    if (msgR.getContent() != null) {                         
                        if (msgR.getContent().contains("WhoTime?")) {
                            try {
                                //send Message with time                                
                                sendMsgWithContent(timeNoSpeak, ACLMessage.PROPOSE, new AID[]{msgR.getSender()}, myAgent, String.valueOf(timeNoSpeak));
                            } catch (IOException ex) {
                                 logger.error(ex);
                            } 
                        }
                    }
                }
                if (performative == ACLMessage.FAILURE) {
                 
                    System.out.println(msgR.getContent());
                }
                if (performative == ACLMessage.PROPOSE) {
                    if (msgR.getContent() != null) {
                        answerMe.put(msgR.getSender(), Integer.parseInt(msgR.getContent()));
                    }
                    if (answerMe.size() == g.numberVertex-1) {
                        chooseNext();
                        answerMe.clear();
                    }
                }
                msgR = receive();
            }
        }

        public void chooseNext() {
            int max = -Integer.MAX_VALUE;
            AID maxAid = null;
            for (Map.Entry ps : answerMe.entrySet()) {
                AID key = (AID) ps.getKey();
                Integer count = (Integer) ps.getValue();
                if (count > max) {
                    max = count;
                    maxAid = key;
                }
            }
          
            try {
                sendMsgWithContent(o, ACLMessage.INFORM, new AID[]{maxAid}, myAgent, "YourTurn");
            } catch (IOException ex) {
                logger.error(ex);
            }
        }
    }
}
