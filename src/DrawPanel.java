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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;

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
	
		private final JFileChooser selecionarVideo = new JFileChooser();
		private final File workingDirectory = new File(System.getProperty("user.dir"));
		//sem jar
		final String currentPath=DrawPanel.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(1).replace('/', '\\');
				
		//com jar
		//final String currentPath="."+DrawPanel.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(1).replace('/', '\\');
				
		public PaintSurface() {
			
			IntegracaoBasicaFFmpeg.comandos=new LinkedList<>();
			
			selecionarVideo.setCurrentDirectory(workingDirectory);
						
			//salvar ou load usando teclado
			KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher( configTeclado	);
			
			//config desenhaArrasta
			this.addMouseListener( configDesenhaArrasta );
			
			//bordinha enquanto desenha
			this.addMouseMotionListener( configSinuetaDinamicaDesenho );


			//menu popup
			// build poup menu
		    popup = new JPopupMenu();

			JMenuItem m1,m2,m3,m4;
			
		    m1 = new JMenuItem("Selecionar video");
		    m1.setMnemonic(KeyEvent.VK_V);
		    
		    m2 = new JMenuItem("Delete");
		    m2.setMnemonic(KeyEvent.VK_D);
		    
		    m3 = new JMenuItem("Cortar");
		    m3.setMnemonic(KeyEvent.VK_C);
		    
		    m4 = new JMenuItem("Inserir Texto nesta posicao");
		    m4.setMnemonic(KeyEvent.VK_T);
		    
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
		    	    
		    popup.add(m1);
		    popup.add(m2);	
		    popup.add(m3);
		    popup.add(m4);			    
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
		        	if(selecionarVideo.showOpenDialog(videoAtualpopup)==JFileChooser.APPROVE_OPTION){
		        		videoAtualpopup.video=selecionarVideo.getSelectedFile().getAbsolutePath();
						System.out.println(videoAtualpopup.video);
						VariavelGlobal.selecionouAoMenosUmVideo=true;
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
        	        	IntegracaoBasicaFFmpeg.executarFFmpeg(IntegracaoBasicaFFmpeg.cortar(field1.getText(), field2.getText(), videoAtualpopup.video));
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
	        		for(int i=0; i<videoAtualpopup.shape.ypoints.length;i++){
	        			System.out.println(videoAtualpopup.shape.ypoints[i]+" ");
	        		}
	        		videoAtualpopup.shape.ypoints[3]=videoAtualpopup.shape.ypoints[2]=(int) (videoAtualpopup.shape.ypoints[0]  + (0.8 * IntegracaoBasicaFFmpeg.tamanhoFonte));
	        		repaint();
	        		return;
	        	}else{
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

	        		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(configTeclado);
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
				
				case ('r'):
					if(VariavelGlobal.selecionouAoMenosUmVideo){
						String[] enderecoVideos = new String[id-1];
						int i=0;
						int tamanho= shapes.size();
						int[] xpoints = new int[tamanho], ypoints= new int[tamanho],x2points = new int[tamanho], y2points= new int[tamanho];
						
						int proporcaoWidth = VariavelGlobal.resolucaoWidthVideoOutput / VariavelGlobal.limitadorVermelhoX, 
								proporcaoHeight = VariavelGlobal.resolucaoHeightVideoOutput / VariavelGlobal.limitadorVermelhoY;
						for(VideoBox s: shapes){
							enderecoVideos[i]=s.video;
							xpoints[i]=s.shape.xpoints[0] * proporcaoWidth;
							ypoints[i]=s.shape.ypoints[0] * proporcaoHeight;
							
							x2points[i]=s.shape.xpoints[1] * proporcaoWidth;
							y2points[i]=s.shape.ypoints[3] * proporcaoHeight;
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

}