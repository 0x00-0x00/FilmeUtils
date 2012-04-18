package filmeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

public class SimpleHttpClient {

	public static final int TIMEOUT = 60;
	private final DefaultHttpClient httpclient;
	
	public SimpleHttpClient() {
		httpclient = new DefaultHttpClient();
		final HttpParams httpParameters = httpclient.getParams();
		final int connectionTimeOutSec= TIMEOUT;
		final int socketTimeoutSec = TIMEOUT;
		HttpConnectionParams.setConnectionTimeout(httpParameters, connectionTimeOutSec * 1000);
		HttpConnectionParams.setSoTimeout        (httpParameters, socketTimeoutSec * 1000);
	}
	
	public void close() {
		httpclient.getConnectionManager().shutdown();
	}

	public String get(final String get) throws ClientProtocolException, IOException {
		final HttpGet httpGet = new HttpGet(get);
		return executeAndGetResponseContents(httpGet);
	}

	public String post(final String postUrl, final Map<String, String> params) throws ClientProtocolException, IOException {
		final HttpPost httpost = new HttpPost(postUrl);
		final List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		final Set<Entry<String, String>> entrySet = params.entrySet();
		for (final Entry<String, String> entry : entrySet) {
			nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		return executeAndGetResponseContents(httpost);
	}

	public String getOrNull(final String url) {
		try {
			return get(url);
		} catch (final Exception e) {
			return null;
		}
	}

	public String getToFile(final String link, final File destFile) throws ClientProtocolException, IOException {
		final HttpGet httpGet = new HttpGet(link);
		return executeSaveResponseToFileReturnContentType(httpGet, destFile);
	}

	private HttpResponse execute(final HttpUriRequest httpost) throws ClientProtocolException, IOException {
		return httpclient.execute(httpost);
	}

	private String executeAndGetResponseContents(final HttpUriRequest httpost)throws IOException, ClientProtocolException {
		final HttpResponse response = execute(httpost);
	    final HttpEntity entity = response.getEntity();
		final InputStream contentIS = entity.getContent();
		final String content = IOUtils.toString(contentIS);
		contentIS.close();
		return content;
	}
	
	private String executeSaveResponseToFileReturnContentType(final HttpUriRequest httpost,final File dest)throws IOException, ClientProtocolException {
		final HttpResponse response = execute(httpost);
	    final HttpEntity entity = response.getEntity();
	    final String contentType = entity.getContentType().getValue();
		final InputStream in = entity.getContent();
		final OutputStream out = new FileOutputStream(dest);
		IOUtils.copy(in, out);
		out.flush();
		out.close();
		in.close();
		return contentType;
	}

}
