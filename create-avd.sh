#!/bin/bash
set -e

echo "=== Creando Android Virtual Device ===" 

export ANDROID_SDK_ROOT="$HOME/Android/Sdk"
export ANDROID_HOME="$ANDROID_SDK_ROOT"
export PATH="$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools:$ANDROID_SDK_ROOT/emulator"

AVD_NAME="StradaOS_TS9_API27"

echo "Eliminando AVD anterior si existe..."
avdmanager delete avd -n "$AVD_NAME" 2>/dev/null || echo "No había AVD anterior"

echo ""
echo "Creando nuevo AVD: $AVD_NAME"
echo "no" | avdmanager create avd \
    -n "$AVD_NAME" \
    -k "system-images;android-27;google_apis;armeabi-v7a" \
    -d "7in_WSVGA" \
    --force

echo ""
echo "Configurando AVD para simular Topway TS9..."

AVD_CONFIG="$HOME/.android/avd/${AVD_NAME}.avd/config.ini"

cat >> "$AVD_CONFIG" << 'EOF'

# Topway TS9 Configuration
hw.ramSize=1024
hw.lcd.density=160
hw.lcd.width=1024
hw.lcd.height=600
hw.gps=yes
hw.audioInput=yes
hw.audioOutput=yes
hw.battery=no
hw.camera.back=no
hw.camera.front=no
hw.keyboard=no
hw.gpu.enabled=yes
hw.gpu.mode=auto
disk.dataPartition.size=2048M
vm.heapSize=256
fastboot.forceColdBoot=no
EOF

echo ""
echo "✓ AVD creado y configurado"
echo ""
echo "AVDs disponibles:"
avdmanager list avd

echo ""
echo "Para iniciar el emulador ejecuta:"
echo "  emulator -avd $AVD_NAME -gpu auto -memory 1024 &"
echo ""
echo "O usa el script: ./start-emulator.sh"
