package filmeUtils.subtitleSites;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import filmeUtils.commons.OutputListener;
import filmeUtils.subtitle.subtitleSites.LegendasTv;
import filmeUtils.subtitle.subtitleSites.SubtitleLinkSearchCallback;
import filmeUtils.subtitle.subtitleSites.SubtitlePackageAndLink;

public class LegendasTvTest {
	
	private final OutputListener dummyOutputListener;
	final SimpleHttpClientMock mock = new SimpleHttpClientMock();

	public LegendasTvTest() {
		dummyOutputListener = new DummyOutputListener();
	}
	
	@Test
	public void simpleSearchWithOneResultTest(){
		mock.setResponseForUrl("http://legendas.tv/util/carrega_legendas_busca/termo:Breaking_Bad_S05E13_HDTV_x264_EVOLVE_AFG_mSD_ChameE_IMMERSE_BS/page:1", "LegendasTvOneResult.html");
		
		final LegendasTv subject = new LegendasTv(mock, dummyOutputListener);
		final AtomicBoolean wasCalled = new AtomicBoolean(false);
		subject.search("Breaking_Bad_S05E13_HDTV_x264_EVOLVE_AFG_mSD_ChameE_IMMERSE_BS", new SubtitleLinkSearchCallback() {
			@Override
			public void process(final SubtitlePackageAndLink subAndLink) {
				Assert.assertEquals("Breaking.Bad.S05E13.HDTV.x264-EVOLVE-AFG-mSD-ChameE-IMMERSE-BS",subAndLink.name);
				Assert.assertEquals("http://legendas.tv/pages/downloadarquivo/137620a9b17d9065bbcad03a9e3feaf7",subAndLink.link);
				wasCalled.set(true);
			}
		});
		Assert.assertTrue(wasCalled.get());
	}

	@Ignore
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
			@Override
			public void process(final SubtitlePackageAndLink subAndLink) {
				final String name = subAndLink.name;
				final String link = subAndLink.link;
				final String linkExpected = expectedResults.get(name);
				Assert.assertEquals("For "+name,linkExpected, link);
				expectedResults.remove(name);
			}
		});
		Assert.assertEquals(0, expectedResults.size());
	}
	
	@Test
	public void simpleSearchWithMoreThanOneResultTwoPagesGreedyTest(){
		mock.setResponseForUrl("http://legendas.tv/util/carrega_legendas_busca/termo:house of the dead/page:1", "LegendasTvLotsOfResultsTwoPages.html");
		mock.setResponseForUrl("http://legendas.tv/util/carrega_legendas_busca/termo:house of the dead/page:2", "LegendasTvOneResult.html");
		final LegendasTv subject = new LegendasTv(mock, dummyOutputListener);
		final Map<String, String> expectedResults = new LinkedHashMap<String, String>();
		expectedResults.put("House [2008]DVDRip[Xvid AC3[5.1]-RoCK&BlueLadyRG", "http://legendas.tv/pages/downloadarquivo/c214ba8d3a08aa5ed2e6d0c228cb7c08");
		expectedResults.put("House.(1986).DVDRip.XviD-DitriS", "http://legendas.tv/pages/downloadarquivo/2a385576ec54161d101d59aeae496bbf");
		expectedResults.put("House (1986) Xvid [ENG] Dvdrip", "http://legendas.tv/pages/downloadarquivo/b9348478cdbca402907acfb6ab509874");
		expectedResults.put("Breaking.Bad.S05E13.HDTV.x264-EVOLVE-AFG-mSD-ChameE-IMMERSE-BS", "http://legendas.tv/pages/downloadarquivo/137620a9b17d9065bbcad03a9e3feaf7");
		subject.search("house of the dead", new SubtitleLinkSearchCallback() {
			@Override
			public void process(final SubtitlePackageAndLink subAndLink) {
				final String name = subAndLink.name;
				final String link = subAndLink.link;
				final String linkExpected = expectedResults.get(name);
				Assert.assertEquals("For "+name,linkExpected, link);
				expectedResults.remove(name);
			}
		});
		Assert.assertEquals(0, expectedResults.size());
	}
}