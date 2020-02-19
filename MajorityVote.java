/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VoteSMA;

import jade.core.AID;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author claire
 */
public class MajorityVote {

    private HashMap<AID, Opinion> mapOpinion;
    private int nbVoter;
    private Opinion o;

    public MajorityVote(HashMap<AID, Opinion> mapOpinion, int nbVoter, Opinion oToFill) {
        this.mapOpinion = mapOpinion;
        this.nbVoter = nbVoter;
        o = oToFill;
    }

    synchronized public Opinion updateOMajority() {
        int countTrue, countFalse;
        for (int i = 0; i < o.getTabOpinion().length; i++) {
            countTrue = 0;
            countFalse = 0;
            boolean oMe = o.getTabOpinion()[i];
            Iterator it = mapOpinion.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry ps = (Map.Entry) it.next();
                Opinion op = (Opinion) ps.getValue();
                if (op.getTabOpinion()[i]) {
                    countTrue++;
                } else {
                    countFalse++;
                }
            }
          
            
            if (countTrue > (nbVoter / 2)) {
                oMe = true;
            }
            if (countFalse > (nbVoter / 2)) {
                oMe = false;
            }
        }
        return o;
    }
}
