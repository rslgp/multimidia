@echo off
set /p "video=arraste o video: "
D:\VLC\vlc.exe --extraintf=http:logger --verbose=2 --file-logging --logfile=vlc-log.txt %video%
set /a "numero=1"
echo.
echo tempos inicio e fim tem esse formato: 
echo (HH:MM:SS.0) ou em segundos (numero)
echo.
:loop
set /p "inicio=tempo inicio: "
set /p "fim=tempo final: "

start "" ffmpeg -y -i %video% -ss %inicio% -to %fim% -c copy cortado%numero%.mp4

set /a "numero=%numero%+1"
echo.
echo parte %numero%
goto loop