import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class GUINOVA extends JFrame {
	private JPanel contentPane;
	private DrawPanel drawPanel;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//usar tema atual do windows
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					
					GUINOVA frame = new GUINOVA();
					frame.setVisible(true);
					frame.setTitle("Orion Multi-Video Editor");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	private ActionListener configAcao(char acao){
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				drawPanel.executarAcao(acao);
			}
		};
	} 
	public GUINOVA() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		drawPanel = new DrawPanel();
		drawPanel.setLocation(110, 0);
		contentPane.add(drawPanel);
		setContentPane(contentPane);
		
		JButton botaoSalvar = new JButton("Salvar");
		botaoSalvar.addActionListener(configAcao('s'));
		botaoSalvar.setBounds(10, 10, 90, 23);
		contentPane.add(botaoSalvar);
		
		JButton botaoLoad = new JButton("Load");
		botaoLoad.addActionListener(configAcao('l'));
		botaoLoad.setBounds(10, 50, 90, 23);
		contentPane.add(botaoLoad);
		
		JButton botaoRender = new JButton("Render");
		botaoRender.addActionListener(configAcao('r'));
		botaoRender.setBounds(10, 90, 90, 23);
		contentPane.add(botaoRender);		
		
		//rangeslider classe component opcional para cortar https://github.com/ernieyu/Swing-range-slider/tree/master/src/slider
	}
}
