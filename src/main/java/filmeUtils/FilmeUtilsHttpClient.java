package filmeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

public class FilmeUtilsHttpClient {

	public static final int TIMEOUT = 60;
	private final DefaultHttpClient httpclient;
	
	public FilmeUtilsHttpClient() {
		httpclient = new DefaultHttpClient();
		final HttpParams httpParameters = httpclient.getParams();
		final int connectionTimeOutSec= TIMEOUT;
		final int socketTimeoutSec = TIMEOUT;
		HttpConnectionParams.setConnectionTimeout(httpParameters, connectionTimeOutSec * 1000);
		HttpConnectionParams.setSoTimeout        (httpParameters, socketTimeoutSec * 1000);
	}
	
	public String executeAndGetResponseContents(final HttpUriRequest httpost)throws IOException, ClientProtocolException {
		final HttpResponse response = execute(httpost);
	    final HttpEntity entity = response.getEntity();
		final InputStream contentIS = entity.getContent();
		final String content = IOUtils.toString(contentIS);
		contentIS.close();
		EntityUtils.consume(entity);
		return content;
	}
	
	public void executeAndSaveResponseToFile(final HttpUriRequest httpost,final File dest)throws IOException, ClientProtocolException {
		final HttpResponse response = execute(httpost);
	    final HttpEntity entity = response.getEntity();
		final InputStream contentIS = entity.getContent();
		final OutputStream os = new FileOutputStream(dest);
		IOUtils.copy(contentIS, os);
		contentIS.close();
		os.close();
		EntityUtils.consume(entity);
	}

	public String getContentType(final HttpGet httpGet) throws ClientProtocolException, IOException {
		final HttpResponse response = execute(httpGet);
	    final HttpEntity entity = response.getEntity();
	    return entity.getContentType().getValue();
	}

	public HttpResponse execute(final HttpUriRequest httpost) throws ClientProtocolException, IOException {
		return httpclient.execute(httpost);
	}

	public void close() {
		httpclient.getConnectionManager().shutdown();
	}

}
