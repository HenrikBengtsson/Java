package hb.math.graph;

import hb.format.Format;
import hb.format.Parameters;
import java.util.Collection;
import java.util.Iterator;

/**
 A <B>sepset</B> of vertices is a set of vertices that seperates 
 two specified clusters, X and Y. The vertices in a sepset is
 exactly those that can be found in both clusters, i.e. X intersect Y.
 <BR>
 @see Cluster
 @see Vertex
 */
public class Sepset extends Edge implements Collection, Comparable {
  public Cluster X,Y,Sxy;
  public double mass;
  public double cost;
  public Object data;

	/**
   Creates the sepset between cliques X and Y.
   */
	public Sepset(Cluster X, Cluster Y) {
    u = this.X = X;
    v = this.Y = Y;
    Sxy = new Cluster((Collection)u);
    Sxy.retainAll((Collection)v);
    updateName();
    mass = (double)size();
    cost = X.weight+Y.weight;
  };
	
	/**
   Clone the cluster. 
	 */
	public Object clone() {
    return new Sepset(X,Y);
	}
	
	public boolean contains(Object o) {
    Vertex v = (Vertex)o;
    return Sxy.contains(v);
	}

	public boolean containsAll(Collection c) {
    return Sxy.containsAll(c);
	}

	public void clear() {
  }

	public boolean isEmpty() {
    return (size() == 0);
  }

  public int size() {
    return Sxy.size();
	}

	public Object[] toArray() {
    return Sxy.toArray();
	}

	public Object[] toArray(Object[] a) {
    return Sxy.toArray(a);
	}

	public Iterator iterator() {
    return Sxy.iterator(); 
  }
 
	public int compareTo(Object o) {
    Sepset s = (Sepset)o;
    if (mass == s.mass) 
      return (cost<s.cost?-1:cost==s.cost?0:+1);
    else
      return (mass>s.mass?-1:mass==s.mass?0:+1);
	}

	public boolean equals(Object o) {
		if (o instanceof Sepset) {
      Sepset s = (Sepset)o;
      return (s.u == u && s.v == v);
		} else
      return false;
	}	

	public int hashCode() {
    return (int)(Integer.MAX_VALUE-mass*1e6 + cost);
	}	

  public boolean add(Object o) {
    return false;
	}

	public boolean addAll(Collection c) {
    return false;
	}

	public boolean remove(Object o) {
    return false;
	}

	public boolean removeAll(Collection c) {
    return false;
	}

	public boolean retainAll(Collection c) {
    return false;
	}

	protected void updateName() {
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

	public String toString() {
    return Format.sprintf("(%s-[%s]-%s)", new Parameters(X).add(name).add(Y));
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
  								 "sepset [%s] {\n"+
  								 "  X    = %s\n"+
  								 "  Y    = %s\n"+
  								 "  mass = %s\n"+
  								 "  cost = %s\n"+
  								 "}\n",
  								 new Parameters(name).add(X).add(Y).add(mass).add(cost)
  							 ));
  							 return s.toString();
  		}
 		} catch (Exception ex) {
      ex.printStackTrace();
      System.exit(1);
		}
		return name;
  }
} // class Sepset


/*
HISTORY

2000-07-07
* Removed centerPosition().

990330
  Created.
	
*/
