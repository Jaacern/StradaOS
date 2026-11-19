#!/bin/bash
set -e

echo "=== Instalando Android SDK y Emulador ===" 

export ANDROID_SDK_ROOT="$HOME/Android/Sdk"
export ANDROID_HOME="$ANDROID_SDK_ROOT"
export PATH="$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools:$ANDROID_SDK_ROOT/emulator"

echo "1. Instalando platform-tools..."
yes | sdkmanager "platform-tools" || echo "platform-tools ya instalado"

echo "2. Instalando emulator..."
yes | sdkmanager "emulator" || echo "emulator ya instalado"

echo "3. Instalando Android 8.1 (API 27)..."
yes | sdkmanager "platforms;android-27" || echo "platform ya instalado"

echo "4. Instalando imagen del sistema ARMv7..."
yes | sdkmanager "system-images;android-27;default;armeabi-v7a" || echo "system-image ya instalado"

echo "5. Instalando build-tools..."
yes | sdkmanager "build-tools;34.0.0" || echo "build-tools ya instalado"

echo ""
echo "✓ SDK instalado correctamente"
echo ""
echo "Componentes instalados:"
sdkmanager --list_installed

echo ""
echo "Ahora crea el AVD ejecutando: ./create-avd.sh"
