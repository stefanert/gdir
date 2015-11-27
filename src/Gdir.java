
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Gdir {
	
	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		
		int i = 0;
		String current_arg = "";
		
		// args verarbeiten
		
		// hier werden die optionen wie case folding usw. gespeichert
		ArrayList<String> options = new ArrayList<String>();
		
		if (args.length > 0)
		{
			for(i = 0; i <= args.length - 1; i++)
			{
				current_arg = args[i];
				current_arg = current_arg.substring(1);
				
				// cf = case folding
				// st = stemming
				// bw = bi word
				if
				(
					(current_arg.equals("cf")) ||
					(current_arg.equals("st")) ||
					(current_arg.equals("bw")) 
				)
				{
					options.add(current_arg);
				}
				else
				{
					System.out.println("usage: Gdir <path> <options>");
					System.out.println("options:");
					System.out.println("\tcf: use case folding");
					System.out.println("\tst: use stemming");
					System.out.println("\tbw: use bi-words instead of bag of words");
					return;
				}
			}
		}
		else
		{
			System.out.println("keine args");
			return;
		}
		
		String path_rel = "../resources/20_newsgroups_subset/";
		
		Index index = new Index(path_rel, options);
		index.init();
		
		System.out.println("+--------------------------------------------------");
		System.out.println("| Index has been created with the options: " + options);
		System.out.println("| Search for query: -q [query]");
		System.out.println("| Search for topic: -t [topic]");
		System.out.println("| Enter \"exit\" to quit");
		System.out.println("+--------------------------------------------------");
		
		String input = "";
		// wenn das aktuelle arg nicht mit einem "-" beginnt wird es uebersprungen
				// wenn "-query test"
				//~ if (false == current_arg.startsWith("-"))
				//~ {
					//~ System.out.print("contu");
					//~ continue;
				//~ }
		while(true)
		{
			System.out.print("> ");
            input = sc.nextLine();
            
            if (input.equals("exit"))
            {
				break;
			}
            
            System.out.println("Command: " + input);
        }
	}
}