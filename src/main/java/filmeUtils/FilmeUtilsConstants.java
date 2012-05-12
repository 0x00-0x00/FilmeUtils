package filmeUtils;

import java.io.File;

public class FilmeUtilsConstants {
	
	public static final File filmeUtilsFolder(){
		final File filmeUtilsFolder = new File(System.getProperty("user.home"),".filmeUtils");
		if(!filmeUtilsFolder.exists()){
			filmeUtilsFolder.mkdir();
		}
		return filmeUtilsFolder;
	}

}
