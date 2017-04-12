import java.util.LinkedList;

public class IntegracaoBasicaFFmpeg {
	
	final static String[] verbose = {"-loglevel","panic"};
	final static String pastaRaiz= "C:\\Users\\rslgp\\Downloads\\multimidia-master\\src\\";
	final static String ffmpeg = pastaRaiz+"ffmpeg\\bin\\ffmpeg.exe";
	final static String enderecoArquivoVideo = pastaRaiz+"ffmpeg\\videoExemplo\\";
	final static String enderecoArquivoFonteTexto = pastaRaiz.charAt(0)+"\\\\"+pastaRaiz.substring(1).replace('\\', '/')+"ffmpeg/tutorial";
	
	static Process processoFFmpeg;
	
	public static void executarFFmpeg(String[] parametros) throws Exception{		
		processoFFmpeg = Runtime.getRuntime().exec(parametros);
		
		processoFFmpeg.waitFor();
	}
	
	/*
	 * para mostrar o progresso do ffmpeg, coloca antes do parametro ffmpeg 
//				"cmd",
//				"/c",
//				"start",
	 * */
	
	public static String[] padraoParametros(String input, String output, String[] core){
		String arquivoEntrada = enderecoArquivoVideo+input,
				arquivoSaida = enderecoArquivoVideo+output;
		
		String[] padrao={
//			"cmd",
//			"/c",
//			"start",
			ffmpeg,
			verbose[0],
			verbose[1],
			"-y",
			"-i",
			arquivoEntrada,
			//core,
			arquivoSaida
		};

		int tamanhoPadrao=padrao.length, tamanhoCore=core.length;
		
		String[] parameters = new String[ tamanhoPadrao + tamanhoCore ];
		int tamanhoParametros=parameters.length;
		
		int indexParametros=0;
		
		//preenchendo parametros com o padrao
		//output no final
		parameters[tamanhoParametros-1] = padrao[tamanhoPadrao-1];
		tamanhoPadrao--; //ja tirou o output		
		
		for(int i=0; i<tamanhoPadrao; i++){
			parameters[indexParametros++]=padrao[i];
		}
		
		//preenchendo parametros com o core
		for(int i=0; i<tamanhoCore; i++){
			parameters[indexParametros++]=core[i];
		}
		
//		imprimirParametros(parameters);
		
		return parameters;		
	}

	public static String[] cortar(String inicio, String fim) throws Exception{
		String input = "video.mp4",
				output = "split.mp4";
		
		String[] core={
			"-ss",
			inicio,
			"-c",
			"copy",
			"-t",
			fim,//"00:00:30.0"				
		};
		
		return padraoParametros(input, output, core);
	}
	
	public static String[] makeVideoResizedBorder() throws Exception{
		String input = "split.mp4",
				output = "out.mp4";
		
		String[] core={
			"-filter_complex",
			"\"scale=578:462,pad=720:576:71:57\""				
		};		
		
		return padraoParametros(input, output, core);		
	}
	
	public static String getScreenMosaic(int larguraScreen, int alturaScreen, int larguraPip, int alturaPip, int espacamentoAlturaEntrePip){
		String nome0="[base]",nome1="[upperright]",nome2="[upperright]",nome3="[lowerright]";
		String background= "\"color=c=black:size="+larguraScreen+"x"+alturaScreen+" "+nome0+"; ";
		String colagem =  "overlay=shortest=1:x="+(larguraScreen-larguraPip);
		String videoPadrao=":v] setpts=PTS-STARTPTS, scale="+larguraPip+"x"+alturaPip;
		
		String final1="[tmp1]", final2="[tmp2]";
		
		String retorno= background+"[0"+videoPadrao+nome1+"; [1"+videoPadrao+nome2+"; [2"+videoPadrao+nome3+"; "
		+nome0+nome1+colagem+" "+final1+";"
		+final1+nome2+colagem+":y="+(alturaPip+espacamentoAlturaEntrePip)*1+final2+";"
		+final2+nome3+colagem+":y="+(alturaPip+espacamentoAlturaEntrePip)*2+"\"";
				
//		 "[0:v] setpts=PTS-STARTPTS, scale=599x336[upperright];"
//		 "[1:v] setpts=PTS-STARTPTS, scale=599x336 [upperright];"
//		 "[2:v] setpts=PTS-STARTPTS, scale=599x336 [lowerright];"
//		 "[base][upperright] overlay=shortest=1:x=1321 [tmp1];"
//		 "[tmp1][upperright] overlay=shortest=1:x=1321:y=338 [tmp2];"
//		 "[tmp2][lowerright] overlay=shortest=1:x=1321:y=676\"";

		return retorno;
	}
	
	public static String[] mosaic() throws Exception{
		String input = "split.mp4",
				input2 = "split.mp4",
				input3 ="split.mp4",
				output = "out.mp4";
		
		String[] core={
				"-i",
				input2,
				"-i",
				input3,
				"-filter_complex",
				getScreenMosaic(1920,1080,599,336,2),
				"-c:v",
				"libx264"				
		};
		
		return padraoParametros(input, output, core);
	}
	
	public static String[] addText(String texto) throws Exception{		
		String input = "out.mp4",
				output = "text.mp4",
				fonte="\""+enderecoArquivoFonteTexto+"/HelveticaNeueLTStd-Th_1.otf\"",
				time="enable='between(t,0,3)'";
		
		String[] core={
				"-vf",
				"drawtext="+time+":fontfile="+fonte+":text=\'"+texto+"\':fontcolor=white:fontsize=40:x=0:y=0",
				"-codec:a",
				"copy",
		};
		
//		System.out.println("\n"+processoFFmpeg.getErrorStream());
		
		return padraoParametros(input, output, core);
	}	

	public static void imprimirParametros(String[] parameters){
		for(String i : parameters) System.out.print(i+" ");	
		System.out.println("\r\n");
	}
	
	public static String[] girarVideo(){
		String input = "split.mp4",
		output = "girado.mp4";
		
		String[] core={
			"-vf",
			"\"transpose=1\""				
		};
		
		return padraoParametros(input, output, core); 
	}
	static LinkedList<String[]> comandos;
	
	public static void main(String[] args) {
//		String arquivo= JOptionPane.showInputDialog("insira endereco do arquivo: ");
		comandos = new LinkedList<String[]>();
		try {
			comandos.add(cortar("00:00:05.0","00:00:15.0"));
			//comandos.add(girarVideo());
			comandos.add(makeVideoResizedBorder());
			comandos.add(mosaic());
			comandos.add(addText("teste"));
			
			for(String[] parametros : comandos) executarFFmpeg(parametros);
			
			System.out.println("veja a pasta "+enderecoArquivoVideo);
			
		} catch (Exception e){e.printStackTrace();}
		
	}
}