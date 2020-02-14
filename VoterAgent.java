/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VoteSMA;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

/**
 *
 * @author guifei
 */
public class VoterAgent extends Agent {
    
    public void setup() {
        Object[] args = getArguments();
     
        addBehaviour(new Routine());
    }

    public class Routine extends CyclicBehaviour {

        public void action() {
            
        }
    }
}
