package filmeUtils.torrentSites;

import filmeUtils.SimpleHttpClient;

public class BitSnoop implements TorrentSite {

	private final SimpleHttpClient httpclient;
	private static final String BITSNOOP_SEARCH_URL = "http://bitsnoop.com/search/all/"+TorrentSiteUtils.SEARCH_TERM_TOKEN;
	
	public BitSnoop(final SimpleHttpClient httpclient) {
		this.httpclient = httpclient;
	}
	
	public String getMagnetLinkFirstResultOrNull(final String exactFileName) {
		final String url = TorrentSiteUtils.getUrlFor(BITSNOOP_SEARCH_URL,exactFileName);
		throw new RuntimeException("Method not implemented");
	}

}
