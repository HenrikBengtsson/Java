package hb.math.graph;

import java.awt.Point;
import java.io.StringReader;
import java.util.Collection;
import java.util.ArrayList;
import hb.geometry.RelativePoint;
import hb.format.Format;
import hb.format.FormatReader;
import hb.format.Parameters;
import hb.lang.StringVariable;

/**
 A <B>vertex</B> is an element belonging to the vertex set <I>V</I> in a 
 graph <I>G=(V,E)</I>, where <I>E</I> is the edge-set where each edge-element
 connects two vertices.
 <BR>

 @see "[CorLeiRiv1990, pp86]"
 @see Graph
 @see Edge
 */
public class Vertex implements Comparable {
	public String name;
  public String description;
  // position (x,y) that can be used a front ends.
  public Point point = new Point();
	public double x=0.0, y=0.0;

	// Used by some graphical algorithms.
	public double dx=0.0, dy=0.0;

	// the "weight" of this vertex. This attribute can be used for any purpose.
  protected double weight = 1.0;

	// Used by search algoritms
	public Vertex parent;
	public static final int WHITE=0,
                          GRAY=1,   // discovered
		                      BLACK=2;  // finished
	public int color = WHITE;
  public int key;
	
	// Used by BFS
	public int distance = Integer.MAX_VALUE;
	
	// Used by DFS
	public int timestamp_discovered, timestamp_finished;
	
	/**
   Creates a vertex without a name.
	 */
	public Vertex() {
	  this("<no-name>");
	}
	
	/**
   Creates a vertex with specified name.
	 */
	public Vertex(String name) {
	  this.name = name;
	}

	/**
   Creates a vertex identical to specified Vertex.
	 */
	public Vertex(Vertex v) {
		name     = v.name;
    x        = v.x;
    y        = v.y;
		color    = v.color;
		distance = v.distance;
		timestamp_discovered = v.timestamp_discovered;
		timestamp_finished   = v.timestamp_finished;
	}

	public Point getPoint() {
    return point;
	}

	public void setPoint(Point p) {
    point = p;
    if (point instanceof RelativePoint)
      ((RelativePoint)point).setObject(this);
	}

	public Point getLocation() {
    if (point instanceof RelativePoint)
      return point.getLocation();
    return point;
	}


	public int getTimeFound() {
    return timestamp_discovered;
	}

	public int getTimeDone() {
    return timestamp_finished;
	}

	public int getTimeLength() {
    return timestamp_finished-timestamp_discovered+1;
	}

	public boolean equals(Object o) {
    Vertex v = (Vertex)o;
    return name.equals(v.name);
  }

	public int hashCode() {
    return name.hashCode();
	}

	public int compareTo(Object o) {
    Vertex u = (Vertex)o;
    return name.compareTo(u.name);
	}

	public double getWeight() {	
    return weight;
  }

	public void setWeight(double weight) {
    this.weight = weight;
  }

	/**
   Clones the vertex. 
   @return a referense to the cloned object.
	 */
	public Object clone() {
	  return  new Vertex(this);
	}
	
	public void set(String assignments) {
    FormatReader in = new FormatReader(new StringReader(assignments));
    StringVariable var   = new StringVariable();
    StringVariable value = new StringVariable();
		try {
      while (!in.isAtEndOfFile()) {
        in.read(" %[^ =] = %[^ ,] ", new Parameters(var).add(value));
        if (var.equals("x")) {
          int x = Integer.valueOf(value.toString()).intValue();
          point.setLocation(x, point.y);
        } else if (var.equals("y")) {
          int y = Integer.valueOf(value.toString()).intValue();
          point.setLocation(point.x, y);
        } else if (var.equals("name"))
          name = value.toString();
        try {in.read(",");} catch (Exception ex) {}
			}
    } catch (Exception ex) {
      ex.printStackTrace();
      System.exit(1);
    }
	}

  /**
   Creates a Collection of vertices that are specified by the given string. 
   The string should be in format <tt>"[A,B,C,...]"</tt> with or 
   without the brackets.
   @return a Collection containing the vertices.
   */
	public static Collection createVertices(String vsstr) {
    Collection vs = new ArrayList();
    FormatReader in = new FormatReader(new StringReader(vsstr));
    StringVariable svar = new StringVariable();
		try {
			in.read(" ");
			boolean leftBracket = (in.read("[ ") > 0);
      while (!in.isAtEndOfFile()) {
        in.read(" %[^],{ ] ", p.add(svar));
				String uname = svar.toString();
        if (uname.length() > 0) {
  				Vertex u = new Vertex(uname);
  				if (in.lookAHead("{") > 0) {
							in.read("{%[^}]} ", p.add(svar));
  					u.set(svar.toString());
  				}
  				vs.add(u);
        }
				//        Format.printf("<<%s>>, ", p.add(in));
        try {in.read(",");} catch (Exception ex) {}
				//        Format.printf("<<%s>>, ", p.add(in));
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

    return vs;
	}

	/**
   Gets the name of the color ("WHITE","GRAY","BLACK" or "UNKNOWN") of
	 the vertex. These colors are set and used by different search algorithms
   performed on the graph <I>G</I> which the vertex belongs to.

   @see Graph
   @return the type as a String.
	 */
	public String getColorString() {
	  switch(color) {
		  case WHITE: return "WHITE";
		  case GRAY:  return "GRAY";
		  case BLACK: return "BLACK";
			default   : return "UNKNOWN";
		}
	}
		
	/**
   Gets a String describing the vertex.
   @return the name of the vertex.
	 */
	public String toString() {
	  return name;
	}
	
	/**
   Gets a formatted String describing the vertex.
   @return a formatted String describing the vertex.
	 */
	public String toString(int verboseLevel) {
		try {
  		switch (verboseLevel) {
  			case 0 : return Format.sprintf("%s", new Parameters(name));
  			case 99: StringBuffer s = new StringBuffer(Format.sprintf(
  								 "vertex %s {\n"+
  								 "  description = %s\n"+
  								 "  (x,y)       = (%.3f,%.3f)\n"+
  								 "  distance    = %i\n"+
  								 "  parent      = %s\n"+
  								 "  color       = %s\n"+
  								 "  timestamps  = %i/%i\n"+
  								 "}\n",
  								 new Parameters(name)
  										 .add(description)
  										 .add(point.getX()).add(point.getY())
  										 .add(distance)
  										 .add(parent==null?"<none>":parent.name)
  										 .add(getColorString())
  										 .add(timestamp_discovered)
  										 .add(timestamp_finished)
  							 ));
  							 return s.toString();
  		}
 		} catch (Exception ex) {
      ex.printStackTrace();
      System.exit(1);
		}
		return name;
 	}

  static Parameters p = new Parameters();
} // class Vertex


/*
HISTORY

2000-07-07
* Location is represented by a java.awt.Point. Added the methods getPoint()
  and setPoint(Point).
990607
* Added the description-field since it is in the XBN File Format.
990413 [MAJOR CHANGE]
* Removed all references to edges. A vertex does not know about edges.
990331
  Position (x,y) is now with double precision. Before it was integers.
990330
  Now the Vertex is a TreeSet with the edges as elements.
990329
  Replacing all Vector-return with Collection-return.
990325
  Added getChildren(). Had forgotten to set (x,y) when cloning.
990314
  Added some javadoc-comments.
990310
  Now using Vector instead of HashSet, since I want known order on the edges.
990308
  Created.
	
*/
