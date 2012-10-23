package hb.math.graph;

import java.awt.Point;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import hb.geometry.RelativePoint;
import hb.format.Format;
import hb.format.Parameters;
import hb.lang.StringVariable;
import hb.math.graph.Dimension;
import hb.text.TextArea;


/**
 Defines a graph <I><b>G</b>=<b>G</b>(<b>V</b>,<b>E</b>)</I>, where 
 <I><b>V</b></I> is a set of <I>vertices</I> and <I><b>E</b></I> is
 a set of <I>edges</I>.

 @see Vertex
 @see Edge
 */
public class Graph implements Set {
  public String name;
  public String description;
  ArrayList vertices = new ArrayList();
  Map vertexEdges = new HashMap();
  boolean containsCycle = false;
  boolean validDFS = false;      // Is DFS results still valid.
  boolean validBFS = false;      // Is BFS results still valid.
  boolean validSCC = false;      // Is SCC results still valid.
  Collection DFTrees = new ArrayList(); // Depth-first trees.
  List verticesFinishOrder = new ArrayList();
	
	// For internal use only
  private Iterator Vs;

  public Graph() {
	}

  public Graph(Collection c) {
    addAll(c);
	}

	public Graph(Graph G, int dummy) {
    vertices = (ArrayList)G.vertices.clone();
	  Iterator it = G.iterator();
		while (it.hasNext()) {
		  Vertex u = (Vertex)it.next();
      Collection uE = G.getEdges(u);
      Collection edges = new ArrayList(uE);
      vertexEdges.put(u, edges);
			//      Format.printf("vertexEdges.put(%s, %s)\n", new Parameters(u).add(edges));
		}
		//    Format.printf("G: %s\n", new Parameters(this));
	}

	public Graph(Graph G) {
    vertices = (ArrayList)G.vertices.clone();
	  Iterator it = G.iterator();
		while (it.hasNext()) {
		  Vertex u = (Vertex)it.next();
      Collection edges = new ArrayList();
      vertexEdges.put(u, edges);
		}
    Collection edges = G.getEdges();
	  it = edges.iterator();
		while (it.hasNext()) {
		  Edge e = (Edge)it.next();
      Edge e2 = (Edge)e.clone();
      e2.u = getVertex(e.u.name);
      e2.v = getVertex(e.v.name);
      addEdge(e2);
 		}
		//    Format.printf("G: %s\n", new Parameters(this));
	}

	public String getName() {
    return name;
	}

	public void setName(String name) {
    this.name = name;
	}

	public Object clone() {
    return new Graph(this);
  }

	public boolean contains(Object o) {
		if (o instanceof Vertex) {
      Vertex v = (Vertex)o;
      return vertices.contains(v);
		} else if (o instanceof Edge) {
      Edge e = (Edge)o;
      return getEdges().contains(e);
		}
    return false;
	}

	public boolean containsAll(Collection c) {
    return vertices.containsAll(c);
	}

	public boolean add(Object o) {
	  if (vertices.contains(o))
		  return true;
    Vertex v = (Vertex)o;
	  if (vertices.add(v)) {
      invalidateAll();
      vertexEdges.put(v, new ArrayList());
		  return true;
    }
    return false;
	}

	public boolean add(Graph g) {
		if (addAll(g.vertices)) {
      vertexEdges.putAll(g.vertexEdges);
      return true;
    }
    return false;
	}

	public boolean addAll(Vertex[] v) {
    boolean ok = true;
    for (int i=0; i<v.length; i++)
      ok &= add(v[i]);
    invalidateAll();
	  return ok;
	}

	public boolean addAll(Collection c) {
    boolean ok = true;
    Iterator it = c.iterator();
    while (it.hasNext()) {
      Vertex v = (Vertex)it.next();
      ok &= add(v);
		}
    invalidateAll();
	  return ok;
	}

	public boolean remove(Object o) {
    Vertex v = (Vertex)o;
	  if (vertices.contains(v)) {
			if (!vertices.remove(v))
			  return false;
      vertexEdges.remove(v);
      Iterator itU = vertices.iterator();
      while (itU.hasNext()) {
        Vertex u = (Vertex)itU.next();
        Collection edges = (Collection)getEdges(u);
        Iterator itE = edges.iterator();
        while (itE.hasNext()) {
          Edge e = (Edge)itE.next();
          if (e.otherVertex(u) == v) {
            itE.remove();
          }
				}
			}
      invalidateAll();
			return true;
		}
		return true;
	}

	public boolean removeAll(Collection c) {
    boolean ok = true;
    Iterator it = c.iterator();
    while (it.hasNext()) {
      Vertex v = (Vertex)it.next();
      ok &= remove(v);
		}
    return ok;
  }

	public boolean retainAll(Collection c) {
    return false;
	}

	public boolean isEmpty() {
    return (size() == 0); 
  }

	public void clear() {
    vertices.clear();
    vertexEdges.clear();
    invalidateAll();
  }

	private void invalidateAll() {
	  validDFS = false;
    validBFS = false;
    validSCC = false;
  }

	public Object[] toArray() {
    return vertices.toArray();
	}

	public Object[] toArray(Object[] a) {
    return vertices.toArray(a);
	}

	public Collection getVertices() {
	  return vertices;
	}

	public int indexOf(Vertex v) {
	  return vertices.indexOf(v);
	}

	public int[] indicesOf(Vertex[] vs) {
    int[] index = new int[vs.length];
    for (int i=0; i<index.length; i++)
			index[i] = vertices.indexOf(vs[i]);
    return index;
	}

	public Collection getChildren(Vertex u) {
	  Collection children = new ArrayList();
    Collection uE = (Collection)vertexEdges.get(u);
    if (uE == null)
      return null;
    Iterator ituE = uE.iterator();
    while (ituE.hasNext()) {
      Edge e = (Edge)ituE.next();
      Vertex v = e.otherVertex(u);
      if (!children.contains(v))
    		children.add(v);
		}
    return children;
  }

	public Collection getParents(Vertex w) {
	  Collection parents = new ArrayList();
	  Iterator Vs = vertices.iterator();
		while (Vs.hasNext()) {
		  Vertex v = (Vertex)Vs.next();
      Collection vE = (Collection)vertexEdges.get(v);
			//      if (v.name.equals("b")) Format.printf("%s.E = %s\n", p.add(v).add(vE));
      if (vE != null) {
  			Iterator Es = vE.iterator();
  			while (Es.hasNext()) {
  				Edge e = (Edge)Es.next();
  				if (e.otherVertex(v) == w) {
  					if (!parents.contains(v))
  						parents.add(v);
  				}
  			}
			}
		}
	  return parents;
	}
	
	public Collection getFamily(String name) {
    Vertex u = getVertex(name);
    return getFamily(u);
  }
    
	/**
   Gets the family of the specified vertex.
   The family of a vertex <i>u</i> is defined as the union between the vertex
   itself and its parents; <i>{u} U pa(u)</i>.
   @return a collection containing the family.
	 */
	public Collection getFamily(Vertex u) {
    if (u == null)
      return null;
    Collection family = new ArrayList();
    Collection parents = getParents(u);
    if (parents != null)
      family.addAll(parents);
    family.add(u);
    return family;
  }

	/**
   Gets the neighbor of the specified vertex.
   The neighbor of a vertex <i>u</i> is defined as the union between the 
   vertex itself, its parents and its children; <i>{u} U pa(u) U ch(u)</i>.
   @return a collection containing the neighbor.
	 */
	public Collection getNeighbors(Vertex u) {
    if (u == null)
      return null;
    Collection neighbors = new HashSet();  // Do not use TreeSet()! 990603
    Collection parents = getParents(u);
		//    Format.printf("pa(u)=%s\n", new Parameters(parents));
    if (parents != null)
      neighbors.addAll(parents);
    Collection children = getChildren(u);
		//    Format.printf("ch(u)=%s\n", new Parameters(children));
    if (children != null)
      neighbors.addAll(children);
		//    Format.printf("ne(u)=%s\n", new Parameters(neighbors));
    return neighbors;
  }

	public Collection getNeighbors(String name) {
    Vertex u = getVertex(name);
    return getNeighbors(u);
  }

	public Edge getEdge(Vertex u, Vertex v) {
    Collection uedges = (Collection)vertexEdges.get(u);
		//      Format.printf("getEdge(%s,%s): %s\n", new Parameters(u).add(v).add(uedges));
    Iterator it = uedges.iterator();
    while(it.hasNext()) {
      Edge e = (Edge)it.next();
      if (e.isConnectedTo(v))
        return e;
    }
    return null;
  }

	public Collection getEdges(Vertex v) {
    return (Collection)vertexEdges.get(v);
  }

	public Collection getEdges(Collection c) {
    Collection allEdges = new ArrayList();
    Iterator it = c.iterator();
		while(it.hasNext()) {
      Vertex v = (Vertex)it.next();
      allEdges.addAll(getEdges(v));
    }
    return allEdges;
	}

	public Collection getEdges() {
	  Collection E = new ArrayList();
	  Iterator Vs = vertices.iterator();
		while (Vs.hasNext()) {
		  Vertex v = (Vertex)Vs.next();
      Collection vE = (Collection)vertexEdges.get(v);
			//      Format.printf("vertex %s.E = %s\n", new Parameters(v).add(vE));
			Iterator itEs = vE.iterator();
			while (itEs.hasNext()) {
			  Edge e = (Edge)itEs.next();
				//        Format.printf("e = %s\n", new Parameters(e));
				if (!E.contains(e))
			    E.add(e);
			}
		}
	  return E;
	}
		
	public Collection getExternalEdges(Collection c) {
    Collection externalEdges = new ArrayList();
    Iterator it = c.iterator();
		while(it.hasNext()) {
      Vertex u = (Vertex)it.next();
      Iterator itE = getEdges(u).iterator();
      while(itE.hasNext()) {
				Edge e = (Edge)itE.next();
        // Is the edge an outgoing edge or an introvert one?
        if ((u == e.u && !c.contains(e.v)) || (u == e.v && !c.contains(e.u)))
			  	if (!externalEdges.contains(e))
            externalEdges.add(e);
      }
    }
    return externalEdges;
	}

	public Collection getInternalEdges(Collection c) {
    Collection internalEdges = new ArrayList();
    Iterator it = c.iterator();
		while(it.hasNext()) {
      Vertex u = (Vertex)it.next();
      Iterator itE = getEdges(u).iterator();
      while(itE.hasNext()) {
				Edge e = (Edge)itE.next();
        if ((u == e.u && c.contains(e.v)) || (u == e.v && c.contains(e.u)))
			  	if (!internalEdges.contains(e))
            internalEdges.add(e);
      }
    }
    return internalEdges;
	}


	public void removeInternalEdges(Collection c) {
    Iterator it = c.iterator();
		while (it.hasNext()) {
      Vertex u = (Vertex)it.next();
      Collection uE = getEdges(u);
      Iterator itE = uE.iterator();
      while (itE.hasNext()) {
				Edge e = (Edge)itE.next();
        if (u == e.u && c.contains(e.v)) {
          itE.remove();
          Collection vE = getEdges(e.v);
          vE.remove(e);
				} else if (u == e.v && c.contains(e.u)) {
          itE.remove();
          Collection vE = getEdges(e.u);
          vE.remove(e);
        }
      }
    }
	}

	public double getWeight(Vertex v) {
    return v.getWeight();
  }

	public double getWeight(Collection c) {
    double weight = 1.0;
    Iterator it = c.iterator();
    while (it.hasNext()) {
      Vertex v = (Vertex)it.next();
      weight *= v.weight;
    }
    return weight;
  }

	private double calculateCost(Collection c) {
    int n = c.size();
    int maxNbrOfEdges = n*(n-1)/2;
    Collection intEdges = getInternalEdges(c);
    int internalNbrOfEdges = intEdges.size();
		//    Format.printf("Internal edges of %s (%d/%d): %s\n", 
		//      p.add(c).add(intEdges.size()).add(maxNbrOfEdges).add(intEdges));
    int nbrOfEdgedAdded = maxNbrOfEdges-internalNbrOfEdges;
    return (double)nbrOfEdgedAdded;
	}

	public Graph transpose() {
    invalidateAll();
    Map newVertexEdges = new HashMap();
	  Collection E = new ArrayList();
	  Iterator Vs = vertices.iterator();
		while (Vs.hasNext()) {
		  Vertex u = (Vertex)Vs.next();
      Collection uE = (Collection)vertexEdges.get(u);
			Iterator ituE = uE.iterator();
			while (ituE.hasNext()) {
			  Edge e = (Edge)ituE.next();
        Vertex v = e.otherVertex(u);
        Collection vE = (Collection)newVertexEdges.get(v);
        if (vE == null) {
          vE = new ArrayList();
          newVertexEdges.put(v, vE);
				}
        ituE.remove();
        vE.add(e);
 	  		e.swapDirection();
      }
		}
    vertexEdges = newVertexEdges;
		return this;
	}
	
	public Iterator iterator() {
	  return vertices.iterator();
	}

	public Iterator edgeIterator() {
	  return getEdges().iterator();
	}

	public int size() {
	  return vertices.size();
	}

	/**
   Checks if the graph contains any cycles. The algorithm used for the test
   is a depth-first-search algorithm, which is performed more than once only
   if the graph is modified between calls.
 
   @return <tt>true</tt> if the graph contains cycles, 
           otherwise <tt>false</tt>.
   */
	public boolean isCyclic() {
		if (!validDFS)
		  DFS();
	  return containsCycle;
	}
		
	/**
   Checks if the graph is directed, i.e. if there is any directed edges.
 
   @return <tt>true</tt> if the graph is directed, otherwise <tt>false</tt>.
   */
	public boolean isDirected() {
    Iterator itV = vertices.iterator();
		while(itV.hasNext()) {
      Vertex v = (Vertex)itV.next();
      Collection vE = (Collection)vertexEdges.get(v);
      Iterator it = vE.iterator();
  		while (it.hasNext()) {
				Edge e = (Edge)it.next();
        if (e.isDirected())
          return true;
      }
		}
    return false;
	}

	/**
   Checks if the graph is a directed acyclic graph (DAG).
 
   @return <tt>true</tt> if the graph is a DAG, otherwise <tt>false</tt>.
   */
	public boolean isDAG() {
    return (isDirected() && !isCyclic());
  }

	/**
   Checks if the graph is a undirected acyclic graph (tree).
 
   @return <tt>true</tt> if the graph is a tree, otherwise <tt>false</tt>.
   */
	public boolean isTree() {
    return (!isDirected() && !isCyclic());
  }

	/**
   Checks if the graph is singly connected. A graph is singly connected if
   you get a tree when you remove all directions on edges.
 
   @return <tt>true</tt> if the graph is singly connected, 
           otherwise <tt>false</tt>.
   */
	public boolean isSinglyConnected() {
    Graph G = (Graph)clone();
    G.undirect();
    return G.isTree();
  }

	/**
   Checks if vertex <i>v</i> is reachable from vertex <i>u</i>. The 
   algorithm used for the test is a breadth-first-search algorithm, 
   which is performed more than once only if the graph is modified 
   between calls.
   
   @return <tt>true</tt> if vertex <i>v</i> is reachable from 
           vertex <i>u</i>, otherwise <tt>false</tt>.
	*/
	public boolean isReachable(Vertex u, Vertex v) {
    if (!validBFS)
      BFS(u);
    Collection p = shortestPath(u,v);
    return (p.size() > 0 || u == v);
  }
	

  /**
   Set vertices specified by Collection.
   @return the number of vertices added to the graph.
   */
	public boolean setVertices(Collection vs) {
    vertices.clear();
    vertexEdges.clear();
    return addAll(vs);
  }
		
	public Vertex getVertex(String name) {
    name = name.trim();
	  Vertex v;
	  Iterator it = vertices.iterator();
		while (it.hasNext()) {
		  v = (Vertex)it.next();
      boolean res = v.name.equals(name);
			//      Format.printf("'%s'(%d)=='%s'(%d) is %l\n", new Parameters(name).add(name.length()).add(v.name).add(v.name.length()).add(name.equals(v.name)));
			if (res)
			  return v;
		}
		//    Format.printf("Could not find vertex %s\nG=G(V,E); %s\n", new Parameters(name).add(this));
	  return null;
	}


 	public boolean addEdge(Edge e) {
	  if (vertices.contains(e.u) && vertices.contains(e.v)) {
      Collection edges = (Collection)vertexEdges.get(e.u);
      if (edges.contains(e))
        return false;
      edges.add(e);
		  if (!e.isDirected()) {
        edges = (Collection)vertexEdges.get(e.v);
        edges.add(e);
      }
      invalidateAll();
			return true;
		} else
		  return false;
	}
		
	public boolean addEdge(Vertex u, Vertex v) {
	  return addEdge(u,v, false);
	}

	public boolean addDirectedEdge(Vertex u, Vertex v) {
	  return addEdge(u,v, true);
	}

  /**
   Add edges in an array.
   @return the number of edges actually added to the graph.
   */
	public int addEdges(Edge[] es) {
    int nbrAdded = 0;
    for (int i=0; i<es.length; i++) {
      if (addEdge(es[i]))
        nbrAdded++;
		}
    return nbrAdded;
	}
		
  /**
   Add edges in a Collection. 
   @return the number of edges actually added to the graph.
   */
	public int addEdges(Collection es) {
    int nbrAdded = 0;
    Iterator it = es.iterator();
    while (it.hasNext()) {
      if (addEdge( (Edge)it.next() ))
        nbrAdded++;
		}
    return nbrAdded;
	}
		
	public int addEdges(String edges) {
    Collection es = Edge.createEdges(this, edges);
    return addEdges(es);
  }

	public boolean addEdge(String name) {
    StringVariable uname = new StringVariable();
    StringVariable vname = new StringVariable();
    StringVariable connection = new StringVariable();
		try {
      Format.sscanf(name, "( %[^,- ] %[,->] %[^ )] )", 
         new Parameters(uname).add(connection).add(vname));
    } catch (Exception ex) {
      ex.printStackTrace();
      System.exit(1);
    }
    boolean directed = false;
    if (connection.equals("-") || connection.equals(","))
      directed = false;
    else if (connection.equals("->"))
      directed = true;
    else
      return false;
	  return addEdge(uname.toString(),vname.toString(), directed);
	}

	public boolean addEdge(String uname, String vname) {
	  return addEdge(uname,vname, false);
	}

	public boolean addDirectedEdge(String uname, String vname) {
	  return addEdge(uname,vname, true);
	}
	
	public boolean addEdge(String uname, String vname, boolean directed) {
	  Vertex u = getVertex(uname);
		if (u == null)
		  return false;
	  Vertex v = getVertex(vname);
		if (v == null)
		  return false;
		return addEdge(u,v, directed);
	}
	
	public boolean addEdge(Vertex u, Vertex v, boolean directed) {
	  if (vertices.contains(u) && vertices.contains(v)) {
	    Edge e = new Edge(u,v, directed);
      return addEdge(e);
		}
		return false;
	}

	/**
   Makes a collection of vertices fully connected to each other. 
   @return a Collection containing the edges added.
  */
	public Collection connectFully(Collection c) {
    Collection Es = new ArrayList();
    int Vlen = c.size();
    ArrayList V = new ArrayList(c);
		for (int i=0; i<Vlen-1; i++) {
  		for (int j=i+1; j<Vlen; j++) {
        Vertex u = (Vertex)V.get(i);
        Vertex v = (Vertex)V.get(j);
        Edge e = new Edge(u,v);
        if (addEdge(e))
          Es.add(e);
      }
    }
    return Es;
  }

	public void topologicalSort() {
		if (!validDFS)
      DFS();
		vertices.clear();
		Iterator Vfs = verticesFinishOrder.iterator();
		while (Vfs.hasNext()) {
		  vertices.add(0,Vfs.next());
		}
	}

	public Collection SCC() {
		if (validSCC)
			return DFTrees;

    DFS();
		topologicalSort();
		transpose();
    DFS();
		transpose();
    validSCC=true;
    return DFTrees;
	} // SCC


	public Graph SCCGraph() {
		Graph GSCC = new Graph();
		HashMap map = new HashMap();
		SCC();
		Iterator trees = DFTrees.iterator();
		while (trees.hasNext()) {
		  Cluster scc = (Cluster)trees.next();
			GSCC.add(scc);
      map.putAll(scc.getMapping());
		}
		Collection E = getEdges();
		Iterator Es = E.iterator();
		while (Es.hasNext()) {
		  Edge e = (Edge)Es.next();
			Cluster uSCC = (Cluster)map.get(e.u);
			Cluster vSCC = (Cluster)map.get(e.v);
			if (!uSCC.equals(vSCC)) {
			  Edge eSCC = new Edge(uSCC,vSCC, true);
			  GSCC.addEdge(eSCC);
			}
		}
		GSCC.containsCycle = false;
		return GSCC;
	}
	
	/**
	 Depth-first search algorithm (DFS) as describes in [CorLeiRiv1990, pp478].
	 It can also be shown that DFS discovers every vertex in <i><b>V</b></i>
   that is reachable from the source <i>s</i>.
	 */
	public Collection DFS() {
    invalidateAll();
	  DFTrees.clear();
	  verticesFinishOrder.clear();
		containsCycle = false;
		
	  // Clear the color and parent of all V.
	  Iterator Vs = vertices.iterator();
		while (Vs.hasNext()) {
		  Vertex u = (Vertex)Vs.next();
      u.color = Vertex.WHITE;
			u.parent = null;
		}

		int time = 0;

		//    Format.printf("Trying vertices: %s\n", new Parameters(vertices));
	  Vs = vertices.iterator();
		while (Vs.hasNext()) {
		  Vertex u = (Vertex)Vs.next();
      if (u.color == Vertex.WHITE) {
					//        Format.printf("Creating a new cluster...\n");
        Cluster cv = new Cluster();
			  time = DFSVisit(u, time+1, cv);
        cv.centerPosition();
        boolean res2 = DFTrees.contains(cv);
  			boolean res = DFTrees.add(cv);
				//        Format.printf("Cluster finished: %s (added=%l, contains=%l, {%s})\n", 
				//new Parameters(cv).add(res).add(res2).add(DFTrees));
			}
		}
    validDFS = true;
		//    Format.printf("verticesFinishOrder: %s\n", 
		//new Parameters(verticesFinishOrder));
    return DFTrees;
	} // DFS

	private int DFSVisit(Vertex u, int time, Cluster cv) {
    u.color = Vertex.GRAY;
		u.timestamp_discovered = time;
    cv.add(u);

		//    TextArea screen = new TextArea();
		//    screen.put(0,0, "time="+time);
		//    screen.pasteArea(0,10, toTextArea());
		//    Format.printf(screen.copyMinimized().toString());
		
    Collection uE = getEdges(u);
		//    Format.printf("%s.edges:%s\n", new Parameters(u).add(uE));
	  Iterator Es = uE.iterator();
		while (Es.hasNext()) {
		  Edge e = (Edge)Es.next();
		  Vertex v = e.otherVertex(u);
			//      Format.printf("From %s following edge %s to %s...",
			//          new Parameters(u).add(e).add(v));
			if (v.color == Vertex.WHITE) {
			  e.type = Edge.TREE_EDGE;
				//        Format.printf("TREE_EDGE.\n");
			  v.parent = u;
				//        Format.printf("DFSVisit(%s, %d, %s)\n", 
				//          new Parameters(v).add(time+1).add(cv));
				time = DFSVisit(v, time+1, cv);
			} else if (v.color == Vertex.GRAY) {
			  e.type = Edge.BACK_EDGE;
				//        Format.printf("BACK_EDGE.\n");
				containsCycle = true;
			} else if (v.color == Vertex.BLACK) {
				if (u.timestamp_discovered < v.timestamp_discovered) {
			    e.type = Edge.FORWARD_EDGE;
					//          Format.printf("FORWARD_EDGE.\n");
				} else {
			    e.type = Edge.CROSS_EDGE;
					//          Format.printf("CROSS_EDGE.\n"); 
        }
			}
		}
    u.color = Vertex.BLACK;
		time++;
		u.timestamp_finished = time;
    verticesFinishOrder.add(u);
		return time;
	} // DFSVisit
	

	/**
	 Breadth-first search algorithm (BFS) as describes in [CorLeiRiv1990, pp469].
	 It can be shown that BFS allways computes the shortest-path distances. 
	 It can also be shown that DFS discovers every vertex in <i><b>V</b></i>
   that is reachable from the source <i>s</i>.
	 */
	public void BFS(Vertex s) {
    invalidateAll();
    // The queue Q will allways contain gray V.
	  List Q = new ArrayList();
		
	  // Clear the color and parent of all V.
	  Iterator Vs = vertices.iterator();
		while (Vs.hasNext()) {
		  Vertex v = (Vertex)Vs.next();
      v.color = Vertex.WHITE;
			v.parent = null;
			v.distance = Integer.MAX_VALUE;
		}

		s.color = Vertex.GRAY;
		s.distance = 0;
//	s.parent = null;  // already done
		Q.add(s);

		// As long as there are gray V do...
    while (!Q.isEmpty()) {
		  Vertex u = (Vertex)Q.get(0);
      Collection uE = (Collection)vertexEdges.get(u);
      Iterator it = uE.iterator();
			while (it.hasNext()) {
     	  Edge e = (Edge)it.next();
			  if (!e.directed || e.u == u) {
  				Vertex v = e.otherVertex(u);
  				// if v is not discovered, then discover it...
  				if (v.color == Vertex.WHITE) {
  					v.color = Vertex.GRAY;
  					v.distance = u.distance+1;
  					v.parent = u;
  					Q.add(v);
  				}
 				}
			}
			Q.remove(0);
			u.color = Vertex.BLACK;
		}
	  validBFS = true;
	}

	private void shortestPath0(List p, Vertex s, Vertex v) {
	  if (s == v || v.parent == null) {
		  return;
		} else {
		  shortestPath0(p, s, v.parent);
	    Collection vpE = (Collection)vertexEdges.get(v.parent);
	    Iterator it = vpE.iterator();
		  while (it.hasNext()) {
		    Edge e = (Edge)it.next();
  			if (e.otherVertex(v.parent) == v) {
    			p.add(e);
	  		  break;
        }
		  }
		}
	}
	
	public List shortestPath(Vertex s, Vertex v) {
    if (!validBFS)
      BFS(s);
	  List p = new ArrayList();
		shortestPath0(p, s, v);
		return p;
	}

	public void undirect() {
    Iterator itV = vertices.iterator();
    while (itV.hasNext()) {
		  Vertex u = (Vertex)itV.next();
      Collection uE = (Collection)vertexEdges.get(u);
      Iterator it = uE.iterator();
      while(it.hasNext()) {
			  Edge e = (Edge)it.next();
				e.undirect();
        Vertex v = e.otherVertex(u);
        if (!u.equals(v)) {
          Collection vE = (Collection)vertexEdges.get(v);
          vE.add(e);
				}
			}
		}
	}
	
	public Collection moralize() {
			//    Format.printf("\nG=\n%s\n", p.add(getEdges()));
	  Collection edgesAdded = new ArrayList();

    Iterator itV = vertices.iterator();
    while (itV.hasNext()) {
		  Vertex w = (Vertex)itV.next();
		  ArrayList parents = new ArrayList(getParents(w));
			//      Format.printf("pa(%s)=%s\n", p.add(w).add(parents));
			if (parents.size() > 1) {
			  for (int from=0; from<parents.size()-1; from++) {
			    for (int to=from+1; to<parents.size(); to++) {
				    Vertex u = (Vertex)parents.get(from);
				    Vertex v = (Vertex)parents.get(to);
						Edge e = new Edge(u,v);
  				  edgesAdded.add(e);
					}
				}
			}
		}
    addEdges(edgesAdded);
		undirect();
		return edgesAdded;
	}
		
	private void triangulationWeightOfVertices(Collection vs) {
    Iterator it = vs.iterator();
		while (it.hasNext()) {
      Vertex u = (Vertex)it.next();
      if (vertices.contains(u)) {
  			// The cluster induced if u is eliminated.
  			Collection family = getChildren(u); // since graph is undirected.
  			family.add(u);
  			double cost = calculateCost(family);
  			double weight = getWeight(family);
  			u.key = (int)(cost*1e6+weight);
				/*
  			Format.printf("family of %s: %s, key=(%2d,%2d)=%d\n",
	  		  p.add(u).add(family).add(cost).add(weight).add(u.key));
				*/
  		}
    }
  }

	private static void triangulationSort(List vs) {
		//		Format.printf("Before sort: %s\n", p.add(vs));
		Collections.sort(vs, new Comparator() {
 				public int compare(Object o1, Object o2) {
 					Vertex u1 = (Vertex)o1;
 					Vertex u2 = (Vertex)o2;
 					//      		Format.printf("Comparing %s and %s\n", new Parameters(u1).add(u2));
 					return (u1.key < u2.key ? -1 : u1.key == u2.key ? 0: +1);
 				} 
 			}
 		);
		// 		Format.printf("After sort: %s\n", p.add(vs));
  }

	public Collection triangulate() {
    Collection cliques = new ArrayList();
    Graph Gtri = (Graph)clone();
    Gtri.triangulationWeightOfVertices(Gtri);
    List vs = new ArrayList(Gtri.getVertices());
    while (!vs.isEmpty()) {
      triangulationSort(vs);
      Vertex u = (Vertex)vs.get(0);
      Collection neighbor = Gtri.getChildren(u); // since graph is undirected.
			//      Format.printf("%s.neighbor = %s\n", p.add(u).add(neighbor));
      Cluster cv = new Cluster(neighbor); cv.add(u); // cv = "u + ne(u)"
			//      Format.printf("****************************************************\n");
			//      Format.printf("%s+ne(%s)=%s+%s=%s\n", p.add(u).add(u).add(u).add(neighbor).add(cv));
      // Check if this cluster is a subset of a previous found cluster.
      boolean isSubset = false;
      Iterator itCliques = cliques.iterator();
      while(itCliques.hasNext()) {
        Cluster clique = (Cluster)itCliques.next();
        if (clique.containsAll(cv)) {
          isSubset = true;
          break;
  			}
      }
			//      Format.printf(" is subset: %l\n", p.add(isSubset));
      if (!isSubset) {
        cliques.add(cv);
				//        Format.printf("%s added\n", p.add(cv));
        cv.centerPosition();
      }
  		Collection edges_added = connectFully(cv);
  		String edges_added_str = edges_added.toString();
			//			Format.printf("Vertex %s was removed. Its family is %s, edges added = %s\n", new Parameters(u).add(cv).add(edges_added_str));
  		addEdges(edges_added_str);
  		Gtri.addEdges(edges_added_str);
  		vs.remove(u);
  		Gtri.remove(u);
 			Gtri.triangulationWeightOfVertices(cv);
			/*
      TextArea screen = new TextArea(100,100);
      screen.pasteArea(0,0,toTextArea());
      screen.pasteArea(0,30,Gtri.toTextArea());
   		Format.printf(screen.copyMinimized().toString());
			*/
    }
		//    Format.printf("cliques=%s\n", p.add(cliques));
    return cliques;
  }

	public JunctionTree toJunctionTree() {
    return JunctionTree.asJunctionTree(this);
	}

	public String toString() {
		try {
	    return Format.sprintf("V=%s, E=%s",
		    new Parameters(vertices.toString()).add(getEdges()));
    } catch (Exception ex) {
      ex.printStackTrace();
      System.exit(1);
    	return null;
		}
	}
	
	public String toString(int verboseLevel) {
    StringBuffer s = new StringBuffer();
		try {
  		switch (verboseLevel) {
  			case 0 : return toString();
  			case 1 : return Format.sprintf("V=%s\nE=%s\n",
  								 new Parameters(vertices.toString()).add(getEdges()));
  			case 11: Iterator itV = vertices.iterator();
  							 while (itV.hasNext()) {
  								 Vertex v = (Vertex)itV.next();
  								 s.append(Format.sprintf(
                     "vertex %s {\n"+
                     "  edges: %s\n"+
                     "}\n", p.add(v).add(getEdges(v))
                   ));
  							 }
  							 return s.toString();
  			case 99: s.append(toString()).append("\n\n");
  							 Iterator Vs = vertices.iterator();
  							 while (Vs.hasNext()) {
  								 Vertex v = (Vertex)Vs.next();
  								 s.append(v.toString(99)).append('\n');
  							 }
  							 return s.toString();
  		}
 		} catch (Exception ex) {
      ex.printStackTrace();
      System.exit(1);
		}
  	return toString();
	}

	public void scaleAndOffset(double yoffset,double xoffset, 
                                   double yscale,double xscale) {
    Iterator itV = vertices.iterator();
    while (itV.hasNext()) {
		  Vertex v = (Vertex)itV.next();
      Point p = v.getPoint();
      p.setLocation(xoffset+p.x*xscale, yoffset+p.y*yscale);
		}
	}

	public void offset(double yoffset,double xoffset) {
    Iterator itV = vertices.iterator();
    while (itV.hasNext()) {
		  Vertex v = (Vertex)itV.next();
      Point p = v.getPoint();
      p.setLocation(xoffset+p.x, yoffset+p.y);
		}
	}

	public void rescale(double yscale,double xscale) {
    Iterator itV = vertices.iterator();
    while (itV.hasNext()) {
		  Vertex v = (Vertex)itV.next();
      Point p = v.getPoint();
      p.setLocation(p.x*xscale, p.y*yscale);
		}
	}

	public TextArea toTextArea() {
    Dimension dimension = graphicalSize();
    int xmin = (int)dimension.xmin;
    int ymin = (int)dimension.ymin;
    int xmax = (int)dimension.xmax;
    int ymax = (int)dimension.ymax;
		//    TextArea tm = new TextArea(xmax-xmin+20,ymax-ymin+20);
    TextArea tm = new TextArea(1000,1000);
    tm.setOffset(-ymin,-xmin);
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

	public Dimension graphicalSize() {
    double x0=Double.MAX_VALUE; double y0=Double.MAX_VALUE;
    double x1=Double.MIN_VALUE; double y1=Double.MIN_VALUE;

    Iterator it = iterator();
    while (it.hasNext()) {
      Vertex v = (Vertex)it.next();
      Point pv = v.getPoint().getLocation();
      if (pv.x < x0) x0 = pv.x;
      if (pv.x > x1) x1 = pv.x;
      if (pv.y < y0) y0 = pv.y;
      if (pv.y > y1) y1 = pv.y;
		}

    return new Dimension(x0,y0, x1,y1);
	}

  public void relaxPositions() {
    Iterator it = edgeIterator();
    while(it.hasNext()) {
      Edge e = (Edge)it.next();
      Point pu = e.u.getPoint().getLocation();
      Point pv = e.v.getPoint().getLocation();
      double vx = pv.x - pu.x;
      double vy = pv.y - pu.y;
      double len = Math.sqrt(vx*vx + vy*vy);
      double f = (len-len)/(len*3) ;
      double dx = f*vx;
      double dy = f*vy;

      e.u.dx +=  dx;
      e.u.dy +=  dy;
      e.v.dx += -dx;
      e.v.dy += -dy;
    }

    Object[] v = vertices.toArray();
    for (int i=0 ; i<v.length ; i++) {
      Vertex n1 = (Vertex)v[i];
      Point pn1 = n1.getPoint().getLocation();
      double dx = 0;
      double dy = 0;

      for (int j=0 ; j<v.length ; j++) {
        if (i == j)
          continue;
        Vertex n2 = (Vertex)v[j];
        Point pn2 = n2.getPoint().getLocation();
        double vx = pn1.x-pn2.x;
        double vy = pn1.y-pn2.y;
        double len = vx*vx + vy*vy;
        if (len == 0) {
          dx += 100*Math.random();
          dy += 100*Math.random();
        } else if (len < 3000*3000) {
          dx += vx / len;
          dy += vy / len;
        }
      } // for

      double dlen = dx*dx + dy*dy;
      if (dlen > 0) {
        dlen = Math.sqrt(dlen) / 2;
        n1.dx += dx / dlen;
        n1.dy += dy / dlen;
      }
    } // for

    Dimension d = graphicalSize();
		//	  Format.printf("d=(%d,%d)\n", p.add(d.width).add(d.height));
		//  Format.printf("d=(%d,%d,%d,%d)\n",
		//       p.add(d.xmin).add(d.ymin).add(d.xmax).add(d.ymax));
    for (int i=0 ; i<v.length ; i++) {
      Vertex n = (Vertex)v[i];
      Point pn = n.getPoint();
      pn.translate( (int)Math.max(-1000, Math.min(1000, n.dx)),
                    (int)Math.max(-1000, Math.min(1000, n.dy)) );
			//    Format.printf("(x,y)=(%.3f,%.3f) ==> ", p.add(n.x).add(n.y));
      if (pn.x < 0) {
        pn.x = 0;
      } else if (pn.x > d.xmax) {
        pn.x = (int)d.xmax;
      }
      if (pn.y < 0) {
        pn.y = 0;
      } else if (pn.y > d.ymax) {
        pn.y = (int)d.ymax;
      }
      n.dx /= 2;
      n.dy /= 2;
			//    Format.printf("(x,y)=(%.3f,%.3f)\n", p.add(n.x).add(n.y));
    } // for
	} // relaxPositions
  static Parameters p = new Parameters();
} // class Graph


/*
HISTORY

2000-07-07
* Added support for java.awt.Point-location.
* Added getName() and setName(String).
2000-05-02
* Added indexOf() and indicesOf().
990609
* Oopps! Added edges to graph while moralizing. Now adding the edges
  at the end of the moralizing.
990608
* Added name and description.
990603
* Added getEdge(Vertex u, Vertex v).
* Changed name on getNeighbor(...) to getNeighbors(...).
990602
  Added addAll(Vertex[]).
990331
  Added complete toJunctionTree(). Seems to work. At least the result is
  a clique-tree.
990329
  Replacing all Vector-return with Collection-return.
990327
  Added triangulate(). Seems to work, but no serious testing yet.
990325
  Added getFamily() and getChildren().
990317
  Added isDAG(), isDirected(), isTree(), isSinglyConnected(). SCC() does
  now return a graph G=G(V,E) where V is a set of ClusterVertices.
990316 
  Changed notation on an edge from "{u,v}" to "(u,v)".
990315
  Added toTextArea().
990314
  Added addEdge(String) and addEdges(String) by using sscanf.
990310
  Now using Vector instead of HashSet to represent V. This way the V will
	have a given order.
	Implemented DFS, SCC, SCCGraph, topologicalSort.
990308
  Created.
	
*/
