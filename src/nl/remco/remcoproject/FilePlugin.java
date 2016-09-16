package nl.remco.remcoproject;

public class FilePlugin {
	private Sha sha;
	void changePoints( )
	{
		sha.setPoints( sha.getPoints() + 1);
	}
	public FilePlugin()
	{
		sha= new Sha();
		sha.setPoints( 0);
	}
}
