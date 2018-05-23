@echo off
REM echo a img de fundo tem o nome fundo.png e o gif eh gif.gif
REM set /p "ajuste=insira o ajuste de 0.100 a 1.000 (padrao 0.40): "

REM ffmpeg -y -i fundo.png -vf palettegen palette.png

REM 720 375 altura 189
REM 480 250 altura 126
set "altura=126"

ffmpeg -y -i fundo.png -vf scale=-1:480 fundo-scale.png

ffmpeg -y -i .\fundo-scale.png -i .\gif.gif -filter_complex "overlay=(W-w)/2:%altura%:shortest=1,format=yuv420p, palettegen" .\palette.png
ffmpeg -y -loop 1 -i .\fundo-scale.png -i .\gif.gif -i palette.png -filter_complex "[0:v]overlay=(W-w)/2:%altura%:shortest=1,format=yuv420p[vid],[vid][2]paletteuse" .\gifGerado.gif

del ".\fundo-scale.png"
del ".\palette.png"
del ".\gif-scale.gif"

REM PAUSE