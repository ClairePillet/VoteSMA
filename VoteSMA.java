/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VoteSMA;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
/**
 *
 * @author guifei
 */
public class VoteSMA {

    /**
     * @param args the command line arguments
     */
     static int NBVOTER=20;
     
     public static void main(String[] args) {
         
        // TODO code application logic here
        Runtime runtime = Runtime.instance();
        Profile config = new ProfileImpl("localhost", 8888, null);
        config.setParameter("gui", "false");
        AgentContainer mc = runtime.createMainContainer(config);
        AgentController acA;
        AgentController acB;
        Graphe g= new Graphe(NBVOTER, NBVOTER, 0.1f);
        GraphView gv= new GraphView(g);
        try {
            Object[] param = {};
            int i = 0;
            while (i < NBVOTER) {
                acA = mc.createNewAgent("A" + i, VoterAgent.class.getName(), param);
                acA.start();
                i++;
            }

        } catch (StaleProxyException ignored) {
            System.err.println(ignored);
        }
    }

    
}
