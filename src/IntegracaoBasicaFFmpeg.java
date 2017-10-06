import java.io.PrintStream;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.LinkedList;

public class IntegracaoBasicaFFmpeg
{
  private static final String[] verbose = { "-loglevel", "panic" };
  static final String pastaRaiz = "." + IntegracaoBasicaFFmpeg.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(1).replace('/', '\\');
  private static final String ffmpeg = pastaRaiz + "ffmpeg\\ffmpeg.exe";
  private static final String enderecoArquivoVideo = pastaRaiz + "ffmpeg\\videoExemplo\\";
  private static final String enderecoArquivoFonteTexto = pastaRaiz.charAt(0) + "\\\\" + pastaRaiz.substring(1).replace('\\', '/') + "ffmpeg/tutorial";
  private static Process processoFFmpeg;
  public static int tamanhoFonte = 40;
  private static String[] juntarAudios = new String[6];
  private static String arquivoCortar = "split";
  static LinkedList<String[]> comandos;
  
  public static void executarFFmpeg(String[] parametros)
  {
    try
    {
      processoFFmpeg = Runtime.getRuntime().exec(parametros);
      processoFFmpeg.waitFor();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static String getExtensaoVideo(String enderecoVideo)
  {
    return enderecoVideo.substring(enderecoVideo.lastIndexOf('.'));
  }
  
  public static String[] padraoParametros(String input, String output, String[] core)
  {
    String[] padrao = {
      "cmd", 
      "/c", 
      "start", 
      ffmpeg, 
      
      "-y", 
      "-i", 
      input, 
      
      output };
    
    int tamanhoPadrao = padrao.length;int tamanhoCore = core.length;
    
    String[] parameters = new String[tamanhoPadrao + tamanhoCore];
    int tamanhoParametros = parameters.length;
    
    int indexParametros = 0;
    
    parameters[(tamanhoParametros - 1)] = padrao[(tamanhoPadrao - 1)];
    tamanhoPadrao--;
    for (int i = 0; i < tamanhoPadrao; i++) {
      parameters[(indexParametros++)] = padrao[i];
    }
    for (int i = 0; i < tamanhoCore; i++) {
      parameters[(indexParametros++)] = core[i];
    }
    imprimirParametros(parameters);
    
    return parameters;
  }
  
  public static String[] gifit(String enderecoVideo, String inicio, String fim)
  {
    String input = enderecoVideo;
    String output = enderecoVideo.substring(0, enderecoVideo.lastIndexOf('\\')) + "\\gifGerado.gif";
    
    String[] core = {
      "-ss", 
      inicio, 
      "-to", 
      fim, 
      "-b:v", 
      "2048k", 
      "-r", 
      "10", 
      "-vf", 
      "scale=440:-1" };
    
    return padraoParametros(input, output, core);
  }
  
  public static String[] cortar(String inicio, String fim, String enderecoVideo, boolean temFinal)
  {
    String input = enderecoVideo;
    String output = enderecoVideo.substring(0, enderecoVideo.lastIndexOf('\\')) + "\\" + arquivoCortar + getExtensaoVideo(enderecoVideo);
    
    String[] parametroFim = { "-to", fim };
    if (!temFinal) {
      parametroFim[0] = (parametroFim[1] = "");
    }
    String[] core = {
      "-ss", 
      inicio, 
      parametroFim[0], 
      parametroFim[1], 
      "-c", 
      "copy" };
    
    arquivoCortar = "split";
    return padraoParametros(input, output, core);
  }
  
  public static String[] juntar(String enderecoVideo)
  {
    String pasta = enderecoVideo.substring(0, enderecoVideo.lastIndexOf('\\')) + "\\";
    
    String input = pasta + "tempEnderecos.txt";
    String output = pasta + "juntado" + getExtensaoVideo(enderecoVideo);
    
    String[] core = {
      "cmd", 
      "/c", 
      "start", 
      ffmpeg, 
      
      "-y", 
      "-f", 
      "concat", 
      "-safe", 
      "0", 
      "-i", 
      input, 
      "-codec", 
      "copy", 
      output };
    
    imprimirParametros(core);
    return core;
  }
  
  public static String getScreenMosaic(int larguraScreen, int alturaScreen, int larguraPip, int alturaPip, int espacamentoAlturaEntrePip, int[] xpoints, int[] ypoints, int[] x2points, int[] y2points)
  {
    int qtdVideos = xpoints.length;
    String retorno = "\"color=c=black:size=" + larguraScreen + "x" + alturaScreen + " [base]; ";
    for (int i = 0; i < qtdVideos; i++) {
      retorno = retorno + "[" + i + ":v] setpts=PTS-STARTPTS, scale=" + (x2points[i] - xpoints[i]) + "x" + (y2points[i] - ypoints[i]) + "[" + (char)(i + 97) + "]; ";
    }
    retorno = retorno + "[base][a]overlay=shortest=1:x=" + xpoints[0] + ":y=" + ypoints[0] + " [y];";
    for (int i = 1; i < qtdVideos; i++) {
      retorno = retorno + "[" + (char)(122 - i) + "][" + (char)(i + 97) + "]overlay=shortest=1:x=" + xpoints[i] + ":y=" + ypoints[i] + "  [" + (char)(122 - (i + 1)) + "];";
    }
    retorno = retorno.substring(0, retorno.length() - 6) + "\"";
    
    return retorno;
  }
  
  public static String[] mosaic(String[] enderecoVideos, int[] xpoints, int[] ypoints, int[] x2points, int[] y2points)
  {
    String input = enderecoVideos[0];
    String output = enderecoVideos[0].substring(0, enderecoVideos[0].lastIndexOf('\\')) + "\\mosaic" + getExtensaoVideo(enderecoVideos[0]);
    if (enderecoVideos.length > 1)
    {
      juntarAudios[0] = "-filter_complex";
      juntarAudios[1] = "\"amerge\"";
      juntarAudios[2] = "-ac";
      juntarAudios[3] = "2";
      juntarAudios[4] = "-c:a";
      juntarAudios[5] = "libmp3lame";
    }
    else
    {
      juntarAudios[0] = "";
      juntarAudios[1] = "";
      juntarAudios[2] = "";
      juntarAudios[3] = "";
      juntarAudios[4] = "";
      juntarAudios[5] = "";
    }
    String[] coreMosaic = {
      juntarAudios[0], 
      juntarAudios[1], 
      "-filter_complex", 
      getScreenMosaic(VariavelGlobal.resolucaoWidthVideoOutput, VariavelGlobal.resolucaoHeightVideoOutput, 599, 336, 2, xpoints, ypoints, x2points, y2points), 
      "-c:v", 
      "libx264", 
      juntarAudios[2], 
      juntarAudios[3], 
      juntarAudios[4], 
      juntarAudios[5] };
    
    int tamanho1 = enderecoVideos.length;
    int tamanho2 = coreMosaic.length;
    
    int novoTamanho1 = tamanho1 * 2;
    
    String[] core = new String[novoTamanho1 + tamanho2 - 2];
    
    int i = 0;
    for (int j = 1; j < tamanho1; j++)
    {
      core[i] = "-i";
      core[(i + 1)] = enderecoVideos[j];i += 2;
    }
    for (int j = 0; j < tamanho2; j++)
    {
      core[i] = coreMosaic[j];i++;
    }
    return padraoParametros(input, output, core);
  }
  
  public static String[] addText(String texto, int x, int y, int duracaoInicio, int duracaoFinal, String enderecoVideo)
  {
    String input = enderecoVideo;
    String output = enderecoVideo.substring(0, enderecoVideo.lastIndexOf('\\')) + "\\texto" + getExtensaoVideo(enderecoVideo);
    
    String fonte = "\"" + enderecoArquivoFonteTexto + "/HelveticaNeueLTStd-Th_1.otf\"";
    String time = "enable='between(t," + duracaoInicio + "," + duracaoFinal + ")'";
    
    String[] core = {
      "-vf", 
      "drawtext=" + time + ":fontfile=" + fonte + ":text='" + texto + "':fontcolor=white:fontsize=" + tamanhoFonte + ":x=" + x + ":y=" + y, 
      "-codec:a", 
      "copy" };
    
    return padraoParametros(input, output, core);
  }
  
  public static String[] addBorda(String hexCor, int tamanhoLinha, String enderecoVideo)
  {
    String input = enderecoVideo;
    String output = enderecoVideo.substring(0, enderecoVideo.lastIndexOf('\\')) + "\\borda" + getExtensaoVideo(enderecoVideo);
    
    String[] core = {
      "-vf", 
      "drawbox= x=0:y=0:0:0:color=0x" + hexCor + ":t=" + tamanhoLinha };
    
    return padraoParametros(input, output, core);
  }
  
  public static String[] inserirAudio(String enderecoAudio, String enderecoVideo)
  {
    String input = enderecoVideo;
    String output = enderecoVideo.substring(0, enderecoVideo.lastIndexOf('\\')) + "\\audioInserido" + getExtensaoVideo(enderecoVideo);
    
    String[] core = {
      "-i", 
      enderecoAudio, 
      "-c:v", 
      "copy", 
      "-c:a", 
      "aac", 
      "-strict", 
      "experimental", 
      "-map", 
      "0:v:0", 
      "-map", 
      "1:a:0" };
    
    return padraoParametros(input, output, core);
  }
  
  public static void imprimirParametros(String[] parameters)
  {
    String[] arrayOfString = parameters;int j = parameters.length;
    for (int i = 0; i < j; i++)
    {
      String i = arrayOfString[i];System.out.print(i + " ");
    }
    System.out.println("\r\n");
  }
  
  public static String[] girarVideo()
  {
    String input = "split.mp4";
    String output = "girado.mp4";
    
    String[] core = {
      "-vf", 
      "\"transpose=1\"" };
    
    return padraoParametros(input, output, core);
  }
  
  public static String[] cortarJ(String arquivoSaida, String inicio, String fim, String enderecoVideo, boolean temFinal)
  {
    arquivoCortar = arquivoSaida;
    return cortar(inicio, fim, enderecoVideo, temFinal);
  }
}
