/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VoteSMA;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author claire
 */
public class Opinion implements Serializable {

    int numberOfProposition;
    boolean[] tabOpinion;

    public Opinion(int numberOfProposition) {
        this.numberOfProposition = numberOfProposition;
        randomBools(numberOfProposition);
    }

    void randomBools(int len) {
        Random random = new Random();
        tabOpinion = new boolean[len];
        for (int i = 0; i < len; i++) {
            tabOpinion[i] = random.nextBoolean();
        }
    }

    public Opinion(int numberOfProposition, boolean[] tab) {
        this.numberOfProposition = numberOfProposition;
        tabOpinion = tab;
    }

    public boolean[] getTabOpinion() {
        return tabOpinion;
    }

    public void setTabOpinion(boolean[] tabOpinion) {
        this.tabOpinion = tabOpinion;
    }

    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < numberOfProposition; i++) {
            s = s + " " + i + " " + String.valueOf(this.tabOpinion[0]);
        }
        return s;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Opinion other = (Opinion) obj;
        if (this.tabOpinion.length == other.getTabOpinion().length) {
            for (int i = 0; i < this.tabOpinion.length; i++) {
                if (Boolean.compare(this.tabOpinion[i], other.getTabOpinion()[i]) != 0) {
                    System.out.println(this.tabOpinion[i] + "+" + other.getTabOpinion()[i]);
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

}
