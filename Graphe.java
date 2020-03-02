/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VoteSMA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import javafx.application.Application;

/**
 *
 * @author claire
 */
public class Graphe {

    private Set<Node> nodes = new HashSet<Node>();
    private Set<Edge> edges = new HashSet<>();
    int numberVertex; // sommet
    int numberBase;// arrete diriger d influence ( mettre les influenceurs dans les noeuds)
    float probaEdge;

    int sumDegree;

    public Graphe(int numberVertex, int numberEdge, float probaEdge, int type) {
        this.numberVertex = numberVertex;
        this.numberBase = numberEdge;
        this.probaEdge = probaEdge;
        createGraph(type);
    }

    public void see() {
        GraphView gv = new GraphView(this);
    }

    public void addNode(Node nodeA) {
        nodes.add(nodeA);
    }

    public void initSumDegree() {
        this.sumDegree = 0;
        for (Node n : nodes) {
            this.sumDegree =  this.sumDegree +n.getInfluNode().size() + n.getfriendNode().size();
        }
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

    private void createNode(int number) {
        for (int i = 0; i < number; i++) {
            addNode(new Node(i));
        }
    }

    public void graphFullConnected() {
        for (Node n : this.nodes) {
            for (Node n2 : this.nodes) {

                if (n != n2 && !edgeExist(n, n2)) {       // *2 because edge i,j and j,i but j in fluemce only by i and two edge exist                      

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

    public boolean edgeExist(Node n, Node n2) {
        Edge testE = new Edge(n.getId(), n2.getId());
        for (Edge e : this.edges) {
            if (e.equals(testE)) {
                return true;
            }
        }
        return false;
    }

    private void createGraph(int type) {
        switch (type) {
            case 1://Erdös-Renyi 
                createNode(this.numberVertex);
                createEdgesUndirectedErdos();
                break;
            case 2://bar
                createNode(this.numberBase);
                graphFullConnected();
                initSumDegree();
                createEdgesUndirectedBar();
                break;
            default:
            // code block
        }
    }

    public String[] madeValue() {
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();

        for (Node n : this.nodes) {
            int degree = n.getInfluNode().size() + n.getfriendNode().size();
            if (map.containsKey(degree)) {
                map.put(degree, map.get(degree) + 1);
            } else {
                map.put(degree, 1);
            }
        }
        int i = 0;
        String[] tab = new String[map.size() * 2];
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry ps = (Map.Entry) it.next();
            tab[i] = String.valueOf(ps.getKey());
            i++;
            tab[i] = String.valueOf(ps.getValue());
            i++;
        }
        return tab;
    }

    public void testGraph() {
        String[] tab = madeValue();
        final DegreeLineChart mainStage = new DegreeLineChart();

        Application.launch(DegreeLineChart.class, tab);

    }

    public void importGraphUndirective() {
        // add node 
        //add edge
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

    private void createEdgesUndirectedBar() {
        Random random = new Random();
        int i = this.numberBase;
        int edge;
        double pn;
        float r;
        while (i < this.numberVertex) {
            Node addN = new Node(i);
            addNode(addN);
            edge = this.numberBase;
            for (Node n : this.nodes) {
                int deg=n.getInfluNode().size() + n.getfriendNode().size();
                pn = deg / this.sumDegree;
                System.out.println(deg+"/" +this.sumDegree+"="+pn);
                r = random.nextFloat();
                if (r <= pn) {
                    addN.addInfluNode(n);
                    addN.addfriendNode(n);
                    n.addInfluNode(addN);
                    n.addfriendNode(addN);
                    addEdge(new Edge(n.getId(), addN.getId()));
                    addEdge(new Edge(addN.getId(), n.getId()));
                    edge--;     
                    initSumDegree();
                     
                }
                
        
                if (edge == 0) {
                    break;
                }
            }
            i++;
        }
    }

    private void createEdgesUndirectedBarold() {
        Random random = new Random();
        ArrayList nodeSetTemp = new ArrayList<Node>();
        for (Node n : this.nodes) {
            int totalSum = 0;
            int diff = 0;
            for (Node n2 : this.nodes) {
                if (n != n2) {
                    diff = this.numberBase - (n2.getInfluNode().size() * 2);
                    if (diff > 0) {
                        nodeSetTemp.add(n2);
                        totalSum = totalSum + diff;
                    }
                }
            }
            while ((n.getInfluNode().size() * 2) <= (this.numberBase - 2) && totalSum > 0) {
                totalSum = proba(n, random, totalSum, nodeSetTemp);

            }
            System.out.println(n.getId() + " : " + n.getInfluNode().size());
            nodeSetTemp.clear();
        }
    }

    public int proba(Node n, Random random, int totalSum, ArrayList nodeSetTemp) {
        // on prend un noeud radom en fn du degre sortant n.getInfluNode().size()*2/this>numberBase          
        int index = random.nextInt(totalSum + 1);
        int sum = 0;
        int i = 0;
        while (sum < index && i < nodeSetTemp.size() - 1) {
            sum = sum + (this.numberBase - (((Node) nodeSetTemp.get(i)).getInfluNode().size() * 2));
            i++;
        }
        Node addN = (Node) nodeSetTemp.get(Math.max(0, i - 1));
        totalSum = totalSum - (this.numberBase - (addN.getInfluNode().size() * 2));
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
