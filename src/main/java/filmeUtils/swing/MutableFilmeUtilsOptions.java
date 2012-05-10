package filmeUtils.swing;

import java.io.File;

import filmeUtils.FilmeUtilsOptions;

public class MutableFilmeUtilsOptions implements FilmeUtilsOptions {

	private boolean shouldRefuseHD;
	private boolean shouldRefuseNonHD;
	private File subtitlesFolder;

	public String getUser() {
		throw new RuntimeException("Method not implemented");
	}

	public String getPassword() {
		throw new RuntimeException("Method not implemented");
	}

	public boolean shouldRefuseNonHD() {
		return shouldRefuseNonHD;
	}
	
	public void setShouldRefuseHD(final boolean shouldRefuseHD) {
		this.shouldRefuseHD = shouldRefuseHD;
	}
	
	public void setShouldRefuseNonHD(final boolean shouldRefuseNonHD) {
		this.shouldRefuseNonHD = shouldRefuseNonHD;
	}

	public boolean shouldRefuseHD() {
		return this.shouldRefuseHD;
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


}
