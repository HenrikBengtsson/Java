package hb.bn.event;

import hb.math.graph.Cluster;
import hb.math.graph.Sepset;

public class PassSampleMessageEvent extends PassMessageEvent {
	public PassSampleMessageEvent(Cluster from, Sepset s, Cluster to) {
    super(from,s,to);
	}

  public String toString() {
    return "PassSampleMessageEvent: From: "+from+", To: "+to+", Via: "+sepset;
  }
} // class PassSampleMessageEvent


/* HISTORY:

2000-08-08
* Created.

*/
