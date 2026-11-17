#!/bin/bash
# Bloque 14 (2.24): Personalización (temas gratis).
# Uso: bloque14.sh API APK CARPETA
API=$1; APK=$2; OUT=$3; mkdir -p $OUT
PKG=com.stradaos.launcher.debug
ACT=$PKG/com.stradaos.launcher.Main
adb wait-for-device
for i in $(seq 1 100); do [ "$(adb shell getprop sys.boot_completed | tr -d '\r')" = "1" ] && break; sleep 3; done
sleep 15
adb shell input keyevent 82 || true
size() {  # tamaño de la pantalla, densidad 160
  if [ "$API" -ge 18 ]; then adb shell wm size $1; adb shell wm density 160
  else adb shell am display-size $1; adb shell am display-density 160; fi
  sleep 6
}
size 1024x600
SW=1024; SH=600
if [ "$API" -ge 23 ]; then adb install -r -g "$APK" | tail -1; else adb install -r "$APK" | tail -1; fi
shot() {
  rm -f /tmp/shot.png; rm -rf /tmp/emushot; mkdir -p /tmp/emushot
  adb shell screencap -p /data/local/tmp/s.png >/dev/null 2>&1 && adb pull /data/local/tmp/s.png /tmp/shot.png >/dev/null 2>&1
  if [ ! -s /tmp/shot.png ] || [ "$(stat -c %s /tmp/shot.png)" -lt 15000 ]; then
    adb emu screenrecord screenshot /tmp/emushot >/dev/null 2>&1; sleep 2
    f=$(ls -t /tmp/emushot/*.png 2>/dev/null | head -1)
    [ -n "$f" ] && cp "$f" /tmp/shot.png
  fi
  [ -s /tmp/shot.png ] && cp /tmp/shot.png "$OUT/$1.png" && echo "captura $1"
}
go() { adb shell am start -n $ACT --es prueba "$1" >/dev/null; sleep "${2:-5}"; }
back() { adb shell input keyevent 4; sleep 2; }
adb shell am start -n $ACT --es prueba saltar >/dev/null; sleep 25
go idioma:es 6; go tema:oscuro 6; go inicio 4
go idioma:es 6; go tema:oscuro 6; go inicio 4; go pista-demo 4; go vel:62 3
shot p01-inicio
# ajustes «sucios» como los de un usuario que ya movió cosas: el tema debe fijar todo lo que muestra su miniatura
go pref:map_gauge_corner=tl 3; go pref:nav_btn_side=right 3; go pref:aa_panel_open=false 3
go ajustes:11 5; shot p02-personalizacion
adb shell input swipe 600 500 600 150 400; sleep 2; shot p03-personalizacion-abajo
go vista-temas:v 5; shot p04-vista-vertical
go vista-temas:h 4
for t in 1 2 3 4; do
  go tema-gratis:$t 5; shot p1$t-tema-$t-ajustes
  go inicio 4; go pista-demo 3; go vel:62 3; shot p2$t-tema-$t-inicio
done
go tema:claro 6; go ajustes:11 5; shot p30-claro
go vista-temas:v 4; shot p30b-claro-vertical
go vista-temas:h 3
go idioma:en 6; go ajustes:11 5; shot p31-ingles
go idioma:ar 6; go ajustes:11 5; shot p32-arabe
go idioma:zh 6; go ajustes:11 5; shot p33-chino
go idioma:es 6; go tema:oscuro 5
# vertical: misma prueba con la pantalla alta
if [ "$API" -ge 18 ]; then size 1080x1920; else go sim:7 6; fi
go pref:map_gauge_corner=tl 3; go pref:nav_btn_side=right 3
go ajustes:11 6; shot p40-vertical
go vista-temas:h 4; shot p41-vertical-vista-horizontal
for t in 1 2 3 4; do
  go tema-gratis:$t 5
  go inicio 4; go pista-demo 3; go vel:62 3; shot p5$t-vertical-tema-$t-inicio
done
go ajustes:11 5; shot p59-vertical-ajustes-final
if [ "$API" -ge 18 ]; then size 1024x600; else go sim:0 6; fi
go inicio 5
adb logcat -d | grep -E "FATAL EXCEPTION|AndroidRuntime: (java|Caused|\tat com.stradaos)" | tail -60 > $OUT/logcat.txt
if grep -q "com.stradaos" $OUT/logcat.txt; then echo "CIERRE" > $OUT/CIERRES.txt; fi
ls -l $OUT
