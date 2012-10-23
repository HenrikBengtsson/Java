package hb.bn;

import hb.format.FormatWriter;
import hb.format.FormatString;
import hb.format.Parameters;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class SimulationWriter extends FormatWriter implements Collector {
  protected FormatString fmtstr;
  protected int[] map = null;
  protected int columns = -1;
  private   Parameters p = new Parameters();

	public SimulationWriter(Writer out, FormatString fmtstr) {
    super(out);
    this.fmtstr = fmtstr;
	}

	public SimulationWriter(Writer out, String format, int columns) {
    super(out);
    this.columns = columns;
    fmtstr = createFormatString(format, columns);
	}

	public SimulationWriter(Writer out) {
    super(out);
    fmtstr = null;
  }

	public FormatString createFormatString(String format, int columns) {
    StringBuffer s = new StringBuffer();
    for (int k=columns; --k>=0;)
      s.append(format);
    s.append('\n');
    return compileFormatString(s.toString());
	}

	public void write(int[] value) throws IOException {
    if (columns == -1)
      columns = value.length;
		if (fmtstr == null)
      fmtstr = createFormatString("%d ", columns);
    for (int k=0; k<columns; k++)
      p.add(value[k]);
    try {
      write(fmtstr, p);
		} catch(hb.format.ParseException ex) {
			// Should never happens.
      ex.printStackTrace();
		}
	}

	public boolean add(int[] value) throws IOException {
		if (map != null) {
      if (columns == -1)
        columns = value.length;
      int[] mediator = new int[columns];
      for (int k=columns; --k>=0;)
        mediator[k] = value[map[k]];
      value = mediator;
		}
    write(value);
    return true;
	}

	public void setMap(int[] map) {
    this.map = map;
	}

	public boolean add(Object obj) {
		if (obj instanceof int[]) {
      try {
        write((int[])obj);
        return true;
	   	} catch(IOException ex) {
			  // Should never happens.
        ex.printStackTrace();
	  	}
		}

    return false;
	}
	public boolean addAll(Collection c) {
    Iterator it = c.iterator();
    while (it.hasNext()) {
      add(it.next());
		}
    return true;
	}
} // class SimulationWriter

/*
HISTORY

2000-06-30
* Created.
	
*/
