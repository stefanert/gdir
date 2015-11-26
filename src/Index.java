
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.File;
import java.util.ArrayList;

public class Index {
	
	private String path;
	private ArrayList<String> options;
	private ArrayList<Token> tokens;
	
	public Index()
	{
		tokens = new ArrayList<Token>();
	}
	
	public void init(String path, ArrayList<String> options)
	{
		// das mit dem pfad und den optionen vielleicht in public Indesx()
		this.path = path;
		this.options = options;
		
		System.out.println("INIT");
		
		System.out.println("path: " + this.path);
		System.out.println("options: ");
		for (int i = 0; i < this.options.size(); ++i)
		{
			System.out.println(this.options.get(i));
		}
		
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
		
		System.out.println("aaaa: " + all_files.get(0).getName() );
		
		// wir holen aus der datei alle brauchbaren zeilen.
		// hier wird zur zeit einfach die erste datei verwendet.
		ArrayList<String> usefull_lines = get_usefull_lines(all_files.get(0));
		
		// die zeilen werden bei " " getrennt und die woerter in eine
		// arraylist gesteckt. das ergebnis sind alle woerter der datei.
		ArrayList<String> all_words = get_all_words(usefull_lines);
		
		// normalize words. die optionen aus dem cli werden hier verwendet.
		ArrayList<String> all_words_normalized = normalize_words(all_words, options);
		
		//~ ArrayList<Token> at = new ArrayList<Token>();
		
		
		for (int i = 0; i < all_words_normalized.size(); ++i)
		{
			// 1. es wird ein token angelegt
			// 2. es wird ueberprueft ob der token in den tokens enthalten ist.
			// dabei wird token.term verglichen.
			// 3. wenn der token noch nicht vorhanden ist, wird er neu hinzugefuegt
			// 4. auf jeden fall wird die id der datei zu der posting list hinzugefuegt
			// ob die id schon in der posting list ist wird in der add_id funktion
			// ueberprueft
			
			Token temp = new Token(all_words_normalized.get(i));
			
			if (tokens.contains(temp) == false)
			{
				tokens.add(temp);
			}
			else
			{
				System.out.println("asdf");
			}
			
			
			//~ tokens.add(new Token(all_words_normalized.get(i)));
		}
		
		System.out.println("========================================");
		int i;
		for (i = 0; i < tokens.size(); ++i)
		{
			tokens.get(i).print();
		}
		System.out.println(i);
		
		System.out.println("========================================");
		//~ 
		//~ tokens.get(0).print();
		//~ 
		//~ if (tokens.get(0).has_id(10) == false)
		//~ {
			//~ tokens.get(0).add_id(10);
			//~ tokens.get(0).add_id(10);
			//~ tokens.get(0).add_id(10);
		//~ }
		//~ 
		//~ tokens.get(0).print();
		//~ 
		//~ Token t1 = new Token("auhto");
		//~ Token t2 = new Token("auto");
		//~ 
		//~ t2.add_id(12);
		//~ 
		//~ tokens.add(t1);
		//~ 
		
		//~ System.out.println(t1.equals(t2));
		//~ System.out.println(t2.equals(t1));
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
					all_words.add(line_words[j]);
				}
			}
		}
		
		return all_words;
	}
	
	public ArrayList<String> normalize_words(ArrayList<String> words, ArrayList<String> options)
	{
		ArrayList<String> normalized_words = new ArrayList<String>();
		
		if (options.contains("cf")) // case folding
		{
			for (int i = 0; i < words.size(); ++i)
			{
				normalized_words.add(words.get(i).toLowerCase());
			}
		}
		
		if (options.contains("st")) // stemming
		{
			
		}
		
		return normalized_words;
	}
	
}