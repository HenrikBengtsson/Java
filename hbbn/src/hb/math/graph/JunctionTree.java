package hb.math.graph;

import java.awt.Point;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import hb.format.Format;
import hb.format.Parameters;
import hb.text.TextArea;

/**
 Defines a graph <I><b>G</b>=<b>G</b>(<b>V</b>,<b>E</b>)<\I>, where 
 <I><b>V</b></I> is a set of <I>vertices</I> and <I><b>E</b></I> is
 a set of <I>edges</I>. The graph is represented using adjacency lists.

 @see Vertex
 @see Edge
 */
public class JunctionTree extends Graph implements Set {
  Map vertexClusterMap = new HashMap();

  public JunctionTree() {
	}

  public JunctionTree(Cluster c) {
    add(c);
	}

  public JunctionTree(Collection c) {
    addAll(c);
	}

  public JunctionTree(Graph g) {
    super(g);
	}

	public Object clone() {
    return new JunctionTree(this);
  }
 
	public void clear() {
    super.clear();
    vertexClusterMap.clear();
  }
 
  public Cluster getFamilyCluster(Vertex u) {
    return (Cluster)vertexClusterMap.get(u);
 	}

  public void mapVertex(Vertex u, Cluster c) {
    vertexClusterMap.put(u,c);
 	}

	public static JunctionTree asJunctionTree(Graph G) {
    if (G.size() == 0)
      return new JunctionTree();

			//    Format.printf("G=\n%s\n", p.add(G.toTextArea()));
    Graph Gtri = (Graph)G.clone();
		//    Gtri.rescale(1.0/2,1.0/2);
		//    Format.printf("asJunctionTree: %s\n", p.add(G));
		Collection newEdges = Gtri.moralize();
		//    Format.printf("asJunctionTree: %s\n", p.add(G));
		//    Format.printf("\nGtri=\n%s\n", p.add(Gtri));
		//    Format.printf("Edges added:\n"+newEdges);
    Collection cliques = Gtri.triangulate();
		//    Format.printf("cliques=%s\n", p.add(cliques));

    List sepsets = new ArrayList();
    List list = new ArrayList(cliques);
    // For each pair of clusters (X,Y), create a sepset Sxy.
    for(int i=0; i<list.size()-1; i++) {
      Cluster ci = (Cluster)list.get(i);
      for(int j=i+1; j<list.size(); j++) {
        Cluster cj = (Cluster)list.get(j);
        Sepset Sxy = new Sepset(ci,cj);
				//        if (Sxy.size() > 0) {
          sepsets.add(Sxy);
					//          Format.printf("Created sepset %s\n", new Parameters(Sxy));
      }
    }

    // Sort sepsets in or largest mass and smallest cost.
	  Collections.sort(sepsets);

		//    Format.printf("cliques=%s\nsepsets=%s\n", new Parameters(cliques).add(sepsets));
   
    // Begin with a set of n trees, each consisting of a single clique.
    Collection Ts = new ArrayList();
    Map map = new HashMap();
    Iterator itC = cliques.iterator();
    while (itC.hasNext()) {
      Cluster c = (Cluster)itC.next();
      Graph T = new Graph();
      T.add(c);
      Ts.add(T);
      map.put(c,T);
    }

    int n = cliques.size()-1;
    // Repeat until n-1 sepsets have been inserted into the forest...
		//    Format.printf("\ncliques (#%d)=%s\n", p.add(cliques.size()).add(cliques));
		//    Format.printf("sepsets (#%d)=%s\n", p.add(sepsets.size()).add(sepsets));
    Iterator itS = sepsets.iterator();
    while (itS.hasNext() & n > 0) {
      Sepset Sxy = (Sepset)itS.next();
      Cluster X = (Cluster)Sxy.u;
      Cluster Y = (Cluster)Sxy.v;
			//      Format.printf("Sepset %s...", new Parameters(Sxy));
      // insert Sxy between X and Y *only if* X and Y are on different
      // trees on the forest (otherwise a cycle would form).
      Graph TX = (Graph)map.get(X);
      Graph TY = (Graph)map.get(Y);
      if (TX != TY) {
					//        Format.printf("sepset %s used\n", p.add(Sxy));
					//        Format.printf("TX=%s + TY=%s => ", p.add(TX.getVertices()).add(TY.getVertices()));
        TX.add(TY);
				//        Format.printf("TX=%s ", p.add(TX.getVertices()));
        TX.addEdge(Sxy);
        Ts.remove(TY);
        Iterator it = TY.iterator();
        while (it.hasNext()) {
          Cluster c = (Cluster)it.next();
          map.put(c,TX);
				}
        n--;
      }
			//      Format.printf("\n");
      itS.remove();
		}

    // Get the only tree left in forest...
    Iterator itT = Ts.iterator();
		//    Format.printf("Ts.size = %d\n", p.add(Ts.size()));
    JunctionTree junctionTree = new JunctionTree((Graph)itT.next());

    // Map each vertex in the original DAG into one cluster in the
    // junction tree. 
    Map vertexClusterMap = new HashMap();
    Iterator itV = G.iterator();
    while (itV.hasNext()) {
      Vertex v = (Vertex)itV.next();
      Cluster Fv = new Cluster(G.getFamily(v));
			//      Format.printf("F(%s)=%s\n", new Parameters(v).add(Fv));
      Iterator itJ = junctionTree.iterator();
      while (itJ.hasNext()) {
        Cluster c = (Cluster)itJ.next();
        boolean res = c.containsAll(Fv);
				//        Format.printf("%s < %s = %l\n", new Parameters(Fv).add(c).add(res));
        if (res) {
          vertexClusterMap.put(v, c);
          break;
        }
      }
    }
		//    Format.printf("mapping : %s\n", new Parameters(vertexClusterMap));
		//	  Format.printf("Induces clusters: %s\n", new Parameters(cliques));

    junctionTree.vertexClusterMap = vertexClusterMap;
		//	  Format.printf("junctionTree: %s\n", new Parameters(junctionTree));
    junctionTree.name = G.name+".JoinTree";
    return junctionTree;
	}


	public TextArea toTextArea() {
    TextArea tm = new TextArea(100,100);
    Iterator it = edgeIterator();
	  while(it.hasNext()) {
      Edge e = (Edge)it.next();
      Point pu = e.u.getPoint().getLocation();
      Point pv = e.v.getPoint().getLocation();
 			int y0=pu.y; int x0=pu.x; 
 			int y1=pv.y; int x1=pv.x; 
      if (e.u != e.v) {
  			int dy=y1-y0; int dx=x1-x0;
  			//      Format.printf("dx=%d,dy=%d\n", new Parameters(dx).add(dy));
  			char ch = '-'; char arrow = '+';
  			if (dy == 0) {
  				ch = '-';
          arrow = (dx > 0 ? '>':'<');
  			} else if (dx == 0) {
  				ch = '|';
          arrow = (dy > 0 ? 'v':'^');
  			} else if (((double)dy/dx) >=  0.1)
  				ch = '\\';
  			else if (((double)dy/dx) <= -0.1)
  				ch = '/';
  			int pos[][] = tm.drawLine(y0,x0, y1,x1, ch, false);
  			if (e.directed) {
  				int lastpos[] = pos[pos.length-1];
  				if (lastpos[0]==pv.y && lastpos[1]==pv.x) {
            if (dy != 0)
  					  lastpos = pos[pos.length-2];
            else
  					  lastpos = pos[pos.length-3];
          }
  				tm.put(lastpos[0],lastpos[1], arrow);
  			}
        int yc = (y1+y0)/2; int xc = (x1+x0)/2;
 				tm.put(yc,xc, "["+e.name+"]");
  		} else {
 				tm.put(y0-1,x0+1, "/\\");
 				tm.put(y0  ,x0+1, "  |");
 				tm.put(y0+1,x0+1,"\\/");
			}
    }

    it = getVertices().iterator();
	  while(it.hasNext()) {
      Vertex v = (Vertex)it.next();
      Point pv = v.getPoint().getLocation();
      if (v.color == Vertex.WHITE)
        tm.put(pv.y,pv.x-1, "("+v.toString()+")");
      else if (v.color == Vertex.GRAY)
        tm.put(pv.y,pv.x-1, "{"+v.toString()+"}");
      else if (v.color == Vertex.BLACK)
        tm.put(pv.y,pv.x-1, "["+v.toString()+"]");
    }

    return tm.copyMinimized();
  }
} // class JunctionTree


/*
HISTORY

2000-07-07
* Added support for java.awt.Point-location.
990414
* asJunctionTree() works after the major change of the data structure Graph.
990409
* Created.
	
*/
