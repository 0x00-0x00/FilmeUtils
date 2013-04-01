package filmeUtils;

import java.io.File;

public interface FilmeUtilsOptions {

	public String getUser();
	public String getPassword();
	public String subtitleRegex();
	public File getSubtitlesDestinationFolderOrNull();
	public boolean isVerbose();
	public boolean isGeedy();
	public boolean isLazy();

}
