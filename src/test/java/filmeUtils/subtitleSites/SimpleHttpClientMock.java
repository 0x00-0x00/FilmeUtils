package filmeUtils.subtitleSites;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.ClientProtocolException;

import filmeUtils.http.SimpleHttpClient;

class SimpleHttpClientMock implements SimpleHttpClient {

	private String response;

	public String getToFile(final String link, final File destFile)
			throws ClientProtocolException, IOException {
		throw new RuntimeException("Method not implemented");
	}

	public String getOrNull(final String url) {
		throw new RuntimeException("Method not implemented");
	}

	public String post(final String postUrl, final Map<String, String> params) throws ClientProtocolException, IOException {
		return response;
	}

	public String get(final String get) throws ClientProtocolException, IOException {
		throw new RuntimeException("Method not implemented");
	}

	public void close() {
		throw new RuntimeException("Method not implemented");
	}

	public void setResponse(final String string) {
		final InputStream resourceAsStream = SimpleHttpClientMock.class.getResourceAsStream(string);
		try {
			response = IOUtils.toString(resourceAsStream);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		try {
			resourceAsStream.close();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

}
