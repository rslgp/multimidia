@echo off
set /p "video=insira video: "
set /p "inicio=inicio hh:mm:ss : "

set /p "fim=fim hh:mm:ss : "

set filtros="fps=10,scale=440:-1:flags=lanczos"

ffmpeg -y -i %video% -ss %inicio% -vframes 1 frame1.png
ffmpeg -y -i frame1.png -vf palettegen palette.png
ffmpeg -y -i %video% -ss %inicio% -to %fim% -b:v 2048k -r 10 cortado.mp4
ffmpeg -y -i cortado.mp4 -i palette.png -lavfi "%filtros% [x]; [x][1:v] paletteuse" gifGerado.gif

del ".\frame1.png"
del ".\palette.png"
del ".\cortado.mp4