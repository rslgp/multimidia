#Projeto de Multimidia
Programa para gerar um video reproduzindo varios videos simultaneos em certa moldura, usando ffmpeg

##Tecnologias usadas:
ffmpeg<br>
java

##Input
- >= 2 videos<br>
- configuração de como os videos input estarão localizados no video output

##To-Do
* ~~Resultado com ffmpeg solo (**done**)~~<br>
* Integração ffmpeg-java sem GUI (~90%)<br>
* Brainstorm da GUI e de novas ideias<br>
* GUI em java para configuração do video output (Swing)<br>
  * Redimensionar os videos<br>
  * Escolher a localização de cada video input<br>
  * Escolher os arquivos dos videos ou a pasta dos arquivos<br>
  * (se possivel) Expor o log do processamento feito pelo ffmpeg em tela do java<br>
 <br>
* (se possivel) (sugestao professor) (se der tempo)
  * Integração ffmpeg-android<br>
  * GUI android para se comunicar com o core do projeto para ser a versão android<br>
   
[aguardando novas sugestoes, To be continued...]<br>

##Referência API (arquivo java: IntegracaoBasicaFFmpeg) 

em https://github.com/rslgp/multimidia/tree/master/src <br>
todos retornam um array de parametros (String[]) usados no ffmpeg e para rodar ele <br>
* cortar
 <br> pega um trecho de um video (input: tempo de inicio e final do trecho do video original a ser cortado)

* makeVideoResizedBorder 
 <br> ajusta o tamanho da screen do video

* mosaic 
 <br> junta varios vídeos em um só, e reproduz simultaneamente

* addText
 <br> adiciona um texto a um video (texto a ser inserido, proxima atualizacao por local do video a inserir)

##Equipe
Anderson, Eládia, Mayara, Rafael, Thiago
