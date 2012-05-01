package filmeUtils.subtitleSites;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.ClientProtocolException;

import filmeUtils.http.SimpleHttpClient;

class SimpleHttpClientMock implements SimpleHttpClient {

	private final Map<String, String> responseForUrl = new LinkedHashMap<String, String>();

	public String getToFile(final String link, final File destFile)
			throws ClientProtocolException, IOException {
		throw new RuntimeException("Method not implemented");
	}

	public String getOrNull(final String url) {
		throw new RuntimeException("Method not implemented");
	}

	public String post(final String postUrl, final Map<String, String> params) throws ClientProtocolException, IOException {
		final String response = responseForUrl.get(postUrl);
		if(response == null){
			throw new RuntimeException("Response not found for url: "+postUrl);
		}
		return response;
	}

	public String get(final String get) throws ClientProtocolException, IOException {
		throw new RuntimeException("Method not implemented");
	}

	public void close() {
		throw new RuntimeException("Method not implemented");
	}

	public void setResponseForUrl(final String url,final String responseResource) {
		final InputStream resourceAsStream = SimpleHttpClientMock.class.getResourceAsStream(responseResource);
		try {
			final String response = IOUtils.toString(resourceAsStream);
			responseForUrl.put(url,response);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		try {
			resourceAsStream.close();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean requested(final String string) {
		return true;
	}

	public void storeCookiesOn(final File storeDir) {
	}

}
