package nl.remco.remcoproject;

public class Main {
	public static void main (String[] args)
	{
		FilePlugin fp= new FilePlugin();
		
		String encoded= new Sha().sha256( "secret");
		System.out.println("Encoded:"+ encoded);
		//return 0;
	}

}
