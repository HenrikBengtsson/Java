package hb.io;

import hb.format.Format;
import hb.format.Parameters;
import hb.format.Conversion;
import hb.format.ConversionParser;
import java.io.IOException;
import hb.io.DataTypeReader;
import hb.format.FormatString;

/**
<P> 
  <font size=-1 color=red>[internal use only]</font>.
</P> 

    
@author Henrik Bengtsson, <A HREF="mailto:d93hb@efd.lth.se">d93hb@efd.lth.se</A>
 */
final public class LaTeXConversionParser extends ConversionParser {
  private LaTeXConversion conversion;
 
  public boolean triggedBy(char ch) {
    return (ch == '%');
  }
    
  public Conversion parse(DataTypeReader in) throws java.io.IOException {
    this.in = in;
    in.mark();
    
    if (in.readChar('%') != '%') {
      in.reset();
      return null;
    }
        
    conversion = new LaTeXConversion();

    if (in.readChar('[') == '[') {
      while (readOption());
		}

    if (readConversionType())
      in.unmark();
    else {
      in.reset();
      conversion = null;
    }

		//    System.err.println(conversion);
    return conversion;
  }

  private boolean readOption() throws IOException {

    if (in.readChar('\\') == '\\') {
      conversion.flags |= conversion.FLAG_COMMAND;
    } else if (in.readChar('@') == '@') {
      conversion.flags |= conversion.FLAG_DIGITS_TO_ALPHA;
    } else if (in.readChars("^_").equals("^_")) {
      conversion.flags |= conversion.FLAG_NO_UNDERSCORES;
    } else if (in.readChar(']') == ']')
      return false;
    return true;
  }

  private boolean readConversionType() throws IOException {
    return in.readChars("LaTeX").equals("LaTeX");
  }

  public static void main(String args[]) {
    ConversionParser cp = new LaTeXConversionParser();
    Format.out.addConversionParser(cp);
    
    String fmtstr = args[0]+"\n";
    Parameters p = new Parameters();
    for (int k=1; k<args.length; k++)
      p.add(args[k]);
    Format.printf(fmtstr, p);
  }

}

/* HISTORY

2000-06-20
* Created from PrintfConversionParser.

*/
