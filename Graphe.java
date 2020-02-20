/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VoteSMA;

import java.util.ArrayList;
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

    public Graphe(int numberVertex, int numberEdge, float probaEdge, int type) {
        this.numberVertex = numberVertex;
        this.numberEdge = numberEdge;
        this.probaEdge = probaEdge;
        createNode();
        createEdges(type);
    }
    public void see(){
           GraphView gv= new GraphView(this);
    }
    public void addNode(Node nodeA) {
        nodes.add(nodeA);
    }

    public void addEdge(Edge e) {
        edges.add(e);
    }

    public Node getNode(int p) {
        for (Node n : nodes) {
            if (n.getId() == (p)) {
                return n;
            }
        }
        return null;
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    public void setNodes(Set<Node> nodes) {
        this.nodes = nodes;
    }

    private void createNode() {
        for (int i = 0; i < numberVertex; i++) {
            addNode(new Node(i));
        }
    }

    public boolean edgeExist(Node n, Node n2) {
        Edge testE = new Edge(n.getId(), n2.getId());
        for (Edge e : this.edges) {
            if (e.equals(testE)) {
                return true;
            }
        }
        return false;
    }
 private void createEdges(int type) {
       switch(type) {
            case 1://Erdös-Renyi                            
               createEdgesUndirectedErdos();
              break;
            case 2://Erdös-Renyi homophily
                createEdgesUndirectedBar();
              break;
            default:
              // code block
          }
 
 }
    private void createEdgesUndirectedErdos() {
        Random random = new Random();
        for (Node n : this.nodes) {
            for (Node n2 : this.nodes) {
                float r = random.nextFloat();
                if (n != n2 && !edgeExist(n, n2)) {       // *2 because edge i,j and j,i but j in fluemce only by i and two edge exist                      
                    if (r <= this.probaEdge) {
                        n2.addInfluNode(n);
                        n2.addfriendNode(n);
                        n.addInfluNode(n2);
                        n.addfriendNode(n2);
                        addEdge(new Edge(n.getId(), n2.getId()));
                        addEdge(new Edge(n2.getId(), n.getId()));
                    }

                }
            }
        }
    }

    private void createEdgesUndirectedBar( ) {
        Random random = new Random();
        ArrayList nodeSetTemp = new ArrayList<Node>();
        for (Node n : this.nodes) {
            int totalSum = 0;
            int diff=0;
            for (Node n2 : this.nodes) {
                if (n != n2) {
                    diff=this.numberEdge - (n2.getInfluNode().size() * 2);
                    if(diff>0){
                        nodeSetTemp.add(n2);
                        totalSum = totalSum + diff;
                    }                    
                }
            }
            while ((n.getInfluNode().size() * 2) <= (this.numberEdge - 2) ) {
                totalSum=proba(n, random, totalSum, nodeSetTemp);
                
            }
            System.out.println(n.getId()+" : "+n.getInfluNode().size());
            nodeSetTemp.clear();
        }
    }

    public int proba(Node n, Random random, int totalSum, ArrayList nodeSetTemp) {
        // on prend un noeud radom en fn du degre sortant n.getInfluNode().size()*2/this>numberEdge          
        int index = random.nextInt(totalSum+1);
        int sum = 0;
        int i = 0;
        while (sum < index && i < nodeSetTemp.size() - 1) {
            sum = sum + (this.numberEdge - (((Node) nodeSetTemp.get(i)).getInfluNode().size() * 2));
            i++;
        }
        Node addN = (Node) nodeSetTemp.get(Math.max(0, i - 1));
        totalSum=totalSum- (this.numberEdge -(addN.getInfluNode().size() * 2));
        addN.addInfluNode(n);
        addN.addfriendNode(n);
        n.addInfluNode(addN);
        n.addfriendNode(addN);
        addEdge(new Edge(n.getId(), addN.getId()));
        addEdge(new Edge(addN.getId(), n.getId()));
        nodeSetTemp.remove(Math.max(0, i - 1));
        return totalSum;
    }
}
