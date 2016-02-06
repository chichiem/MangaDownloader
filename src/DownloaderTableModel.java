import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class DownloaderTableModel extends AbstractTableModel {

	// These are the names for the table's columns.
	private static final String[] columnNames = { "Manga", "Chapter", "Progress", "Status" };
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Vector<Vector<String>> rowData = new Vector<Vector<String>>();
	private Vector<String> row;
	

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return rowData.size();
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		return rowData.elementAt(rowIndex).elementAt(columnIndex);
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public void addChapters(Manga m) {
		for(Chapter c : m.getAllChapters()){
			row  = new Vector<String>(5); 
			row.addElement(m.getMangaName());
			row.addElement(c.getChapterName());
			row.addElement("");
			row.addElement("Downloading...");
			
			rowData.add(row);
		}
		
		fireTableDataChanged();
	}

}
