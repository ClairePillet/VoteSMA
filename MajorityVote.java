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
import java.util.Objects;

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

    public MajorityVote() {

    }

    public void setDetailedListOpinion(ArrayList<HashMap<Object, Integer>> DetailedListOpinion) {
        this.DetailedListOpinion = DetailedListOpinion;
    }

    public ArrayList<HashMap<Object, Integer>> getDetailedListOpinion() {
        return DetailedListOpinion;
    }

    synchronized public Opinion updateOMajority() {
        int countTrue, countFalse;
        for (int i = 0; i < o.getTabOpinion().length; i++) {
            countTrue = 0;
            countFalse = 0;
            for (Map.Entry ps : mapOpinion.entrySet()) {
                Opinion op = (Opinion) ps.getValue();
                if (op.getTabOpinion()[i]) {
                    countTrue++;
                } else {
                    countFalse++;
                }
            }
            DetailedOpinionResult = new HashMap<>();
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
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.DetailedOpinionResult);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {

        if (getClass() != obj.getClass()) {
            return false;
        }
        MajorityVote other = (MajorityVote) obj;
        for (int i = 0; i < DetailedListOpinion.size(); i++) {
            Iterator it = DetailedListOpinion.get(i).entrySet().iterator();
            HashMap<Object, Integer> hm = other.DetailedListOpinion.get(i);
            if (hm != null) {
                while (it.hasNext()) {
                    Map.Entry ps = (Map.Entry) it.next();
                    Object key = ps.getKey();
                    Integer count = (Integer) ps.getValue();
                    Integer count2 = hm.get(key);
                 
                    if (!Objects.equals(count, count2)) {
                       
                        return false;
                    }
                }
            }
        }
       
        return true;
    }

    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < DetailedListOpinion.size(); i++) {
            for (Map.Entry ps : DetailedListOpinion.get(i).entrySet()) {
                String key = String.valueOf(ps.getKey());
                Integer count = (Integer) ps.getValue();
                s = s + key + " " + count;
            }
            s = s + "\n";
        }
        return s;
    }
}
