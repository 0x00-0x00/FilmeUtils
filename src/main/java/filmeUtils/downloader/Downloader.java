package filmeUtils.downloader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.ClientProtocolException;

import filmeUtils.commons.FileSystemUtils;
import filmeUtils.commons.OutputListener;
import filmeUtils.subtitle.Subtitle;
import filmeUtils.subtitle.subtitleSites.legendasTV.LegendasTv;
import filmeUtils.torrent.torrentSites.TorrentSearcher;
import filmeUtils.torrent.torrentSites.TorrentSearcherImpl;
import filmeUtils.utils.RegexForSubPackageAndSubFile;
import filmeUtils.utils.extraction.Extractor;
import filmeUtils.utils.http.FilmeUtilsHttpClient;
import filmeUtils.utils.http.URISchemeLinkHandlerImpl;

public class Downloader {

    private static final String ZIP = "zip";
    private static final String RAR = "rar";
    private final Extractor extract;
    private final LegendasTv legendasTv;
    private OutputListener outputListener;

    public Downloader(final Extractor extract, final LegendasTv legendasTv, final OutputListener outputListener) {
        this.legendasTv = legendasTv;
        this.extract = extract;
        this.outputListener = outputListener;
    }

    public void downloadFromNewest(final String zipRegex, final String subtitleRegex,
            final File subtitlesDestinationFolder) {
        final RegexForSubPackageAndSubFile regexForSubPackageAndSubFile = new RegexForSubPackageAndSubFile(zipRegex,
                subtitleRegex);
        this.downloadFromNewest(regexForSubPackageAndSubFile, subtitlesDestinationFolder);
    }

    public void downloadFromNewest(final RegexForSubPackageAndSubFile regex, final File destinantion) {
        final List<RegexForSubPackageAndSubFile> regexes = new ArrayList<>();
        regexes.add(regex);
        this.downloadFromNewest(regexes, destinantion);
    }

    public void downloadFromNewest(final List<RegexForSubPackageAndSubFile> regexes, final File destinantion) {
        this.downloadFromNewest(regexes, destinantion, new ArrayList<String>());
    }

    public void downloadFromNewest(final List<RegexForSubPackageAndSubFile> regexes, final File destinantion,
            final List<String> subtitlesPackagesToIgnore) {
        final Subtitle subtitle = this.getSubtitleDownloader();
        final File tmpFolder = this.createTempDir();
        final Map<String, List<String>> donloadedSubtitlesByPackageName = new LinkedHashMap<>();
        subtitle.downloadNewer(tmpFolder, regexes, subtitlesPackagesToIgnore, donloadedSubtitlesByPackageName);
        this.searchTorrentsForSubtitlesOnFolderAndCopySubtitlesThatHaveTorrentToDestFolderThenDeleteSourceFolder(
                tmpFolder, destinantion, donloadedSubtitlesByPackageName);
    }

    public void download(final String searchTerm, final File subtitlesDestinationFolder, final String subtitleRegex) {
        final Subtitle subtitle = this.getSubtitleDownloader();
        final File tmpFolder = this.createTempDir();
        subtitle.download(searchTerm, subtitleRegex, tmpFolder);
        final boolean success = this
                .searchTorrentsForSubtitlesOnFolderAndCopySubtitlesThatHaveTorrentToDestFolderThenDeleteSourceFolder(
                        tmpFolder, subtitlesDestinationFolder, null);
        if (!success) {
            this.outputListener.out("Nenhum torrent para nenhuma legenda encontrada");
        }
    }

    public boolean downloadWithKnownLink(final String name, final String link, final String subtitleRegex,
            final File subtitlesDestinationFolder) {
        final File temporaryFolder = this.createTempDir();
        this.downloadLinkToTempDir(name, link, temporaryFolder);
        final boolean success = this
                .searchTorrentsForSubtitlesOnFolderAndCopySubtitlesThatHaveTorrentToDestFolderThenDeleteSourceFolder(
                        temporaryFolder, subtitlesDestinationFolder, null);
        return success;
    }

    public void setOutputListener(final OutputListener outputListener) {
        this.outputListener = outputListener;
    }

    private Subtitle getSubtitleDownloader() {
        final Subtitle subtitle = new Subtitle(this.outputListener, this.legendasTv);
        return subtitle;
    }

    private File createTempDir() {
        File tmpFolder;
        try {
            tmpFolder = File.createTempFile("FilmeUtils", "FilmeUtils");
            tmpFolder.delete();
            tmpFolder.mkdir();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return tmpFolder;
    }

    private boolean searchTorrentsForSubtitlesOnFolderAndCopySubtitlesThatHaveTorrentToDestFolderThenDeleteSourceFolder(
            final File tmpFolder, final File subtitlesDestinationFolder,
            final Map<String, List<String>> donloadedSubtitlesByPackageName) {
        boolean success = false;
        final TorrentSearcher torrentSearcher = new TorrentSearcherImpl();
        final Iterator<File> iterateFiles = FileUtils.iterateFiles(tmpFolder, new String[] { "srt" }, true);
        while (iterateFiles.hasNext()) {
            final File subtitleFile = iterateFiles.next();
            final String filenameWithoutExtension = FilenameUtils.removeExtension(subtitleFile.getName());
            final Optional<String> maybeMagnetLinkForTerm = torrentSearcher
                    .getMagnetLinkForTerm(filenameWithoutExtension, this.outputListener);
            if (!maybeMagnetLinkForTerm.isPresent()) {
                this.outputListener.out("Legenda sem torrent não será salva " + filenameWithoutExtension);
            }

            if (maybeMagnetLinkForTerm.isPresent()) {
                if (donloadedSubtitlesByPackageName != null) {
                    donloadedSubtitlesByPackageName.forEach((packageName, subtileFileList) -> {
                        if (subtileFileList.contains(subtitleFile.getName())) {
                            FileSystemUtils.getInstance().addAlreadyDownloaded(packageName);
                        }
                    });
                }
                this.downloadMagnetLink(maybeMagnetLinkForTerm.get());
                try {
                    this.outputListener.out("Salvando legenda em " + subtitlesDestinationFolder.getAbsolutePath());
                    FileUtils.copyFile(subtitleFile, new File(subtitlesDestinationFolder, subtitleFile.getName()));
                    success = true;
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        try {
            FileUtils.deleteDirectory(tmpFolder);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return success;
    }

    private void downloadMagnetLink(final String magnetLink) {
        final FileSystemUtils instance = FileSystemUtils.getInstance();
        if (instance.shouldSaveToFile()) {
            try {
                final File magnetsDestination = instance.magnetsDestination();
                FileUtils.writeStringToFile(magnetsDestination, magnetLink + "\n", true);
                this.outputListener.outVerbose("Salvando magnet link : " + magnetLink);
                this.outputListener.outVerbose("em " + magnetsDestination.getAbsolutePath());
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            this.outputListener.outVerbose("Abrindo magnet link no cliente: " + magnetLink);
            new URISchemeLinkHandlerImpl().openURL(magnetLink);
        }
    }

    private void downloadLinkToTempDir(final String name, final String link, final File folder) {
        try {
            this.downloadLinkToFolder(link, folder);
        } catch (final Exception e) {
            this.outputListener.outVerbose("Erro fazendo download de legenda de " + link);
            final FileSystemUtils filmeUtilsFolder = FileSystemUtils.getInstance();
            final File errorFile = filmeUtilsFolder.writeErrorFile(e);
            this.outputListener.outVerbose("Mais detalhes em " + errorFile);
        }
    }

    private void downloadLinkToFolder(final String link, final File folder)
            throws IOException, ClientProtocolException, ZipException {
        final File destFile = File.createTempFile("filmeUtils", "filmeUtils");

        destFile.delete();
        final String filename = FilmeUtilsHttpClient.getToFile(link, destFile);
        this.extract(destFile, folder, filename);
        destFile.delete();
    }

    private void extract(final File compressedFile, final File destinationFolder, final String fileName)
            throws ZipException, IOException {
        this.outputListener.outVerbose("Extraindo legendas para " + destinationFolder.getAbsolutePath());
        final boolean isRar = fileName.toLowerCase().endsWith(RAR);
        final boolean isZip = fileName.toLowerCase().endsWith(ZIP);
        if (!isRar && !isZip) {
            throw new RuntimeException("Tipo desconhecido: " + fileName);
        }

        if (isRar) {
            this.extract.unrar(compressedFile, destinationFolder);
        }
        if (isZip) {
            this.extract.unzip(compressedFile, destinationFolder);
        }
        this.outputListener.outVerbose("Extraido com sucesso para " + destinationFolder.getAbsolutePath());
    }

}
