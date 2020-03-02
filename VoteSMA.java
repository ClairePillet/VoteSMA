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
import java.io.IOException;
import javax.swing.JFrame;


/**
 *
 * @author claire
 */
public class VoteSMA {

    /**
     * @param args the command line arguments
     */
    static int NBVOTER = 100;
    static int NUMBEROPINION = 1;
   
    static int NBBASENODE = 45;
    static int DIFFUSIONTYPE = 3;
   static float PROBAEDGE = 0.5f;
    static int GRAPHETYPE = 2;
    public static void main(String[] args)  {

        Runtime runtime = Runtime.instance();
        Profile config = new ProfileImpl("localhost", 8888, null);
        config.setParameter("gui", "false");
        AgentContainer mc = runtime.createMainContainer(config);
        AgentController acA;
        Graphe g = new Graphe(NBVOTER, NBBASENODE, PROBAEDGE, GRAPHETYPE);
        g.testGraph();

        try {
            int i = 0;
            while (i < NBVOTER) {
                Object[] params = {i, g, NUMBEROPINION, DIFFUSIONTYPE};
                acA = mc.createNewAgent(String.valueOf(i), VoterAgent.class.getName(), params);
                acA.start();
                i++;
            }
            Object[] param = {NUMBEROPINION, NBVOTER};
            acA = mc.createNewAgent("Legislateur", LegAgent.class.getName(), param);
            acA.start();
   
        } catch (StaleProxyException ignored) {
            System.err.println(ignored);
        }
    }

}
