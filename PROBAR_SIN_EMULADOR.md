# 🚗 Probar StradaOS sin Emulador

El APK de StradaOS ya está compilado y listo para probar. Aquí tienes las opciones más prácticas:

## ✅ Opción 1: Probar en Tu Radio Real (Recomendado)

La forma más rápida y confiable es usar tu radio Topway TS9 directamente:

### Paso 1: Habilitar Depuración USB en la Radio

1. Ve a **Ajustes** → **Acerca de**
2. Busca **"Número de compilación"** o **"Build number"**
3. Toca 7 veces hasta que diga *"Eres un desarrollador"*
4. Vuelve a **Ajustes** → **Opciones de desarrollador** (o **Developer options**)
5. Activa **"Depuración USB"** (USB debugging)

### Paso 2: Conectar la Radio por USB

```bash
# Conecta la radio a tu PC con un cable USB

# Verifica que se detectó
adb devices

# Debe mostrar algo como:
# 1234567890ABCDEF    device
```

Si no aparece, verifica:
- Cable USB con datos (no solo carga)
- Drivers USB en tu PC
- En la radio, acepta el diálogo "¿Permitir depuración USB?"

### Paso 3: Instalar StradaOS

```bash
cd /home/fx01/StradaOS

# Instalar directamente
./gradlew installDebug

# O instalar el APK ya compilado
adb install app/build/outputs/apk/debug/StradaOS-2.23.0-prueba.apk
```

### Paso 4: Abrir StradaOS

Desde el launcher de tu radio busca **"StradaOS Prueba"** o ábrelo directamente:

```bash
adb shell am start -n com.stradaos.launcher.debug/.Main
```

---

## ⚡ Opción 2: Transferir el APK Manualmente

Si no puedes conectar la radio por USB:

### Paso 1: Copiar el APK a una USB

```bash
# El APK está en:
cp /home/fx01/StradaOS/app/build/outputs/apk/debug/StradaOS-2.23.0-prueba.apk /ruta/a/tu/usb/
```

### Paso 2: Instalar desde la USB

1. Conecta la USB a tu radio
2. Abre el explorador de archivos en la radio
3. Navega a la USB y toca el archivo APK
4. Sigue las instrucciones de instalación

---

## 🖥️ Opción 3: Usar Android Studio (Más Fácil que Comandos)

Si quieres usar un emulador, **Android Studio** es la mejor opción:

### Instalar Android Studio

```bash
# Descarga desde: https://developer.android.com/studio
# O con snap:
sudo snap install android-studio --classic
```

### Crear Emulador

1. Abre Android Studio
2. Ve a **Tools** → **Device Manager**
3. Click en **Create Device**
4. Selecciona **Phone** → **Nexus 7 (2012)** (1280×800, similar a tu radio)
5. Selecciona **API Level 27** (Android 8.1)
6. Elige **x86** (ARM es muy lento)
7. Click **Finish**

### Ejecutar StradaOS

1. En Android Studio: **File** → **Open** → Selecciona `/home/fx01/StradaOS`
2. Espera a que Gradle sincronice
3. Click en el botón verde **▶ Run**
4. Selecciona tu emulador

---

## 📊 Comparación de Opciones

| Método | Velocidad | Realismo | Dificultad |
|--------|-----------|----------|------------|
| **Radio Real** | ⚡⚡⚡ Instantáneo | ⭐⭐⭐ Perfecto | ⭐ Fácil |
| **APK en USB** | ⚡⚡ Rápido | ⭐⭐⭐ Perfecto | ⭐ Muy fácil |
| **Android Studio** | ⚡⚡ Rápido | ⭐⭐ Bueno | ⭐⭐ Medio |
| **Emulador ARM CLI** | ⚡ Muy lento | ⭐⭐ Bueno | ⭐⭐⭐ Difícil |

---

## 🔍 Debugging y Logs

Una vez que StradaOS esté corriendo (en radio o emulador):

```bash
# Ver logs en tiempo real
adb logcat | grep -i stradaos

# Ver solo errores
adb logcat *:E | grep -i stradaos

# Capturar screenshot
adb exec-out screencap -p > screenshot.png

# Ver información del dispositivo
adb shell getprop ro.build.version.release  # Versión Android
adb shell getprop ro.product.cpu.abi        # Arquitectura
adb shell getprop ro.build.display.id       # Build ID
```

---

## 🎯 Mi Recomendación

**Para probar rápidamente:** Usa tu radio Topway TS9 directamente
- Es lo más rápido
- Prueba el hardware real
- GPS, Bluetooth y audio funcionan como en producción

**Para desarrollo frecuente:** Android Studio
- Emulador rápido x86
- Debug integrado
- Hot reload

**Evitar por ahora:** Emuladores ARM desde línea de comandos
- Extremadamente lentos
- Configuración compleja
- No aporta ventajas sobre probar en la radio real

---

## ✨ APK Listo

El APK ya compilado está aquí:
```
/home/fx01/StradaOS/app/build/outputs/apk/debug/StradaOS-2.23.0-prueba.apk
```

**Tamaño:** ~1 MB  
**Versión:** 2.23.0-prueba  
**Min Android:** 4.1 (API 16)  
**Target:** Android 8.1 (API 28)

---

## 🆘 Problemas Comunes

### "adb: device unauthorized"

La radio no aceptó el diálogo de depuración USB. Desconecta y reconecta, luego acepta en la radio.

### "adb: no devices/emulators found"

```bash
# Reiniciar servidor ADB
adb kill-server
adb start-server
adb devices
```

### "Installation failed"

```bash
# Desinstalar versión anterior si existe
adb uninstall com.stradaos.launcher.debug

# Reinstalar
./gradlew installDebug
```

---

¡Tu APK está listo para probar! 🎉
