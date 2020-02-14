/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VoteSMA;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author claire
 */
public class Graphe {

    private Set<Node> nodes = new HashSet<>();
    private Set<Edge> edges = new HashSet<>();
    int numberVertex; // sommet
    int numberEdge;// arrete diriger d influence ( mettre les influenceurs dans les noeuds)
    float probaEdge;
    public Graphe(int numberVertex, int numberEdge, float probaEdge) {
        this.numberVertex = numberVertex;
        this.numberEdge = numberEdge;
        this.probaEdge = probaEdge;
        createNode();
        createEdges(1);
    }
    

    public void addNode(Node nodeA) {
        nodes.add(nodeA);
    }
    public void addEdge(Edge e) {
        edges.add(e);
    }
    public Node getNode(int p) {
        for (Node n : nodes) {
            if (n.getId()==(p)) {
                return n;
            }
        }
        return null;
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public void setNodes(Set<Node> nodes) {
        this.nodes = nodes;
    }
    
    private void createNode(){
        for (int i=0; i< numberVertex;i++){
            addNode(new Node(i));
        }
    }
     private void createEdges(int type ){
         
         for( Node n : this.nodes){
            for( Node n2 : this.nodes){
                  if(n!=n2){                             
                      createEdge(type, n,n2);
                  }
            }
        }
    }
    
    private void createEdge(int type , Node n , Node n2){
        Random random = new Random();
         float r= random.nextFloat();
        switch(type) {
            case 1://Erdös-Renyi                            
               
                if(r<=this.probaEdge){
                    n2.addInfluNode(n);
                    addEdge(new Edge(n.getId(),n2.getId()));
                }
              break;
            case 2://Erdös-Renyi homophily
                //float h = 1 - Math.abs(pi - pj)/100; proba i et j ?
                if(r<=this.probaEdge){
                    n2.addInfluNode(n);
                    addEdge(new Edge(n.getId(),n2.getId()));
                }
              break;
            default:
              // code block
          }
    }
}