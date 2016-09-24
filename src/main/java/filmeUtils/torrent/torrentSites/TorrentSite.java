package filmeUtils.torrent.torrentSites;

import java.util.Optional;

public interface TorrentSite {
    public String getSiteName();

    public Optional<String> getMagnetLinkFirstResult(final String exactFileName) throws SiteOfflineException;
}