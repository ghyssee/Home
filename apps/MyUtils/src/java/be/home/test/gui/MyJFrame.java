package be.home.test.gui;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JLabel;

import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class MyJFrame extends javax.swing.JFrame {
	private JLabel jLabel1;
	private JLabel jLabel2;
	private JButton jButton1;

	/**
	* Auto-generated main method to display this JFrame
	*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MyJFrame inst = new MyJFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}
	
	public MyJFrame() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			getContentPane().setForeground(new java.awt.Color(255,255,128));
			{
				jLabel1 = new JLabel();
				getContentPane().add(jLabel1, BorderLayout.NORTH);
				jLabel1.setText("HelloWorld1");
				jLabel1.setFont(new java.awt.Font("Microsoft Sans Serif",1,11));
				jLabel1.setSize(142, 173);
				jLabel1.setPreferredSize(new java.awt.Dimension(70, 20));
				jLabel2 = new JLabel();
				getContentPane().add(jLabel2, BorderLayout.CENTER);
				jLabel2.setText("HelloWorl24");
				jLabel2.setFont(new java.awt.Font("Microsoft Sans Serif",1,11));
				jLabel2.setSize(142, 173);
				jLabel2.setPreferredSize(new java.awt.Dimension(140, 60));
			}
			{
				jButton1 = new JButton();
				getContentPane().add(jButton1, BorderLayout.SOUTH);
				jButton1.setText("OK");
				jButton1.setPreferredSize(new java.awt.Dimension(392, 26));
			}
			pack();
			setSize(400, 300);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
