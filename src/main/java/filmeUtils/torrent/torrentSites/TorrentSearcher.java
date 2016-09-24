package filmeUtils.torrent.torrentSites;

import java.util.Optional;

import filmeUtils.commons.OutputListener;

public interface TorrentSearcher {
    public Optional<String> getMagnetLinkForTerm(final String term, OutputListener outputListener);
}