package filmeUtils.torrent.torrentSites;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class TorrentSiteUtils {

	public static String getUrlFor(final String searchUrl,final String searchTerm) {
		final String encoded;
		try {
			encoded = URLEncoder.encode(searchTerm,"UTF-8");
		} catch (final UnsupportedEncodingException e1) {
			throw new RuntimeException("Never gonna happen",e1);
		}
		return searchUrl.replace(SEARCH_TERM_TOKEN,encoded);
	}

	public static final String SEARCH_TERM_TOKEN = "{SEARCH_TERM}";

}
