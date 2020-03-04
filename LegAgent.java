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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    Graphe g;
    CsvHelper csvGlobale;
    CsvHelper csvDetail;

    public void setup() {
        Object[] args = getArguments();
        nbVoter = (int) args[1];
        o = new Opinion((int) args[0]);
        g = (Graphe) args[2];
        opinionVoter = new HashMap<AID, Opinion>();
        try {
            csvGlobale = new CsvHelper(",", "Global.csv",false);
            csvDetail = new CsvHelper(",", "Detailed.csv",true);
        } catch (IOException ex) {
            System.out.println(LegAgent.class.getName() + " " + ex);
        }
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

        public void csvWritingGlobale() throws IOException {
            List<String> readFile = csvGlobale.readFile();
            List<Map<String, String>> lst = new ArrayList<Map<String, String>>();
            Map<String, String> oneData = new HashMap<String, String>();
           // oneData.put("id", "id");
            oneData.put("result", String.valueOf(o));
            oneData.put("nbSwitch", String.valueOf(switchMaj));
            oneData.put("degMoy", String.valueOf(g.getDegreeMoy()));
            lst.add(oneData);
            if (readFile.size() == 0) {
                csvGlobale.write(lst, true);
            } else {
                csvGlobale.write(lst, false);
            }

        }

        public void csvWritingDetailed(MajorityVote mv) throws IOException {
            List<String> readFile = csvDetail.readFile();
            List<Map<String, String>> lst = new ArrayList<Map<String, String>>();

            for (int i = 0; i < mv.getDetailedListOpinion().size(); i++) {
                Iterator it = mv.getDetailedListOpinion().get(i).entrySet().iterator();
                Map<String, String> oneData = new HashMap<String, String>();
                while (it.hasNext()) {
                    Map.Entry ps = (Map.Entry) it.next();
                    String key = String.valueOf(ps.getKey());
                    Integer count = (Integer) ps.getValue();
                    oneData.put(key, String.valueOf(count));
                }
                lst.add(oneData);
            }
            if (readFile.size() == 0) {
                csvDetail.write(lst, true);
            } else {
                csvDetail.write(lst, false);
            }
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
                    if (lastChangeCount > 4) {
                        term = true;
                        try {
                            csvWritingGlobale();
                        } catch (IOException ex) {
                            Logger.getLogger(LegAgent.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        SendStop();
                    }
                } else {
                    if (o != oP) {
                        switchMaj++;
                    }
                    System.out.println(evaCount + " op " + mv);
                    lastChangeCount = 0;
                }
                if (evaCount > 100) {
                    term = false;
                    System.out.println(evaCount + " ol " + mv);

                    try {
                        csvWritingGlobale();
                    } catch (IOException ex) {
                        Logger.getLogger(LegAgent.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    SendStop();
                }
            }
            try {
                csvWritingDetailed(mv);
            } catch (IOException ex) {
                Logger.getLogger(LegAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
            oP = o;
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
