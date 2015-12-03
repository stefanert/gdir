
import java.util.ArrayList;
import java.util.HashMap;

public class Token {
	
	private String term;
	private HashMap<String, Integer> posting_list;
	
	public Token(String term)
	{
		this.term = term;
		this.posting_list = new HashMap<String, Integer>();
	}
	
	/* ob die id schon in der posting list ist.
	public boolean has_id(int id)
	{
		return this.posting_list.contains(id);
	}
	*/
	
	public void addToPostinglist(String id){
		// die id wird hinzugefuegt, wenn sie noch nicht enthalten ist, ansonsten die haeufigkeit erhoeht
		if (!this.posting_list.containsKey(id)){
			this.posting_list.put(id, 1);
		} else {
			this.posting_list.put(id, posting_list.get(id) + 1);
		}
	}
	
	public HashMap<String, Integer> get_posting_list()
	{
		return this.posting_list;
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
