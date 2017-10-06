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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.NumberFormatter;

public class GUINOVA
  extends JFrame
{
  private JPanel contentPane;
  private DrawPanel drawPanel;
  private JComboBox resolucoesList;
  private JFormattedTextField customBase;
  private JFormattedTextField customAltura;
  private JFormattedTextField customBaseEdit;
  private JFormattedTextField customAlturaEdit;
  private NumberFormatter formatter;
  private int base;
  private int altura;
  
  public static void main(String[] args)
  {
    EventQueue.invokeLater(new Runnable()
    {
      public void run()
      {
        try
        {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
          
          GUINOVA frame = new GUINOVA();
          frame.setVisible(true);
          frame.setTitle("Orion Multi-Video Editor");
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    });
  }
  
  private ActionListener configAcao(final char acao)
  {
    new ActionListener()
    {
      public void actionPerformed(ActionEvent arg0)
      {
        GUINOVA.this.drawPanel.executarAcao(acao);
      }
    };
  }
  
  private ActionListener configResolucao()
  {
    new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        String s = (String)GUINOVA.this.resolucoesList.getSelectedItem();
        String str1;
        switch ((str1 = s).hashCode())
        {
        case 1572835: 
          if (str1.equals("360p")) {}
          break;
        case 1688155: 
          if (str1.equals("720p")) {}
          break;
        case 46737913: 
          if (str1.equals("1080p")) {
            break;
          }
          break;
        case 2029746065: 
          if (!str1.equals("Custom"))
          {
            return;GUINOVA.this.customBase.setText("1920");
            GUINOVA.this.customAltura.setText("1080");
            VariavelGlobal.resolucaoWidthVideoOutput = 1920;
            VariavelGlobal.resolucaoHeightVideoOutput = 1080;
            return;
            
            GUINOVA.this.customBase.setText("1280");
            GUINOVA.this.customAltura.setText("720");
            VariavelGlobal.resolucaoWidthVideoOutput = 1280;
            VariavelGlobal.resolucaoHeightVideoOutput = 720;
            return;
            
            GUINOVA.this.customBase.setText("640");
            GUINOVA.this.customAltura.setText("360");
            VariavelGlobal.resolucaoWidthVideoOutput = 640;
            VariavelGlobal.resolucaoHeightVideoOutput = 360;
          }
          else
          {
            Object[] message = {
              "base:", GUINOVA.this.customBaseEdit, 
              "altura:", GUINOVA.this.customAlturaEdit };
            
            int option = JOptionPane.showConfirmDialog(null, message, "Login", 2);
            if (option == 0)
            {
              GUINOVA.this.base = ((Integer)GUINOVA.this.customBaseEdit.getValue()).intValue();
              GUINOVA.this.altura = ((Integer)GUINOVA.this.customAlturaEdit.getValue()).intValue();
              
              GUINOVA.this.customBase.setText(GUINOVA.this.base);
              GUINOVA.this.customAltura.setText(GUINOVA.this.altura);
              VariavelGlobal.resolucaoWidthVideoOutput = GUINOVA.this.base;
              VariavelGlobal.resolucaoHeightVideoOutput = GUINOVA.this.altura;
            }
            GUINOVA.this.customBase.setEnabled(false);
            GUINOVA.this.customAltura.setEnabled(false);
          }
          break;
        }
      }
    };
  }
  
  private final JFileChooser selecionarImg = new JFileChooser();
  private final File workingDirectory = new File(System.getProperty("user.dir"));
  
  public GUINOVA()
  {
    this.selecionarImg.setCurrentDirectory(this.workingDirectory);
    
    setDefaultCloseOperation(3);
    setBounds(100, 100, 800, 485);
    this.contentPane = new JPanel();
    this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    this.contentPane.setLayout(null);
    this.drawPanel = new DrawPanel();
    this.drawPanel.setLocation(130, 13);
    this.drawPanel.setBackground(new Color(232, 232, 232));
    this.contentPane.add(this.drawPanel);
    setContentPane(this.contentPane);
    
    JButton botaoSalvar = new JButton("Salvar");
    botaoSalvar.addActionListener(configAcao('s'));
    botaoSalvar.setBounds(10, 10, 90, 23);
    this.contentPane.add(botaoSalvar);
    
    JButton botaoLoad = new JButton("Load");
    botaoLoad.addActionListener(configAcao('l'));
    botaoLoad.setBounds(10, 50, 90, 23);
    this.contentPane.add(botaoLoad);
    
    JButton botaoRender = new JButton("Render");
    botaoRender.addActionListener(configAcao('r'));
    botaoRender.setBounds(10, 90, 90, 23);
    this.contentPane.add(botaoRender);
    
    JButton gifItButton = new JButton("Gif it!");
    gifItButton.addActionListener(configAcao('g'));
    gifItButton.setBounds(10, 390, 91, 23);
    this.contentPane.add(gifItButton);
    
    JButton botaoBackground = new JButton("<html>Imagem de<br>Refer�ncia");
    botaoBackground.addActionListener(configAcao('b'));
    botaoBackground.setBounds(10, 130, 109, 43);
    this.contentPane.add(botaoBackground);
    
    JButton botaoFullscreen = new JButton("<html>Video on<br>Fullscreen");
    botaoFullscreen.addActionListener(configAcao('f'));
    botaoFullscreen.setBounds(10, 184, 109, 43);
    this.contentPane.add(botaoFullscreen);
    
    String[] resolucoes = { "1080p", "720p", "360p" };
    DefaultComboBoxModel<String> comboModel = new DefaultComboBoxModel(resolucoes);
    JPanel panel = new JPanel();
    panel.setBorder(new TitledBorder(new EtchedBorder(1, null, null), "<html>Resolu��o do<br>v�deo resultado", 4, 2, null, new Color(0, 0, 0)));
    panel.setBounds(10, 245, 102, 123);
    this.contentPane.add(panel);
    panel.setLayout(null);
    this.resolucoesList = new JComboBox(comboModel);
    this.resolucoesList.setBounds(10, 37, 90, 23);
    panel.add(this.resolucoesList);
    
    DecimalFormat format = new DecimalFormat();
    format.setGroupingUsed(false);
    this.formatter = new NumberFormatter(format);
    
    this.formatter.setMinimum(Integer.valueOf(0));
    this.formatter.setMaximum(Integer.valueOf(Integer.MAX_VALUE));
    this.formatter.setAllowsInvalid(false);
    
    this.formatter.setCommitsOnValidEdit(true);
    
    this.customBase = new JFormattedTextField(this.formatter);
    this.customBase.setEnabled(false);
    this.customBase.setToolTipText("base");
    this.customBase.setBounds(10, 71, 35, 20);
    panel.add(this.customBase);
    
    this.customAltura = new JFormattedTextField(this.formatter);
    this.customAltura.setEnabled(false);
    this.customAltura.setToolTipText("altura");
    this.customAltura.setBounds(50, 71, 42, 20);
    panel.add(this.customAltura);
    
    this.customBase.setText("1920");
    this.customAltura.setText("1080");
    
    JLabel label = new JLabel("16:9");
    label.setBounds(10, 98, 46, 14);
    panel.add(label);
    
    this.customBaseEdit = new JFormattedTextField(this.formatter);
    this.customAlturaEdit = new JFormattedTextField(this.formatter);
    
    this.resolucoesList.addActionListener(configResolucao());
  }
}
