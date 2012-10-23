HBBN
----
Copyright ©1999,2000 Henrik Bengtsson (hb@maths.lth.se)

HBBN is a Java based Bayesian Network enginee for inference.
It does not contain anything about learning, but mostly 
reading XML-based Bayes Nets, transforming them with graph
theory into Junction Tree which allows inference. Graphics
are automatically generated as PSTricks and LaTeX code.
No graphical user interface is available. Richly document
Javadoc API exists. Several example networks are available.

Reference:
[1] H. Bengtsson, Bayesian Networks - a self-contained 
    introduction with implementation remarks, Master's Thesis 
    in Computer Science, Mathematical Statistics, Lund Institute
    of Technology, 1999. For BibTeX see below.


Requirements:
-------------
1. xml4j (~2Mb) from IBM AlphaWorks, 2000. 
   Downloadable from http://www.alphaworks.ibm.com/tech/xml4j/

   Commment May 2004: Much have happend since 2000 and 
   you will most likely find that the hbbn code is not 
   compatible with any new XML parsers. 

2. Henrik Bengtsson's Java scanf downloadable from 
   http://www.braju.com/. There beta version is what you need.
   
   Comment May 2004: In the hbbn source code the Java printf/scanf
   methods were defined in the hb.format.* packages. Since then
   these methods are now (renamed to be) in the Java printf/scanf
   package com.braju.format.*. Thus, all occurance of hb.format.*
   in the source should be replace by com.braju.format.*.

3. In order to use the makefile.jmk to compile the whole package
   you need Java make (JMK) from http://jmk.sourceforge.net/.
   Comment May 2004: It may also have been updated such that
   the makefile is not compatible anymore.

Note May 2004: I have not maintained the HBBN package for a *long*
time now and it will not occur either. I have not compiled/build
the package myself since mid 2000. Thus, you have to do the
necessary modifications yourself in order to get it up and running
again. However, you should now that the code itself it reliant and
the major problem is make it compatible with the XSL/XML parsers.
If you do not need to be able to read in Graphical Models described
in XML, but define them from the API (see the javadocs) you much
better off. 

The package has many hidden features such as text-based graphics for
debugging the graphical operations etc. Methods are also available
to create graphs automatically in LaTeX and PSTricks which are 
directly insertable to your LaTeX documents. Most graphs you have
seen in the paper [1] have been automatically generated.

Contributions: If you are serious about using this package you
are more than welcome to take over the maintainance. That would
be really great! I would the suggest a sourceforge.net project
or similar. However, I will not have much time to spend on it
myself.
Note that in R (http://www.r-project.org/) there is an effort of
making graphical model enginees available under open source. 
You should be aware of that before working too much on this
package.


Citation:
Please site

@MastersThesis{HBBN_1999,
  author      = "Henrik Bengtsson",
  title       = "Bayesian Networks - a self-contained introduction with
                 implementation remarks",
  school      = "Mathematical Statistics, Lund Institute of Technology",
  address     = "Box 118, SE-22100 Lund, Sweden",
  year        =  1999,
  month	      =  sep,
  keywords    = "Bayesian networks, belief networks, junction tree algorithm,
                 probabilistic inference, probability propagation, reasoning
                 under uncertainty"
}


Cheers

Henrik Bengtsson
May 2004
