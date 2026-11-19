#!/bin/bash
# Script simplificado para configurar emulador Android

echo "════════════════════════════════════════════════════════════════"
echo "  Setup Emulador Android para StradaOS (Topway TS9)"
echo "════════════════════════════════════════════════════════════════"
echo ""

# Variables
ANDROID_SDK_ROOT="${HOME}/Android/Sdk"
AVD_NAME="StradaOS_TS9_API27"

# Crear directorio del SDK
mkdir -p "$ANDROID_SDK_ROOT"

# Descargar Android Command Line Tools
echo "[1/5] Descargando Android SDK Command Line Tools..."
SDK_URL="https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip"

cd "$ANDROID_SDK_ROOT"
if [ ! -d "cmdline-tools/latest" ]; then
    curl -L "$SDK_URL" -o cmdline-tools.zip
    unzip -q cmdline-tools.zip
    mkdir -p cmdline-tools
    mv cmdline-tools cmdline-tools/latest 2>/dev/null || mv cmdline-tools.zip.*/cmdline-tools cmdline-tools/latest
    rm -f cmdline-tools.zip
    echo "✓ SDK Command Line Tools descargado"
else
    echo "✓ SDK Command Line Tools ya existe"
fi

# Configurar PATH
export ANDROID_HOME="$ANDROID_SDK_ROOT"
export ANDROID_SDK_ROOT="$ANDROID_SDK_ROOT"
export PATH="$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools:$ANDROID_SDK_ROOT/emulator"

echo ""
echo "[2/5] Instalando componentes del SDK..."

# Aceptar licencias y instalar componentes
yes | "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" --licenses 2>/dev/null || true

"$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" \
    "platform-tools" \
    "emulator" \
    "platforms;android-27" \
    "system-images;android-27;default;armeabi-v7a" \
    "build-tools;34.0.0"

echo "✓ Componentes instalados"

echo ""
echo "[3/5] Creando AVD..."

# Eliminar AVD si existe
"$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/avdmanager" delete avd -n "$AVD_NAME" 2>/dev/null || true

# Crear AVD
echo "no" | "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/avdmanager" create avd \
    -n "$AVD_NAME" \
    -k "system-images;android-27;default;armeabi-v7a" \
    -d "7in_WSVGA"

echo "✓ AVD creado"

echo ""
echo "[4/5] Configurando AVD..."

AVD_CONFIG="$HOME/.android/avd/${AVD_NAME}.avd/config.ini"

# Agregar configuración personalizada
cat >> "$AVD_CONFIG" << 'EOF'

# Configuración Topway TS9
hw.ramSize=1024
hw.lcd.density=160
hw.lcd.width=1024
hw.lcd.height=600
hw.gps=yes
hw.audioInput=yes
hw.audioOutput=yes
hw.battery=no
hw.gpu.enabled=yes
hw.gpu.mode=auto
disk.dataPartition.size=2048M
vm.heapSize=256
EOF

echo "✓ AVD configurado"

echo ""
echo "[5/5] Guardando variables de entorno..."

# Agregar al bashrc si no existe
if ! grep -q "ANDROID_SDK_ROOT" ~/.bashrc 2>/dev/null; then
    cat >> ~/.bashrc << EOF

# Android SDK
export ANDROID_SDK_ROOT="$ANDROID_SDK_ROOT"
export ANDROID_HOME="\$ANDROID_SDK_ROOT"
export PATH="\$PATH:\$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:\$ANDROID_SDK_ROOT/platform-tools:\$ANDROID_SDK_ROOT/emulator"
EOF
    echo "✓ Variables agregadas a ~/.bashrc"
fi

echo ""
echo "════════════════════════════════════════════════════════════════"
echo "  ✓ Configuración completa"
echo "════════════════════════════════════════════════════════════════"
echo ""
echo "Emulador: $AVD_NAME"
echo "Android: 8.1 Oreo (API 27)"
echo "Arquitectura: ARMv7 32-bit"
echo "Resolución: 1024x600"
echo ""
echo "Para iniciar el emulador:"
echo "  source ~/.bashrc"
echo "  emulator -avd $AVD_NAME -gpu auto -memory 1024 &"
echo ""
echo "Luego instala StradaOS:"
echo "  ./gradlew installDebug"
echo ""
