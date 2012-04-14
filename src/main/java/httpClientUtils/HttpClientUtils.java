package httpClientUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpClientUtils {

	public static String executeAndGetResponseContents(final HttpUriRequest httpost,final DefaultHttpClient httpclient)throws IOException, ClientProtocolException {
		final HttpResponse response = httpclient.execute(httpost);
	    final HttpEntity entity = response.getEntity();
		final InputStream contentIS = entity.getContent();
		final String content = IOUtils.toString(contentIS);
		contentIS.close();
		return content;
	}
	
	public static void executeAndSaveResponseToFile(final HttpUriRequest httpost,final File dest,final DefaultHttpClient httpclient)throws IOException, ClientProtocolException {
		final HttpResponse response = httpclient.execute(httpost);
	    final HttpEntity entity = response.getEntity();
		final InputStream contentIS = entity.getContent();
		final OutputStream os = new FileOutputStream(dest);
		IOUtils.copy(contentIS, os);
		contentIS.close();
		os.close();
	}

}
