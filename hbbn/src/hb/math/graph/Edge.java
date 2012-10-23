package hb.math.graph;

import hb.format.Format;
import hb.format.FormatReader;
import hb.format.Parameters;
import hb.lang.StringVariable;
import java.io.StringReader;
import java.util.Collection;
import java.util.ArrayList;

/**
 An <B>edge</B> is a pair of vertices; <I>(u,v)</I>, where <I>u,v</I>
 belongs to <I>V</I> and <I>u!=v</I> in the graph <I>G=(V,E)</I>.
 If <I>(u,v)</I> is an edge in a directed graph, we say that
 <I>(u,v)</I> is <B>incident from</B> or <B>leaves</B> vertex <I>u</I> and
 is <B>incident to</B> or <B>enters</B> vertex <I>v</I>.
 If <I>(u,v)</I> is an edge in a undirected graph, we say that
 <I>(u,v)</I> is <B>incident on</B> vertex <I>u</I> and <I>v</I>.<BR>
 If <I>(u,v)</I> is an edge in a graph, we say that <I>v</I> is
 <B>adjacent</B> to vertex <I>u</I>. When a graph is undirected, the
 adjacency relation is symmetric. When the graph is directed, the adjacency
 relation is not necessarily symmetric.<BR>
 <BR>

 @see "[CorLeiRiv1990, pp86]"
 @see Graph
 @see Vertex
 */
public class Edge {
  public final static boolean DIRECTED = true, UNDIRECTED = false;
  public String name;
  public Vertex u,v;
	public double weight = 1.0;

	// Set by DFS
	public static final int UNKNOWN      = 0,
										      CROSS_EDGE   = 1,
	                        BACK_EDGE    = 2,
										      FORWARD_EDGE = 3,
										      TREE_EDGE    = 4;
	public int type = UNKNOWN;
	public boolean directed = false;
	public int count = 0;
	
	/**
   Creates an unconnected edge. Should no be used.
   */
	protected Edge() {
	  name = "<unconnected>";
	}
	
	/**
   Creates an <b>undirected</b> edge <i>(u,v)</i> connecting 
   the vertices <i>u</i> and <i>v</i>.
   */
	public Edge(Vertex u, Vertex v) {
	  this(u,v, false);
	}

	/**
   Creates a <b>directed</b> or an <b>undirected</b> edge 
   <i>(u,v)</i> connecting the vertices <i>u</i> and <i>v</i>.
   */
	public Edge(Vertex u, Vertex v, boolean directed) {
    this.u = u;
		this.v = v;
		this.directed = directed;
		updateName();
	}

	/**
   Creates an identical edge as the specified one.
   */
	public Edge(Edge e) {
		name     = e.name;
		u        = e.u;
		v        = e.v;
		type     = e.type;
		weight   = e.weight;
		directed = e.directed;
	}

	/**
   Clone the edge. 
 
   @return the cloned edge.
	 */
	public Object clone() {
	  return new Edge(this);
	}

  /**
   Create a Collection of edges that are specified by the given string. 
   The string should be in format <tt>"[(A,B),(C,D),...]"</tt> with or 
   without the brackets.
   @return a Collection containing the edges.
   */
	public static Collection createEdges(Graph G, String esstr) {
    Collection es = new ArrayList();
    FormatReader in = new FormatReader(new StringReader(esstr));
    StringVariable uname = new StringVariable();
    StringVariable vname = new StringVariable();
    StringVariable connection = new StringVariable();
    Parameters p = new Parameters();
		try {
			in.read(" ");
			boolean leftBracket = (in.read("[ ") > 0);
      while (!in.isAtEndOfFile()) {
        in.read(" ");
				if (in.lookAHead("]") == 1)
          return es;
        in.read(" ( %[^,- ] %[,->] %[^ )] ) ",
          p.add(uname).add(connection).add(vname));
        String cnct = connection.toString();
        boolean directed = false;
        if (cnct.equals("-") || cnct.equals(","))
          directed = false;
        else if (cnct.equals("->"))
          directed = true;
        Vertex u = G.getVertex(uname.toString());
        Vertex v = G.getVertex(vname.toString());
				//        Format.printf("createEdges(%s): new (\"%s\",\"%s\")=(%s,%s)\n", new Parameters(esstr).add(vc.getString("uname")).add(vc.getString("vname")).add(u).add(v));
        if (u == null || v == null)
          Format.printf("u or v == null, G=G(V,E); %s\n", new Parameters(G));
	      Edge e = new Edge(u,v, directed);
        es.add(e);
        try {in.read(",");} catch (Exception ex) {}
			}
      if (leftBracket) {
			  boolean rightBracket = (in.read("] ") > 0);
        throw new Exception("Graph.addVertices(String): Excpected ']'"+
                            " never found!");
      } else
   			in.read(" ");
    } catch (Exception ex) {
      ex.printStackTrace();
      System.exit(1);
    }

    return es;
	}
	/**
   If the edge is directed it will be undirected. 
   */
	public void undirect() {
    directed = false;
		updateName();
	}

	/**
   If directed, the the edge <i>(u,v)</i> will has its directioned changed,
   and the new name of the edge become <i>(v,u)</i>.
   */
	public void swapDirection() {
	  if (directed) {
	    Vertex tmp = u;
		  u = v;
		  v = tmp;
		  type = UNKNOWN;
		  updateName();
		}
	}
	
	/**
   Check if the edge is directed or not.
 
   @return <tt>true</tt> if the edge is directed, otherwise <tt>false</tt>.
   */
	public boolean isDirected() {
	  return directed;
	}


	/**
   Gets the type of the edge ("CROSS","BACK","FORWARD" or "TREE") of
	 the edge. These types are set and used by different search algorithms
   performed on the graph <I>G</I> which the vertex belongs to.

   @see Graph
   @return the type as a String.
	 */
	public String getTypeString() {
	  switch (type) {
		  case CROSS_EDGE:   return "CROSS";
		  case BACK_EDGE:    return "BACK";
		  case FORWARD_EDGE: return "FORWARD";
		  case TREE_EDGE:    return "TREE";
			default   : return "UNKNOWN";
		}
	}
	
	private void updateName() {
		try {
		  name = Format.sprintf("(%s-%c%s)",
		    new Parameters(u.name).add(directed?'>':'\0').add(v.name));
    } catch (Exception ex) {
      ex.printStackTrace();
      System.exit(1);
		}
	}


	/**
   Check if the edge is connected to a given vertex.
 
   @return <tt>true</tt> if the edge is connected to the vertex, 
           otherwise <tt>false</tt>.
   */
	public boolean isConnectedTo(Vertex w) {
	  return (u == w || v == w);
	}

	/**
   Get the vertex that has this edge <i>(u,v)</i> leaving it; <i>u</i>.
 
   @return the vertex that has this edge leaving it.
   */
	public Vertex leaves() {
    return u;
	}
		
	/**
   Gets the vertex that has this edge <i>(u,v)</i> entering it; <i>v</i>.
 
   @return the vertex that has this edge entering it.
   */
	public Vertex enters() {
	  return v;
	}

	/**
   Gets the vertex on the opposite side of the edge <i>(u,v)</i>, relative
   the given vertex. If the given vertex is <i>u</i>, <i>v</i> is returned
   and if <i>v</i> is given, <i>u</i> is returned.
 
   @return the vertex on the opposite side of the edge, relative the given
           vertex.
   */
	public Vertex otherVertex(Vertex w) {
	  if (w == u)
	    return v;
		else if (w == v)
		  return u;
		return null;
	}

	/**
   Check if this edge equals another edge. The edges is equal if they
   are equally directed and connected to the same vertices.

   @return <tt>true</tt> if the edges are equal, otherwise <tt>false</tt>.
   */
	public boolean equals(Object o) {
			//    Format.printf("%s.equals(%s)...\n", new Parameters(this).add(o));
	  if (o instanceof Edge) {
		  // This relation is reflexive, symmetric, transitive and consistent.
		  Edge e = (Edge)o;
			if (directed) {
			  if (e.directed)			  
			    return (e.u == u && e.v == v);
			} else {
			  if (e.u == u)
				  return (e.v == v);
				else if (e.u == v)
				  return (e.v == u);
		  }
		}
	  return false;
	}
	
	/**
   Gets a unique hashcode for this edge.
 
   @return the hashcode.
   */
	public int hashCode() {
	  return u.hashCode()*v.hashCode();
	}	

	/**
   Gets a String describing the edge, i.e. returning the name which is
   in format "<tt>(u,v)</tt>".
   @return the name of the edge.
	 */
	public String toString() {
	  return name;
	}
	
	/**
   Gets a formatted String describing the edge.
   @return a formatted String describing the edge.
	 */
	public String toString(int verboseLevel) {
		try {
  		switch (verboseLevel) {
  			case 0 : return Format.sprintf("(%s,%s)",
  								 new Parameters(u.name).add(v.name));
  			case 1 : return Format.sprintf("(%s,%s) weight=%i",
  								 new Parameters(u.name).add(v.name).add(weight));
  			case 2 : return Format.sprintf("(%s,%s) weight=%i, type=%s",
  								 new Parameters(u.name).add(v.name).add(weight)
  										 .add(getTypeString()));
  			case 98: return Format.sprintf(
  								 "edge %s {\n"+
  								 "  weight  = %f\n"+
  								 "  type    = %s\n"+
  								 "}\n",
  								 new Parameters(name).add(weight)
  										 .add(getTypeString()));
  			case 99: String s;
  							 if (directed)
  								 s = Format.sprintf("  from    = %s\n  to      = %s\n",
  											 new Parameters(u.name).add(v.name));
  							 else
  								 s = Format.sprintf("  between = %s and %s\n",
  											 new Parameters(u.name).add(v.name));
  							 return Format.sprintf(
  								 "edge %s {\n"+
  								 "%s"+
  								 "  weight  = %f\n"+
  								 "  type    = %s\n"+
  								 "}\n",
  								 new Parameters(name).add(s).add(weight)
  										 .add(getTypeString()));
  		}
 		} catch (Exception ex) {
      ex.printStackTrace();
      System.exit(1);
		}
		return name;
	}
} // class Edge


/*
HISTORY

990413 [MAJOR CHANGE]
* Vertices does not know about edges.
990329
  Replacing all Vector-return with Collection-return.
990317
  Added isDirected().
990316
  Changed notation from "{u,v}" to "(u,v)".
990314
  Added some javadoc-comments.
990311
  Added direction-attribute.
990309
  Totally changed.
990308
  Created.
	
*/
