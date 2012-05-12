package filmeUtils.http;

import java.net.URI;

import uriSchemelHandler.URISchemeHandler;


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