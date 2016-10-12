package nl.remco.remcoproject;

public class FilePlugin {
	private Sha sha;
	int changePoints( )
	{
		sha.setPoints( sha.getPoints() + 1);
		return start("This way");
	}
	public FilePlugin()
	{
		sha= new Sha();
		sha.setPoints( 0);
	}
	private int start( String arg)
	{
		return 8;
	}
}
