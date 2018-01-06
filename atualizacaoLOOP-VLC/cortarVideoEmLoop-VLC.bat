@echo off
del vlc-log.txt > nul
set /p "video=arraste o video: "
start "" vlc --extraintf=http:logger --verbose=2 --file-logging --logfile=vlc-log.txt %video%
set /a "numero=1"
echo.
echo aperte enter apos pausar no tempo inicial e final do corte no video reproduzido no vlc
echo --SEMPRE CLIQUE ONDE VC QUER MARCAR (NAO PAUSE APENAS)--
echo -- SEMPRE PAUSE E CLIQUE (safe way)--
echo.
:loop
echo guardar inicio, enter:
PAUSE
capturarTempoAtualVLC vlc-log.txt>tempo.txt
timeout /t 1
set /p inicio=<tempo.txt
echo %inicio%

echo guardar final, enter:
PAUSE
capturarTempoAtualVLC vlc-log.txt>tempo.txt
timeout /t 1
set /p fim=<tempo.txt
echo %fim%

start "" ffmpeg -y -i %video% -ss %inicio% -to %fim% -c copy cortado%numero%.mp4

set /a "numero=%numero%+1"
echo.
echo parte %numero%
goto loop