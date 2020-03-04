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
import jade.wrapper.ControllerException;
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
    static int NBVOTER = 169;
    static int NUMBEROPINION = 1;

    static int NBBASENODE = 50;
    static int DIFFUSIONTYPE = 2;
    static float PROBAEDGE = 0.8f;
    static int GRAPHETYPE = 2;

    public static void main(String[] args) throws ControllerException {
        int i=0;
        while(i<1){
            createSimu(i);
            i++;
        }
    }

    public static void createSimu(int count) throws ControllerException {
        Runtime runtime = Runtime.instance();
        Profile config = new ProfileImpl("localhost", 8888, null);
        config.setParameter("gui", "false");
        AgentContainer mc = runtime.createMainContainer(config);
        AgentController acA;
        Graphe g = new Graphe(NBVOTER, NBBASENODE, PROBAEDGE, GRAPHETYPE);
        g.testGraph();
      //  g.see();
        try {
            int i = 0;
            while (i < NBVOTER) {
                Object[] params = {i, g, NUMBEROPINION, DIFFUSIONTYPE};
                acA = mc.createNewAgent(String.valueOf(i), VoterAgent.class.getName(), params);
                acA.start();
                i++;
            }
            Object[] param = {NUMBEROPINION, NBVOTER,g};
            acA = mc.createNewAgent("Legislateur", LegAgent.class.getName(), param);
            acA.start();
           
        } catch (StaleProxyException ignored) {
            System.out.println(ignored);
        }
    }

}
