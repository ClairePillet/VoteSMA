/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VoteSMA;

import jade.core.Profile;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.ControllerException;

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
    static int NBBASENODE = 12;
    static int DIFFUSIONTYPE = 1;
    static float PROBAEDGE = 0.7f;
    static int GRAPHETYPE = 1;
    static AgentContainer mc;
    static Runtime runtime;
    static Profile config;
    static int i = 0;
    static int NBRUN = 10;

    public static void main(String[] args) throws ControllerException {

        Simulaton s = new Simulaton();

    }

}
