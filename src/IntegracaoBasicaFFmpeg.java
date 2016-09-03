import java.util.LinkedList;

public class IntegracaoBasicaFFmpeg {
	
	final static String[] verbose = {"-loglevel","panic"};
	final static String pastaRaiz= "D:\\Downloads\\gitportable\\workspace\\multimidia\\src\\";
	final static String ffmpeg = pastaRaiz+"ffmpeg\\bin\\ffmpeg.exe";
	final static String enderecoArquivoVideo = pastaRaiz+"ffmpeg\\videoExemplo\\";
	final static String enderecoArquivoFonteTexto = pastaRaiz.charAt(0)+"\\\\"+pastaRaiz.substring(1).replace('\\', '/')+"ffmpeg/tutorial";
	
	static Process processoFFmpeg;
	
	public static void executarFFmpeg(String[] parametros) throws Exception{		
		processoFFmpeg = Runtime.getRuntime().exec(parametros);
		
		processoFFmpeg.waitFor();
	}

	public static String[] cortar(String inicio, String fim) throws Exception{
		String input = enderecoArquivoVideo+"video.mp4",
				output = enderecoArquivoVideo+"split.mp4";
				
		String[] parameters={
				ffmpeg,
				verbose[0],
				verbose[1],
				"-y",
				"-i",
				input,
				"-ss",
				inicio,
				"-c",
				"copy",
				"-t",
				fim,//"00:00:30.0"
				output
		};
		
		return parameters;
	}
	
	public static String[] makeVideoResizedBorder() throws Exception{
		String input = enderecoArquivoVideo+"split.mp4",
				output = enderecoArquivoVideo+"out.mp4";
//		String[] parameters={
//				ffmpeg, 
//				"-y",
//				"-i",
//				input,
//				"-f",
//				"lavfi",
//				"-i",
//				"color=c=black:s=1920x1080",
//				"-filter_complex",
//				"\"[0:v]scale=w=0.80*iw:h=0.80*ih[scaled];",
//				"[1:v][scaled]overlay=x=0.10*main_w:y=0.10*main_h:eof_action=endall[out];",
//				"[0:a]anull[aud]\"",
//				"-map",
//				"\"[out]\"",
//				"-map",
//				"\"[aud]\"",
//				"-strict",
//				"-2",
//				output
//		};
		
//		String[] parameters={
//				ffmpeg,
//				"-y",
//				"-i",
//				input,
//				"-filter_complex",
//				"\"scale=578:462,pad=720:576:71:57\"",
//				output
//		};
		
		String[] parameters={
//				"cmd",
//				"/c",
//				"start",
				ffmpeg,
				verbose[0],
				verbose[1],
				"-y",
				"-i",
				input,
				"-filter_complex",
				"\"scale=578:462,pad=720:576:71:57\"",
				output
		};

		imprimirParametros(parameters);
		
		return parameters;
		
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
		String input1 = enderecoArquivoVideo+"split.mp4",
				input2 = enderecoArquivoVideo+"split.mp4",
						input3 = enderecoArquivoVideo+"split.mp4",
				output = enderecoArquivoVideo+"out.mp4";

		String[] parameters={
				ffmpeg,
				verbose[0],
				verbose[1],
				"-y",
				"-i",
				input1,
				"-i",
				input2,
				"-i",
				input3,
				"-filter_complex",
				getScreenMosaic(1920,1080,599,336,2),
				"-c:v",
				"libx264",
				output
		};

		imprimirParametros(parameters);

//		executarFFmpeg(parameters);
		
		return parameters;
	}
	
	
//	public static String getText(){
//		String fonte="\""+enderecoArquivoFonteTexto+"/HelveticaNeueLTStd-Th_1.otf\"",
//				time="enable='between(t,0,10)'",
//				texto="teste";
//		
//		return "-vf drawtext="+time+":fontfile="+fonte+":text=\'"+texto+"\':fontcolor=white:fontsize=40:x=0:y=0";
//	}
	
	public static String[] addText(String texto) throws Exception{
		String input = enderecoArquivoVideo+"out.mp4",
				output = enderecoArquivoVideo+"text.mp4",
				fonte="\""+enderecoArquivoFonteTexto+"/HelveticaNeueLTStd-Th_1.otf\"",
				time="enable='between(t,0,3)'";
				
		String[] parameters={
				ffmpeg,
				verbose[0],
				verbose[1],
				"-y",
				"-i",
				input,
				"-vf",
				"drawtext="+time+":fontfile="+fonte+":text=\'"+texto+"\':fontcolor=white:fontsize=40:x=0:y=0",
				"-codec:a",
				"copy",
				output
		};

		
//		executarFFmpeg(parameters);
//		System.out.println("\n"+processoFFmpeg.getErrorStream());
		
		return parameters;
		
//		ffmpeg -i input.mp4 -vf drawtext="fontfile=/path/to/font.ttf: \
//				text='Stack Overflow': fontcolor=white: fontsize=24: box=1: boxcolor=black@0.5: \
//				boxborderw=5: x=(w-text_w)/2: y=(h-text_h)/2" -codec:a copy output.mp4
	}	

	public static void imprimirParametros(String[] parameters){
		for(String i : parameters) System.out.print(i+" ");	
		System.out.println("\r\n");
	}
	
	static LinkedList<String[]> comandos;
	
	public static void main(String[] args) {
//		String arquivo= JOptionPane.showInputDialog("insira endereco do arquivo: ");
		comandos = new LinkedList<String[]>();
		try {
			comandos.add(cortar("00:00:05.0","00:00:15.0"));
			comandos.add(makeVideoResizedBorder());
			comandos.add(mosaic());
			comandos.add(addText("teste"));
			
			for(String[] parametros : comandos) executarFFmpeg(parametros);
			
			System.out.println("veja a pasta "+enderecoArquivoVideo);
			
		} catch (Exception e){e.printStackTrace();}
		
	}

}
