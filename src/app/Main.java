
package app;
import structures.Arc;
import structures.Graph;
import structures.PartialTree;
import structures.Vertex;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	static Scanner sc = new Scanner(System.in);

	
	public static void main(String[] args) throws IOException
	{
		System.out.println("Enter graph file");
		String graphFile = sc.nextLine();
		Graph graph = new Graph(graphFile);
		PartialTreeList list = PartialTreeList.initialize(graph);
		ArrayList<Arc> arcs = PartialTreeList.execute(list);
		
		int weight=0;
		for(Arc i:arcs)
		{
			System.out.println(i);
			weight+=i.getWeight();
		}
		System.out.println(weight);
	}
}
