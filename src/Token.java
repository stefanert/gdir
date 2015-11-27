
import java.util.ArrayList;

public class Token {
	
	private String term;
	private ArrayList<Integer> posting_list;
	
	public Token(String term)
	{
		this.term = term;
		this.posting_list = new ArrayList<Integer>();
	}
	
	// ob die id schon in der posting list ist.
	public boolean has_id(int id)
	{
		return this.posting_list.contains(id);
	}
	
	public void add_id(int id)
	{
		// die id wird nur hinzugefuegt, wenn sie ncoh nicht enthalten ist
		if (this.posting_list.contains(id) == false)
		{
			this.posting_list.add(id);
		}
	}
	
	public String get_term()
	{
		return this.term;
	}
	
	public void print()
	{
		System.out.println("token: " + this.term + " - " + this.posting_list);
	}
	
	// wird fuer das arraylist.contains benoetigt
	@Override
	public boolean equals(Object obj)
	{
		Token obj_token = (Token) obj;
		
		//~ System.out.println(this.term + " = " + obj_token.get_term());
		
		if (this.term.equals(obj_token.get_term()))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public int hashCode()
	{
		return term.hashCode();
	}
}
