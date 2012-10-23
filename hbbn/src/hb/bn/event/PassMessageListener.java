package hb.bn.event;

import java.util.EventListener;

public interface PassMessageListener extends EventListener {
  public void projection(PassMessageEvent e);
  public void absorption(PassMessageEvent e);
  public void messagePassed(PassMessageEvent e);
} // interface PassMessageListener

/* HISTORY:

2000-06-20
* Created to build the foundation for automatically generated analytical 
  expressions for the junction tree algorithm.
*/
