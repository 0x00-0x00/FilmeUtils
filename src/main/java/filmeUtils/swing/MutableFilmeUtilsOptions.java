package filmeUtils.swing;

import java.io.File;

import filmeUtils.FilmeUtilsOptions;

public class MutableFilmeUtilsOptions implements FilmeUtilsOptions {

	private String subtitleRegex;
	private File subtitlesFolder;

	public String getUser() {
		throw new RuntimeException("Method not implemented");
	}

	public String getPassword() {
		throw new RuntimeException("Method not implemented");
	}

	public void setNormalResolution() {
		subtitleRegex = "^(720|1080)";
	}
	
	public void setHD() {
		subtitleRegex = "(720|1080)";
	}
	
	public void setShouldAcceptAll() {
		subtitleRegex = ".*";
	}

	public File getSubtitlesDestinationFolderOrNull() {
		return subtitlesFolder;
	}

	public boolean isVerbose() {
		throw new RuntimeException("Method not implemented");
	}

	public boolean isGeedy() {
		throw new RuntimeException("Method not implemented");
	}

	public boolean isLazy() {
		return true;
	}

	public void setSubtitlesDestinationFolder(final File subtitlesFolder) {
		this.subtitlesFolder = subtitlesFolder;
	}

	@Override
	public String subtitleRegex() {
		return subtitleRegex;
	}

}
