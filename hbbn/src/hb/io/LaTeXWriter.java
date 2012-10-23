package hb.io;

/*
 @author Henrik Bengtsson, hb@maths.lth.se
 */

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.io.OutputStreamWriter;
import hb.format.Format;
import hb.format.ArrayConversionParser;
import hb.format.FormatWriter;
import hb.format.Parameters;


public class LaTeXWriter extends FormatWriter {
  public LaTeXWriter(Writer out) {
    super(out);
    addConversionParser(new LaTeXConversionParser());
	}

	public static void main(String[] args) throws Exception {
    Writer out0 = new OutputStreamWriter(System.out);
    LaTeXWriter out = new LaTeXWriter(out0);
    out.write("This is plain text!\n");
    out.write("%% This is a comment!\n");
    out.write("%%LaTeX(%s) = '%LaTeX'\n",
      new Parameters("bar_\\_boo").add("bar_\\_boo"));
    out.write("\\newcommand{%[\\]LaTeX}{%[^_]LaTeX}\n",
      new Parameters("foo_123").add("bar__boo"));
    out.close();
	}
} // class LaTeXWriter


/* HISTORY:

2000-06-20
* Created.

*/
