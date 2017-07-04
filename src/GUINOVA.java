import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.NumberFormatter;
import javax.swing.JLabel;

public class GUINOVA extends JFrame {
	private JPanel contentPane;
	private DrawPanel drawPanel;
	private JComboBox resolucoesList;
	private JFormattedTextField customBase,customAltura, customBaseEdit,customAlturaEdit;
	private NumberFormatter formatter;
	private int base,altura;
	
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
	
	private ActionListener configResolucao(){
		return new ActionListener() {//add actionlistner to listen for change
		    @Override
		    public void actionPerformed(ActionEvent e) {

		        String s = (String) resolucoesList.getSelectedItem();//get the selected item

		        switch (s) {//check for a match
		            case "1080p":
		        		customBase.setText("1920");
		        		customAltura.setText("1080");
		            	VariavelGlobal.resolucaoWidthVideoOutput=1920;
		            	VariavelGlobal.resolucaoHeightVideoOutput=1080;
		                break;
		            case "720p":
		        		customBase.setText("1280");
		        		customAltura.setText("720");
		            	VariavelGlobal.resolucaoWidthVideoOutput=1280;
		            	VariavelGlobal.resolucaoHeightVideoOutput=720;
		                break;
//		            case "480p":
//	        			customBase.setText("854");
//		        		customAltura.setText("480");
//		            	VariavelGlobal.resolucaoWidthVideoOutput=854;
//		            	VariavelGlobal.resolucaoHeightVideoOutput=480;
//		                break;
		            case "360p":
	        			customBase.setText("640");
		        		customAltura.setText("360");
		            	VariavelGlobal.resolucaoWidthVideoOutput=640;
		            	VariavelGlobal.resolucaoHeightVideoOutput=360;
		                break;

		            case "Custom":
		        		Object[] message = {
		        		    "base:", customBaseEdit,
		        		    "altura:", customAlturaEdit
		        		};

		        		int option = JOptionPane.showConfirmDialog(null, message, "Login", JOptionPane.OK_CANCEL_OPTION);
		        		if (option == JOptionPane.OK_OPTION) {
		        			base = (int)customBaseEdit.getValue();
		        			altura = (int)customAlturaEdit.getValue();
		        			
		        			customBase.setText(base+"");
			        		customAltura.setText(altura+"");
			            	VariavelGlobal.resolucaoWidthVideoOutput=base;
			            	VariavelGlobal.resolucaoHeightVideoOutput=altura;	        		   
		        		}

		            	customBase.setEnabled(false);
		            	customAltura.setEnabled(false);
		                break;
		        }
		    }
		};
	}	

	private final JFileChooser selecionarImg = new JFileChooser();
	private final File workingDirectory = new File(System.getProperty("user.dir"));
	
	public GUINOVA() {

		selecionarImg.setCurrentDirectory(workingDirectory);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 430);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		drawPanel = new DrawPanel();
		drawPanel.setLocation(130, 13);
		drawPanel.setBackground(new Color(232, 232, 232));
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

		//selecionar imagem referencia
		JButton botaoBackground = new JButton("<html>Imagem de<br>Refer\u00EAncia");
		botaoBackground.addActionListener(configAcao('b'));
		botaoBackground.setBounds(10, 130, 90, 43);
		contentPane.add(botaoBackground);	
		//fim imagem referencia
		
		//configurar selecao de resolucoes
		
		String[] resolucoes = new String[] {"1080p", "720p","360p"};
		DefaultComboBoxModel<String> comboModel = new DefaultComboBoxModel<String>(resolucoes);		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "<html>Resolu\u00E7\u00E3o do<br>v\u00EDdeo resultado", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel.setBounds(10, 184, 102, 123);
		contentPane.add(panel);
		panel.setLayout(null);
		resolucoesList = new JComboBox(comboModel);
		resolucoesList.setBounds(10, 37, 90, 23);
		panel.add(resolucoesList);	


	    DecimalFormat format = new DecimalFormat();
	    format.setGroupingUsed(false);
	    formatter = new NumberFormatter(format);
	    //formatter.setValueClass(Integer.class);
	    formatter.setMinimum(0);
	    formatter.setMaximum(Integer.MAX_VALUE);
	    formatter.setAllowsInvalid(false);
	    // If you want the value to be committed on each keystroke instead of focus lost
	    formatter.setCommitsOnValidEdit(true);
	    
		customBase = new JFormattedTextField(formatter);
		customBase.setEnabled(false);
		customBase.setToolTipText("base");
		customBase.setBounds(10, 71, 35, 20);
		panel.add(customBase);
		
		customAltura = new JFormattedTextField(formatter);
		customAltura.setEnabled(false);
		customAltura.setToolTipText("altura");
		customAltura.setBounds(50, 71, 42, 20);
		panel.add(customAltura);
		
		customBase.setText("1920");
		customAltura.setText("1080");
		
		JLabel label = new JLabel("16:9");
		label.setBounds(10, 98, 46, 14);
		panel.add(label);
		
    	customBaseEdit= new JFormattedTextField(formatter);
    	customAlturaEdit= new JFormattedTextField(formatter);
		
		resolucoesList.addActionListener(configResolucao());
		//fim config resolucoes
		
		//rangeslider classe component opcional para cortar https://github.com/ernieyu/Swing-range-slider/tree/master/src/slider
	}
}
