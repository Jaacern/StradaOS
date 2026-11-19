#!/bin/bash
# Script para configurar un emulador Android que simule la radio Topway TS9
# Compatible con Android 8.1 (API 27) ARMv7 32-bit

set -e

echo "════════════════════════════════════════════════════════════════"
echo "  Configurando Emulador Android para StradaOS (Topway TS9)"
echo "════════════════════════════════════════════════════════════════"
echo ""

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Variables de configuración
ANDROID_SDK_ROOT="${HOME}/Android/Sdk"
AVD_NAME="StradaOS_TS9_API27"
API_LEVEL="27"  # Android 8.1 Oreo
ABI="armeabi-v7a"  # ARM 32-bit como tu radio
DEVICE="7in_WSVGA"  # Pantalla típica de radio (1024x600)

echo -e "${YELLOW}[1/6]${NC} Verificando instalación de Android SDK..."

# Verificar si Android SDK está instalado
if [ ! -d "$ANDROID_SDK_ROOT" ]; then
    echo -e "${YELLOW}Android SDK no encontrado. Instalando...${NC}"
    
    # Descargar Android SDK Command Line Tools
    SDK_TOOLS_URL="https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip"
    TEMP_DIR=$(mktemp -d)
    
    echo "Descargando Android SDK Command Line Tools..."
    wget -q --show-progress "$SDK_TOOLS_URL" -O "$TEMP_DIR/cmdline-tools.zip"
    
    mkdir -p "$ANDROID_SDK_ROOT/cmdline-tools"
    unzip -q "$TEMP_DIR/cmdline-tools.zip" -d "$ANDROID_SDK_ROOT/cmdline-tools"
    mv "$ANDROID_SDK_ROOT/cmdline-tools/cmdline-tools" "$ANDROID_SDK_ROOT/cmdline-tools/latest"
    
    rm -rf "$TEMP_DIR"
    echo -e "${GREEN}✓ Android SDK Command Line Tools instalado${NC}"
else
    echo -e "${GREEN}✓ Android SDK encontrado en $ANDROID_SDK_ROOT${NC}"
fi

# Configurar variables de entorno
export ANDROID_HOME="$ANDROID_SDK_ROOT"
export ANDROID_SDK_ROOT="$ANDROID_SDK_ROOT"
export PATH="$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools:$ANDROID_SDK_ROOT/emulator"

# Agregar al bashrc si no está
if ! grep -q "ANDROID_SDK_ROOT" ~/.bashrc; then
    echo "" >> ~/.bashrc
    echo "# Android SDK" >> ~/.bashrc
    echo "export ANDROID_SDK_ROOT=\"$ANDROID_SDK_ROOT\"" >> ~/.bashrc
    echo "export ANDROID_HOME=\"\$ANDROID_SDK_ROOT\"" >> ~/.bashrc
    echo "export PATH=\"\$PATH:\$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:\$ANDROID_SDK_ROOT/platform-tools:\$ANDROID_SDK_ROOT/emulator\"" >> ~/.bashrc
    echo -e "${GREEN}✓ Variables de entorno agregadas a ~/.bashrc${NC}"
fi

echo ""
echo -e "${YELLOW}[2/6]${NC} Instalando componentes necesarios del SDK..."

# Aceptar licencias
yes | sdkmanager --licenses > /dev/null 2>&1 || true

# Instalar componentes necesarios
echo "Instalando platform-tools, emulator y sistema Android 8.1..."
sdkmanager "platform-tools" "emulator" \
    "platforms;android-${API_LEVEL}" \
    "system-images;android-${API_LEVEL};default;${ABI}" \
    "build-tools;34.0.0" || true

echo -e "${GREEN}✓ Componentes del SDK instalados${NC}"

echo ""
echo -e "${YELLOW}[3/6]${NC} Verificando emulador existente..."

# Eliminar AVD existente si existe
if avdmanager list avd | grep -q "$AVD_NAME"; then
    echo "Emulador existente encontrado. Eliminando..."
    avdmanager delete avd -n "$AVD_NAME"
fi

echo ""
echo -e "${YELLOW}[4/6]${NC} Creando emulador Android Virtual Device (AVD)..."

# Crear AVD con configuración específica para radio de carro
echo "no" | avdmanager create avd \
    -n "$AVD_NAME" \
    -k "system-images;android-${API_LEVEL};default;${ABI}" \
    -d "$DEVICE" \
    -f

echo -e "${GREEN}✓ AVD '$AVD_NAME' creado${NC}"

echo ""
echo -e "${YELLOW}[5/6]${NC} Configurando AVD para simular radio Topway TS9..."

# Ruta del archivo de configuración del AVD
AVD_CONFIG="$HOME/.android/avd/${AVD_NAME}.avd/config.ini"

# Modificar configuración para simular mejor una radio de carro
cat >> "$AVD_CONFIG" << EOF

# Configuración personalizada para simular Topway TS9
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
hw.sensors.orientation=no
hw.sensors.magnetic_field=yes
hw.gpu.enabled=yes
hw.gpu.mode=auto
disk.dataPartition.size=2048M
vm.heapSize=256
fastboot.forceColdBoot=no
EOF

echo -e "${GREEN}✓ Configuración del AVD personalizada${NC}"

echo ""
echo -e "${YELLOW}[6/6]${NC} Verificando instalación..."

# Listar AVDs creados
echo "Emuladores disponibles:"
avdmanager list avd

echo ""
echo -e "${GREEN}════════════════════════════════════════════════════════════════${NC}"
echo -e "${GREEN}  ✓ Emulador configurado exitosamente${NC}"
echo -e "${GREEN}════════════════════════════════════════════════════════════════${NC}"
echo ""
echo "Especificaciones del emulador:"
echo "  • Nombre: $AVD_NAME"
echo "  • Android: 8.1 Oreo (API 27)"
echo "  • Arquitectura: ARMv7 32-bit (igual que tu radio)"
echo "  • Resolución: 1024x600 (típica de radios de carro)"
echo "  • RAM: 1GB"
echo "  • GPS: Habilitado"
echo ""
echo "Para iniciar el emulador, ejecuta:"
echo -e "${YELLOW}  ./start-emulator.sh${NC}"
echo ""
echo "O manualmente:"
echo -e "${YELLOW}  emulator -avd $AVD_NAME -gpu auto -memory 1024${NC}"
echo ""
