package hb.io;

import hb.format.StringConversion;
import hb.lang.*;
import java.io.*;
/**
  "Welcome to %sConversion!", "\\LaTeX"
    "Welcome to \LaTeXConversion!"

  "\\newcommand{\\%LaTeX(command)}{%LaTeX(string)}", "foo_123", "bar_456"
    "\newcommand{fooBCD}{bar\_456}"

  "%LaTeX(integer)", (int)1230
    "BCDA"

  "$%s$", "P\{X_{3}=A\}"
    "P\{X_{3}=A\}"

  <font size=-1 color=blue>[JDK1.0.2 compatible]</font>
  <font size=-1 color=red>[internal use only]</font>.
</P> 
@author Henrik Bengtsson, <A HREF="mailto:d93hb@efd.lth.se">d93hb@efd.lth.se</A>
*/
final class LaTeXConversion extends StringConversion {
  private boolean voidParameter;
  // types
  public final static int TYPE_UNKNOWN =  0;
  public final static int TYPE_LATEX   =  1;

  public int type;
  // flags
  public static final int FLAG_NONE            = 0;
  public static final int FLAG_DIGITS_TO_ALPHA = 1<<2;
  public static final int FLAG_NO_UNDERSCORES  = 1<<3;
  public static final int FLAG_COMMAND         = 1<<4;
  public int flags;
  
  public LaTeXConversion() {
    type      = TYPE_LATEX;
    flags     = FLAG_NONE;
    voidParameter = false;
  }
   
  private String getTypeString() {
    String res; 
    switch (type) {
      case TYPE_LATEX : res = "LaTeX"; break;
      default         : res = "error";
    }
    return res;
  }

  private String getOptionString() {
    StringBuffer res = new StringBuffer(); 
    if (flags != FLAG_NONE)
      res.append("[");

    if ((flags & FLAG_COMMAND) != 0)
      res.append("\\");
    if ((flags & FLAG_NO_UNDERSCORES) != 0)
      res.append("^_");
    if ((flags & FLAG_DIGITS_TO_ALPHA) != 0)
      res.append("@");

    if (flags !=  FLAG_NONE)
      res.append("]");
    return res.toString();
  }
    
  public String toString() {
    StringBuffer s = new StringBuffer("%");
    s.append(getOptionString());
    s.append(getTypeString());
    return s.toString();
  }

  public int nbrOfParametersNeeded() {
    return 1;
  }
  
  public int[] parametersWanted() {
    int paramPos[] = new int[] {PARAMETER_NEXT};
    return paramPos;
  }
  
	/**
   Convert a string where all '_' are replaced by "\_".
   Rules: 
     "_" -> "\_"
   Example: "my_string\_" is converted to "my\_string\_".

   @return a converted String
	 */
	protected String convertUnderscores(String s) {
    StringBuffer sb = new StringBuffer(s);
    char lastCh = '\\';
    for (int i=0; i<sb.length(); i++) {
      char ch = sb.charAt(i);
      if (ch == '_' && lastCh != '\\') {
        sb.insert(i++,'\\');
			}
      lastCh = ch;
		}
    return sb.toString();
	}

	/**
   Remove all underscores.
   Example: "foo_123" is converted to "foo123".

   @return a converted String
	 */
	protected String removeUnderscores(String s) {
    StringBuffer sb = new StringBuffer(s);
    StringBuffer sb2 = new StringBuffer();
    for (int i=0; i<sb.length(); i++) {
      char ch = sb.charAt(i);
      if (ch != '_')
        sb2.append(ch);
		}
    return sb2.toString();
	}

	/**
   Converts a string containing digits into a string where the digits are
   replaced by letters.
   Example: "foo123" is converted to "fooBCD".

   @return a converted String
	 */
	protected String digitsToAlpha(String s) {
    StringBuffer sb = new StringBuffer(s);
    StringBuffer sb2 = new StringBuffer();
    for (int i=0; i<sb.length(); i++) {
      char ch = sb.charAt(i);
      if (Character.isDigit(ch)) {
        sb2.append((char)(ch-'0'+'A'));
			} else
        sb2.append(ch);
		}
    return sb2.toString();
	}


	/**
   Converts a string containing digits into a string where the digits are
   replaced by letters.
   Example: "foo123" is converted to "fooBCD".

   @return a converted String
	 */
	protected String toLaTeXCommand(String s) {
    StringBuffer sb = new StringBuffer(s);
    StringBuffer sb2 = new StringBuffer();
    for (int i=0; i<sb.length(); i++) {
      char ch = sb.charAt(i);
      if (Character.isLetter(ch))
        sb2.append(ch);
      else if (Character.isDigit(ch)) {
        sb2.append((char)(ch-'0'+'A'));
			}
		}
    return sb2.toString();
	}

	/**
   Convert a string into a "LaTeX-safe" string.

   @return a converted String
	 */
	protected String toLaTeXString(String s) {
    s = convertUnderscores(s);
    return s;
	}

/**
 The parameters in the vector are converted according to 
 the format-string and then returned as a string
 <font size=-1 color=gray>[...]</font>.
 The number of conversion 
 specifications must match the number of parameters. If not, null is returned.
 @return The converted string if possible, otherwise null is returned.
 */
  public String toString(Object obj[]) {
    if (type != TYPE_LATEX)
      return null;
    Object param = obj[0];
    if (param == null)
      param = "null";
    String s = param.toString();
    if ((flags & FLAG_COMMAND) != 0) {
      s = toLaTeXCommand(s);
    }
    if ((flags & FLAG_NO_UNDERSCORES) != 0) {
      s = removeUnderscores(s);
    }
    if ((flags & FLAG_DIGITS_TO_ALPHA) != 0) {
      s = digitsToAlpha(s);
    }
    s = toLaTeXString(s);
    return s;
  }
  
  
  public static void main(String args[]) {
    LaTeXConversion conversion = new LaTeXConversion();
    int n = 1;
   
    String res;
    Object [] obj = new Object[n];
    String t = args[args.length-2];
    if (t.equalsIgnoreCase("%LaTeX")) {
      obj[0] = args[args.length-1];
    } else if (t.equalsIgnoreCase("%[\\]LaTeX")) {
      conversion.flags |= FLAG_COMMAND;
      obj[0] = args[args.length-1];
    } else {
      conversion.type = TYPE_UNKNOWN;
		}

    System.out.println("'"+conversion.toString()+"'");
    res = conversion.toString(obj);
    System.out.println("'"+res+"'");
  }  
}

/*
  HISTORY:
2000-06-10
* Created from PrintfConversion.
*/
