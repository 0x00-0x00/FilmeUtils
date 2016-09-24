package filmeUtils.commons;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import filmeUtils.Debug;
import filmeUtils.utils.RegexUtils;

public class FileSystemUtils {

    private static final String FILME_UTILS_DEFAULT_FOLDER = ".filmeUtils";
    private static final String ALREADY_DOWNLOADED_FILE = "alreadyDownloaded";
    private static final String REGEX_FILE_WITH_PATTERNS_TO_DOWNLOAD = "downloadThis";
    private static final String SUBTITLE_FOLDER_CONFIG_FILE = "subtitlefolder";
    private static final String OPTIONS_FILE = "config";
    private static String filmeUtilsConfigDir = "";

    private static final String CONFIG_NEWER_PAGES_TO_SEARCH = "quantidade_de_paginas_de_novas_legendas_para_procurar";
    private static final String CONFIG_USER = "user";
    private static final String CONFIG_PASSWORD = "password";

    private static FileSystemUtils filmeUtilsFolder;

    public static final FileSystemUtils getInstance() {
        if (filmeUtilsFolder == null) {
            filmeUtilsFolder = new FileSystemUtils();
        }
        return filmeUtilsFolder;
    }

    private String saveToFile = null;

    public final File getSubtitlesDestination() {
        final File filmeUtilsFolder = getFolder();
        final File subtitleFolder = new File(filmeUtilsFolder, SUBTITLE_FOLDER_CONFIG_FILE);
        if (!subtitleFolder.exists()) {
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
        if (!subtitlesDestinationFolder.exists()) {
            throw new RuntimeException("Pasta não existe " + destinationFolderPath);
        }
        if (!subtitlesDestinationFolder.isDirectory()) {
            throw new RuntimeException("Não é uma pasta: " + destinationFolderPath);
        }
        return subtitlesDestinationFolder;
    }

    public File writeErrorFile(final Exception e) {
        final File errorFile = new File(getFolder(), Calendar.getInstance().getTimeInMillis() + ".error");
        try {
            FileUtils.writeStringToFile(errorFile, e.getMessage() + "\n" + e.getStackTrace());
        } catch (final IOException e1) {
            throw new RuntimeException(e1);
        }
        return errorFile;
    }

    public void setSubtitleDestinationFolder(final String absolutePath) {
        final File filmeUtilsFolder = getFolder();
        final File file = new File(filmeUtilsFolder, SUBTITLE_FOLDER_CONFIG_FILE);
        try {
            file.createNewFile();
            FileUtils.writeStringToFile(file, absolutePath);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getAlreadyDownloaded() {
        if (Debug.IS_DEBUG) {
            return new ArrayList<>();
        }
        try {
            return FileUtils.readLines(getFileContainingAlreadyDownloaded());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addAlreadyDownloaded(final String alreadyDownloadedFile) {
        Debug.log("Adding to already downloaded " + alreadyDownloadedFile);
        final File alreadyDownloadedFiles = getFileContainingAlreadyDownloaded();
        try {
            final boolean append = true;
            FileUtils.writeStringToFile(alreadyDownloadedFiles, "\n" + alreadyDownloadedFile, append);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getSubtitlesToDownloadPatterns() {
        if (Debug.IS_DEBUG) {
            final ArrayList<String> strings = new ArrayList<>();
            strings.add(".*Game.*thrones.*:.*720.*");
            return strings;
        }
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

    public static List<String> copyFilesMatchingRegexAndDeleteSourceDir(final File source, final File dest,
            final String regex) {
        final ArrayList<String> filesThatMatch = new ArrayList<>();

        final Iterator<File> iterateFiles = FileUtils.iterateFiles(source, new IOFileFilter() {
            @Override
            public boolean accept(final File file) {
                final String name = file.getName();
                return isNameValid(name);
            }

            @Override
            public boolean accept(final File dir, final String name) {
                return isNameValid(name);
            }

            private boolean isNameValid(final String name) {
                return FilenameUtils.getExtension(name).toLowerCase().equals("srt");
            }
        }, TrueFileFilter.INSTANCE);

        while (iterateFiles.hasNext()) {
            final File file = iterateFiles.next();
            try {
                final String subtitleFilename = file.getName();
                if (RegexUtils.matchesCaseInsensitive(subtitleFilename, regex)) {
                    filesThatMatch.add(file.getName());
                    FileUtils.copyFile(file, new File(dest, subtitleFilename));
                }
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            FileUtils.deleteDirectory(source);
        } catch (final IOException e) {
            /* don't really care, it's on temp */}
        return filesThatMatch;
    }

    private final File getFolder() {
        final File filmeUtilsFolder;
        if (filmeUtilsConfigDir.isEmpty()) {
            filmeUtilsFolder = new File(System.getProperty("user.home"), FILME_UTILS_DEFAULT_FOLDER);
        } else {
            filmeUtilsFolder = new File(filmeUtilsConfigDir);
        }

        if (!filmeUtilsFolder.exists()) {
            filmeUtilsFolder.mkdir();
        }
        return filmeUtilsFolder;
    }

    private File getRegexFileWithPatternsToDownload() {
        return new File(getFolder(), REGEX_FILE_WITH_PATTERNS_TO_DOWNLOAD);
    }

    private File getFileContainingAlreadyDownloaded() {
        final File fileContainingAlreadyDownloaded = new File(getFolder(), ALREADY_DOWNLOADED_FILE);
        if (!fileContainingAlreadyDownloaded.exists()) {
            try {
                fileContainingAlreadyDownloaded.createNewFile();
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
        return fileContainingAlreadyDownloaded;
    }

    private File getFileContainingOptions() {
        final File fileOptions = new File(getFolder(), OPTIONS_FILE);
        if (!fileOptions.exists()) {
            try {
                fileOptions.createNewFile();
                FileUtils.writeLines(fileOptions, Arrays.asList(new String[] { CONFIG_NEWER_PAGES_TO_SEARCH + "=10"

                }));
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
        return fileOptions;
    }

    public int newerPagesSearchCount() {
        final File fileContainingOptions = getFileContainingOptions();
        try {
            final Configuration config = new PropertiesConfiguration(fileContainingOptions);
            final String string = config.getString(CONFIG_NEWER_PAGES_TO_SEARCH);
            final int parseInt = Integer.parseInt(string);
            return parseInt;
        } catch (final ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public String user() {
        final File fileContainingOptions = getFileContainingOptions();
        try {
            final Configuration config = new PropertiesConfiguration(fileContainingOptions);
            final String user = config.getString(CONFIG_USER);
            if (user == null) {
                return "filmeutils";
            }
            return user;
        } catch (final ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public String password() {
        final File fileContainingOptions = getFileContainingOptions();
        try {
            final Configuration config = new PropertiesConfiguration(fileContainingOptions);
            final String passwd = config.getString(CONFIG_PASSWORD);
            if (passwd == null) {
                return "filmeutilsfilme";
            }
            return passwd;
        } catch (final ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public void setHome(final String homeDir) {
        filmeUtilsConfigDir = homeDir;
    }

    public void useSaveMagnetsToFileStrategy(final String path) {
        this.saveToFile = path;
    }

    public boolean shouldSaveToFile() {
        return saveToFile != null;
    }

    public File magnetsDestination() {
        return new File(saveToFile);
    }
}
