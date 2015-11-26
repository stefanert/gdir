
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.File;
import java.util.ArrayList;

public class Index {
	
	private String path;
	private ArrayList<String> options;
	
	public Index()
	{
		
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
		
		System.out.println("========================================");
		
		ArrayList<String> usefull_lines = get_usefull_lines(all_files.get(0));
		
		for (int i = 0; i < usefull_lines.size(); ++i)
		{
			System.out.println(usefull_lines.get(i));
		}
		
		System.out.println("========================================");
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
	
}