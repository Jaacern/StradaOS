#!/bin/bash
# Script todo-en-uno para probar StradaOS en emulador

set -e

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${BLUE}╔════════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║         StradaOS - Test en Emulador Topway TS9 API27          ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════════╝${NC}"
echo ""

ANDROID_SDK_ROOT="${HOME}/Android/Sdk"
AVD_NAME="StradaOS_TS9_API27"

# Configurar PATH
export ANDROID_HOME="$ANDROID_SDK_ROOT"
export ANDROID_SDK_ROOT="$ANDROID_SDK_ROOT"
export PATH="$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools:$ANDROID_SDK_ROOT/emulator"

# Función para verificar si el emulador está corriendo
is_emulator_running() {
    adb devices 2>/dev/null | grep -q "emulator"
}

# Función para esperar a que el emulador esté listo
wait_for_emulator() {
    echo -e "${YELLOW}Esperando a que el emulador esté listo...${NC}"
    timeout=300
    elapsed=0
    while [ $elapsed -lt $timeout ]; do
        if is_emulator_running; then
            boot_complete=$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r' || echo "0")
            if [ "$boot_complete" == "1" ]; then
                echo -e "${GREEN}✓ Emulador listo${NC}"
                return 0
            fi
        fi
        sleep 3
        elapsed=$((elapsed + 3))
        printf "."
    done
    echo ""
    echo -e "${RED}Timeout esperando al emulador${NC}"
    return 1
}

# Paso 1: Verificar si el SDK está instalado
echo -e "${YELLOW}[Paso 1/5]${NC} Verificando Android SDK..."
if [ ! -d "$ANDROID_SDK_ROOT/cmdline-tools/latest" ]; then
    echo -e "${YELLOW}SDK no encontrado. Ejecutando setup...${NC}"
    if [ -f "./setup-emulator-simple.sh" ]; then
        bash ./setup-emulator-simple.sh
    else
        echo -e "${RED}Error: setup-emulator-simple.sh no encontrado${NC}"
        exit 1
    fi
else
    echo -e "${GREEN}✓ SDK encontrado${NC}"
fi

# Recargar PATH
export PATH="$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools:$ANDROID_SDK_ROOT/emulator"

# Paso 2: Verificar que el AVD existe
echo ""
echo -e "${YELLOW}[Paso 2/5]${NC} Verificando AVD..."
if ! "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/avdmanager" list avd | grep -q "$AVD_NAME"; then
    echo -e "${RED}Error: AVD '$AVD_NAME' no existe${NC}"
    echo "Ejecuta primero: ./setup-emulator-simple.sh"
    exit 1
fi
echo -e "${GREEN}✓ AVD '$AVD_NAME' encontrado${NC}"

# Paso 3: Iniciar el emulador si no está corriendo
echo ""
echo -e "${YELLOW}[Paso 3/5]${NC} Iniciando emulador..."
if is_emulator_running; then
    echo -e "${GREEN}✓ Emulador ya está corriendo${NC}"
else
    echo -e "${YELLOW}Iniciando emulador (esto puede tomar unos minutos)...${NC}"
    "$ANDROID_SDK_ROOT/emulator/emulator" -avd "$AVD_NAME" \
        -gpu auto \
        -memory 1024 \
        -no-snapshot-save \
        -no-audio \
        > emulator.log 2>&1 &
    
    if ! wait_for_emulator; then
        echo -e "${RED}El emulador no pudo iniciarse. Revisa emulator.log${NC}"
        exit 1
    fi
fi

# Paso 4: Compilar la app
echo ""
echo -e "${YELLOW}[Paso 4/5]${NC} Compilando StradaOS..."
./gradlew assembleDebug
echo -e "${GREEN}✓ Compilación exitosa${NC}"

# Paso 5: Instalar la app en el emulador
echo ""
echo -e "${YELLOW}[Paso 5/5]${NC} Instalando StradaOS en el emulador..."
./gradlew installDebug

# Verificar la instalación
if adb shell pm list packages | grep -q "com.stradaos.launcher"; then
    echo -e "${GREEN}✓ StradaOS instalado correctamente${NC}"
else
    echo -e "${RED}Error: StradaOS no se instaló correctamente${NC}"
    exit 1
fi

echo ""
echo -e "${GREEN}╔════════════════════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║                  ✓ StradaOS listo para probar                  ║${NC}"
echo -e "${GREEN}╚════════════════════════════════════════════════════════════════╝${NC}"
echo ""
echo -e "${BLUE}La app StradaOS está instalada en el emulador.${NC}"
echo ""
echo -e "${YELLOW}Comandos útiles:${NC}"
echo "  • Ver logs de la app:    adb logcat | grep StradaOS"
echo "  • Abrir app:             adb shell am start -n com.stradaos.launcher.debug/.Main"
echo "  • Reiniciar emulador:    adb reboot"
echo "  • Detener emulador:      adb emu kill"
echo "  • Ver logs del emulador: tail -f emulator.log"
echo ""
echo -e "${GREEN}Abre el emulador y busca StradaOS en el launcher!${NC}"
echo ""
