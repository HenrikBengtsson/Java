package hb.bn;

import java.io.Writer;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class SimulationFileList implements List {
  protected FormatWriter out;
  protected int[] shuffleMap;
  private   Parameters p = new Parameters();

	public SimulationFileList() {
    this.out = FormatWriter.conform(out);
    this.bn = bn;
	}

	public void write(String format, int[] value)
       throws hb.format.ParseException, java.io.IOException {
    // 1. Create a format string for a row.
    StringBuffer fmtstr = new StringBuffer();
    int n = value.length;
    for (int j=0; j<n; j++)
      fmtstr.append(format);
    fmtstr.append('\n');
    // 2. Pre-compile the format string.
    FormatString fmt = fout.compileFormatString(fmtstr.toString());

    // 3. Write the elements of the matrix.
    for (int i=0; i<n; i++)
      p.add(value[i]);
    fout.write(fmt, p);
 	} // write

	public void write(int[] value) {
    write("%4d ", value);
	}

	public void add(int[] instance) {
    write(instace);
	}

  public void add(int, java.lang.Object) {
  }

  public boolean add(java.lang.Object) {
  }

  public boolean addAll(int, java.util.Collection) {
  }

  public boolean addAll(java.util.Collection) {
  }

  public void clear() {
  }

  public boolean contains(java.lang.Object) {
  }

  public boolean containsAll(java.util.Collection) {
  }

  public boolean equals(java.lang.Object) {
  }

  public java.lang.Object get(int) {
  }

  public int hashCode() {
  }

  public int indexOf(java.lang.Object) {
  }

  public boolean isEmpty() {
  }

  public java.util.Iterator iterator() {
  }

  public int lastIndexOf(java.lang.Object) {
  }

  public java.util.ListIterator listIterator() {
  }

  public java.util.ListIterator listIterator(int) {
  }

  public java.lang.Object remove(int) {
  }

  public boolean remove(java.lang.Object) {
  }

  public boolean removeAll(java.util.Collection) {
  }

  public boolean retainAll(java.util.Collection) {
  }

  public java.lang.Object set(int, java.lang.Object) {
  }

  public int size() {
  }

  public java.util.List subList(int, int) {
  }

	public java.lang.Object[] toArray() {
  }

	public java.lang.Object[] toArray(java.lang.Object[]) {
  }
} // class SimulationWriter

/*
HISTORY

2000-06-30
* Created.
	
*/
