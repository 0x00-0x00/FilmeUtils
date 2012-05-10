package filmeUtils.subtitleSites;

import java.io.File;

import filmeUtils.FilmeUtilsOptions;

class ArgumentsParserMock implements FilmeUtilsOptions {

	public boolean isLazy;

	public String getUser() {
		throw new RuntimeException("Method not implemented");
	}

	public String getPassword() {
		throw new RuntimeException("Method not implemented");
	}

	public boolean shouldRefuseNonHD() {
		throw new RuntimeException("Method not implemented");
	}

	public boolean shouldRefuseHD() {
		throw new RuntimeException("Method not implemented");
	}

	public File getSubtitlesDestinationFolderOrNull() {
		throw new RuntimeException("Method not implemented");
	}

	public boolean isVerbose() {
		throw new RuntimeException("Method not implemented");
	}

	public boolean isGeedy() {
		throw new RuntimeException("Method not implemented");
	}

	public boolean isLazy() {
		return isLazy;
	}

}
