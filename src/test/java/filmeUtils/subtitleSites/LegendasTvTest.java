package filmeUtils.subtitleSites;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.Assert;

import org.junit.Test;

import filmeUtils.commons.OutputListener;
import filmeUtils.subtitle.subtitleSites.LegendasTv;
import filmeUtils.subtitle.subtitleSites.SubtitlePackageAndLink;
import filmeUtils.subtitle.subtitleSites.SubtitleLinkSearchCallback;

public class LegendasTvTest {
	
	private final OutputListener dummyOutputListener;
	final SimpleHttpClientMock mock = new SimpleHttpClientMock();

	public LegendasTvTest() {
		dummyOutputListener = new DummyOutputListener();
	}
	
	@Test
	public void simpleSearchWithOneResultTest(){
		final String response = "LegendasTvOneResult.html";
		mock.setResponseForUrl("http://legendas.tv/index.php?opcao=buscarlegenda&pagina=1", response);
		final LegendasTv subject = new LegendasTv(mock, dummyOutputListener);
		final AtomicBoolean wasCalled = new AtomicBoolean(false);
		subject.search("foo", new SubtitleLinkSearchCallback() {
			public void process(final SubtitlePackageAndLink subAndLink) {
				Assert.assertEquals("Castle.S04E21.720p.WEB-DL.DD5.1.H.264-NFHD",subAndLink.name);
				Assert.assertEquals("http://legendas.tv/info.php?c=1&d=e613c192c4279ff32db5f3ad0640e8d0",subAndLink.link);
				wasCalled.set(true);
			}
		});
		Assert.assertTrue(wasCalled.get());
	}

	@Test
	public void simpleSearchWithMoreThanOneResultOnePageGreedyTest(){
		final String response = "LegendasTvLotsOfResultsOnePage.html";
		mock.setResponseForUrl("http://legendas.tv/index.php?opcao=buscarlegenda&pagina=1", response);
		final LegendasTv subject = new LegendasTv(mock, dummyOutputListener);
		final Map<String, String> expectedResults = new LinkedHashMap<String, String>();
		expectedResults.put("Community.S01.Complete.HDTV (PACK DE LEGENDAS)", "http://legendas.tv/info.php?c=1&d=ae752094b5a1fc977e933fb619527d1a");
		expectedResults.put("Community.S01.720p.WEB-DL.DD5.1.H.264-myTV/HoodBag (PACK DE LEGENDAS)","http://legendas.tv/info.php?c=1&d=a7faf31ace51a2110b69b822ff84b434");
		expectedResults.put("Community.S02.720p.WEB-DL.DD5.1.H.264-HoodBag (PACK DE LEGENDAS)","http://legendas.tv/info.php?c=1&d=fe19c9afc9dbf6a53a74d0782da8861a");
		subject.search("foo", new SubtitleLinkSearchCallback() {
			public void process(final SubtitlePackageAndLink subAndLink) {
				String name = subAndLink.name;
				String link = subAndLink.link;
				final String linkExpected = expectedResults.get(name);
				Assert.assertEquals("For "+name,linkExpected, link);
				expectedResults.remove(name);
			}
		});
		Assert.assertEquals(0, expectedResults.size());
	}
	
	@Test
	public void simpleSearchWithMoreThanOneResultTwoPagesGreedyTest(){
		mock.setResponseForUrl("http://legendas.tv/index.php?opcao=buscarlegenda&pagina=1", "LegendasTvLotsOfResultsTwoPages.html");
		mock.setResponseForUrl("http://legendas.tv/index.php?opcao=buscarlegenda&pagina=2", "LegendasTvOneResult.html");
		final LegendasTv subject = new LegendasTv(mock, dummyOutputListener);
		final Map<String, String> expectedResults = new LinkedHashMap<String, String>();
		expectedResults.put("Community.S01.Complete.HDTV (PACK DE LEGENDAS)", "http://legendas.tv/info.php?c=1&d=ae752094b5a1fc977e933fb619527d1a");
		expectedResults.put("Community.S01.720p.WEB-DL.DD5.1.H.264-myTV/HoodBag (PACK DE LEGENDAS)","http://legendas.tv/info.php?c=1&d=a7faf31ace51a2110b69b822ff84b434");
		expectedResults.put("Community.S02.720p.WEB-DL.DD5.1.H.264-HoodBag (PACK DE LEGENDAS)","http://legendas.tv/info.php?c=1&d=fe19c9afc9dbf6a53a74d0782da8861a");
		expectedResults.put("Castle.S04E21.720p.WEB-DL.DD5.1.H.264-NFHD","http://legendas.tv/info.php?c=1&d=e613c192c4279ff32db5f3ad0640e8d0");
		subject.search("foo", new SubtitleLinkSearchCallback() {
			public void process(final SubtitlePackageAndLink subAndLink) {
				String name = subAndLink.name;
				String link = subAndLink.link;
				final String linkExpected = expectedResults.get(name);
				Assert.assertEquals("For "+name,linkExpected, link);
				expectedResults.remove(name);
			}
		});
		Assert.assertEquals(0, expectedResults.size());
	}
}