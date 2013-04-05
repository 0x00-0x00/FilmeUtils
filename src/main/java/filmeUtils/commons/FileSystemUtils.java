package filmeUtils.commons;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;

import filmeUtils.utils.RegexUtils;

public class FileSystemUtils {
	
	private static final String ALREADY_DOWNLOADED_FILE = "alreadyDownloaded";
	private static final String REGEX_FILE_WITH_PATTERNS_TO_DOWNLOAD = "downloadThis";
	private static final String SERIALIZED_COOKIES_FILE = "cookies.serialized";
	private static final String SUBTITLE_FOLDER_CONFIG_FILE = "subtitlefolder";
	private static final String FILME_UTILS_FOLDER = ".filmeUtils";
	private static FileSystemUtils filmeUtilsFolder;
	
	public static final FileSystemUtils getInstance(){
		if(filmeUtilsFolder == null) filmeUtilsFolder = new FileSystemUtils();
		return filmeUtilsFolder;
	}
	
	private final File getFolder(){
		final File filmeUtilsFolder = new File(System.getProperty("user.home"),FILME_UTILS_FOLDER);
		if(!filmeUtilsFolder.exists()){
			filmeUtilsFolder.mkdir();
		}
		return filmeUtilsFolder;
	}
	
	public final File getSubtitlesDestination(){
		File filmeUtilsFolder = getFolder();
		File subtitleFolder = new File(filmeUtilsFolder,SUBTITLE_FOLDER_CONFIG_FILE);
		if(!subtitleFolder.exists()){
			try {
				subtitleFolder.createNewFile();
				FileUtils.writeStringToFile(subtitleFolder, System.getProperty("user.home"));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		String SubtitleDestinationFolder;
		try {
			SubtitleDestinationFolder = FileUtils.readFileToString(subtitleFolder);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		File subtitlesDestinationFolder = new File(SubtitleDestinationFolder);
		if(subtitlesDestinationFolder.exists() && subtitlesDestinationFolder.isDirectory()){
			return subtitlesDestinationFolder;
		}
		throw new RuntimeException("Invalid subtitle destination folder "+SubtitleDestinationFolder);
	}

	public File getCookiesFile() {
		return new File(getFolder(),SERIALIZED_COOKIES_FILE);
	}

	public File writeErrorFile(Exception e) {
		File errorFile = new File(getFolder(), Calendar.getInstance().getTimeInMillis()+".error");
		try {
			FileUtils.writeStringToFile(errorFile, e.getMessage()+"\n"+e.getStackTrace());
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
		return errorFile;
	}

	public void setSubtitleDestinationFolder(String absolutePath) {
		final File filmeUtilsFolder = getFolder();
		final File file = new File(filmeUtilsFolder,SUBTITLE_FOLDER_CONFIG_FILE);
		try {
			file.createNewFile();
			FileUtils.writeStringToFile(file, absolutePath);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	private File getRegexFileWithPatternsToDownload() {
		return new File(getFolder(),REGEX_FILE_WITH_PATTERNS_TO_DOWNLOAD);
	}

	private File getFileContainingAlreadyDownloaded() {
		File fileContainingAlreadyDownloaded = new File(getFolder(),ALREADY_DOWNLOADED_FILE);
		if(!fileContainingAlreadyDownloaded.exists()){
			try {
				fileContainingAlreadyDownloaded.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return fileContainingAlreadyDownloaded;
	}

	public List<String> getAlreadyDownloaded() {
		try {
			return FileUtils.readLines(getFileContainingAlreadyDownloaded());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void addAlreadyDownloaded(String alreadyDownloadedFile) {
		File alreadyDownloadedFiles = getFileContainingAlreadyDownloaded();
		try {
			boolean append = true;
			FileUtils.writeStringToFile(alreadyDownloadedFiles, "\n"+alreadyDownloadedFile, append);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public List<String> getSubtitlesToDownloadPatterns() {
		File file = getRegexFileWithPatternsToDownload();
		return getSubtitlesToDownloadPatterns(file);
	}

	public List<String> getSubtitlesToDownloadPatterns(File file) {
		try {
			return FileUtils.readLines(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean subtitlesToDownloadPatternFileExists() {
		return getRegexFileWithPatternsToDownload().exists();
	}

	public String getRegexFileWithPatternsToDownloadPath() {
		return getRegexFileWithPatternsToDownload().getAbsolutePath();
	}

	public static void copyFilesMatchingRegexAndDeleteSourceDir(final File source,final File dest, final String regex) {
		final Iterator<File> iterateFiles = FileUtils.iterateFiles(source, new String[]{"srt"}, true);
		while(iterateFiles.hasNext()){
			final File file = iterateFiles.next();
			try {
				String subtitleFilename = file.getName();
				if(RegexUtils.matchesCaseInsensitive(subtitleFilename, regex)){
					FileUtils.copyFile(file, new File(dest,subtitleFilename));
				}
			}catch(IOException e){throw new RuntimeException(e);}
		}
		try {
			FileUtils.deleteDirectory(source);
		}catch(IOException e){/*don't really care*/}
	}

}
