package filmeUtils.utils.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class FilmeUtilsHttpClient {

	private static CloseableHttpClient httpClient;
	
    public static String getToFile(String link, File zipTempDestination) {
        try {
            HttpUriRequest request = RequestBuilder.get()
                    .setUri(link)
                    .setHeader("User-Agent", "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.10 (maverick) Firefox/3.6.13")
                    .build();

            if(httpClient == null){
            	httpClient = HttpClients.createDefault();
            	//Aqui vai o código pra logar
//            	SimpleHttpClientImpl simpleHttpClientImpl = new SimpleHttpClientImpl();
//        		simpleHttpClientImpl.post("http://legendas.tv/login", new BasicNameValuePair("data[User][username]", "filmeutils"), 
//        				new BasicNameValuePair("data[User][password]", "filmeutilsfilme"));
//        		System.out.println(simpleHttpClientImpl.get("http://legendas.tv/pages/downloadarquivo/546fad1aa88c5"));
//        		
//            	
//            	HttpUriRequest login = RequestBuilder.post()
//                        .setUri(link)
//                        .setHeader("User-Agent", "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.10 (maverick) Firefox/3.6.13")
//                        .build();
//            	httpClient
            }
            
            CloseableHttpResponse execute = httpClient.execute(request);
            final HttpEntity entity = execute.getEntity();
            final InputStream contentIS = entity.getContent();

            final OutputStream out = new FileOutputStream(zipTempDestination);
            IOUtils.copy(contentIS, out);
            out.flush();
            out.close();
            contentIS.close();

            String filename = "unknown.rar";

            final Header[] allHeaders = execute.getAllHeaders();
            for (final Header header : allHeaders) {
                final HeaderElement[] elements = header.getElements();
                for (final HeaderElement headerElement : elements) {
                    final NameValuePair[] parameters = headerElement.getParameters();
                    for (final NameValuePair nameValuePair : parameters) {
                        if(nameValuePair.getName().equals("filename")){
                            filename = nameValuePair.getValue();
                        }
                    }
                }
            }
            return filename;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
