package hb.util;

/**
* Class comment: Static class that makes it easier to parse
* command line arguments.
*
* @author Henrik Bengtsson <henrik@cs.ucsb.edu>
*/


public class Arguments {
  static final private String emptyString = "";
  static String[] arguments;
  static boolean isSet = false;
  static int exitStatus = 1;

  private static void assertArguments() {
	 if( !isSet ) {
		System.out.println("Parameter: Error. setArguments() "+
								 "must first be called.");
		System.exit(10);
	 }
  }

  private static void printError(String errormsg) {
	 // Will only exits if there is an error message.
	 if (errormsg.length() > 0) {
	   System.err.println(errormsg);
	   System.exit(exitStatus);
	 }
  }

  private static String getValue(int pos) {
	 if (pos >= 0 && pos+1 < arguments.length) {
		String value = new String(arguments[pos+1]);
		//		if (value.charAt(0) != '-')
		  return value;
	 }

	 return "";
  }
  
  /**
   * Method comment: This method must be called before any other.
   */
  public static void set(String argv[]) {
	 arguments = argv;
	 isSet = true;
  }
  
  /**
   * Method comment: Set exit satus used when System.exit() is called.
	* Default is 1 (one).
   */
  public static void setExitStatus(int status) {
	 exitStatus = status;
  }
  
  /**
   * Method comment: Find position of given parameter. If it does not
   * exist -1 is returned.
   */
  public static int indexOf(String argument) {
	 assertArguments();
	 
	 for (int i=0; i<arguments.length; i++) {
		if ( arguments[i].equals(argument) )
		  return i;
	 }

	 return -1;
  }
	
  /**
   * Method comment: Return true if the given parameter is in argument
   * list, otherwise false.
   */
  public static boolean getFlag(String flag) {
	 return (indexOf(flag) >= 0);
  } // getFlag

  /**
   * Method comment: Assert that the flag exists. Otherwise an error
	* message will be printed and System.exit() will be called.
	*/
  public static void assertFlag(String option, String errormsg) {
	 if ( indexOf(option) == -1 ) {
		printError(errormsg);
	 }
  } // assertFlag
	
  /**
   * Method comment: Assert that the option and its following value
	* does exist. Otherwise an error message will be printed and
	* System.exit() will be called.
	*/
  public static void assertOption(String option, String errormsg) {
	 int pos = indexOf(option);
	 if ( getValue(pos).length() > 0 ) {
		printError(errormsg);
	 }
  } // assertOption
	
  /**
   * Method comment: Get value following option (as a String). If it
	* does not exist an empty string is returned.
	*/
  public static String getOption(String option) {
	 return getOption(option, null);
  } // getOption
	
  /**
   * Method comment: Get value following option (as a String). If it
	* does not exist an errormsg will be printed and the program will
	* exits with an System.exit()-call.
	*/
  public static String getOption(String option, String def) {
	 int pos = indexOf(option);
	 String value = getValue(pos);
	 if (value.length() > 0)
  	 return value;
	 else
	   return def;
  } // getOption
	
  /**
   * Method comment: Get value following option (as a int). If it
	* does not exist Integer.MIN_VALUE is returned.
   */
  public static int getOptionAsInt(String option) {
	 return getOptionAsInt(option, "");
  } // getOptionAsInt

  /**
   * Method comment: Get value following option (as a int). If it
	* does not exist or is a non-integer an errormsg will be printed
	* and the program will exits with an System.exit()-call.
   */
  public static int getOptionAsInt(String option, String errormsg) {
	 try {
		String value = getOption(option,errormsg);
	   return Integer.valueOf(value).intValue();
	 } catch( Exception e ) {
		printError(errormsg);
	   return Integer.MIN_VALUE;
	 }
  } // getOptionAsInt

  /**
   * Method comment: Get value following option (as a int). If it
	* does not exist or is a non-integer an errormsg will be printed
	* and the program will exits with an System.exit()-call.
   */
  public static int getOptionAsInt(String option, int defaultValue) {
	 try {
		String value = getOption(option);
    if (value == null)
      return defaultValue;
    return Integer.valueOf(value).intValue();
	 } catch(Exception e) {
	    throw new RuntimeException("Option "+option+" was followed by a valid integer!");
	 }
  } // getOptionAsInt

  /**
   * Method comment: Get value following option (as a double). If it
	* does not exist Double.MIN_VALUE is returned.
   */
  public static double getOptionAsDouble(String option) {
	 return getOptionAsDouble(option, "");
  } // getOptionAsDouble
  
  /**
   * Method comment: Get value following option (as a double). If it
	* does not exist or is a non-double an errormsg will be printed
	* and the program will exits with an System.exit()-call.
   */
  public static double getOptionAsDouble(String option, String errormsg) {
	 try {
		String value = getOption(option,errormsg);
	   return Double.valueOf(value).doubleValue();
	 } catch(Exception e) {
		printError(errormsg);
	   return Double.MIN_VALUE;
	 }
  } // getOptionAsDouble

  /**
	* Method comment: Return the last argument as a string. If there is
	* no such argument an empty string is returned.
	*/
  public static String getLastOption() {
	 if (arguments.length > 0)
	   return arguments[arguments.length-1];
	 else
		return "";
  }

  
  /**
	* Method comment: Return the last argument as a string. If there is
	* no such argument an errormsg will be printed and the program will
	* exits with an System.exit()-call.
	*/
  public static String getLastOption(String errormsg) {
	 String res = getLastOption();
	 if (res.length() == 0 && errormsg.length() > 0) {
		printError(errormsg);
	 }
	 
	 return res;
  }
  
} // Arguments


/*
  HISTORY:

  970516 Added method getOptionAsDouble()
 */

