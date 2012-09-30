package filmeUtils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class FilmeUtilsFolder {
	
	public static final File get(){
		final File filmeUtilsFolder = new File(System.getProperty("user.home"),".filmeUtils");
		if(!filmeUtilsFolder.exists()){
			filmeUtilsFolder.mkdir();
		}
		return filmeUtilsFolder;
	}
	
	public static final File getSubtitlesDestinationOrNull(){
		File filmeUtilsFolder = FilmeUtilsFolder.get();
		File file = new File(filmeUtilsFolder,"subtitlefolder");
		if(!file.exists()){
			return null;
		}
		String readFileToString;
		try {
			readFileToString = FileUtils.readFileToString(file);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		File subtitlesDestinationFolder = new File(readFileToString);
		if(subtitlesDestinationFolder!=null && subtitlesDestinationFolder.exists() && subtitlesDestinationFolder.isDirectory()){
			return subtitlesDestinationFolder;
		}
		return null;
	}

}
