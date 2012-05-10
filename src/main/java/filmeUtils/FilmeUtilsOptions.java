package filmeUtils;

import java.io.File;

public interface FilmeUtilsOptions {

	String getUser();
	String getPassword();
	boolean shouldRefuseNonHD();
	boolean shouldRefuseHD();
	File getSubtitlesDestinationFolderOrNull();
	boolean isVerbose();
	boolean isGeedy();
	boolean isLazy();

}
