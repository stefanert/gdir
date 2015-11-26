
public class Gdir {
	
	public static void main(String[] args) {
		
		System.out.println("Hello, gdir");
		
		if (args.length > 0)
		{
			System.out.println("args:");
			for(int i = 0; i <= args.length - 1; i++)
			{
				System.out.print(args[i].substring(1));
				System.out.print(", ");
			}
		}
		else
		{
			System.out.println("keine args");
		}
		System.out.println();
		
	}
	
}
