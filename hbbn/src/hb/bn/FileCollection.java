package hb.bn;

import java.io.Writer;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
public class FileCollection implements Collection {
  protected FormatWriter out;
  protected FormatString fmtstr;
  private   Parameters p = new Parameters();

	public FileCollection(Writer out, FormatString fmtstr) {
    this.out = FormatWriter.conform(out);
    this.fmtstr = fmtstr;
	}

	public FileCollection(Writer out, String format) {
    this.out = FormatWriter.conform(out);
    // 1. Create a format string for a row.
    StringBuffer s = new StringBuffer();
    int n = value.length;
    for (int j=0; j<n; j++)
      s.append(format);
    s.append('\n');
    fmtstr = fout.compileFormatString(s.toString());
	}

	public void write(int[] value) {
    for (int i=0; i<n; i++)
      p.add(value[i]);
    fout.write(fmtstr, p);
	}

	public boolean add(int[] value) {
    write(value);
    return true;
	}

	public boolean add(Object obj) { // interface Collection
		if (obj instanceof int[]) {
      write((int[])obj);
      return true;
		} else
      return false;
	}

	public Iterator iterator() {
			//    return new FileCollectionIterator(this);
    return null;
	}

	public Object[] toArray() {
    Object[] obj = new Object[n*m];
    for (int j=0; j<n; j++)
      for (int k=0; k<m; k++)
        obj[j*n+k] = new Double(value[j][k]);
    return obj;
	}

	public Object[] toArray(Object[] obj) {
    return toArray();
	}

	public boolean containsAll(Collection c) {
    return false;
	}

	public boolean contains(Object obj) {
		if (obj instanceof Number) {
      double d = ((Number)obj).doubleValue();
      return contains(d);
		} else
      return false;
	}
   
	public boolean retainAll(Collection c) {
    return false;
	}

	public void clear() {
    fill(0.0);
	}

	public boolean addAll(Collection c) {
    return false;
	}

	public boolean remove(Object obj) {
    return false;
	}

	public boolean removeAll(Collection c) {
    return false;
	}

	public boolean isEmpty() {
    return false;
	}
} // class SimulationWriter

/*
HISTORY

2000-07-04
* Created.
	
*/
