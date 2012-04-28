package filmeUtils.subtitleSites;

import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.Assert;

import org.junit.Test;

import filmeUtils.ArgumentsParserImpl;
import filmeUtils.OutputListener;
import filmeUtils.SearchListener;

public class LegendasTvTest {
	
	private final OutputListener dummyOutputListener;

	public LegendasTvTest() {
		dummyOutputListener = new OutputListener() {
			public void out(final String string) {
				//do nothing
			}

			public void outVerbose(final String string) {
				//do nothing
			}
		};
	}
	
	@Test
	public void searchTest(){
		final String response = "LegendasTvOneResult.html";
		final SimpleHttpClientMock mock = getHttpMock(response);
		final ArgumentsParserImpl cli = new ArgumentsParserImpl();
		cli.parse(new String[]{});
		final LegendasTv subject = new LegendasTv(cli,mock, dummyOutputListener);
		final AtomicBoolean wasCalled = new AtomicBoolean(false);
		subject.search("foo", new SearchListener() {
			public void found(final String name, final String link) {
				Assert.assertEquals("Castle.S04E21.720p.WEB-DL.DD5.1.H.264-NFHD",name);
				Assert.assertEquals("http://legendas.tv/info.php?c=1&d=e613c192c4279ff32db5f3ad0640e8d0",link);
				wasCalled.set(true);
			}
		});
		Assert.assertTrue(wasCalled.get());
	}

	private SimpleHttpClientMock getHttpMock(final String response) {
		final SimpleHttpClientMock mock = new SimpleHttpClientMock();
		mock.setResponse(response);
		return mock;
	}

}
