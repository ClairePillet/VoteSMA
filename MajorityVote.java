/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VoteSMA;

import jade.core.AID;
import java.util.ArrayList;
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
    private HashMap<Object, Integer> DetailedOpinionResult;
    private ArrayList<HashMap<Object, Integer>> DetailedListOpinion;

    public MajorityVote(HashMap<AID, Opinion> mapOpinion, int nbVoter, Opinion oToFill) {
        this.mapOpinion = mapOpinion;
        this.nbVoter = nbVoter;
        DetailedListOpinion = new ArrayList<>();
        o = oToFill;
    }

    public ArrayList<HashMap<Object, Integer>> getDetailedListOpinion() {
        return DetailedListOpinion;
    }

    synchronized public Opinion updateOMajority() {
        int countTrue, countFalse;
        for (int i = 0; i < o.getTabOpinion().length; i++) {
            countTrue = 0;
            countFalse = 0;
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
            DetailedOpinionResult = new HashMap<Object, Integer>();
            DetailedOpinionResult.put(true, countTrue);
            DetailedOpinionResult.put(false, countFalse);
            DetailedListOpinion.add(DetailedOpinionResult);

            if (countTrue > (nbVoter / 2)) {
                 o.getTabOpinion()[i] = true;
            }
            if (countFalse > (nbVoter / 2)) {
                 o.getTabOpinion()[i] = false;
            }
        }
        return o;
    }

    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < DetailedListOpinion.size(); i++) {
            Iterator it = DetailedListOpinion.get(i).entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry ps = (Map.Entry) it.next();
                String key = String.valueOf(ps.getKey());
                Integer count = (Integer) ps.getValue();
                s = s + key + " " + count;
            }
            s = s + "\n";
        }
        return s;
    }
}
