import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class GUINOVA extends JFrame {
	private JPanel contentPane;
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUINOVA frame = new GUINOVA();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public GUINOVA() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		DrawPanel drawPanel = new DrawPanel();
		drawPanel.setLocation(110, 0);
		contentPane.add(drawPanel);
		setContentPane(contentPane);
		
		JButton btnNewButton = new JButton("New button");
		btnNewButton.setBounds(10, 11, 90, 23);
		contentPane.add(btnNewButton);
		
		JButton button = new JButton("New button");
		button.setBounds(10, 61, 90, 23);
		contentPane.add(button);
		
		JPanel panel = new JPanel();
		panel.setBounds(114, 155, 204, 150);
		contentPane.add(panel);
	}
}
