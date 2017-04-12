package b;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class GUI extends JFrame {
  public GUI() {
	this.setSize(300, 300);
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	getContentPane().add(new PaintSurface(), BorderLayout.CENTER);	
	this.setVisible(true);
  }
  
  private class VideoBox extends JComponent{
	  public Polygon shape;
	  public int id;
	  public String video;
	  public VideoBox(Polygon shape, int id){
		  this.shape=shape;		  
		  this.id=id;
	  }
  }

  private class PaintSurface extends JComponent {
	ArrayList<VideoBox> shapes = new ArrayList<VideoBox>();

	Point startDrag, endDrag;
	
    private Polygon dragged;
    private Point lastLocation;
	
    boolean wantDraw=true;
	int id=0;

	JFileChooser selecionarVideo = new JFileChooser();
	File workingDirectory = new File(System.getProperty("user.dir"));
	
	public PaintSurface() {
		selecionarVideo.setCurrentDirectory(workingDirectory);
		
		this.addMouseListener(new MouseAdapter() {
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
		});
		
		this.addMouseMotionListener(new MouseMotionAdapter() {
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
		});
	}
	
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Color[] colors = { Color.YELLOW, Color.MAGENTA, Color.CYAN , Color.RED, Color.BLUE};
		int qtdCores=colors.length;
		int colorIndex = 0;

		g2.setStroke(new BasicStroke(2));
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.50f));

		for (VideoBox s : shapes) {
			g2.setPaint(Color.BLACK);
			g2.draw(s.shape);
			g2.setPaint(colors[(colorIndex++) % qtdCores]);
			g2.fill(s.shape);
		}

		if (startDrag != null && endDrag != null) {
			g2.setPaint(Color.LIGHT_GRAY);
			Polygon r = makeRectangle(startDrag.x, startDrag.y, endDrag.x, endDrag.y);
			g2.draw(r);
		}
	}
	
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
  }
}