import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class GUI extends JFrame {
	
	public String getDesktop(){return System.getProperty("user.home") + "\\Desktop";}
	String caminhoSave = getDesktop()+"\\salvar.txt";
	
	public GUI() {
		this.setSize(300, 300);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().add(new PaintSurface(), BorderLayout.CENTER);	
		this.setVisible(true);
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

	private class PaintSurface extends JComponent {
		Graphics2D tela;
		ArrayList<VideoBox> shapes = new ArrayList<VideoBox>();
	
		Point startDrag, endDrag;
		
		private Polygon dragged;
		private Point lastLocation;
		
		boolean wantDraw=true;
		int id=0;
	
		JFileChooser selecionarVideo = new JFileChooser();
		File workingDirectory = new File(System.getProperty("user.dir"));
		
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
					Polygon r = makeRectangle(x1, y1, x2, y2);
					VideoBox r2 = new VideoBox(r,id++);
					shapes.add(r2);
					repaint();
				}
				in.close();
			}catch(Exception ex){}
		}
	//fim saveload
		
	//configs
		public KeyEventDispatcher configTeclado(){
			return new KeyEventDispatcher() {
				@Override
				public boolean dispatchKeyEvent(KeyEvent e) {
					synchronized (PaintSurface.class) {
						if(e.getID()==KeyEvent.KEY_RELEASED){						
							switch(e.getKeyCode()){
								case ('s'-32):
									String texto="";
									for(VideoBox s : shapes) texto+=s.toString()+"\r\n";
									
									salvarTxt(getDesktop()+"\\salvar.txt",texto);
								break;
								
								case ('l'-32):
									System.out.println("load");
									loadTxt(getDesktop()+"\\salvar.txt");
								break;							
							}						
						}
						return false;
					}
				}
			};
		}
		
		public MouseAdapter configDesenhaArrasta(){
			return new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					wantDraw=true;
					for(VideoBox s : shapes) {
						if (s.shape.contains(e.getPoint())) {//check if mouse is clicked within shape
							wantDraw=false;
							System.out.println("Clicked a "+s.id);
							dragged = s.shape;
							lastLocation = e.getPoint();
							
							if(e.getButton()==e.BUTTON3){
								if(selecionarVideo.showOpenDialog(s)==JFileChooser.APPROVE_OPTION){
									s.video=selecionarVideo.getSelectedFile().getAbsolutePath();
									System.out.println(s.video);
								}
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
					if(wantDraw){
						Polygon r = makeRectangle(startDrag.x, startDrag.y, e.getX(), e.getY());
						VideoBox r2 = new VideoBox(r,id++);
						shapes.add(r2);
						startDrag = null;
						endDrag = null;
						repaint();
					}
				}
			};
		}
	
		public MouseMotionAdapter configSinuetaDinamicaDesenho(){
			return new MouseMotionAdapter() {
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
		}
	//fim configs
		
		public PaintSurface() {
			selecionarVideo.setCurrentDirectory(workingDirectory);
			
			//salvar ou load usando teclado
			KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher( configTeclado()	);
			
			//config desenhaArrasta
			this.addMouseListener( configDesenhaArrasta() );
			
			//bordinha enquanto desenha
			this.addMouseMotionListener( configSinuetaDinamicaDesenho() );
		}
		
	//desenha na tela ( repaint() usa esse metodo)
		public void paint(Graphics g) {
			tela = (Graphics2D) g;
			tela.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			Color[] colors = { Color.YELLOW, Color.MAGENTA, Color.CYAN , Color.RED, Color.BLUE};
			int qtdCores=colors.length;
			int colorIndex = 0;
	
			tela.setStroke(new BasicStroke(2));
			tela.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.50f));
	
			for (VideoBox s : shapes) {
				tela.setPaint(Color.BLACK);
				tela.draw(s.shape);
				tela.setPaint(colors[(colorIndex++) % qtdCores]);
				tela.fill(s.shape);
			}
	
			if (startDrag != null && endDrag != null) {
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
	//fim poligono
	}
}