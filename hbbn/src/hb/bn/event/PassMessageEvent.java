package hb.bn.event;

import hb.math.graph.Cluster;
import hb.math.graph.Sepset;
import java.util.EventObject;

public class PassMessageEvent extends EventObject {
  protected Cluster from, to;
  protected Sepset  sepset;

	public PassMessageEvent(Cluster from, Sepset s, Cluster to) {
    super(from);
    this.from   = from;
    this.to     = to;
    this.sepset = s;
	}

	public Cluster getFrom() {
    return from;
	}

	public Cluster getTo() {
    return to;
	}

	public Sepset getSepset() {
    return sepset;
	}

  public String toString() {
    return "PassMessageEvent: From: "+from+", To: "+to+", Via: "+sepset;
  }
} // class PassMessageEvent


/* HISTORY:

2000-08-08
* Added toString().
2000-06-20
* Created to build the foundation for automatically generated analytical 
  expressions for the junction tree algorithm.
*/
