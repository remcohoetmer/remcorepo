package nl.remco.remcoproject;

public class FilePlugin {
	private Sha sha;
	void changePoints( )
	{
		sha.setPoints( sha.getPoints() + 1);
		start("This way");
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
