package filmeUtils;

public class Version {
	
	public static String getVersion(){
		return Version.class.getPackage().getImplementationVersion();
	}

}
