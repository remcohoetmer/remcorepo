package nl.remco.remcoproject;

public class FilePlugin {
	void changePoints( Sha sha)
	{
		sha.setPoints( sha.getPoints() + 1);
	}
}
