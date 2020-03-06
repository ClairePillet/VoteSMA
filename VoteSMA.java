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
import java.util.logging.Level;
import java.util.logging.Logger;
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

    static int NBBASENODE = 12;
    static int DIFFUSIONTYPE = 2;
    static float PROBAEDGE = 0f;
    static int GRAPHETYPE = 2;
    static AgentContainer mc;
    static Runtime runtime;
    static Profile config;
    static int i = 0;
      static int NBRUN=10;

    public static void main(String[] args) throws ControllerException {

      Simulaton s= new Simulaton();

    }

   

}
