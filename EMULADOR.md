# 📱 Configuración del Emulador Android para StradaOS

Este documento explica cómo configurar un emulador Android que simula tu radio **Topway TS9** con Android 8.1 Oreo.

## 🎯 Especificaciones del Emulador

El emulador está configurado para replicar las características de tu radio:

- **Placa Base:** Topway TS9 (ts9p3_bsp)
- **Sistema Operativo:** Android 8.1 Oreo (API 27)
- **Arquitectura:** ARMv7l 32-bit (igual que tu radio)
- **Kernel:** Linux 4.9.117
- **Resolución:** 1024×600 (pantalla típica de radio 7")
- **RAM:** 1GB
- **GPS:** Habilitado
- **Audio:** Entrada y salida habilitadas

## 🚀 Método Rápido (Recomendado)

Ejecuta un solo script que configura todo automáticamente e instala StradaOS:

```bash
./run-stradaos-test.sh
```

Este script:
1. ✅ Descarga e instala el Android SDK
2. ✅ Configura el emulador Android con las especificaciones de tu radio
3. ✅ Inicia el emulador
4. ✅ Compila StradaOS
5. ✅ Instala StradaOS en el emulador

**Tiempo estimado:** 5-10 minutos (primera vez)

## 📋 Método Manual

### Paso 1: Configurar el Emulador

```bash
./setup-emulator-simple.sh
```

Este script descarga el Android SDK y crea un emulador llamado `StradaOS_TS9_API27`.

### Paso 2: Recargar Variables de Entorno

```bash
source ~/.bashrc
```

### Paso 3: Iniciar el Emulador

```bash
emulator -avd StradaOS_TS9_API27 -gpu auto -memory 1024 &
```

O usa el script helper:

```bash
./start-emulator.sh
```

**Nota:** El emulador puede tardar 2-5 minutos en iniciar completamente la primera vez.

### Paso 4: Compilar e Instalar StradaOS

```bash
# Compilar
./gradlew assembleDebug

# Instalar en el emulador
./gradlew installDebug
```

## 🔧 Comandos Útiles

### Gestión del Emulador

```bash
# Listar emuladores disponibles
emulator -list-avds

# Ver dispositivos conectados
adb devices

# Detener el emulador
adb emu kill

# Reiniciar el emulador
adb reboot
```

### Gestión de la App

```bash
# Ver logs de StradaOS
adb logcat | grep -i stradaos

# Abrir StradaOS directamente
adb shell am start -n com.stradaos.launcher.debug/.Main

# Desinstalar StradaOS
adb uninstall com.stradaos.launcher.debug

# Reinstalar después de cambios
./gradlew installDebug
```

### Depuración

```bash
# Ver todos los logs del sistema
adb logcat

# Ver logs del emulador
tail -f emulator.log

# Tomar screenshot
adb exec-out screencap -p > screenshot.png

# Grabar video de la pantalla (max 180 segundos)
adb shell screenrecord /sdcard/demo.mp4
adb pull /sdcard/demo.mp4
```

## 🐛 Solución de Problemas

### El emulador no inicia

1. Verifica que tienes suficiente espacio en disco (mínimo 8GB libres)
2. Revisa los logs: `cat emulator.log`
3. Intenta con aceleración de hardware desactivada:
   ```bash
   emulator -avd StradaOS_TS9_API27 -gpu swiftshader_indirect
   ```

### La app no se instala

```bash
# Limpiar build anterior
./gradlew clean

# Reinstalar
./gradlew installDebug
```

### El emulador está muy lento

El emulador ARM es inherentemente más lento que x86. Opciones:

1. **Reducir resolución:** Edita `~/.android/avd/StradaOS_TS9_API27.avd/config.ini`
   ```
   hw.lcd.width=800
   hw.lcd.height=480
   ```

2. **Usar snapshot:** Quita `-no-snapshot-save` al iniciar el emulador

3. **Dispositivo físico:** Conecta tu radio real por USB:
   ```bash
   # Habilita "Depuración USB" en tu radio
   adb devices
   ./gradlew installDebug
   ```

## 📊 Diferencias con tu Radio Real

| Característica | Radio Real | Emulador |
|----------------|------------|----------|
| Sistema Base | Android 8.1/9 (build.prop editado) | Android 8.1 puro |
| Chip | Spreadtrum/Unisoc | ARM genérico |
| Velocidad | Hardware real | Emulado (más lento) |
| GPS | Antena física | GPS simulado |
| Audio Bluetooth | Hardware BT real | Simulado |
| Pantalla | Táctil capacitiva | Táctil emulado |

**Nota:** El emulador es perfecto para desarrollo y pruebas de UI, pero para probar funciones de GPS y Bluetooth real, necesitarás usar tu radio física.

## 🎓 Recursos Adicionales

- **Android Emulator:** https://developer.android.com/studio/run/emulator
- **ADB Commands:** https://developer.android.com/tools/adb
- **StradaOS Issues:** https://github.com/lordnordic-debug/StradaOS/issues

## 💡 Consejos

1. **Primera vez:** El emulador tarda más en iniciar. Ten paciencia.
2. **Desarrollo iterativo:** Mantén el emulador abierto entre compilaciones para ahorrar tiempo.
3. **Hot reload:** Usa `./gradlew installDebug` para reinstalar rápidamente después de cambios.
4. **Testing en real:** Siempre prueba en tu radio real antes de lanzar una versión.

---

**¿Problemas?** Abre un issue en el repositorio con:
- Output completo del script que falló
- Contenido de `emulator.log`
- Tu sistema operativo y versión
