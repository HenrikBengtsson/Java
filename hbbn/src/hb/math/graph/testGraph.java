import java.util.Collection;
import java.util.Iterator;

import hb.math.graph.Graph;
import hb.math.graph.Vertex;
import hb.math.graph.Sepset;
import hb.math.graph.Cluster;
import hb.math.graph.Edge;
import hb.math.graph.JunctionTree;
import hb.text.TextArea;
import hb.format.Format;
import hb.format.Parameters;


public class testGraph {
  static Parameters p = new Parameters();
  static TextArea screen = new TextArea();

	private static Graph loadGraph(int nbr) {
    Graph G = new Graph();
    String V = "", E = V;
    int xscale=5,yscale=5, xoffset=1,yoffset=1;
    switch (nbr) {
  		case  1: 
        V="["+
                     "A {x=2,y=1},"+
      "B {x=1,y=2},"+               "C {x=3,y=2},"+    "G {x=4,y=2},"+
      "D {x=1,y=3},"+               "E {x=3,y=3},"+    "H {x=4,y=3},"+
                     "F {x=2,y=4},"+
          "]";
        E="["+
      "(A->B),(A->C),(B->D),(C->E),(C->G),(D->F),(E->F),(E->H),(G->H)"+
          "]";
        break;
  		case  2: 
        V="["+
                     "A {x=3,y=1},"+
      "B {x=1,y=2},"+"C {x=3,y=2},"+"D {x=5,y=2},"+
      "E {x=1,y=3},"+"F {x=3,y=3},"+"G {x=5,y=3},"+
            "H {x=2,y=4},"+"I {x=4,y=4},"+
          "]";
        E="["+
      "(A->B),(A->C),(A->D),(B->E),(C->F),(D->G),(E->H),(F->H),(F->I),(G->I)"+
          "]";
        break;
  		case  3: 
        V="["+
      "undershorts {x=0,y=0},"+
      "pants {x=0,y=1},"+
      "belt {x=0,y=2},"+
      "shirt {x=1,y=2},"+
      "tie {x=1,y=3},"+
      "jacket {x=0,y=3},"+
      "socks {x=2,y=0},"+
      "shoes {x=1,y=1},"+
      "watch {x=2,y=1}"+
          "]";
        E="["+
      "(undershorts->pants),(undershorts->shoes),(pants->belt),"+
      "(belt->jacket),(shirt->belt),(shirt->tie),(tie->jacket),"+
      "(socks->shoes)"+
           "]";
        xscale=12;yscale=4; xoffset=5;yoffset=5;
        break;
  		case  4: 
        V="["+
      "a {x=0,y=0},"+"b {x=1,y=0},"+"c {x=2,y=0},"+"d {x=3,y=0},"+
      "e {x=0,y=1},"+"f {x=1,y=1},"+"g {x=2,y=1},"+"h {x=3,y=1},"+
          "]";
        E="["+
      "(a->b),(b->c),(b->e),(b->f),(c-d),(c->g),"+
      "(d->h),(e->a),(e->f),(f-g),(g->h),(h->h)"+
           "]";
        break;
  		case  41: 
        V="["+
      "c {x=2,y=0},"+"g {x=2,y=1},"+"f {x=1,y=1},"+"h {x=3,y=1},"+
      "d {x=3,y=0},"+"b {x=1,y=0},"+"e {x=0,y=1},"+"a {x=0,y=0},"+
          "]";
        E="["+
      "(a->b),(b->c),(b->e),(b->f),(c->g),(c-d),"+
      "(d->h),(e->a),(e->f),(f-g),(g->h),(h->h)"+
           "]";
        break;
  		case  5: 
    		V = "["+
   				"u{x=1,y=1}, v{x=2,y=1}, w{x=3,y=1},"+
   				"x{x=1,y=2}, y{x=2,y=2}, z{x=3,y=2}"+
    				"]";
    		E = "["+
  				"(u->v),(u->x),(v->y),(w->y),(w->z),(x->v),(y->x),(z->z)"+
    				"]";
 				break;
  		case  6: 
    		V = "["+
   				"            A{x=2,y=1},             B{x=4,y=1},"+
   				"D{x=1,y=2},             C{x=3,y=2},"+
   				"            E{x=2,y=3},             F{x=4,y=3},"+
   				"G{x=1,y=4},             H{x=3,y=4},"+
    				"]";
    		E = "["+
  				"(A->C),(B->C),(D->E),(C->E),(C->F),(E->G),(E->H)"+
    				"]";
 				break;
  		case  7: 
        V="["+
                     "A {x=2,y=1},"+
      "B {x=1,y=2},"+               "C {x=3,y=2},"+
      "D {x=1,y=3},"+               "E {x=3,y=3},"+
                     "F {x=2,y=4},"+
          "]";
        E="["+
      "(A->B),(A->C),(B->D),(C->E),(D->F),(E->F)"+
          "]";
        break;
  		case  8: 
        V="["+
      "r {x=1,y=1}, s {x=2,y=1}, t {x=3,y=1}, u {x=4,y=1},"+
      "v {x=1,y=2}, w {x=2,y=2}, x {x=3,y=2}, y {x=4,y=2},"+
          "]";
        E="["+
      "(r-s), (r-v), (s-w), (t-u), (t-w), (t-x), (u-y), (w-x), (x-y)"+
          "]";
        break;
  		case  9: 
        V="["+
      "y {x=1,y=1}, z {x=2,y=1}, s {x=3,y=1}, t {x=4,y=1},"+
      "x {x=1,y=2}, w {x=2,y=2}, v {x=3,y=2}, u {x=4,y=2},"+
          "]";
        E="["+
      "(y->x),(z->y),(z->w),(s->z),(s->w),(t->v),(t->u),"+
      "(x->z),(w->x),(v->w),(v->s),(u->v),(u->t)"+
          "]";
        break;
		case  91:
        V="["+
      "s {x=3,y=1}, t {x=4,y=1}, u {x=4,y=2}, v {x=3,y=2}, "+
      "w {x=2,y=2}, x {x=1,y=2}, y {x=1,y=1}, z {x=2,y=1}, "+
          "]";
        E="["+
      "(y->x),(z->y),(z->w),(s->z),(s->w),(t->v),(t->u),"+
      "(x->z),(w->x),(v->w),(v->s),(u->v),(u->t)"+
          "]";
        break;
 		}

    Collection vs = Vertex.createVertices(V);
		G.setVertices(vs);
		Collection es = Edge.createEdges(G,E);
		G.addEdges(es);
    G.scaleAndOffset(yoffset,xoffset, yscale,xscale);
    return G;
	}

	private static void testBFS() throws Exception {
	  Format.printf("testBFS...\n");
    screen.clear();

		Graph G = loadGraph(8);
	  Format.printf("Graph G=G(V,E) where %s\n", p.add(G));
		Vertex u = G.getVertex("s");
    screen.pasteArea( 0,  0, G.toTextArea());
		G.BFS(u);
    screen.pasteArea( 0, 30, G.toTextArea());
    Format.printf(screen.copyMinimized().toString());
    Iterator it = G.iterator();
    while (it.hasNext()) {
      Vertex v = (Vertex)it.next();
		  Format.printf("dmin(%s,%s)=%d : %s\n",
			  p.add(u).add(v).add(v.distance).add(G.shortestPath(u,v)));
    }
	}
	
	private static void testDFS(int model) throws Exception {
	  Format.printf("testDFS...\n");
	  Parameters p = new Parameters();

		Graph G = loadGraph(model);

    TextArea tm1 =G.toTextArea();
    Collection c = G.DFS();
		Format.printf("G.DFS(): %s (size=%d)\n", p.add(c).add(c.size()));
    TextArea tm2 =G.toTextArea();
    screen.clear();
    screen.pasteArea( 0, 0, tm1);
    screen.pasteArea( 0,30, tm2);
    Format.printf(screen.copyMinimized().toString());

    Format.printf("                  1        2         3\n");
    Format.printf("  t0/t1: 12345678901235678901234567890\n");
    Format.printf("--------------------------------------\n");
    Iterator it = G.iterator();
    while (it.hasNext()) {
      Vertex v = (Vertex)it.next();
      int t0 = v.getTimeFound();
      int t1 = v.getTimeDone();
      int tlen = v.getTimeLength();
      Format.printf("%s %2d/%2d:%*c%.*s\n", 
        p.add(v).add(t0).add(t1).add(t0).add(' ')
         .add(tlen).add("#################################"));
		}
	}

	private static void testTranspose(int model) throws Exception {
	  Format.printf("testTranspose...\n");
	  Parameters p = new Parameters();
    TextArea screen = new TextArea();

		Graph G = loadGraph(model);
    Graph GT = new Graph(G).transpose();
    screen.clear();
    screen.pasteArea( 0,  0, G.toTextArea());
    screen.pasteArea( 0, 30, GT.toTextArea());
    Format.printf(G.toString(11));
    Format.printf(screen.copyMinimized().toString());
    Format.printf(GT.toString(11));
	}
	
	private static void testTopologicalSort() {
	  Format.printf("testTopologicalSort...\n");
	  Parameters p = new Parameters();
    TextArea screen = new TextArea();
    
		Graph G = loadGraph(03);

		G.topologicalSort();

    TextArea tm1 =G.toTextArea();
    screen.clear();
    screen.pasteArea( 0, 0, tm1);
    Format.printf(screen.copyMinimized().toString());
	  Format.printf("Topological sort: %s\n", p.add(G.getVertices()));
	}
	
	private static void testSCC(int model) throws Exception {
 	  Format.printf("testSCC...\n");
	  Parameters p = new Parameters();
    TextArea screen = new TextArea();

		Graph G = loadGraph(model);

    screen.clear();
    screen.pasteArea( 0, 0, G.toTextArea());
 
    Graph GSCC = G.SCCGraph();
		//    Collection c = GSCC.SCC();
		//Format.printf("G.SCC(): %s\n", p.add(c));
    screen.pasteArea( 0,40, GSCC.toTextArea());

    int tmax = 2*G.size();
    TextArea ta = new TextArea(tmax,tmax);
    Object[] o = G.toArray();
    for (int t=1; t<=tmax; t++) {
      for (int i=0; i<o.length; i++) {
				Vertex v = (Vertex)o[i];
        if (v.getTimeFound() == t) {
          ta.drawLine(i,t+2, i,t+v.getTimeLength()+1, '#');
          ta.put(i,0, v.name+":");
          break;
        }
      }
		}
    Format.printf("\n");
    screen.pasteArea(10,0, ta);
    Format.printf(screen.copyMinimized().toString());

		Format.printf("G is %ccyclic, ", p.add(G.isCyclic()?'\0':'a'));
		Format.printf("GSCC is %ccyclic\n\n", p.add(GSCC.isCyclic()?'\0':'a'));
	}

	private static void testClone() throws Exception {
	  Format.printf("testClone...\n");
	  Parameters p = new Parameters();
    TextArea screen = new TextArea();

		Graph G = loadGraph(04);
		//		Format.printf("G=%s\n", new Parameters(G.toString(99)));
    Graph Gclone = (Graph)G.clone();
    screen.clear();
		screen.pasteArea( 0,  0, G.toTextArea());
		screen.pasteArea( 0, 30, Gclone.toTextArea());
    Format.printf(screen.copyMinimized().toString());
	}

	private static void testMoralize() throws Exception {
	  Format.printf("testMoralize...\n");
	  Parameters p = new Parameters();

		Graph G = loadGraph(02);

    TextArea tm1 =G.toTextArea();
		Collection newEdges = G.moralize();
    TextArea tm2 =G.toTextArea();
    TextArea screen = new TextArea();
    screen.pasteArea( 0, 0, tm1);
    screen.pasteArea( 0,30, tm2);
    Format.printf(screen.copyMinimized().toString());
	  Format.printf("Moralized by adding edges: %s\n", p.add(newEdges));
	}



	private static void testTriangulate(int graph) throws Exception {
	  Format.printf("testTriangulate...\n");
    TextArea screen = new TextArea(100,100);

		Graph G = loadGraph(graph);
    Graph Gm = (Graph)G.clone();
		Collection newEdges = Gm.moralize();
    Graph Gt = (Graph)Gm.clone();
    Collection cliques = Gt.triangulate();
    screen.clear();
    screen.pasteArea( 0,  0, G.toTextArea());
    screen.pasteArea( 0, 30, Gm.toTextArea());
    screen.pasteArea( 0, 60, Gt.toTextArea());
 		Format.printf(screen.copyMinimized().toString());
	}

	private static void testJunctionTree(int graph) throws Exception {
	  Format.printf("testJunctionTree...\n");
	  Parameters p = new Parameters();
    TextArea screen = new TextArea(100,100);

		Graph G = loadGraph(graph);
    JunctionTree junctionTree = JunctionTree.asJunctionTree(G);
    junctionTree.rescale(1.3,1.6);
    screen.clear();
    screen.pasteArea( 0, 0, G.toTextArea());
    screen.pasteArea( 0, 35, junctionTree.toTextArea());
 		Format.printf(screen.copyMinimized().toString());
    Format.printf("G=%s\n", new Parameters(junctionTree));
    Iterator it = G.iterator();
    while (it.hasNext()) {
      Vertex u = (Vertex)it.next();
      Cluster X = junctionTree.getFamilyCluster(u);
      Format.printf("u->X : %s->%s\n", new Parameters(u).add(X));
      Cluster pau = new Cluster(G.getParents(u));
      // phi(X) = phi(X)*P(V|pa(V))
    }
	}


	public static void main(String args[]) throws Exception {
/*
	  testTranspose(4);
	  testBFS();
	  testDFS(9);
	  testDFS(91);
   	testSCC(4);
   	testSCC(41);
    testMoralize();
    testTriangulate(01);
	  testTriangulate(02);
	  testTriangulate(06);
	  testTriangulate(07);
	*/
    testJunctionTree(05);
    testJunctionTree(06);
    testJunctionTree(07);
	}
} // class Graph

/*
HISTORY

990325
  Have now implemented the basic operations required to do a triangulation.
990311
  Created.
	
*/
