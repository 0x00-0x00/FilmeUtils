package filmeUtils.commons;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;

import filmeUtils.utils.RegexUtils;

public class FileSystemUtils {
	
	private static final String DEFAULT_USER = "filmeutils";
	private static final String USER_PASSWORD_SEPARATOR = "/";
	private static final String DEFAULT_PASSWORD = "filmeutilsfilme";
	private static final String ALREADY_DOWNLOADED_FILE = "alreadyDownloaded";
	private static final String USER_PASSWORD_FILE = "userPassword";
	private static final String REGEX_FILE_WITH_PATTERNS_TO_DOWNLOAD = "downloadThis";
	private static final String SERIALIZED_COOKIES_FILE = "cookies.serialized";
	private static final String SUBTITLE_FOLDER_CONFIG_FILE = "subtitlefolder";
	private static final String FILME_UTILS_FOLDER = ".filmeUtils";
	private static FileSystemUtils filmeUtilsFolder;
	
	public static final FileSystemUtils getInstance(){
		if(filmeUtilsFolder == null) filmeUtilsFolder = new FileSystemUtils();
		return filmeUtilsFolder;
	}
	
	public final File getSubtitlesDestination(){
		final File filmeUtilsFolder = getFolder();
		final File subtitleFolder = new File(filmeUtilsFolder,SUBTITLE_FOLDER_CONFIG_FILE);
		if(!subtitleFolder.exists()){
			try {
				subtitleFolder.createNewFile();
				FileUtils.writeStringToFile(subtitleFolder, System.getProperty("user.home"));
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}
		String destinationFolderPath;
		try {
			destinationFolderPath = FileUtils.readFileToString(subtitleFolder).trim();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		final File subtitlesDestinationFolder = new File(destinationFolderPath);
		if(!subtitlesDestinationFolder.exists()){
			throw new RuntimeException("Pasta não existe "+destinationFolderPath);
		}
		if(!subtitlesDestinationFolder.isDirectory()){
			throw new RuntimeException("Não é uma pasta: "+destinationFolderPath);
		}
		return subtitlesDestinationFolder;
	}

	public File getCookiesFile() {
		return new File(getFolder(),SERIALIZED_COOKIES_FILE);
	}

	public File writeErrorFile(final Exception e) {
		final File errorFile = new File(getFolder(), Calendar.getInstance().getTimeInMillis()+".error");
		try {
			FileUtils.writeStringToFile(errorFile, e.getMessage()+"\n"+e.getStackTrace());
		} catch (final IOException e1) {
			throw new RuntimeException(e1);
		}
		return errorFile;
	}

	public void setSubtitleDestinationFolder(final String absolutePath) {
		final File filmeUtilsFolder = getFolder();
		final File file = new File(filmeUtilsFolder,SUBTITLE_FOLDER_CONFIG_FILE);
		try {
			file.createNewFile();
			FileUtils.writeStringToFile(file, absolutePath);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public List<String> getAlreadyDownloaded() {
		try {
			return FileUtils.readLines(getFileContainingAlreadyDownloaded());
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void addAlreadyDownloaded(final String alreadyDownloadedFile) {
		final File alreadyDownloadedFiles = getFileContainingAlreadyDownloaded();
		try {
			final boolean append = true;
			FileUtils.writeStringToFile(alreadyDownloadedFiles, "\n"+alreadyDownloadedFile, append);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public List<String> getSubtitlesToDownloadPatterns() {
		final File file = getRegexFileWithPatternsToDownload();
		return getSubtitlesToDownloadPatterns(file);
	}

	public List<String> getSubtitlesToDownloadPatterns(final File file) {
		try {
			return FileUtils.readLines(file);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean subtitlesToDownloadPatternFileExists() {
		return getRegexFileWithPatternsToDownload().exists();
	}

	public String getRegexFileWithPatternsToDownloadPath() {
		return getRegexFileWithPatternsToDownload().getAbsolutePath();
	}

	public static List<String> copyFilesMatchingRegexAndDeleteSourceDir(final File source,final File dest, final String regex) {
		final ArrayList<String> filesThatMatch = new ArrayList<String>();
		final Iterator<File> iterateFiles = FileUtils.iterateFiles(source, new String[]{"srt"}, true);
		while(iterateFiles.hasNext()){
			final File file = iterateFiles.next();
			try {
				final String subtitleFilename = file.getName();
				if(RegexUtils.matchesCaseInsensitive(subtitleFilename, regex)){
					filesThatMatch.add(file.getName());
					FileUtils.copyFile(file, new File(dest,subtitleFilename));
				}
			}catch(final IOException e){throw new RuntimeException(e);}
		}
		try {
			FileUtils.deleteDirectory(source);
		}catch(final IOException e){/*don't really care*/}
		return filesThatMatch;
	}

	private final File getFolder(){
		final File filmeUtilsFolder = new File(System.getProperty("user.home"),FILME_UTILS_FOLDER);
		if(!filmeUtilsFolder.exists()){
			filmeUtilsFolder.mkdir();
		}
		return filmeUtilsFolder;
	}
	
	private File getRegexFileWithPatternsToDownload() {
		return new File(getFolder(),REGEX_FILE_WITH_PATTERNS_TO_DOWNLOAD);
	}

	private File getFileContainingAlreadyDownloaded() {
		final File fileContainingAlreadyDownloaded = new File(getFolder(),ALREADY_DOWNLOADED_FILE);
		if(!fileContainingAlreadyDownloaded.exists()){
			try {
				fileContainingAlreadyDownloaded.createNewFile();
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}
		return fileContainingAlreadyDownloaded;
	}

	public String getUser() {
		final String userPassword = getUserPassword();
		return userPassword.split(USER_PASSWORD_SEPARATOR)[0];
	}

	public String getPassword() {
		final String userPassword = getUserPassword();
		return userPassword.split(USER_PASSWORD_SEPARATOR)[1];
	}
	
	private String getUserPassword(){
		final File fileContainingUserAndPassword = new File(getFolder(),USER_PASSWORD_FILE);
		try {
			if(!fileContainingUserAndPassword.exists()){
				fileContainingUserAndPassword.createNewFile();
				FileUtils.writeStringToFile(fileContainingUserAndPassword, DEFAULT_USER+USER_PASSWORD_SEPARATOR+DEFAULT_PASSWORD);
			}
			return FileUtils.readFileToString(fileContainingUserAndPassword);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}


}
