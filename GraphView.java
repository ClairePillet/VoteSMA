/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VoteSMA;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import javax.swing.JFrame;

/**
 *
 * @author claire
 */
public class GraphView  extends JFrame
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2707712944901661771L;

	public GraphView(Graphe g)
	{
		
		mxGraph graph = new mxGraph();
		Object parent = graph.getDefaultParent();

		graph.getModel().beginUpdate();
		try
		{
                    for( Node n : g.getNodes()){
                        graph.insertVertex(parent, String.valueOf(n.getId()), String.valueOf(n.getId()),20,20,80,30);
                    }
			Object v1 = graph.insertVertex(parent, null, "Hello", 20, 20, 80,
					30);
			Object v2 = graph.insertVertex(parent, null, "World!", 240, 150,
					80, 30);
			graph.insertEdge(parent, null, "Edge", v1, v2);
		}
		finally
		{
			graph.getModel().endUpdate();
		}

		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		getContentPane().add(graphComponent);
                
                this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(400, 320);
		this.setVisible(true);
	}

	


}
