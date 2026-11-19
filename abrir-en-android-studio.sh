#!/bin/bash
# Script para abrir StradaOS en Android Studio

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║         Abriendo StradaOS en Android Studio                    ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

# Configurar variables de entorno
export ANDROID_SDK_ROOT="$HOME/Android/Sdk"
export ANDROID_HOME="$ANDROID_SDK_ROOT"
export STUDIO_JDK="$HOME/android-studio/jbr"

PROJECT_PATH="/home/fx01/StradaOS"

echo "Proyecto: $PROJECT_PATH"
echo "SDK: $ANDROID_SDK_ROOT"
echo ""

# Verificar que Android Studio está instalado
if [ ! -f "$HOME/android-studio/bin/studio.sh" ]; then
    echo "❌ Error: Android Studio no está instalado en ~/android-studio"
    echo "Ejecuta primero el script de instalación"
    exit 1
fi

echo "Iniciando Android Studio con el proyecto StradaOS..."
echo ""

# Abrir Android Studio con el proyecto
"$HOME/android-studio/bin/studio.sh" "$PROJECT_PATH" &

STUDIO_PID=$!

echo "✓ Android Studio iniciado (PID: $STUDIO_PID)"
echo ""
echo "═══════════════════════════════════════════════════════════════"
echo "  PRIMERA VEZ: Configuración Inicial"
echo "═══════════════════════════════════════════════════════════════"
echo ""
echo "1. ✓ Acepta los términos y condiciones"
echo "2. ✓ Selecciona 'Standard installation'"
echo "3. ⏳ Espera a que descargue SDK y herramientas"
echo "4. ✓ Acepta las licencias del SDK"
echo ""
echo "═══════════════════════════════════════════════════════════════"
echo "  DESPUÉS DE LA CONFIGURACIÓN INICIAL"
echo "═══════════════════════════════════════════════════════════════"
echo ""
echo "1. ⏳ Espera a que Gradle sincronice el proyecto"
echo "   (Verás 'Gradle Sync' en la barra inferior)"
echo ""
echo "2. 🔧 Crear un Emulador:"
echo "   • Tools → Device Manager"
echo "   • Click en 'Create Device'"
echo "   • Selecciona: Pixel 7 o Medium Phone"
echo "   • System Image: API 28 (Pie) x86_64"
echo "   • Click 'Finish'"
echo ""
echo "3. ▶️  Ejecutar StradaOS:"
echo "   • Click en el botón verde ▶ Run"
echo "   • Selecciona tu emulador"
echo "   • Espera ~30 segundos"
echo ""
echo "4. 🐛 Ver Logs:"
echo "   • Panel 'Logcat' en la parte inferior"
echo "   • Filtra por: 'stradaos'"
echo ""
echo "═══════════════════════════════════════════════════════════════"
echo "  ATAJOS ÚTILES"
echo "═══════════════════════════════════════════════════════════════"
echo ""
echo "  Shift+F10     - Run (ejecutar app)"
echo "  Ctrl+F9       - Build (compilar)"
echo "  Alt+Enter     - Quick fix"
echo "  Ctrl+Space    - Autocompletar"
echo "  Shift+Shift   - Buscar en todo"
echo ""
echo "═══════════════════════════════════════════════════════════════"
echo ""
echo "💡 Tip: La primera compilación puede tardar 2-3 minutos"
echo ""
