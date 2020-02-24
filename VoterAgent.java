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
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author claire
 */
public class VoterAgent extends Agent {

    int id;
    int DIFFUSIONTYPE;
    Graphe g;
    ArrayList<Integer> friend;//ceux qu il influ
    HashMap<AID, Opinion> influencer;// opinion des influ
    int nbInflu;
    Opinion o;
    boolean MyTurn = false;

    public void setup() {
        Object[] args = getArguments();
        id = (int) args[0];
        g = (Graphe) args[1];
        o = new Opinion((int) args[2]);
        DIFFUSIONTYPE = (int) args[3];
        friend = new ArrayList<>();
        Node myNode = g.getNode(id);
        for (Node n : myNode.getfriendNode()) {
            friend.add(n.getId());
        }
        influencer = new HashMap<AID, Opinion>();
        influencer.put(getAID(), o);
        nbInflu = myNode.getInfluNode().size() + 1;//us
        addBehaviour(new Routine());
        addBehaviour(new Tick(this, 10));
        if (id == 0) {
            MyTurn = true;
        }
    }

    synchronized public void sendMsgWithContent(Opinion Content, int Performative, AID[] reciver, Agent myAgent, String strContent) throws IOException {
        ACLMessage msgSend = new ACLMessage(Performative);
        Date d = new Date();
        msgSend.setConversationId(d.getTime() + getLocalName());
        if (strContent.equalsIgnoreCase("")) {
            msgSend.setContentObject(Content);
        } else {
            msgSend.setContent(strContent);
        }

        for (int i = 0; i < reciver.length; i++) {
            msgSend.addReceiver(reciver[i]);
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
                Logger.getLogger(VoterAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public class Routine extends CyclicBehaviour {

        public void onStart() {
            try {
                sendMsgWithContent(o, ACLMessage.PROPAGATE, new AID[]{new AID("Legislateur", AID.ISLOCALNAME)}, myAgent, "");
            } catch (IOException ex) {
                Logger.getLogger(VoterAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        synchronized public void action() {
            getMessage();
            try {
                if (friend.size() > 0) {
                    diffusion(DIFFUSIONTYPE);
                }
            } catch (IOException ex) {
                Logger.getLogger(VoterAgent.class.getName()).log(Level.SEVERE, null, ex);
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
                try {
                    sendMsgWithContent(o, ACLMessage.INFORM, new AID[]{rAid}, myAgent, "YourTurn");
                } catch (Exception e) {
                    sendMsgWithContent(o, ACLMessage.INFORM, new AID[]{new AID(String.valueOf(0), AID.ISLOCALNAME)}, myAgent, "YourTurn");
                }
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
                try {
                    sendMsgWithContent(o, ACLMessage.INFORM, new AID[]{rAid}, myAgent, "YourTurn");
                } catch (Exception e) {
                    sendMsgWithContent(o, ACLMessage.INFORM, new AID[]{new AID(String.valueOf(0), AID.ISLOCALNAME)}, myAgent, "YourTurn");
                }
                MyTurn = false;
            }
        }

        synchronized public void diffSync() throws IOException {//rqndom
            Random random = new Random();
            int r = random.nextInt(friend.size());
            sendMsgWithContent(o, ACLMessage.PROPAGATE, new AID[]{new AID(String.valueOf(friend.get(r)), AID.ISLOCALNAME)}, myAgent, "");
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
                    }
                }
                msgR = receive();
            }
        }
    }
}
