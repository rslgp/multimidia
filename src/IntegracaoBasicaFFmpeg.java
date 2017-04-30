import java.util.LinkedList;

public class IntegracaoBasicaFFmpeg {
	
	final static String[] verbose = {"-loglevel","panic"};
	final static String pastaRaiz= IntegracaoBasicaFFmpeg.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(1).replace('/', '\\');
	final static String ffmpeg = pastaRaiz+"ffmpeg\\bin\\ffmpeg.exe";
	final static String enderecoArquivoVideo = pastaRaiz+"ffmpeg\\videoExemplo\\";
	final static String enderecoArquivoFonteTexto = pastaRaiz.charAt(0)+"\\\\"+pastaRaiz.substring(1).replace('\\', '/')+"ffmpeg/tutorial";
	
	static Process processoFFmpeg;
	
	static int tamanhoFonte=40;
	
	public static void executarFFmpeg(String[] parametros){		
		try {
			processoFFmpeg = Runtime.getRuntime().exec(parametros);
			processoFFmpeg.waitFor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static String getExtensaoVideo(String enderecoVideo){ return enderecoVideo.substring(enderecoVideo.lastIndexOf('.'));}
	
	/*
	 * para mostrar o progresso do ffmpeg (tirar o verbose), coloca antes do parametro ffmpeg 
//				"cmd",
//				"/c",
//				"start",
	 * */
	
	public static String[] padraoParametros(String input, String output, String[] core){
//		String arquivoEntrada = enderecoArquivoVideo+input,
//				arquivoSaida = enderecoArquivoVideo+output;
		String arquivoEntrada = input,
				arquivoSaida = output;
		
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
		
		imprimirParametros(parameters);
		
		return parameters;		
	}

	public static String[] cortar(String inicio, String fim, String enderecoVideo){
//		String input = "video.mp4",
//				output = "split.mp4";
		String input = enderecoVideo,
				output = enderecoVideo.substring(0,enderecoVideo.lastIndexOf('\\'))+"\\split"+getExtensaoVideo(enderecoVideo);
		
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
	
//	public static String[] makeVideoResizedBorder(){
//		String input = "split.mp4",
//				output = "out.mp4";
//		
//		String[] core={
//			"-filter_complex",
//			"\"scale=578:462,pad=720:576:71:57\""				
//		};		
//		
//		return padraoParametros(input, output, core);		
//	}
	
	public static String getScreenMosaic(int larguraScreen, int alturaScreen, int larguraPip, int alturaPip, int espacamentoAlturaEntrePip, int[] xpoints, int[] ypoints, int[] x2points, int[] y2points){
//		String nome0="[base]",nome1="[upperright]",nome2="[upperright]",nome3="[lowerright]";
//		String background= "\"color=c=black:size="+larguraScreen+"x"+alturaScreen+" "+nome0+"; ";
//		String colagem =  "overlay=shortest=1:x=" /* +(larguraScreen-larguraPip)*/;
//		String videoPadrao=":v] setpts=PTS-STARTPTS, scale="+larguraPip+"x"+alturaPip;
//		
//		String final1=" [tmp1]", final2="[tmp2]";
//		
//		String retorno= background+"[0"+videoPadrao+nome1+"; [1"+videoPadrao+nome2+"; [2"+videoPadrao+nome3+"; "
//		+nome0+nome1+colagem+xpoints[0]+":y="+ypoints[0]+final1+";"
//		+final1+nome2+colagem+xpoints[1]+":y="+ypoints[1]+final2+";"
//		+final2+nome3+colagem+xpoints[2]+":y="+ypoints[2]+"\"";
		
		
		int qtdVideos=xpoints.length;
		String retorno="\"color=c=black:size="+larguraScreen+"x"+alturaScreen+" [base]; ";
		for(int i=0; i<qtdVideos;i++){
			retorno+="["+i+":v] setpts=PTS-STARTPTS, scale="+ (x2points[i]-xpoints[i])+"x"+(y2points[i]-ypoints[i])+"["+(char)(i+'a')+"]; ";
		}
		retorno+="[base][a]overlay=shortest=1:x="+xpoints[0]+":y="+ypoints[0]+" [y];";
		for(int i=1; i<qtdVideos;i++){
			retorno+="["+(char)('z'-i)+"]["+(char)(i+'a')+"]overlay=shortest=1:x="+xpoints[i]+":y="+ypoints[i]+"  ["+(char)('z'-(i+1))+"];";
		}
		retorno = retorno.substring(0,retorno.length()- 6)+"\"";
				
//		 "[0:v] setpts=PTS-STARTPTS, scale=599x336[upperright];"
//		 "[1:v] setpts=PTS-STARTPTS, scale=599x336 [upperright];"
//		 "[2:v] setpts=PTS-STARTPTS, scale=599x336 [lowerright];"
//		 "[base][upperright] overlay=shortest=1:x=1321 [tmp1];"
//		 "[tmp1][upperright] overlay=shortest=1:x=1321:y=338 [tmp2];"
//		 "[tmp2][lowerright] overlay=shortest=1:x=1321:y=676\"";

		return retorno;
	}
	
	public static String[] mosaic(String[] enderecoVideos, int[] xpoints, int[] ypoints,int[] x2points, int[] y2points){
//		public static String[] mosaic(){
//		String input = "split.mp4",
//				input2 = enderecoArquivoVideo+"split.mp4",
//				input3 =enderecoArquivoVideo+"split.mp4",
//				output = "mosaic.mp4";
		String input = enderecoVideos[0],
				output=enderecoVideos[0].substring(0, enderecoVideos[0].lastIndexOf('\\'))+"\\mosaic"+getExtensaoVideo(enderecoVideos[0]);

		
		String[] coreMosaic={
				"-filter_complex",
				getScreenMosaic(1920,1080,599,336,2,xpoints,ypoints,x2points,y2points),
				"-c:v",
				"libx264"				
		};
//		String[] core={
//				"-i",
//				input2,
//				"-i",
//				input3,
//				"-filter_complex",
//				getScreenMosaic(1920,1080,599,336,2),
//				"-c:v",
//				"libx264"				
//		};
		
		int tamanho1=enderecoVideos.length,
				tamanho2=coreMosaic.length;
		
		int novoTamanho1=tamanho1*2;
		
		String[] core = new String[novoTamanho1 + tamanho2 -2];
		
		int i=0;
		for(int j=1; j<tamanho1;i+=2, j++){
			core[i]="-i";
			core[i+1]=enderecoVideos[j];
		}
		
		for(int j=0; j<tamanho2;i++, j++){
			core[i]=coreMosaic[j];
		}
		
		return padraoParametros(input, output, core);
	}
	
	public static String[] addText(String texto, int x, int y, int duracaoInicio, int duracaoFinal, String enderecoVideo){		
//		String input = "out.mp4",
//				output = "text.mp4",

		String input = enderecoVideo,
				output=enderecoVideo.substring(0, enderecoVideo.lastIndexOf('\\'))+"\\texto"+getExtensaoVideo(enderecoVideo);
		
		String fonte="\""+enderecoArquivoFonteTexto+"/HelveticaNeueLTStd-Th_1.otf\"",
		time="enable='between(t,"+duracaoInicio+","+duracaoFinal+")'";
		
		String[] core={
				"-vf",
				"drawtext="+time+":fontfile="+fonte+":text=\'"+texto+"\':fontcolor=white:fontsize="+tamanhoFonte+":x="+x+":y="+y,
				"-codec:a",
				"copy"
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
	
//	public static void main(String[] args) {
////		String arquivo= JOptionPane.showInputDialog("insira endereco do arquivo: ");
//		comandos = new LinkedList<String[]>();
//		try {
////			comandos.add(cortar("00:00:05.0","00:00:15.0"));
//			//comandos.add(girarVideo());
////			comandos.add(makeVideoResizedBorder());
//			comandos.add(mosaic());
////			comandos.add(addText("teste"));
//			
//			for(String[] parametros : comandos) executarFFmpeg(parametros);
//			
//			System.out.println("veja a pasta "+enderecoArquivoVideo);
//			
//		} catch (Exception e){e.printStackTrace();}
//		
//	}
}
