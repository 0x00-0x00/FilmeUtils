package filmeUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

public class LegendasTv {
	
	public static void login(final DefaultHttpClient httpclient) throws UnsupportedEncodingException, IOException, ClientProtocolException {
		final HttpPost httpost = new HttpPost("http://legendas.tv/login_verificar.php");
        final List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        nvps.add(new BasicNameValuePair("txtLogin", "greasemonkey"));
        nvps.add(new BasicNameValuePair("txtSenha", "greasemonkey"));

        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        httpclient.execute(httpost);
	}

	public static void search(final DefaultHttpClient httpclient) throws UnsupportedEncodingException, IOException, ClientProtocolException {
		final HttpPost httpost = new HttpPost("http://legendas.tv/index.php?opcao=buscarlegenda");
	    final List <NameValuePair> nvps = new ArrayList <NameValuePair>();
	    nvps.add(new BasicNameValuePair("txtLegenda", "house"));
	    nvps.add(new BasicNameValuePair("selTipo", "1"));
	    nvps.add(new BasicNameValuePair("int_idioma", "1"));
	    
	    httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
	    final HttpResponse response = httpclient.execute(httpost);
	    final HttpEntity entity = response.getEntity();
		final InputStream contentIS = entity.getContent();
		final String string = IOUtils.toString(contentIS);
		System.out.println(string);
		contentIS.close();
	}
	
	
}
