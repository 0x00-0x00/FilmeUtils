package filmeUtils;

public class Version {
	
	public static String getVersion(){
		final String implementationVersion = Version.class.getPackage().getImplementationVersion();
		if(implementationVersion == null)
			return "DEVELOPMENT";
		return implementationVersion;
	}

}
