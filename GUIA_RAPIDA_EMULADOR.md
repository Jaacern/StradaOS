# 🚀 Guía Rápida: Probar StradaOS en Emulador

## ⚡ Opción 1: Script Automático (Recomendado)

```bash
cd /home/fx01/StradaOS
./run-stradaos-test.sh
```

Espera 5-10 minutos. El script hará todo automáticamente.

---

## 🔧 Opción 2: Paso a Paso Manual

### 1️⃣ Instalar Android SDK

```bash
cd ~
mkdir -p Android/Sdk
cd Android/Sdk

# Descargar command line tools
curl -L https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -o cmdline-tools.zip

# Extraer
unzip cmdline-tools.zip
mkdir -p cmdline-tools
mv cmdline-tools cmdline-tools/latest

# Agregar al PATH
echo 'export ANDROID_SDK_ROOT="$HOME/Android/Sdk"' >> ~/.bashrc
echo 'export ANDROID_HOME="$ANDROID_SDK_ROOT"' >> ~/.bashrc
echo 'export PATH="$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools:$ANDROID_SDK_ROOT/emulator"' >> ~/.bashrc

source ~/.bashrc
```

### 2️⃣ Instalar Componentes del SDK

```bash
cd ~/Android/Sdk

# Aceptar licencias
yes | cmdline-tools/latest/bin/sdkmanager --licenses

# Instalar componentes necesarios
cmdline-tools/latest/bin/sdkmanager \
    "platform-tools" \
    "emulator" \
    "platforms;android-27" \
    "system-images;android-27;default;armeabi-v7a" \
    "build-tools;34.0.0"
```

### 3️⃣ Crear Emulador

```bash
# Crear AVD (Android Virtual Device)
echo "no" | cmdline-tools/latest/bin/avdmanager create avd \
    -n StradaOS_TS9_API27 \
    -k "system-images;android-27;default;armeabi-v7a" \
    -d "7in_WSVGA"
```

### 4️⃣ Configurar Emulador para Radio

```bash
# Editar configuración
cat >> ~/.android/avd/StradaOS_TS9_API27.avd/config.ini << 'EOF'

# Topway TS9 Config
hw.ramSize=1024
hw.lcd.width=1024
hw.lcd.height=600
hw.gps=yes
hw.audioInput=yes
hw.audioOutput=yes
hw.battery=no
hw.gpu.enabled=yes
disk.dataPartition.size=2048M
EOF
```

### 5️⃣ Iniciar Emulador

```bash
cd ~/Android/Sdk
emulator/emulator -avd StradaOS_TS9_API27 -gpu auto -memory 1024 &
```

**Espera 3-5 minutos** hasta que el emulador cargue completamente.

### 6️⃣ Verificar Emulador

```bash
# Ver dispositivos conectados
adb devices

# Debe mostrar algo como:
# emulator-5554   device
```

### 7️⃣ Compilar e Instalar StradaOS

```bash
cd /home/fx01/StradaOS

# Compilar
./gradlew assembleDebug

# Instalar
./gradlew installDebug
```

### 8️⃣ Abrir StradaOS

La app estará en el launcher del emulador, o ábrela directamente:

```bash
adb shell am start -n com.stradaos.launcher.debug/.Main
```

---

## 📱 Opción 3: Usar Tu Radio Real (Más Rápido)

Si tienes tu radio Topway TS9 física:

1. **Habilitar Depuración USB** en la radio:
   - Ve a Ajustes → Acerca de
   - Toca 7 veces en "Número de compilación"
   - Ve a Ajustes → Opciones de desarrollador
   - Activa "Depuración USB"

2. **Conectar por USB:**
   ```bash
   adb devices
   # Debes ver tu dispositivo listado
   ```

3. **Instalar StradaOS:**
   ```bash
   cd /home/fx01/StradaOS
   ./gradlew installDebug
   ```

---

## 🎯 Verificación Rápida

```bash
# ¿Está el SDK instalado?
ls ~/Android/Sdk/cmdline-tools/latest/bin/sdkmanager

# ¿Existe el emulador?
~/Android/Sdk/cmdline-tools/latest/bin/avdmanager list avd

# ¿Está corriendo el emulador?
adb devices

# ¿Está instalada la app?
adb shell pm list packages | grep stradaos
```

---

## 💡 Tips

- **Emulador lento:** Es normal con ARM. Para desarrollo rápido usa tu radio real.
- **Emulador no inicia:** Verifica que tienes al menos 8GB libres en disco.
- **App crashea:** Mira los logs: `adb logcat | grep AndroidRuntime`

---

## 🆘 Ayuda

Si algo falla, verifica:

```bash
# Ver logs del emulador
tail -f ~/Android/Sdk/emulator.log

# Ver logs de Android
adb logcat

# Estado del dispositivo
adb shell getprop ro.build.version.release  # Versión Android
adb shell getprop ro.product.cpu.abi        # Arquitectura
```

---

**¡Listo para probar StradaOS!** 🎉
