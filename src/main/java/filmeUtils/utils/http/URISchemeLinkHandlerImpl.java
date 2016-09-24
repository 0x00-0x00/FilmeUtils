package filmeUtils.utils.http;

import java.net.URI;

import uriSchemeHandler.URISchemeHandler;

public class URISchemeLinkHandlerImpl implements URISchemeLinkHandler {

    private final URISchemeHandler urlProtocolHandler;

    public URISchemeLinkHandlerImpl() {
        urlProtocolHandler = new URISchemeHandler();
    }

    @Override
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