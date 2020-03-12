/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VoteSMA;

import static VoteSMA.VoteSMA.DIFFUSIONTYPE;
import static VoteSMA.VoteSMA.GRAPHETYPE;
import static VoteSMA.VoteSMA.NBBASENODE;
import static VoteSMA.VoteSMA.NBVOTER;
import static VoteSMA.VoteSMA.NUMBEROPINION;
import static VoteSMA.VoteSMA.PROBAEDGE;
import com.sun.tools.javac.util.ArrayUtils;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import jade.wrapper.State;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.application.Application;

/**
 *
 * @author claire
 */
public class Simulaton implements Runnable {

    volatile AgentContainer mc;
    volatile jade.core.Runtime runtime;
    volatile Profile config;
    int i = 0;

    public Simulaton() {

        try {
            createSimu(i);
        } catch (ControllerException ex) {
            Logger.getLogger(Simulaton.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public synchronized void kill() {
        if (i < VoteSMA.NBRUN) {
            try {
                if (i != 0 && i % 10 == 0) {
                    VoteSMA.NBBASENODE += 2;
                    VoteSMA.PROBAEDGE += 0.1f;
                }
              
                Thread.sleep(6000);
                  i++;
                createSimu(i);
            } catch (ControllerException | InterruptedException ex) {
                System.out.println(ex.fillInStackTrace());
            }
        } else {
            String[] parametter = new String[3];
            parametter[0] = "Global.csv";
            parametter[1] = "Detailed";
            parametter[2] = String.valueOf(VoteSMA.NBRUN);
            Application.launch(Chart.class, parametter);
        }
    }

    public synchronized void createSimu(int count) throws ControllerException {
        runtime = jade.core.Runtime.instance();
        config = new ProfileImpl("localhost", 8888, null);
        config.setParameter("gui", "false");
        //  config.setParameter("jade_core_messaging_MessageManager_poolsize", "100000");
        // config.setParameter("jade_core_messaging_MessageManager_maxqueuesize", "10000000000");

        mc = runtime.createMainContainer(config);
        AgentController acA;
        Graphe g = new Graphe(NBVOTER, NBBASENODE, PROBAEDGE, GRAPHETYPE);

        int nb = 0;
        while (nb < NBVOTER) {
            Object[] params = {nb, g, NUMBEROPINION, DIFFUSIONTYPE};
            acA = mc.createNewAgent(String.valueOf(nb), VoterAgent.class.getName(), params);
            acA.start();
            nb++;
        }
        System.out.println(this.i);
        Object[] param = {NUMBEROPINION, NBVOTER, g, this};
        acA = mc.createNewAgent("Legislateur", LegAgent.class.getName(), param);
        acA.start();
        runtime.invokeOnTermination(this);

        // Process p1 =Process.clas
        //    p1.waitFor()
        kill();

    }

    @Override
    public void run() {
        kill();
    }
}
