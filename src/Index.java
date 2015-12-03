
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Index {
	
	private String path;
	private Options option;
	private ArrayList<String> options;
	private Dictionary dictionary;

	public Index(){
	}

	public Index(String path)
	{

		this.path = path;
		this.options = new ArrayList<String>();
		this.dictionary = new Dictionary();
	}
	
	public Index(String path, Options options)
	{
		
		this.path = path;
		this.options = new ArrayList<String>();
		this.setOptions(options);
		this.dictionary = new Dictionary();
	}

	public Dictionary getDictionary(){
		return this.dictionary;
	}


	public boolean pathIsValid(){
		File check = new File(this.path);
		return check.listFiles() != null;
	}

	public void setPath(String path){
		this.path = path;
	}

	public void setOptions(Options options){

		option = options;

		String[] persOpt = options.getOptions();
		if(persOpt != null) {
			for (int i = 0; i < persOpt.length; ++i) {
				this.options.add(persOpt[i]);
			}
		}
	}

	public String handlePath(){
		if(!this.pathIsValid()) {
			Scanner sc = new Scanner(System.in);
			System.out.print("It appears as if your path is either wrong or the folders empty. Are you sure you want to proceed?\n\n" +
					"Type yes to continue!\n" +
					"Type exit to exit the program!\n" +
					"Type path to enter a new path!\n" +
					"> ");
			String howToProceed = sc.nextLine();
			if (howToProceed.equals("exit")) {
				System.out.println("Bye bye!");
				return "exit";
			} else if (howToProceed.equals("path")) {
				System.out.print("Enter your new path now: ...\n" +
						"> ");
				this.setPath(sc.nextLine());
				this.handlePath();
			} else if (howToProceed.equals("yes")){
				return "ok";
			} else {
				System.out.println("I don't know this command :( \n\n");
				this.handlePath();
			}
		}
		return "ok";
	}


	public void init()	{

		HashMap<File, String> allDocuments = new HashMap<File, String>();

		//Get all Documents in the Resource Folder
        File[] subDirectories = new File(this.path).listFiles();
        
        File[] documentsInDirectory = new File[0];
        for (int i = 0; i < subDirectories.length; ++i){
			documentsInDirectory = subDirectories[i].listFiles();
			for (int j = 0; j < documentsInDirectory.length; ++j){
				String parent = documentsInDirectory[j].getParent();
				parent = parent.substring(parent.lastIndexOf("\\") + 1, parent.length());
				allDocuments.put(documentsInDirectory[j], parent);
				this.dictionary.addDocument(documentsInDirectory[j]);
			}
		}

		//Index all words in those documents
		for (Map.Entry<File, String> entry : allDocuments.entrySet()) {
			index_file(entry.getKey(), entry.getValue());
		}

	}
	
	
	public void index_file(File file, String folder)
	{
		String doc_id = folder + "\\" +Integer.parseInt(file.getName());
		
		ArrayList<String> usefull_lines = get_usefull_lines(file);      // wir holen aus der datei alle brauchbaren zeilen.
		ArrayList<String> all_words     = get_all_words(usefull_lines); // die zeilen werden bei " " getrennt um die woerter zu bekommen

		// normalize words. die optionen aus dem cli werden hier verwendet.
		if(this.options.contains("-cf") || this.options.contains("-st"))	all_words = this.option.normalize(all_words);
		
		// wenn es bi word ist, dann wird das letzte wort nicht mehr eingelesen
		// (das letzte wort ist das zweite beim vorletzten)
		int bi_word_mod = this.options.contains("bw") ? 1 : 0;
		
		String current_term = "";

		for (int i = 0; i < all_words.size()-bi_word_mod; ++i)
		{
			current_term = all_words.get(i);
			
			// das naechste wort wird auch genommen
			if (bi_word_mod == 1) { current_term += " " + all_words.get(i+1); }
			
			Token temp = new Token(current_term);
			
			if ( ! dictionary.contains(temp)) {
				temp.addToPostinglist(doc_id);
				dictionary.addToDict(temp);
			} else dictionary.getToken(current_term).addToPostinglist(doc_id);
		}
	}
	
	// es wird alles aufgenommen ausser: header und leere zeilen
	public ArrayList<String> get_usefull_lines(File file)
	{
		ArrayList<String> usefull_lines = new ArrayList<String>();
		
		InputStream is;
		InputStreamReader isr;
		BufferedReader br;
		
		try
		{
			is = new FileInputStream(file);
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			
			boolean still_reading_header = true;
			
			for (String line = br.readLine(); line != null; line = br.readLine())
			{
				// wenn ich noch dabei bin den header zu lesen
				if (still_reading_header == true){
					// wenn in der zeile ein ": " vorkommt ist es
					// teil des headers und wird ignoriert
					if (line.contains(": "))	continue;
					// es wird davon ausgegangen, dass der header zu ende ist
					else	still_reading_header = false;
				}
				// leere zeielen werden uebersprungen
				if (line.equals("") == false)	usefull_lines.add(remove_garbage(line));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return usefull_lines;
	}
	
	// zeichen aus der zeile loeschen, die nicht beachtet werden.
	// das wird in der funktion get_usefull_lines aufgerufen,
	// wenn eine zeile zu den brauchbaren hinzugefuegt wird.
	
	public String remove_garbage(String line)
	{
		String[] garbage = {
			".",
			",",
			"?",
			"!",
			":",
			";",
			"-",
			"/",
			"\\",
			"'",
			"\"",
			"(",
			")",
			"[",
			"]",
			"{",
			"}",
			"~",
			"_",
			"*",
			"#",
			"`",
			"´",
			"^",
			"|",
			"+",
			"=",
			"$",
			"&",
			"%",
			"§",
			"<",
			">" // das wird auch fuer die zeilen verwendet auf die geantwortet wird
		};
		
		for (int i = 0; i < garbage.length; ++i)
		{
			line = line.replace(garbage[i], "");
		}
		
		return line;
	}
	
	// aus den zeilen einer datei werden die einzelnen woerter geholt
	public ArrayList<String> get_all_words(ArrayList<String> lines)
	{
		ArrayList<String> all_words = new ArrayList<String>();
		String[] line_words;
		
		for (int i = 0; i < lines.size(); ++i){
			line_words = lines.get(i).split(" ");
			
			for (int j = 0; j < line_words.length; ++j){
				// leere woerter werden nicht aufgenommen
				if (line_words[j].equals("") == false)	all_words.add(line_words[j].trim());
			}
		}
		
		return all_words;
	}
	
}