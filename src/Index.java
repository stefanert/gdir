
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Index {
	
	private String path;
	private ArrayList<String> options;
	private HashMap<String, Token> tokens;
	
	public Index()
	{
		tokens = new HashMap<String, Token>();
	}
	
	public void init(String path, ArrayList<String> options)
	{
		// das mit dem pfad und den optionen vielleicht in public Index()
		this.path = path;
		this.options = options;
		
		// ordner und unterordner holen
		File directory_base = new File(this.path);
        File[] subdirectories = directory_base.listFiles();
        
        // arraylist wo alle datein gespeichert werden
        ArrayList<File> all_files = new ArrayList<File>();
        
        File[] current_dir_files;
        for (int i=0; i < subdirectories.length ; ++i)
		{
			// datein des aktuellen ordners
			current_dir_files = subdirectories[i].listFiles();
			
			// datein des aktuellen ordners durchwandern
			for (int j = 0; j < current_dir_files.length; ++j)
			{
				all_files.add(current_dir_files[j]);
			}
		}
		
		for (int i = 0; i < 50; ++i)
		{
			index_file(all_files.get(i));
		}
		
		//~ Token t1 = new Token("auto");
		//~ Token t2 = new Token("a");
		//~ tokens.put("auto", t1);
		//~ tokens.put("a", t2);
		//~ 
		
		System.out.println("========================================");
		
		int i = 0;
		for (Token value : tokens.values())
		{
			i++;
			value.print();
		}
		
		System.out.println(i);
		
		System.out.println("========================================");
	}
	
	
	public void index_file(File file)
	{
		int doc_id = Integer.parseInt(file.getName());
		
		System.out.println("file name: " + file.getName() );
		
		ArrayList<String> usefull_lines = get_usefull_lines(file);             // wir holen aus der datei alle brauchbaren zeilen.
		ArrayList<String> all_words     = get_all_words(usefull_lines);        // die zeilen werden bei " " getrennt um die woerter zu bekommen
		ArrayList<String> all_words_n   = normalize_words(all_words, options); // normalize words. die optionen aus dem cli werden hier verwendet.
		
		//~ ArrayList<Token> at = new ArrayList<Token>();
		
		// wenn es bi word ist, dann wird das letzte wort nicht mehr eingelesen
		// (das letzte wort ist das zweite beim vorletzten)
		int bi_word_mod = 0;
		if (options.contains("bw") == true)
		{
			bi_word_mod = 1;
		}
		
		String current_term = "";
		
		for (int i = 0; i < all_words_n.size()-bi_word_mod; ++i)
		{
			// 1. es wird ein token angelegt
			// 2. es wird ueberprueft ob der token in den tokens enthalten ist.
			// dabei wird token.term verglichen.
			// 3. wenn der token noch nicht vorhanden ist, wird er neu hinzugefuegt
			// 4. auf jeden fall wird die id der datei zu der posting list hinzugefuegt
			// ob die id schon in der posting list ist wird in der add_id funktion
			// ueberprueft
			
			current_term = all_words_n.get(i);
			
			if (options.contains("bw") == true)
			{
				current_term += "," + all_words_n.get(i+1); // das naechste wort wird auch genommen
			}
			
			Token temp = new Token(current_term);
			
			if (tokens.containsKey(current_term) == false)
			{
				temp.add_id(doc_id);
				tokens.put(current_term, temp);
			}
			else
			{
				tokens.get(current_term).add_id(doc_id);
			}
		}
	}
	
	// es wird alles aufgenommen ausser: header und leere zeilen
	// TODO: ueberlegen, ob hier alle zeielen geloescht werden ,die mit
	// ">" beginnen. also wo noch der text steht auf den geantwortet wird.
	
	public ArrayList<String> get_usefull_lines(File file)
	{
		ArrayList<String> usefull_lines = new ArrayList<String>();
		
		InputStream is = null; 
		InputStreamReader isr = null;
		BufferedReader br = null;
		
		try
		{
			is = new FileInputStream(file);
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			
			boolean still_reading_header = true;
			
			for (String line = br.readLine(); line != null; line = br.readLine())
			{
				// wenn ich noch dabei bin den header zu lesen
				if (still_reading_header == true)
				{
					// wenn in der zeile ein ": " vorkommt ist es
					// teil des headers und wird ignoriert
					if (line.contains(": "))
					{
						continue;
					}
					// es wird davon ausgegangen, dass der header zu ende ist
					else
					{
						still_reading_header = false;
					}
				}
				
				// die ueberpruefung kann eigentlich weggelassen werden
				if (still_reading_header == false)
				{
					// leere zeielen werden uebersprungen
					if (line.equals("") == false)
					{
						usefull_lines.add(remove_garbage(line));
					}
				}
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
			"+",
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
		
		for (int i = 0; i < lines.size(); ++i)
		{
			line_words = lines.get(i).split(" ");
			
			for (int j = 0; j < line_words.length; ++j)
			{
				// leere woerter werden nicht aufgenommen
				if (line_words[j].equals("") == false)
				{
					all_words.add(line_words[j].trim());
				}
			}
		}
		
		return all_words;
	}
	
	public ArrayList<String> normalize_words(ArrayList<String> words, ArrayList<String> options)
	{
		ArrayList<String> normalized_words = new ArrayList<String>();
		Stemmer st = new Stemmer();
		
		for (int i = 0; i < words.size(); ++i)
		{
			String normalized_word = words.get(i);
			
			if (options.contains("cf")) // case folding
			{
				normalized_word = normalized_word.toLowerCase();
			}
			
			if (options.contains("st")) // stemming
			{
				st.add(normalized_word.toCharArray(), normalized_word.length());
				st.stem();
				normalized_word = st.toString();
			}
			
			normalized_words.add(normalized_word);
		}
		
		return normalized_words;
	}
	
}