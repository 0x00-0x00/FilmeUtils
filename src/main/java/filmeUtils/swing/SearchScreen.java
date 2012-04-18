package filmeUtils.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import filmeUtils.SearchListener;
import filmeUtils.SimpleHttpClient;
import filmeUtils.subtitleSites.LegendasTv;

public class SearchScreen extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTextArea resultsArea;
	private ExecutorService executor;

	public static void main(String[] args) {
		SearchScreen screen = new SearchScreen();
		screen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public SearchScreen(){
		executor = Executors.newSingleThreadExecutor();
		addComponents();
	}

	private void addComponents() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(createSearchField(), BorderLayout.NORTH);
		mainPanel.add(getResultsArea(), BorderLayout.CENTER);
		mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		mainPanel.setPreferredSize(new Dimension(640, 480));
		add(mainPanel);
		pack();
		setVisible(true);
	}

	private JTextArea getResultsArea() {
		resultsArea = new JTextArea();
		return resultsArea;
	}

	private JTextField createSearchField() {
		final JTextField result = new JTextField(40);
		result.setFont(result.getFont().deriveFont(30f));
		result.addActionListener(new ActionListener() {  public void actionPerformed(ActionEvent e) {
			executor.execute(new Runnable() {  public void run() {
				performQueryWith(result.getText());				
			}});
		}});
		return result;
	}

	protected void performQueryWith(String text) {
		SimpleHttpClient simpleHttpClient = new SimpleHttpClient();
		LegendasTv legendasTv = new LegendasTv(simpleHttpClient);
		resultsArea.setText("Autenticando, aguarde...");
		legendasTv.login("filmeutils", "filmeutilsfilme");
		resultsArea.setText("Pesquisando " + text + " , aguarde...");
		legendasTv.search(text, new SearchListener() {
			public void found(String name, String link) {
				if(!resultsArea.getText().isEmpty()){
					resultsArea.append("\n");
				}
				resultsArea.append(name);
			}
		});
	}
	
}
