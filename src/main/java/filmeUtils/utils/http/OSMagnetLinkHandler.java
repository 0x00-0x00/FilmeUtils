package filmeUtils.utils.http;

import java.net.URI;

import uriSchemeHandler.URISchemeHandler;


public class OSMagnetLinkHandler implements MagnetLinkHandler {
	
	private final URISchemeHandler urlProtocolHandler;

	public OSMagnetLinkHandler() {
		urlProtocolHandler = new URISchemeHandler();
	}
	
	public void openURL(final String uriString) {
		URI uri;
		try {
			uri = new URI(uriString);
			urlProtocolHandler.open(uri);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

}