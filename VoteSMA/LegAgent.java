/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VoteSMA;

import static VoteSMA.VoteSMA.i;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.domain.JADEAgentManagement.ShutdownPlatform;
import jade.lang.acl.ACLMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.xml.XmlConfiguration;

/**
 *
 * @author claire
 */
public class LegAgent extends Agent {

    volatile HashMap<AID, Opinion> opinionVoter;// opinion des influ
    volatile int nbVoter;
    Opinion o;
    Opinion oP;
    volatile int evaCount = 0;
    volatile int lastChangeCount = 0;
    MajorityVote mvP;
    volatile boolean lastW;
    volatile int switchMaj = 0;
    volatile Graphe g;
    volatile CsvHelper csvGlobale;
    volatile CsvHelper csvDetail;
    Simulaton simulationControle;
  private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger("log1");
    public void setup() {
        
        
        Object[] args = getArguments();
        nbVoter = (int) args[1];
        o = new Opinion((int) args[0]);
        oP = new Opinion((int) args[0]);
        g = (Graphe) args[2];
        mvP = new MajorityVote();
        opinionVoter = new HashMap<>();
        simulationControle = (Simulaton) args[3];
        try {
            csvGlobale = new CsvHelper(",", "Global.csv", false);
            csvDetail = new CsvHelper(",", "Detailed" + VoteSMA.i + ".csv", true);
                  System.out.println(VoteSMA.i);
        } catch (IOException ex) {
              logger.error(ex);
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

        public synchronized void csvWritingGlobale(boolean end) throws IOException {
            System.out.println(end);
            List<String> readFile = csvGlobale.readFile();
            List<Map<String, String>> lst = new ArrayList<>();
            Map<String, String> oneData = new HashMap<>();
            // oneData.put("id", "id");
            oneData.put("end", String.valueOf(end));
            oneData.put("nbSwitch", String.valueOf(switchMaj));
            oneData.put("degMoy", String.valueOf(g.getDegreeMoy()));
            lst.add(oneData);
            if (readFile.isEmpty()) {
                csvGlobale.write(lst, true);
            } else {
                csvGlobale.write(lst, false);
            }

        }

        public synchronized void csvWritingDetailed(MajorityVote mv) throws IOException {
            List<String> readFile = csvDetail.readFile();
            List<Map<String, String>> lst = new ArrayList<>();

            for (int i = 0; i < mv.getDetailedListOpinion().size(); i++) {
                Iterator it = mv.getDetailedListOpinion().get(i).entrySet().iterator();
                Map<String, String> oneData = new HashMap<>();
                while (it.hasNext()) {
                    Map.Entry ps = (Map.Entry) it.next();
                    String key = String.valueOf(ps.getKey());
                    Integer count = (Integer) ps.getValue();
                    oneData.put(key, String.valueOf(count));
                }
                lst.add(oneData);
            }
            if (readFile.isEmpty()) {
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
                     System.out.println("evql");
                    evaluation();
                    opinionVoter.clear();
                }
                msgR = receive();
            }
        }

        public void evaluation() {

            MajorityVote mv = new MajorityVote(opinionVoter, nbVoter, o);
            Opinion oHere = mv.updateOMajority();
            evaCount++;
            if (mvP.getDetailedListOpinion() != null) {
                      System.out.println(mv);
                if ((mv.equals(mvP))) {
                    lastChangeCount++;
                    if (lastChangeCount > 20) {                
                        try {
                            csvWritingGlobale(true);
                        } catch (IOException ex) {
                            logger.error(ex);
                        }
                        SendStop();
                    }
                } else {
                    lastChangeCount = 0;
                    if (!oHere.equals(oP)) {
                        switchMaj++;
                        System.out.println("switCh");
                    }
                }
                if (evaCount > 100) {              
                    System.out.println(evaCount + " " + mv);
                    try {
                        csvWritingGlobale(false);
                    } catch (IOException ex) {
                        logger.error(ex);
                    }
                    SendStop();
                }
            }
            try {
                csvWritingDetailed(mv);
            } catch (IOException ex) {
              logger.error(ex);
            }
            oP.setTabOpinion(oHere.getTabOpinion().clone());
            mvP.setDetailedListOpinion((ArrayList<HashMap<Object, Integer>>)mv.getDetailedListOpinion().clone());
        }

        public synchronized void SendStop() {

            for (Map.Entry ps : opinionVoter.entrySet()) {
                AID key = (AID) ps.getKey();
                sendMsg("END", ACLMessage.INFORM, key);
            }
            Codec codec = new SLCodec();
            Ontology jmo = JADEManagementOntology.getInstance();
            getContentManager().registerLanguage(codec);
            getContentManager().registerOntology(jmo);
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(getAMS());
            msg.setLanguage(codec.getName());
            msg.setOntology(jmo.getName());
            try {
                getContentManager().fillContent(msg, new Action(getAID(), new ShutdownPlatform()));
                send(msg);
            } catch (Exception e) {
                logger.error(e);
            }
            // simulationControle.kill();
        }
    }
}
