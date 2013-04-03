package filmeUtils.torrent.torrentSites;

import filmeUtils.commons.OutputListener;

public interface TorrentSearcher {

	public String getMagnetLinkForTermOrNull(final String term,OutputListener outputListener);

}