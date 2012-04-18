package filmeUtils.torrentSites;

import filmeUtils.FilmeUtilsHttpClient;

public class TorrentSearcher {

	private final TorrentSite pirateBaySe;

	public TorrentSearcher(final FilmeUtilsHttpClient httpclient) {
		pirateBaySe = new PirateBaySe(httpclient);
	}
	
	public String getMagnetLinkForFileOrNull(final String exactFileName){
		return pirateBaySe.getMagnetLinkFirstResultOrNull(exactFileName);
	}
	
}
