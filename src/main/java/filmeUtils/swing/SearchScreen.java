package filmeUtils.swing;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListModel;

@SuppressWarnings("serial")
public class SearchScreen extends JFrame {
	
	private final JTextField searchString;
	private final DefaultListModel defaultListModel;
	
	public SearchScreen(final SearchScreenNeeds searchScreenNeeds) {
		getContentPane().setLayout(new BorderLayout(0, 0));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final JPanel searchPanel = new JPanel();
		getContentPane().add(searchPanel, BorderLayout.NORTH);
		final GridBagLayout gbl_searchPanel = new GridBagLayout();
		gbl_searchPanel.columnWidths = new int[]{0, 174, 114, 0};
		gbl_searchPanel.rowHeights = new int[]{25, 0};
		gbl_searchPanel.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0};
		gbl_searchPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		searchPanel.setLayout(gbl_searchPanel);
		
		final JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		final GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{126, 0, 0};
		gbl_panel.rowHeights = new int[]{15, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		final JLabel subtitlesFolder = new JLabel("...");
		final GridBagConstraints gbc_subtitlesFolder = new GridBagConstraints();
		gbc_subtitlesFolder.gridx = 1;
		gbc_subtitlesFolder.gridy = 0;
		final String defaultSubtitlesFolder = searchScreenNeeds.getSubtitleFolder();
		subtitlesFolder.setText(defaultSubtitlesFolder);
		panel.add(subtitlesFolder, gbc_subtitlesFolder);
		
		final JButton subtitlesDest = new JButton("");
		subtitlesDest.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				final JFileChooser jFileChooser = new JFileChooser();
				jFileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY);
				jFileChooser.showDialog(null, "OK");
				final File selectedFile = jFileChooser.getSelectedFile();
				if(selectedFile == null) return;
				searchScreenNeeds.setSubtitleFolder(selectedFile);
				subtitlesFolder.setText(selectedFile.getAbsolutePath());
			}
		});
		subtitlesDest.setIcon(new ImageIcon(SearchScreen.class.getResource("/com/sun/java/swing/plaf/gtk/icons/Directory.gif")));
		final GridBagConstraints gbc_subtitlesDest = new GridBagConstraints();
		gbc_subtitlesDest.anchor = GridBagConstraints.WEST;
		gbc_subtitlesDest.insets = new Insets(0, 0, 0, 5);
		gbc_subtitlesDest.gridx = 0;
		gbc_subtitlesDest.gridy = 0;
		searchPanel.add(subtitlesDest, gbc_subtitlesDest);
		
		final JComboBox resolution = new JComboBox();
		resolution.setModel(new DefaultComboBoxModel(new String[] { searchScreenNeeds.allResolutionsString() , searchScreenNeeds.highResolutionString(), searchScreenNeeds.lowResolutionString()}));
		final GridBagConstraints gbc_resolution = new GridBagConstraints();
		gbc_resolution.anchor = GridBagConstraints.WEST;
		gbc_resolution.insets = new Insets(0, 0, 0, 5);
		gbc_resolution.gridx = 1;
		gbc_resolution.gridy = 0;
		resolution.addActionListener(new ActionListener() {
			
			public void actionPerformed(final ActionEvent arg0) {
				searchScreenNeeds.setResolution(resolution.getSelectedItem().toString());
			}
		});
		searchPanel.add(resolution, gbc_resolution);
		
		searchString = new JTextField();
		final String defaultSearchString = "Procura...";
		searchString.setText(defaultSearchString);
		searchString.addFocusListener(new FocusListener() {
			
			public void focusLost(final FocusEvent e) {
				if(searchString.getText().equals("")){
					searchString.setText(defaultSearchString);
				}
			}
			
			public void focusGained(final FocusEvent e) {
				if(searchString.getText().equals(defaultSearchString)){
					searchString.setText("");
				}
			}
		});
		final ActionListener searchForSearchTerm = new ActionListener() {
			public void actionPerformed(final ActionEvent arg0) {
				final String searchTerm = searchString.getText();
				final String[] resultsFor = searchScreenNeeds.getResultsFor(searchTerm);
				populateList(resultsFor);
			}
		};
		searchString.addActionListener(searchForSearchTerm);
		final GridBagConstraints gbc_searchString = new GridBagConstraints();
		gbc_searchString.fill = GridBagConstraints.HORIZONTAL;
		gbc_searchString.insets = new Insets(0, 0, 0, 5);
		gbc_searchString.gridx = 2;
		gbc_searchString.gridy = 0;
		searchPanel.add(searchString, gbc_searchString);
		searchString.setColumns(10);
		
		final JButton search = new JButton("Procurar");
		final GridBagConstraints gbc_search = new GridBagConstraints();
		gbc_search.anchor = GridBagConstraints.EAST;
		gbc_search.gridx = 3;
		gbc_search.gridy = 0;
		search.addActionListener(searchForSearchTerm);
		searchPanel.add(search, gbc_search);
		
		
		
		final JLabel lblPastaDeLegendas = new JLabel("Pasta de legendas: ");
		final GridBagConstraints gbc_lblPastaDeLegendas = new GridBagConstraints();
		gbc_lblPastaDeLegendas.insets = new Insets(0, 0, 0, 5);
		gbc_lblPastaDeLegendas.anchor = GridBagConstraints.WEST;
		gbc_lblPastaDeLegendas.gridx = 0;
		gbc_lblPastaDeLegendas.gridy = 0;
		panel.add(lblPastaDeLegendas, gbc_lblPastaDeLegendas);
		
		
		
		final JList result = new JList();
		getContentPane().add(result, BorderLayout.CENTER);
		defaultListModel = new DefaultListModel();
		final String[] defaultList = searchScreenNeeds.getDefaultList();
		populateList(defaultList);
		
		result.setModel(defaultListModel);
		
		result.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(final MouseEvent e) {
			if (e.getClickCount() == 2) {
				final int index = result.locationToIndex(e.getPoint());
				final ListModel dlm = result.getModel();
				final Object item = dlm.getElementAt(index);
				result.ensureIndexIsVisible(index);
				searchScreenNeeds.download((String) item);
			}
		}});
		
		setSize(800,600);
		setVisible(true);
	}

	private void populateList(final String[] defaultList) {
		defaultListModel.clear();
		for (final String subtitle : defaultList) {
			defaultListModel.addElement(subtitle);
		}
	}
	
}
