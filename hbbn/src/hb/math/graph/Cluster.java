package hb.math.graph;

import java.awt.Point;
import java.util.Collection;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import hb.format.Format;
import hb.format.Parameters;

/**
 A set of vertices is called a <B>cluster</B> of vertices. 
 <BR>

 @see Graph
 @see Vertex
 @see Edge
 */
public class Cluster extends Vertex implements Set, Comparable {
  protected List vertices = new ArrayList();
  protected double cost;
  public Object data;
  public int flag;

	/**
   Creates an empty cluster.
   */
	public Cluster() {
    clear();
  };
	
	/**
   Creates a cluster of vertices.
	 */
	public Cluster(Vertex vs[]) {
    this(Arrays.asList(vs));
	}

	/**
   Creates a cluster of vertices.
	 */
	public Cluster(Collection c) {
    addAll(c);
	}

	/**
   Creates a cluster of vertices.
	 */
	public Cluster(Cluster c) {
    addAll(c);
	}

	/**
   Clone the cluster. 
	 */
	public Object clone() {
    return new Cluster(this);
	}

	public double getWeight() {
    weight = 1.0;
    Iterator it = vertices.iterator();
    while (it.hasNext()) {
      Vertex v = (Vertex)it.next();
      weight *= v.weight;
    }
    return weight;
  }

	public double getCost() {
    return cost;
  }

	public void setCost(double cost) {
    this.cost = cost;
  }

	private void update() {
    // 1st key: The number of edges added if u is selected next.
			//    cost = calculateCost();
    // 2nd key: The weight of the cluster induced if u is selected next.
    //          Its weight is the product of the weights of its constituent 
    //          vertices.
    weight = getWeight();
    autoName();
	}

	public int compareTo(Object o) {
    Cluster c = (Cluster)o;
    if (cost == c.cost) {
      c.getWeight();
      getWeight();
			return (weight<c.weight?-1:weight==c.weight?0:+1);
    } else
			return (cost<c.cost?-1:cost==c.cost?0:+1);
  }
  
	public boolean equals(Object o) {
    Vertex v = (Vertex)o;
    Cluster c = (Cluster)o;
    return vertices.equals(c.vertices);
  }
  
	public int hashCode() {
    return (int)(cost*1e6+getWeight());
	}
 
	public Cluster intersect(Collection c) {
		//    Format.printf("%s.intersect(%s), ", new Parameters(this).add(c));
    Collection cint = new HashSet(vertices);
		//    Format.printf("%s -> ", new Parameters(cint));
    cint.retainAll(c);
		//    Format.printf("%s\n", new Parameters(cint));
    return new Cluster(cint);
	}

	/**
   Add a vertex to the cluster.
   @return <tt>true</tt> if the vertex was added, otherwise <tt>false</tt>.
  */
	public boolean add(Object o) {
    Vertex v = (Vertex)o;
    if (vertices.contains(v))
      return true;
    if (vertices.add(v)) {
  		update();
      return true;
    } else
      return false;
	}

	/**
   Add vertices to the cluster.
   @return the number of vertices actually added.
  */
	public boolean addAll(Collection c) {
    boolean added = false;
    Iterator it = c.iterator();
    while (it.hasNext()) {
      Vertex v = (Vertex)it.next();
      if (!vertices.contains(v))
        if (vertices.add(v))
          added = true;
    }
		update();
    return added;
	}

	/**
   Checks if the cluster is empty.
   @return <tt>true</tt> if the cluster is empty, otherwise <tt>false</tt>.
  */
	public boolean isEmpty() {
    return (vertices.size() == 0);
	}

	public Collection getVertices() {
    return vertices;
	}

	public Iterator iterator() {
    return vertices.iterator();
	}

	public boolean remove(Object o) {
    Vertex v = (Vertex)o;
		if (vertices.remove(v)) {
   		update();
      return true;
    } else
			return false;
	}

	public boolean removeAll(Collection c) {
		if (vertices.removeAll(c)) {
   		update();
      return true;
    } else
			return false;
	}

	public boolean retainAll(Collection c) {
		if (vertices.retainAll(c)) {
   		update();
      return true;
    } else
			return false;
	}

	public Object[] toArray() {
    return vertices.toArray();
  }

	public Object[] toArray(Object[] a) {
    return vertices.toArray(a);
  }

	/**
   Gets the number of vertices in this cluster.
   @return the number of vertices in this cluster.
  */
	public int size() {
    return vertices.size();
	}

	/**
   Remove all vertices from this cluster.
  */
	public void clear() {
    vertices.clear();
    name = "<empty>";
    weight = 0.0;
    cost   = 0.0;
	}

	public boolean contains(Object o) {
    return vertices.contains(o);
  }

	public boolean containsAll(Collection c) {
    Iterator itC = c.iterator();
    while (itC.hasNext()) {
      Vertex u = (Vertex)itC.next();
			//			Format.printf("%s <= %s ? %s %l\n", new Parameters(c).add(this).add(u).add(contains(u)));
      if (!contains(u))
				return false;
		}
    return true;
	}


	/**
   Get the mapping from vertex <i>u</i> to its cluster <i>xyz</i>.

   @return the mapping from vertex to cluster.
	 */
	public Map getMapping() {
    Map map = new HashMap();
    Iterator it = vertices.iterator();
		while(it.hasNext()) {
      Vertex u = (Vertex)it.next();
      map.put(u, this);
		}
    return map;
	}

	/**
   Position the cluster to the central mass point of all the vertices
   in it.
  */
	public void centerPosition() {
    int x=0, y=0;
    Iterator it = vertices.iterator();
		while(it.hasNext()) {
      Vertex u =(Vertex)it.next();
      Point p = u.getPoint();
      x += p.x;
      y += p.y;
  	}
    int n = size();
    x = x/n; y = y/n;
    point.setLocation(x,y);
	}

	protected void autoName() {
		//    Collections.sort(new ArrayList(this));
    if (size() > 0) {
      StringBuffer s = new StringBuffer();
  		Iterator it = iterator();
  		while(it.hasNext()) {
  			Vertex v = (Vertex)it.next();
  			s.append(v.name);
  		}
  		name = s.toString();
    } else {
      name = "<empty>";
    }
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
  								 "cluster %s {\n"+
  								 "  (x,y)    = (%.3f,%.3f)\n"+
  								 "  vertices = %s\n"+
  								 "}\n",
  								 new Parameters(name)
  										 .add(point.getX()).add(point.getY())
  										 .add(vertices)
  							 ));
  							 return s.toString();
  		}
 		} catch (Exception ex) {
      ex.printStackTrace();
      System.exit(1);
		}
		return name;
  }
} // class Vertex


/*
HISTORY

2000-07-07
* Updated centerPosition() to support Point instead.

990329
  Modified from ClusterVertex. Now it is a true Set.
  Created.
	
*/
