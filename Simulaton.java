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
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.application.Application;

/**
 *
 * @author claire
 */
public class Simulaton {

    AgentContainer mc;
    jade.core.Runtime runtime;
    Profile config;
    int i = 0;

    public Simulaton() {

        try {
            createSimu(i);
        } catch (ControllerException ex) {
            Logger.getLogger(Simulaton.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void kill() {
        try {
            i++;
            if (i < VoteSMA.NBRUN) {
                try {
                    VoteSMA.NBBASENODE+=2;
                    VoteSMA.PROBAEDGE+=0.1f;
                    createSimu(i);
                } catch (ControllerException ex) {
                    Logger.getLogger(VoteSMA.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                /*String[] degreeVal = g.madeValue();
                int size = (degreeVal.length + 2);*/
                String[] parametter = new String[3];
                parametter[0] = "Global.csv";
                parametter[1] = "Detailed";
                parametter[2] =String.valueOf(VoteSMA.NBRUN);
                /*parametter = Stream.concat(Arrays.stream(parametter), Arrays.stream(degreeVal))
                        .toArray(String[]::new);*/
                Application.launch(Chart.class, parametter);
                
            }

        } catch (Exception ex) {
            Logger.getLogger(VoteSMA.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createSimu(int count) throws ControllerException {
        runtime = jade.core.Runtime.instance();
        config = new ProfileImpl("localhost", 8888, null);
        config.setParameter("gui", "false");
        mc = runtime.createMainContainer(config);
        AgentController acA;
        Graphe g = new Graphe(NBVOTER, NBBASENODE, PROBAEDGE, GRAPHETYPE);

        try {
            int nb = 0;
            while (nb < NBVOTER) {
                Object[] params = {nb, g, NUMBEROPINION, DIFFUSIONTYPE};
                acA = mc.createNewAgent(String.valueOf(nb), VoterAgent.class.getName(), params);
                acA.start();
                nb++;
            }
            Object[] param = {NUMBEROPINION, NBVOTER, g, this};
            acA = mc.createNewAgent("Legislateur", LegAgent.class.getName(), param);
            acA.start();

            kill();
        } catch (StaleProxyException ignored) {
            System.out.println(ignored);
        }
    }
}
