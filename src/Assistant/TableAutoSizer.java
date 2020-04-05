package Assistant;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public class TableAutoSizer {
	public static void resizeCellWidthAndHieght(JTable table, int HorizontalAlignment, int VerticalAlignment, JFrame fr, int fontSize) {
		Font font = new Font("Arial", Font.PLAIN, fontSize);
		table.setFont(font);
	    DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer();
	        defaultTableCellRenderer.setHorizontalAlignment(HorizontalAlignment);
	        defaultTableCellRenderer.setVerticalAlignment(VerticalAlignment);
	    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	    final TableColumnModel columnmodel = table.getColumnModel();
	    int width = 40; // Min width
        int height = 15; // Min height
	    for (int column = 0; column < table.getColumnCount(); column++) {
	        table.getColumnModel().getColumn(column).setCellRenderer(defaultTableCellRenderer);
	        
	        for (int row = 0; row < table.getRowCount(); row++) {
	            TableCellRenderer renderer = table.getCellRenderer(row, column);
	            Component comp = table.prepareRenderer(renderer, row, column);
	            width = Math.max(comp.getPreferredSize().width +1 , width);
	            height = Math.max(comp.getPreferredSize().height +1 , height);
	        }
	        if(width > 2000)
	            width=2000;
	        columnmodel.getColumn(column).setPreferredWidth(width);
	    }
	    for (int i = 0; i < table.getRowCount(); i++) {
	        try {
	            table.setRowHeight(i, height);
	        } catch (IllegalArgumentException e) {
	        	JOptionPane.showInternalMessageDialog(fr, "Resrarting Error !", "Error !",
						JOptionPane.ERROR_MESSAGE);
	          }
	    }
	}
}
