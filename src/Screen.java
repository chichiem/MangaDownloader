import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

import net.miginfocom.swing.MigLayout;

import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JLabel;

import org.jsoup.nodes.Element;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JTabbedPane;
import javax.swing.border.LineBorder;
import java.awt.Color;


public class Screen {

	private JFrame mainScreen;
	private JTextField mangaUrl;
	private JScrollPane scrollPane;
	private JTable table;
	private JLabel lblHttp;
	private JButton btnGetSelected;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Screen window = new Screen();
					window.mainScreen.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Screen() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		mainScreen = new JFrame();
		mainScreen.setTitle("Manga Downloader");
		mainScreen.setBounds(100, 100, 600, 600);
		mainScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainScreen.getContentPane().setLayout(new MigLayout("", "[grow]", "[][grow][]"));
		
		lblHttp = new JLabel("http://");
		mainScreen.getContentPane().add(lblHttp, "flowx,cell 0 0");
		
		mangaUrl = new JTextField("mangafox.me/manga/yamada_kun_to_7_nin_no_majo/");
		mainScreen.getContentPane().add(mangaUrl, "cell 0 0,growx");
		mangaUrl.setColumns(10);
		
		JButton getChapterBtn = new JButton("Get Chapters");
		DownloaderTableModel dtm = new DownloaderTableModel();
		Manga m = new Manga();
		getChapterBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m.setMangaUrl(mangaUrl.getText());
				m.run();
				
				dtm.addChapters(m);
				
			}
		});
		mainScreen.getContentPane().add(getChapterBtn, "cell 0 0");
		
		
		scrollPane = new JScrollPane();
		
		table = new JTable(dtm);
		table.setShowGrid(false);
		table.setShowHorizontalLines(true);
		scrollPane.setViewportView(table);
		mainScreen.getContentPane().add(scrollPane, "cell 0 1, growx");
		
		btnGetSelected = new JButton("Get Selected");
		btnGetSelected.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ExecutorService chapterList = Executors.newFixedThreadPool(DownloaderConstants.MAX_NO_OF_THREADS.getValue());
				for(int row : table.getSelectedRows()){
					Chapter c = m.getAllChapters().get(row); 
					System.out.println(c.getChapterName());
					chapterList.execute(c);
				}
				chapterList.shutdown();
				while (!chapterList.isTerminated()) {
				}
			}
		});
		
		mainScreen.getContentPane().add(btnGetSelected, "cell 0 2");
		
	}
	
	public void newTab(String mangaName, JTable newTable){
		
	}

}
