package filmeUtils.swing;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractListModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListModel;

public class SearchScreen extends JFrame {
	
	private final JTextField searchString;
	private final SearchScreenNeeds searchScreenNeeds;
	public SearchScreen(final SearchScreenNeeds searchScreenNeeds) {
		this.searchScreenNeeds = searchScreenNeeds;
		getContentPane().setLayout(new BorderLayout(0, 0));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final JPanel searchPanel = new JPanel();
		getContentPane().add(searchPanel, BorderLayout.NORTH);
		final GridBagLayout gbl_searchPanel = new GridBagLayout();
		gbl_searchPanel.columnWidths = new int[]{174, 114, 95, 0};
		gbl_searchPanel.rowHeights = new int[]{25, 0};
		gbl_searchPanel.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_searchPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		searchPanel.setLayout(gbl_searchPanel);
		
		final JComboBox resolution = new JComboBox();
		resolution.setModel(new DefaultComboBoxModel(new String[] {"Alta definição", "Resolução normal", "Todas as resoluções"}));
		final GridBagConstraints gbc_resolution = new GridBagConstraints();
		gbc_resolution.anchor = GridBagConstraints.WEST;
		gbc_resolution.insets = new Insets(0, 0, 0, 5);
		gbc_resolution.gridx = 0;
		gbc_resolution.gridy = 0;
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
		final GridBagConstraints gbc_searchString = new GridBagConstraints();
		gbc_searchString.fill = GridBagConstraints.HORIZONTAL;
		gbc_searchString.insets = new Insets(0, 0, 0, 5);
		gbc_searchString.gridx = 1;
		gbc_searchString.gridy = 0;
		searchPanel.add(searchString, gbc_searchString);
		searchString.setColumns(10);
		
		final JButton search = new JButton("Procurar");
		final GridBagConstraints gbc_search = new GridBagConstraints();
		gbc_search.anchor = GridBagConstraints.EAST;
		gbc_search.gridx = 2;
		gbc_search.gridy = 0;
		searchPanel.add(search, gbc_search);
		
		final JList result = new JList();
		result.setModel(new AbstractListModel() {
			String[] values = new String[] {"Resultado1", "Resultado2", "Resultado3"};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(final int index) {
				return values[index];
			}
		});
		
		result.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(final MouseEvent e) {
			if (e.getClickCount() == 2) {
				final int index = result.locationToIndex(e.getPoint());
				final ListModel dlm = result.getModel();
				final Object item = dlm.getElementAt(index);
				result.ensureIndexIsVisible(index);
				searchScreenNeeds.download((String) item);
			}
		}});
		
		getContentPane().add(result, BorderLayout.CENTER);
		
	}
	
}
