#!/bin/bash
# Script para iniciar el emulador Android StradaOS

set -e

# Colores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

AVD_NAME="StradaOS_TS9_API27"
ANDROID_SDK_ROOT="${HOME}/Android/Sdk"

# Configurar variables de entorno
export ANDROID_HOME="$ANDROID_SDK_ROOT"
export ANDROID_SDK_ROOT="$ANDROID_SDK_ROOT"
export PATH="$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools:$ANDROID_SDK_ROOT/emulator"

echo -e "${GREEN}════════════════════════════════════════════════════════════════${NC}"
echo -e "${GREEN}  Iniciando Emulador StradaOS (Topway TS9)${NC}"
echo -e "${GREEN}════════════════════════════════════════════════════════════════${NC}"
echo ""

# Verificar que el AVD existe
if ! avdmanager list avd | grep -q "$AVD_NAME"; then
    echo -e "${RED}Error: El emulador '$AVD_NAME' no existe.${NC}"
    echo -e "Ejecuta primero: ${YELLOW}./setup-emulator.sh${NC}"
    exit 1
fi

echo -e "${YELLOW}Iniciando emulador...${NC}"
echo "Esto puede tomar un minuto la primera vez."
echo ""

# Iniciar el emulador en segundo plano
emulator -avd "$AVD_NAME" \
    -gpu auto \
    -memory 1024 \
    -no-snapshot-save \
    -wipe-data \
    > emulator.log 2>&1 &

EMULATOR_PID=$!
echo -e "${GREEN}✓ Emulador iniciado (PID: $EMULATOR_PID)${NC}"
echo ""
echo "Esperando que el emulador esté listo..."

# Esperar a que el emulador esté completamente iniciado
timeout=300
elapsed=0
while [ $elapsed -lt $timeout ]; do
    if adb devices | grep -q "emulator"; then
        # Esperar a que el sistema esté completamente cargado
        boot_complete=$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')
        if [ "$boot_complete" == "1" ]; then
            echo -e "${GREEN}✓ Emulador listo${NC}"
            echo ""
            echo "Dispositivos conectados:"
            adb devices
            echo ""
            echo -e "${YELLOW}Para instalar StradaOS en el emulador:${NC}"
            echo "  ./gradlew installDebug"
            echo ""
            echo -e "${YELLOW}Para ver los logs del emulador:${NC}"
            echo "  tail -f emulator.log"
            echo ""
            echo -e "${YELLOW}Para detener el emulador:${NC}"
            echo "  adb emu kill"
            echo ""
            exit 0
        fi
    fi
    sleep 2
    elapsed=$((elapsed + 2))
    echo -n "."
done

echo ""
echo -e "${RED}Timeout esperando al emulador. Verifica emulator.log${NC}"
exit 1
