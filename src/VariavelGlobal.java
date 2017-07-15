import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;

import javax.swing.JFileChooser;

public final class VariavelGlobal {
    public static final int limitadorVermelhoX = 640,
    							limitadorVermelhoY=360;
    
    public static int resolucaoWidthVideoOutput = 1920,
    		resolucaoHeightVideoOutput=1080;
    

    public static boolean selecionouAoMenosUmVideo=false;
    
    protected static final JFileChooser selecionarVideo = new JFileChooser();

    public static String selecionarArquivo(KeyEventDispatcher configTeclado){
		KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(configTeclado);
		
    	if(selecionarVideo.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
    		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(configTeclado);
        	
    		return selecionarVideo.getSelectedFile().getAbsolutePath();
    	}
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(configTeclado);
    	
    	return null;
    }
    
}