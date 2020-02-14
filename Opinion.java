/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VoteSMA;

import java.util.Random;

/**
 *
 * @author claire
 */
public class Opinion {
    int numberOfProposition;
    boolean[] tabOpinion;

    public Opinion(int numberOfProposition) {
        this.numberOfProposition=numberOfProposition;
        randomBools(numberOfProposition);
    }
    
    void randomBools(int len) {
        Random random = new Random();
        tabOpinion = new boolean[len];
        for(int i = 0; i < len; i++) {
            tabOpinion[i] = random.nextBoolean();
        }
    
    }
    
    public Opinion(int numberOfProposition, boolean[] tab) {
        this.numberOfProposition=numberOfProposition;
        tabOpinion=tab;
    }
}
