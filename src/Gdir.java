
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class Gdir {
	
	public static void main(String[] args) {

		System.out.println	("+--------------------------------------------------\n" 	+
							 "| Hi! I am a simple search system.\n" 					+
							 "+--------------------------------------------------\n" 		+
							 "Let me check the path to your resources ... ");

		//user.dir gibt relativen working directory
		String path_rel = System.getProperty("user.dir") + "/resources/20_newsgroups_subset/";

		//C:\Users\Vede\Dropbox\Dokumente\Uni\TU\sem5\Grundlagen des Information Retrieval\gdir/resources/20_newsgroups_subset/

		Index index = new Index(path_rel);

		if (!index.handlePath().equals("ok")) return;

		System.out.print("Okay, let's proceed.\n" +
				"Now please choose your personal options.\n" +
				"Usage: [-cf] [-st] [-bw]\n" +
				"Type -cf if you want to use casefolding for this session!\n" +
				"Type -st if you want to use stemming for this session!\n" +
				"Type -bw if you want to use bi-words for this session!\n" +
				"Hit Enter if you don't want to use any options for this session!\n" +
				"> ");


		//Hier drinnen werden die Optionen -cf -bw etc. geprüft und gespeichert
		Options persOpt = new Options();
		persOpt.checkArguments();

		System.out.println("Thank you! Now I'm going to create a dictionary out of the resources your provided!\n" +
				"This might take a while. But worry not! I'm fast ...");

		//Hier wird der Index für die Token erstellt
		index.setOptions(persOpt);
		index.init();

		System.out.println("Ok, I'm done now! Let's proceed.");
		
		System.out.println("+--------------------------------------------------");
		System.out.println("| Index has been created with the options: " + persOpt.getInput());
		System.out.println("| Usage: [-f] -t|-q");
		System.out.println("| Search in a specific folder : -f [foldername]");
		System.out.println("| Search for topic: -t [topic]");
		System.out.println("| Search for a query: -q [query]");
		System.out.println("| For example: -f misc.folder -t topic1");
		System.out.println("+--------------------------------------------------");
		System.out.print("> ");
		

		//Hier findet die Suche statt
		while(true){
			Options searchOpt = new Options();
			String[] sQueryOpt = searchOpt.checkMode();

			String results = index.getDictionary().search(sQueryOpt, persOpt);
			if (results.equals("exit")){
				System.out.println("Bye bye!");
				break;
			}

			System.out.print(results + "\n> ");
		}

	}

}