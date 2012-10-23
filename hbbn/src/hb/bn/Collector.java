package hb.bn;

import java.util.Collection;

public interface Collector {
  public boolean add(Object obj);
  public boolean addAll(Collection c);
  public void setMap(int[] map);
} // interface Collector

/*
HISTORY

2000-07-04
* Created.
	
*/
