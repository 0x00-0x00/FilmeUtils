package filmeUtils.swing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import filmeUtils.SearchListener;
import filmeUtils.subtitleSites.LegendasTv;


public class SearchScreenNeeds {

	private static final String LOW = "Resolução normal";
	private static final String HIGH = "Alta definição";
	private static final String ALL = "Todas as resoluções";
	private File subtitleFolder;
	private String resolution = ALL;
	private final LegendasTv legendasTv;

	public SearchScreenNeeds(final LegendasTv legendasTv) {
		this.legendasTv = legendasTv;
		subtitleFolder = new File(System.getProperty("user.home"));
	}
	
	public void download(final String item) {
		System.out.println("Downloading "+item);;
	}

	public void setSubtitleFolder(final File folder) {
		subtitleFolder = folder;
	}

	public String getSubtitleFolder() {
		return subtitleFolder.getAbsolutePath();
	}

	public String[] getDefaultList() {
		
		final List<String> subtitles = new ArrayList<String>();
		
		legendasTv.getNewer(30, new SearchListener(){
			public boolean foundReturnIfShouldStopLooking(final String name,final String link) {
				subtitles.add(name);
				return false;
			}
			
		});
		final String[] array = subtitles.toArray(new String[subtitles.size()]);
		return array;
	}

	public String[] getResultsFor(final String text) {
		return new String[]{"A"+resolution,"B"+text,"C"+subtitleFolder.getAbsolutePath()};
	}

	public void setResolution(final String resolution) {
		this.resolution = resolution;
	}

	public String allResolutionsString() {
		return ALL;
	}

	public String highResolutionString() {
		return HIGH;
	}

	public String lowResolutionString() {
		return LOW;
	}

}
