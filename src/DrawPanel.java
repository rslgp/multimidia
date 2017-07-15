import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
public class DrawPanel extends JPanel {
	
	private PaintSurface componente;
	public DrawPanel() {
		componente = new PaintSurface();
//		this.setBorder(new LineBorder(new Color(0, 0, 0), 5));
//		this.setBounds(76, 0, 536, 374);
//		this.setSize(620,520);
		this.setSize(VariavelGlobal.limitadorVermelhoX+5, VariavelGlobal.limitadorVermelhoY+5);
		this.setLayout(new BorderLayout());
		this.add(componente, BorderLayout.CENTER);
	}
	public void executarAcao(char acao){
		componente.executarAcao(acao);
	}
	

	//classe que cria objetos que guardam a forma, endereco do video, id
	private class VideoBox extends JComponent{
		public Polygon shape;
		public int id;
		public String video;
		public VideoBox(Polygon shape, int id){
			this.shape=shape;
			this.id=id;
		}
		
//		void determineDuration(){
//			if(video!=null) duration = IntegracaoBasicaFFmpeg.getDuration(video);
//		}
		
		String padraoSave(Rectangle rectangle){
			return rectangle.x+" "+rectangle.y+" "+rectangle.width+" "+rectangle.height;
		}
		
		@Override
		public String toString() {
			Rectangle retangulo = shape.getBounds();
			return padraoSave(retangulo);
		}
	}
	//fim classe videobox

	private class PaintSurface extends JComponent {		
		private final JPopupMenu popup;
		
		private JPanel panel,panel2;
		private JTextField field1,field2,field3,field4,field5,field6;
		
		private Graphics2D tela;
		private ArrayList<VideoBox> shapes = new ArrayList<VideoBox>();
	
		private Point startDrag, endDrag;
		
		private Polygon dragged;
		private Point lastLocation;
		
		boolean wantDraw=true;
		private int id=1;
		
		private String arquivoPath;
//		private final JFileChooser selecionarVideo = new JFileChooser();
		private final File workingDirectory = new File(System.getProperty("user.dir"));
		//sem jar
		final String currentPath=DrawPanel.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(1).replace('/', '\\');
				
		//com jar
		//final String currentPath="."+DrawPanel.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(1).replace('/', '\\');
			

	    private BufferedImage image;
	    
		public PaintSurface() {
			
			IntegracaoBasicaFFmpeg.comandos=new LinkedList<>();
			
			VariavelGlobal.selecionarVideo.setCurrentDirectory(workingDirectory);
						
			//salvar ou load usando teclado
			KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher( configTeclado	);
			
			//config desenhaArrasta
			this.addMouseListener( configDesenhaArrasta );
			
			//bordinha enquanto desenha
			this.addMouseMotionListener( configSinuetaDinamicaDesenho );


			//menu popup
			// build poup menu
		    popup = new JPopupMenu();

			JMenuItem m1,m2,m3,m4,m5,m6;
			
		    m1 = new JMenuItem("Selecionar video");
		    m1.setMnemonic(KeyEvent.VK_V);
		    
		    m2 = new JMenuItem("Delete");
		    m2.setMnemonic(KeyEvent.VK_D);
		    
		    m3 = new JMenuItem("Cortar");
		    m3.setMnemonic(KeyEvent.VK_C);
		    
		    m4 = new JMenuItem("Inserir Texto nesta posição");
		    m4.setMnemonic(KeyEvent.VK_T);
		    
		    m5 = new JMenuItem("Inserir Borda nesse video");
		    m5.setMnemonic(KeyEvent.VK_B);

		    m6 = new JMenuItem("Sobrescrever Audio nesse video");
		    m6.setMnemonic(KeyEvent.VK_A);
		    
		    //cortar
		    panel = new JPanel(new GridLayout(0, 1));
		    panel2 = new JPanel(new GridLayout(0, 1));

		    field1 = new JTextField("00:00:05.0");
	        field2 = new JTextField("00:00:15.0");

		    panel.add(new JLabel("tempo inicial no video original (HH:MM:SS.0)"));
		    panel.add(field1);

		    panel.add(new JLabel("tempo final no video original (HH:MM:SS.0)"));
		    panel.add(field2);
		    
		    //texto
		    panel2 = new JPanel(new GridLayout(0, 1));

	        field5 = new JTextField("Texto");
		    field3 = new JTextField("0");
	        field4 = new JTextField("3");
	        field6 = new JTextField("800x600");

		    panel2.add(new JLabel("Dimensao do video selecionado que vai receber o texto"));
		    panel2.add(field6);
		    
		    panel2.add(new JLabel("Texto escolhido"));
		    panel2.add(field5);
		    
		    panel2.add(new JLabel("segundos de video necessarios para aparecer o texto"));
		    panel2.add(field3);

		    panel2.add(new JLabel("segundos de video necessarios para desaparecer o texto"));
		    panel2.add(field4);

		    m1.addActionListener(popupconfig1());
		    m2.addActionListener(popupconfig2());
		    m3.addActionListener(popupconfig3());
		    m4.addActionListener(popupconfig4());
		    m5.addActionListener(popupconfig5());
		    m6.addActionListener(popupconfig6());
		    	    
		    popup.add(m1);
		    popup.add(m2);	
		    popup.add(m3);
		    popup.add(m4);	
		    popup.add(m5);	
		    popup.add(m6);			    
		    //fim menu popup
		}
		
		//desenhar
		public void desenharQuadradoColorido(int x1, int y1, int x2, int y2){
			Polygon r = makeRectangle(x1, y1, x2, y2);
			VideoBox r2 = new VideoBox(r,id++);
			shapes.add(r2);
			startDrag = null;
			endDrag = null;
			repaint();
		}
		
		public void desenharTextoCentral(Graphics g, String text, Polygon rect, Font font) {
			Paint old = tela.getPaint();
			tela.setPaint(Color.BLACK);
			
		    FontMetrics metrics = g.getFontMetrics(font);

		    int x = rect.xpoints[0] + (rect.xpoints[1]-rect.xpoints[0] - metrics.stringWidth(text)) / 2,
		    	y = rect.ypoints[0] + ((rect.ypoints[3]-rect.ypoints[0] - metrics.getHeight()) / 2) + metrics.getAscent();
		    
		    g.setFont(font);
		    g.drawString(text, x, y);
		    
			tela.setPaint(old);
		}		
		//fim desenhar
		
	//saveload
		public void salvarTxt(String enderecoArquivo, String texto){
			try{
				FileOutputStream outputStream = new FileOutputStream(enderecoArquivo);
				outputStream.write(texto.getBytes());
				outputStream.close();
			}catch(Exception ex){}		
		}
		
		public void loadTxt(String enderecoArquivo){
			try{
				BufferedReader in = new BufferedReader(new FileReader(enderecoArquivo));
				String line;
				while((line = in.readLine()) != null)
				{
					String[]a = line.split(" ");
					int x1=Integer.parseInt(a[0]), y1=Integer.parseInt(a[1]), x2=x1+Integer.parseInt(a[2]), y2=y1+Integer.parseInt(a[3]);
					
					//desenhar na tela com os dados
					desenharQuadradoColorido(x1,y1,x2,y2);
				}
				in.close();
			}catch(Exception ex){}
		}
	//fim saveload
	
	//popup config
	public VideoBox videoAtualpopup;
	public ActionListener popupconfig1(){
			return new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
					arquivoPath = VariavelGlobal.selecionarArquivo(configTeclado);
					if(arquivoPath!=null){
		        		videoAtualpopup.video=arquivoPath;
						System.out.println(videoAtualpopup.video);
						VariavelGlobal.selecionouAoMenosUmVideo=true; //easy fast solution fix error no video selected 
		        		//videoAtualpopup.determineDuration(); //determinar duracao usando ffmpeg
					}
		        }
		    };
	}	
	public ActionListener popupconfig2(){
		return new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	int idRemove=videoAtualpopup.id;
	        	shapes.remove(videoAtualpopup);
	        	
	        	for(VideoBox s2:shapes){
	        		if(s2.id>idRemove){
	        			s2.id--;
	        		}
	        	}
	        	id--;
	        	repaint();
	        	return;
	        }
	    };
	}
	
	public ActionListener popupconfig3(){
		return new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	if(videoAtualpopup.video==null){
	        		JOptionPane.showMessageDialog(null, "Selecione um video antes para depois extrair um trecho do video escolhido", "Erro", JOptionPane.INFORMATION_MESSAGE);
	        		return;
	        	}else{
	        		int result = JOptionPane.showConfirmDialog(null, panel, "Extrair intervalo",
		        	            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        	        
	        		if (result == JOptionPane.OK_OPTION) {
        	        	IntegracaoBasicaFFmpeg.executarFFmpeg(IntegracaoBasicaFFmpeg.cortar(field1.getText(), /*field2.getText()*/somarDuracoes(new String[]{field1.getText(),field2.getText()}), videoAtualpopup.video));
        	        }
	        	}	        	
	        }
	    };
	}	
	public ActionListener popupconfig4(){
		return new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	if(videoAtualpopup.video==null){
	        		JOptionPane.showMessageDialog(null, "Selecione um video antes para servir de base para inserir o texto", "Erro", JOptionPane.INFORMATION_MESSAGE);
//	        		for(int i=0; i<videoAtualpopup.shape.ypoints.length;i++){
//	        			System.out.println(videoAtualpopup.shape.ypoints[i]+" ");
//	        		}
	        		videoAtualpopup.shape.ypoints[3]=videoAtualpopup.shape.ypoints[2]=(int) (videoAtualpopup.shape.ypoints[0]  + (0.8 * IntegracaoBasicaFFmpeg.tamanhoFonte));
	        		repaint();
	        		return;
	        	}else{
	        		//desativar atalho teclado
	        		KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(configTeclado);
	        		int result = JOptionPane.showConfirmDialog(null, panel2, "Texto",
		        	            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
	    	        
	        		if (result == JOptionPane.OK_OPTION) {
	        			String[] dimensao = field6.getText().split("x");
	        			int x = videoAtualpopup.shape.xpoints[0] * Integer.parseInt(dimensao[0])/640,
	        					y = videoAtualpopup.shape.ypoints[0] * Integer.parseInt(dimensao[1])/480;

	        			System.out.println("executei texto");
	    	        	IntegracaoBasicaFFmpeg.executarFFmpeg(IntegracaoBasicaFFmpeg.addText(
		    	        			field5.getText(), 
		    	        			x, y, 
		    	        			Integer.parseInt(field3.getText()), Integer.parseInt(field4.getText()), videoAtualpopup.video) 
	    	        			);
	    	        } 

	        		//reativar atalho teclado
	        		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(configTeclado);
	        	}
	        }
	    };
	}	

	public ActionListener popupconfig5(){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(videoAtualpopup.video==null)
	        		JOptionPane.showMessageDialog(null, "Selecione um video antes para servir de base para inserir a borda", "Erro", JOptionPane.INFORMATION_MESSAGE);
				else{
	        		KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(configTeclado);
	        							
					JTextField field1 = new JTextField(),
							url= new JTextField("https://www.webpagefx.com/web-design/color-picker"),
							field2 = new JTextField();
					Object[] message = {
						"url para escolher cor",url,
					    "<html>insira a cor (hex code)(ex.: FFFFFF)<br><a href=https://www.webpagefx.com/web-design/color-picker>https://www.webpagefx.com/web-design/color-picker</a> :", field1,
					    "Escolha o tamanho da linha da borda (recomendado 7 a 28)", field2
					};
					int option = JOptionPane.showConfirmDialog(null, message, "Enter all your values", JOptionPane.OK_CANCEL_OPTION);
					if (option == JOptionPane.OK_OPTION)
					{
					    String value1 = field1.getText();
					    int value2 = Integer.parseInt(field2.getText());

						//https://www.webpagefx.com/web-design/color-picker/
//						Rectangle retangulo = videoAtualpopup.shape.getBounds();
						IntegracaoBasicaFFmpeg.executarFFmpeg(IntegracaoBasicaFFmpeg.addBorda(/*retangulo.width, retangulo.height,*/value1,value2,videoAtualpopup.video));				

					}
	        		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(configTeclado);
	        	}
			}
		};
	}

	public ActionListener popupconfig6(){
		return new ActionListener() {
	        public void actionPerformed(ActionEvent e) {

				if(videoAtualpopup.video==null)
	        		JOptionPane.showMessageDialog(null, "Selecione um video antes para servir de base para sobreescrever o audio", "Erro", JOptionPane.INFORMATION_MESSAGE);
				else{
					executarAcao('a');
	        	}	        	
	        }
	    };
	}	
	//fim popup config
		
	//configs
	//config teclado
	final KeyEventDispatcher configTeclado = new KeyEventDispatcher() {
		@Override
		public boolean dispatchKeyEvent(KeyEvent e) {
			synchronized (PaintSurface.class) {
				if(e.getID()==KeyEvent.KEY_RELEASED)
					executarAcao(Character.toLowerCase((char)e.getKeyCode()));
				return false;
			}
		}
	};
	
	boolean booleanBackgroundImage = false;
	public void executarAcao(char acao){
			switch(acao){
				case ('s'):
					String texto="";
					for(VideoBox s : shapes) texto+=s.toString()+"\r\n";
					
					salvarTxt(currentPath+"salvar.txt",texto);
					System.out.println(currentPath+"salvar.txt");
				break;
				
				case ('l'):
					System.out.println("load");
					loadTxt(currentPath+"salvar.txt");
				break;
				
				case ('a')://inserir audio
					arquivoPath = VariavelGlobal.selecionarArquivo(configTeclado);
					if(arquivoPath!=null)
//					if(selecionarVideo.showOpenDialog(videoAtualpopup)==JFileChooser.APPROVE_OPTION)
						IntegracaoBasicaFFmpeg.executarFFmpeg(IntegracaoBasicaFFmpeg.inserirAudio(arquivoPath, videoAtualpopup.video));
				break;

				case ('b'):
					booleanBackgroundImage=true;
				//reusei o jfile do video
				arquivoPath = VariavelGlobal.selecionarArquivo(configTeclado);
					if(arquivoPath!=null){
						try {
							image = ImageIO.read(new File(arquivoPath));
							image=ajustarTamanho(image,VariavelGlobal.limitadorVermelhoX,VariavelGlobal.limitadorVermelhoY);
							repaint();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				break;
				
				case ('r'):
					if(VariavelGlobal.selecionouAoMenosUmVideo){
						String[] enderecoVideos = new String[id-1];
						int i=0;
						int tamanho= shapes.size();
						int[] xpoints = new int[tamanho], ypoints= new int[tamanho],x2points = new int[tamanho], y2points= new int[tamanho];
						
						double proporcaoWidth = (double) VariavelGlobal.resolucaoWidthVideoOutput / (double) VariavelGlobal.limitadorVermelhoX, 
								proporcaoHeight = (double) VariavelGlobal.resolucaoHeightVideoOutput / (double) VariavelGlobal.limitadorVermelhoY;
						
						int proporcaoWidthResultado= (int)Math.round(proporcaoWidth), proporcaoHeightResultado = (int)Math.round(proporcaoHeight);
						for(VideoBox s: shapes){
							enderecoVideos[i]=s.video;
							xpoints[i]=s.shape.xpoints[0] * proporcaoWidthResultado;
							ypoints[i]=s.shape.ypoints[0] * proporcaoHeightResultado;
							
							x2points[i]=s.shape.xpoints[1] * proporcaoWidthResultado;
							y2points[i]=s.shape.ypoints[3] * proporcaoHeightResultado;
							i++;
						}
//						String[] b = IntegracaoBasicaFFmpeg.mosaic(enderecoVideos,xpoints,ypoints);
//						System.out.println(b[b.length-1]);
						IntegracaoBasicaFFmpeg.executarFFmpeg(IntegracaoBasicaFFmpeg.mosaic(enderecoVideos,xpoints,ypoints,x2points,y2points));						
					}else{
		        		JOptionPane.showMessageDialog(null, "Selecione ao menos um video antes de usar render", "Erro", JOptionPane.INFORMATION_MESSAGE);
					}
					
					break;
			}
	}
	//fim configteclado
	
	//config desenha arrasta
	final MouseAdapter configDesenhaArrasta = new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
			wantDraw=true;
			for(VideoBox s : shapes) {
				if (s.shape.contains(e.getPoint())) {//check if mouse is clicked within shape
					wantDraw=false;
					System.out.println("Clicked a "+s.id);
					dragged = s.shape;
					lastLocation = e.getPoint();
					
					if(e.getButton()==e.BUTTON3){
						videoAtualpopup=s;
						popup.show(e.getComponent(), e.getX(), e.getY());
					}			
					return;//ja entendi o q ele quer nao preciso ver os outros, sair
				}
			}
			
			if(wantDraw){
				startDrag = new Point(e.getX(), e.getY());
				endDrag = startDrag;
			}
		}
		
		public void mouseReleased(MouseEvent e) {
			if(startDrag==endDrag)return;
			if(wantDraw)
				desenharQuadradoColorido(startDrag.x, startDrag.y, e.getX(), e.getY());
		}
	};
	//fim config desenha arrasta
	
	final MouseMotionAdapter configSinuetaDinamicaDesenho = new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				if(wantDraw){
					endDrag = new Point(e.getX(), e.getY());
					repaint();
				}else{
					dragged.translate(e.getX() - lastLocation.x, e.getY() - lastLocation.y);
					lastLocation = e.getPoint();
					repaint();
				}
			}
		};
	//fim configs
		
	//desenha na tela ( repaint() usa esse metodo)
		public void paint(Graphics g) {
			//se escolheu por imagem de fundo
			if(booleanBackgroundImage) g.drawImage(image, 0, 0, this);
			
			tela = (Graphics2D) g;
			tela.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			Color[] colors = { Color.YELLOW, Color.MAGENTA, Color.CYAN , Color.RED /*, Color.BLUE*/};
			int qtdCores=colors.length;
			int colorIndex = 0;
	
			tela.setStroke(new BasicStroke(2)); //define a grossura da linha
			tela.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.50f));
			
			//pintar video base

			tela.setPaint(Color.RED);
			tela.draw(makeRectangle(0, 0, VariavelGlobal.limitadorVermelhoX, VariavelGlobal.limitadorVermelhoY));
			
			gridMaker(tela, VariavelGlobal.limitadorVermelhoX, VariavelGlobal.limitadorVermelhoY, 4,4);
			
			for (VideoBox s : shapes) {
				tela.setPaint(Color.BLACK);
				tela.draw(s.shape);
				tela.setPaint(colors[(colorIndex++) % qtdCores]);
				tela.fill(s.shape);
				
				desenharTextoCentral(g, s.id+"", s.shape, new Font("Arial", 0, 18));
			}
	
			if (startDrag != null && endDrag != null) { //pintar contornado
				tela.setPaint(Color.LIGHT_GRAY);
				Polygon r = makeRectangle(startDrag.x, startDrag.y, endDrag.x, endDrag.y);
				tela.draw(r);
			}
		}
		
	//poligono
		private Polygon RectangleToPolygon(Rectangle2D.Float rect){
			Polygon result = new Polygon();
			result.addPoint((int)rect.x, (int)rect.y);
			result.addPoint((int)(rect.x + rect.width), (int)rect.y);
			result.addPoint((int)(rect.x + rect.width), (int)( rect.y + rect.height));
			result.addPoint((int)rect.x, (int)(rect.y + rect.height));
			return result;
		}
		
		private Polygon makeRectangle(int x1, int y1, int x2, int y2) {
			return RectangleToPolygon( new Rectangle2D.Float(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2)) );
		}
		
		private void gridMaker(Graphics2D g, int width, int height, int rows, int columns){
			g.setStroke(new BasicStroke(1));
			int k;
			int htOfRow = height / (rows);
			for (k = 0; k < rows; k++)
			g.drawLine(0, k * htOfRow , width, k * htOfRow );
			
			int wdOfRow = width / (columns);
			for (k = 0; k < columns; k++)
			g.draw(new Line2D.Float(k*wdOfRow , 0, k*wdOfRow , height));
		}
	//fim poligono
	}
	
	
	public String somarDuracoes(String time[]){
	        int hours = 0, minutes = 0, seconds = 0, miliseconds=0;
	        int valores[][] = new int[2][4];
	        int indice=0;
	        for (String string : time) {
	            String temp[] = string.split(":");
	            valores[indice][0]=Integer.valueOf(temp[0]);
	            valores[indice][1]=Integer.valueOf(temp[1]);
//	            hours = hours - Integer.valueOf(temp[0]);
//	            minutes = minutes - Integer.valueOf(temp[1]);
	            
	            String splitSegundos[] = temp[2].split("\\.");
//	            seconds = seconds - Integer.valueOf(splitSegundos[0]);
	            valores[indice][2]=Integer.valueOf(splitSegundos[0]);
	            valores[indice][3]=Integer.valueOf(splitSegundos[1]);
//	            miliseconds = miliseconds - Integer.valueOf(splitSegundos[1]);
	            indice++;
	            
	        }
	        hours = valores[1][0] - valores[0][0];
	        minutes = valores[1][1] - valores[0][1];
	        seconds =  valores[1][2] - valores[0][2];
	        miliseconds =  valores[1][3] - valores[0][3];
	        System.out.println(hours + ":" + minutes + ":" + seconds + "." +miliseconds);
	        if (miliseconds == 60) {
	            seconds = seconds + 1;
	            miliseconds = 0;
	        } else if (miliseconds > 59) {
	            seconds = seconds + (miliseconds / 60);
	            miliseconds = miliseconds % 60;
	        }
	        
	        System.out.println(hours + ":" + minutes + ":" + seconds);
	        if (seconds == 60) {
	            minutes = minutes + 1;
	            seconds = 0;
	        } else if (seconds > 59) {
	            minutes = minutes + (seconds / 60);
	            seconds = seconds % 60;
	        }
	        System.out.println(hours + ":" + minutes + ":" + seconds);
	        if (minutes == 60) {
	            hours = hours + 1;
	            minutes = 0;
	        } else if (minutes > 59) {
	            hours = hours + (minutes / 60);
	            minutes = minutes % 60;
	        }
	        System.out.println(hours + ":" + minutes + ":" + seconds);
	        String output = "";
	        output = String.valueOf(String.format("%02d", hours));
	        output = output.concat(":" + (String.format("%02d", minutes)));
	        output = output.concat(":" + (String.format("%02d", seconds)));
	        output = output.concat("." + (String.format("%01d", miliseconds)));
	        System.out.println(output);
	        return output;
	}
	
	//resize imagem de fundo
	public static BufferedImage ajustarTamanho(BufferedImage img, int newW, int newH) {  
	    int w = img.getWidth();  
	    int h = img.getHeight();  
	    BufferedImage dimg = new BufferedImage(newW, newH, img.getType());  
	    Graphics2D g = dimg.createGraphics();  
	    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	    RenderingHints.VALUE_INTERPOLATION_BILINEAR);  
	    g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);  
	    g.dispose();  
	    return dimg;  
	}
}
