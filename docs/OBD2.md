# Módulo OBD-II · ELM327

Documentación del monitor de diagnóstico integrado en StradaOS.  
Funciona con cualquier adaptador ELM327 por **Wi-Fi** o **Bluetooth** y no requiere instalar ninguna app adicional.

---

## Índice

1. [Requisitos del adaptador](#1-requisitos-del-adaptador)
2. [Conectar por Wi-Fi](#2-conectar-por-wi-fi)
3. [Conectar por Bluetooth](#3-conectar-por-bluetooth)
4. [Pantalla Vehículo](#4-pantalla-vehículo)
5. [Ajustes del módulo](#5-ajustes-del-módulo)
6. [Sensores y PIDs en vivo](#6-sensores-y-pids-en-vivo)
7. [Sistema de alertas](#7-sistema-de-alertas)
8. [Códigos de falla (DTCs)](#8-códigos-de-falla-dtcs)
9. [Gráficos en vivo](#9-gráficos-en-vivo)
10. [Modo demo (sin escáner)](#10-modo-demo-sin-escáner)
11. [Notas de compatibilidad](#11-notas-de-compatibilidad)
12. [Referencia de SharedPreferences](#12-referencia-de-sharedpreferences)

---

## 1. Requisitos del adaptador

StradaOS usa los **modos genéricos OBD-II** (SAE J1979), que entiende cualquier auto bencinero:

| Región | Desde |
|--------|-------|
| EE. UU. / Canadá | ~2001 |
| Europa | ~2004 |
| Latinoamérica | ~2008 |

Modos usados:

| Modo | Uso |
|------|-----|
| `01` | Datos en vivo (PIDs) |
| `02` | Freeze frame (datos al momento del código) |
| `03` | Códigos de falla confirmados |
| `04` | Borrar códigos y apagar la luz del motor |
| `07` | Códigos de falla pendientes |
| `09` | VIN del vehículo |

Cualquier adaptador basado en ELM327 (versión 1.5 o superior) o compatible funciona, ya sea Wi-Fi, Bluetooth o USB-OTG.

---

## 2. Conectar por Wi-Fi

Los adaptadores Wi-Fi (como el ELM327 rojo) crean su propia red Wi-Fi.

**Pasos:**

1. Conecta el adaptador al puerto OBD-II del auto (generalmente bajo el volante, lado conductor).
2. Pon el contacto (no es necesario arrancar el motor).
3. En la pantalla, entra a los ajustes de Wi-Fi y conéctate a la red del adaptador (suele llamarse `ELM327` o similar; contraseña `12345678` o `66666666` según el modelo).
4. Abre StradaOS → **Vehículo** → **⚙** → **Escáner** → **Wi-Fi (IP y puerto)**.
5. Ingresa la dirección del adaptador. La dirección por defecto es:

   ```
   192.168.0.10:35000
   ```

6. Toca **Conectar**.

> **Dos redes Wi-Fi a la vez:** Si la pantalla tiene también conexión a internet por otra red Wi-Fi, StradaOS detecta automáticamente la interfaz correcta para llegar al escáner. No hace falta desconectarse de internet.

> **Desde el emulador de Android en PC:** usa `10.0.2.2:35000` en lugar de `192.168.0.10:35000`.

---

## 3. Conectar por Bluetooth

1. En los ajustes de Bluetooth del equipo, empareja el adaptador (clave habitual: `1234` o `0000`).
2. Abre StradaOS → **Vehículo** → **⚙** → **Escáner** → elige el dispositivo de la lista.
3. Toca **Conectar**.

La dirección interna se guarda como `bt:AA:BB:CC:DD:EE:FF`.

> StradaOS prueba tres métodos de conexión Bluetooth en orden (socket inseguro, socket estándar SPP, canal 1 por reflexión) para ser compatible con los clones del ELM327 que solo aceptan uno de ellos.

---

## 4. Pantalla Vehículo

Se accede desde el lanzador principal → botón **Mi vehículo** (o como atajo en la barra de inicio).

### Panel principal

Muestra tarjetas con los sensores activos. Los 6 que siempre aparecen (aunque el auto no los tenga) son:

- Refrigerante · Batería · RPM · Velocidad · Carga del motor · Combustible

El resto de tarjetas aparecen en cuanto el auto empieza a enviar el dato.

### Vista "Todos los datos"

Toca el botón **Todos los datos** (arriba a la derecha) para ver en una lista cada PID que entrega el auto, incluidos los que no tienen tarjeta propia, con su nombre y valor en tiempo real.

### Panel lateral (o inferior en vertical)

- **Alertas activas:** con color ámbar (aviso) o rojo (crítico), descripción y qué hacer.
- **Códigos de falla:** lista de DTCs confirmados y pendientes con severidad y descripción.

### Barra de estado

Muestra el estado de la conexión, el nombre del dispositivo y el VIN del vehículo (si lo entrega el auto).

---

## 5. Ajustes del módulo

Acceso: **Vehículo** → **⚙**

| Ajuste | Descripción |
|--------|-------------|
| **Escáner** | Elige el adaptador (Wi-Fi, Bluetooth o demo) |
| **Mi vehículo (para la IA)** | Marca y modelo, para que la IA contextualice los DTCs |
| **Inteligencia artificial** | API key para explicaciones de códigos de falla con IA |
| **Avisos por voz** | Activa/desactiva el TTS para las alertas |
| **Encender al iniciar** | El monitor arranca automáticamente con StradaOS |
| **Cilindrada del motor** | Usada para calcular el consumo cuando el auto no tiene MAF (valor por defecto: 2,4 L) |
| **Protocolo** | Protocolo CAN forzado o automático (ver tabla abajo) |
| **Alertas a vigilar** | Activa/desactiva cada alerta y ajusta sus umbrales |
| **Registro de la conexión** | Log de lo que hace el escáner, útil para diagnosticar problemas |
| **Escanear todo** | Lee todos los PIDs que entrega el auto y guarda un informe en texto |

### Protocolos disponibles

| Opción | Comando AT | Descripción |
|--------|-----------|-------------|
| Automático | `ATSP0` | El escáner detecta solo (recomendado) |
| CAN 11 bits/500 kbps | `ATSP6` | Kia/Hyundai 2010 y similares |
| CAN 29 bits/500 kbps | `ATSP7` | Algunos Toyota/Lexus |
| CAN 11 bits/250 kbps | `ATSP8` | Vehículos más antiguos |
| CAN 29 bits/250 kbps | `ATSP9` | Variante ISO 15765-4 |

> Si el auto tarda mucho en conectar o da error de protocolo, prueba forzando `ATSP6`. Para Kia Sorento 2010 y Hyundai de esa generación funciona mejor que automático.

---

## 6. Sensores y PIDs en vivo

El monitor lee los sensores en un hilo de fondo cada ~700 ms. Los sensores marcados como **rápidos** se leen en cada vuelta; los demás, una de cada cuatro (~2,8 s).

### Sensores principales (rápidos)

| Sensor | PID | Fórmula | Unidad | Alertas |
|--------|-----|---------|--------|---------|
| Temperatura refrigerante | `0x05` | A − 40 | °C | >100 °C aviso / >106 °C crítico |
| Tensión de batería | `0x42` / ATRV | A×256+B / 1000 | V | <12,6 V / >15,2 V aviso |
| RPM | `0x0C` | (A×256+B) / 4 | rpm | >5200 aviso / >6000 crítico |
| Velocidad | `0x0D` | A | km/h | — |
| Carga del motor | `0x04` | A × 100/255 | % | >92 % aviso (hold 20 s) |
| Posición acelerador | `0x11` | A × 100/255 | % | — |
| Presión múltiple (MAP) | `0x0B` | A | kPa | — |
| Consumo calculado | — | MAF o MAP+RPM | L/100 km | — |
| Consumo por hora | — | MAF o MAP+RPM | L/h | — |

> **Voltaje:** Se prefiere el PID `0x42` (medido por el computador del motor) sobre `ATRV` (medido por el escáner). Los clones ELM327 suelen medir ~1 V de menos con `ATRV`.

### Sensores lentos (cada 4 vueltas)

| Sensor | PID | Fórmula | Unidad | Alertas |
|--------|-----|---------|--------|---------|
| Temperatura aire admisión | `0x0F` | A − 40 | °C | >60 °C aviso / >75 °C (hold 20 s) |
| Nivel de combustible | `0x2F` | A × 100/255 | % | <15 % aviso / <8 % crítico (hold 30 s) |
| Corrección mezcla corta | `0x06` | (A−128) × 100/128 | % | ±15 % aviso / ±25 % crítico (hold 30 s) |
| Corrección mezcla larga | `0x07` | (A−128) × 100/128 | % | ±10 % aviso / ±20 % crítico (hold 60 s) |
| Flujo de masa de aire (MAF) | `0x10` | (A×256+B) / 100 | g/s | — |
| Consumo directo del motor | `0x5E` | (A×256+B) / 20 | L/h | — |
| Temperatura aceite motor | `0x5C` | A − 40 | °C | >125 °C aviso / >135 °C crítico |
| Temperatura exterior | `0x46` | A − 40 | °C | — |
| Avance del encendido | `0x0E` | A/2 − 64 | ° | — |
| Sonda O2 banco 1 sensor 1 | `0x14` | A / 200 | V | — |
| Sonda O2 banco 1 sensor 2 | `0x15` | A / 200 | V | — |
| Tiempo de marcha | `0x1F` | A×256+B | s | — |
| Distancia con MIL encendida | `0x21` | A×256+B | km | — |
| EGR comandada | `0x2E` | A × 100/255 | % | — |
| Arranques desde borrar códigos | `0x30` | A | — | — |
| Distancia desde borrar códigos | `0x31` | A×256+B | km | — |
| Presión barométrica | `0x33` | A | kPa | — |
| Temperatura catalizador banco 1 | `0x3C` | (A×256+B)/10 − 40 | °C | — |
| Temperatura catalizador banco 1 s2 | `0x3E` | (A×256+B)/10 − 40 | °C | — |
| Carga absoluta | `0x43` | (A×256+B) × 100/255 | % | — |
| Lambda comandada | `0x44` | (A×256+B) / 32768 | λ | — |
| Acelerador relativo | `0x45` | A × 100/255 | % | — |
| Acelerador B | `0x47` | A × 100/255 | % | — |
| Pedal D | `0x49` | A × 100/255 | % | — |
| Pedal E | `0x4A` | A × 100/255 | % | — |
| Acelerador comandado | `0x4C` | A × 100/255 | % | — |
| Tiempo con MIL encendida | `0x4D` | A×256+B | min | — |
| Tiempo desde borrar códigos | `0x4E` | A×256+B | min | — |
| Corrección O2 banco 1 | `0x55` | (A−128) × 100/128 | % | — |
| Corrección O2 banco 1 s2 | `0x56` | (A−128) × 100/128 | % | — |

### Cálculo del consumo

El consumo se calcula con la primera fuente disponible en este orden:

1. **PID `0x5E`** (consumo que entrega el propio motor, si lo tiene).
2. **MAF** (`0x10`): `L/h = MAF × 3600 / (14,7 × 745)` (mezcla estequiométrica de bencina).
3. **MAP + RPM + cilindrada**: para motores sin MAF (común en Kia/Hyundai 2.4).  
   Fórmula: `gps = MAP × RPM × cilindrada × 0,85 / (34,446 × (T_aire + 273,15))`  
   La cilindrada se configura en Ajustes → **Cilindrada del motor**.

---

## 7. Sistema de alertas

Las alertas se evalúan después de cada lectura de sensores. Un valor fuera de rango activa la alerta solo si se mantiene durante el tiempo de **hold** configurado, para evitar avisos por picos de un segundo.

### Niveles

| Nivel | Color | Tono | Voz |
|-------|-------|------|-----|
| OK | — | — | — |
| Aviso | Ámbar | Beep corto | TTS cada 2 min |
| Crítico | Rojo | Tono de emergencia | TTS cada 20 s |

### Alerta especial: subida rápida del refrigerante

Independiente del umbral absoluto, StradaOS avisa si el refrigerante sube más de **4 °C/min** cuando ya está por encima de 95 °C. Útil para detectar que el electroventilador no encendió antes de llegar al límite.

### Qué se puede configurar por alerta

- Activar / desactivar la alerta para ese sensor.
- Umbral de aviso y umbral crítico (separados para alto y bajo).
- Restaurar los valores de fábrica.

Acceso: **⚙** → **Alertas a vigilar**.

---

## 8. Códigos de falla (DTCs)

El monitor verifica los códigos cada **30 segundos** mientras está conectado. También los verifica inmediatamente al conectar.

### Qué se lee

- **Modo `03`:** Códigos confirmados (luz del motor encendida o previamente encendida).
- **Modo `07`:** Códigos pendientes (el computador los detectó pero aún no confirma).

### Formato de los códigos

Los códigos se decodifican según el estándar OBD-II:

```
P0711
│ └─── número hexadecimal de 4 dígitos (0711 = subsistema de transmisión)
└───── prefijo: P=motor/transmisión, C=chasis, B=carrocería, U=red
```

### Fuentes de descripción

StradaOS usa tres capas sin necesidad de internet:

1. **Tabla en español** (`assets/obd/dtc_es.tsv`): códigos comunes con descripción, severidad y qué hacer.
2. **Lista SAE genérica en inglés** (`assets/obd/dtc_en.tsv`): ~2100 códigos P0/P2/P3.
3. **IA opcional** (requiere API key en Ajustes): explica el código para tu auto específico en el idioma de StradaOS. La respuesta se guarda en el dispositivo para usarse sin internet.

### Severidad

| Nivel | Descripción |
|-------|-------------|
| Leve | Se puede seguir conduciendo, revisar pronto |
| Revisar | Lleva el auto al taller pronto |
| Grave | Detente o ve al taller de inmediato |

### Freeze frame

Cuando se guarda un código confirmado, StradaOS pide automáticamente el **freeze frame** (modo `02`): los valores del motor en el momento exacto en que el computador registró el código. Se guardan los PIDs: `0x02`, `0x04`, `0x05`, `0x06`, `0x07`, `0x0B`, `0x0C`, `0x0D`, `0x0E`, `0x0F`, `0x10`, `0x11`, `0x1F`, `0x2F`, `0x42`, `0x46`.

### Borrar códigos

Botón **Borrar códigos** visible en el panel lateral cuando hay DTCs activos. Usa el modo `04` del protocolo OBD-II. El auto puede rechazar el borrado si las condiciones del motor no lo permiten.

---

## 9. Gráficos en vivo

Toca cualquier tarjeta de sensor para abrir la vista de gráficos.

- Se pueden comparar hasta **3 sensores** a la vez en gráficos apilados.
- Desliza el dedo sobre el gráfico para ver el valor de cada instante.
- Ventanas de tiempo disponibles: 1 min · 5 min · 15 min · 30 min.
- El historial máximo es de ~30 min por sensor (~2700 lecturas).

---

## 10. Modo demo (sin escáner)

StradaOS incluye un **auto simulado** para probar el módulo sin el escáner ni el auto.

Acceso: **Vehículo** → **⚙** → **Escáner** → **Auto simulado**  
O desde la pantalla de bienvenida: **Probar con un auto simulado**.

El simulador incluye:

- Motor que calienta desde 25 °C hasta 90 °C en ~60 s.
- Subida de cuesta simulada entre los 40–140 s (sube carga, RPM, temperatura).
- Código de falla `P0711` que aparece a los ~70 s (caja de cambios).
- Voltaje del alternador que baja después de los 100 s (simula alternador fallando).
- VIN simulado: `DEMO0STRADAOS001`.

---

## 11. Notas de compatibilidad

### Android 4.1 (API 16)

El módulo es compatible con Android 4.1 en adelante. En versiones antiguas:

- El TTS usa `tts.speak()` con `HashMap` de parámetros (la variante con `Bundle` es API 21).
- La conexión TCP no usa `StandardCharsets` (no existe en API 16–18); usa `"US-ASCII"` como string literal.

### Múltiples computadores en el auto

Algunos autos (Kia Sorento, Hyundai con caja automática) tienen más de un computador respondiendo a los PIDs. StradaOS:

- En la lista de PIDs soportados: combina las respuestas de todos los computadores (no se queda solo con el primero).
- En la luz del motor: la enciende si **cualquier** computador la reporta activa.
- En los valores: se queda con la respuesta más completa (más bytes no nulos).

### Clones ELM327

Los clones baratos pueden presentar alguna de estas conductas, todas manejadas por StradaOS:

| Problema | Solución aplicada |
|----------|------------------|
| Solo acepta conexión Bluetooth insegura | Prueba los 3 métodos en orden |
| Mide ~1 V de menos (ATRV) | Prefiere el PID `0x42` del computador |
| CAN multiframe necesita `ATSP6` | Se puede forzar en Ajustes → Protocolo |

---

## 12. Referencia de SharedPreferences

Nombre del archivo de preferencias: `launcher` (modo `MODE_PRIVATE`).

| Clave | Tipo | Valor por defecto | Descripción |
|-------|------|------------------|-------------|
| `obd_addr` | String | `"demo"` | Dirección del adaptador: `"demo"`, `"tcp:IP:PUERTO"` o `"bt:MAC"` |
| `obd_name` | String | — | Nombre legible del dispositivo (solo informativo) |
| `obd_protocol` | String | `"auto"` | Protocolo CAN: `"auto"`, `"6"`, `"7"`, `"8"`, `"9"` |
| `obd_displacement` | float | `2.4` | Cilindrada del motor en litros (para el cálculo del consumo por MAP) |
| `obd_voice` | boolean | `true` | Avisos por voz (TTS) activados |
| `obd_autostart` | boolean | `false` | Iniciar el monitor automáticamente al abrir StradaOS |
| `obd_vehicle` | String | `""` | Descripción del vehículo para la IA (ej: `"Kia Sorento 2010 2.4"`) |
| `obd_all_view` | boolean | `false` | Vista "Todos los datos" vs panel de tarjetas |
| `obd_chart_win` | long | `300000` | Ventana del gráfico en ms (60000/300000/900000/1800000) |
| `obd_alert_<key>` | boolean | `true` | Alerta del sensor `<key>` activada |
| `obd_thr_<key>_hi1` | float | (fábrica) | Umbral de aviso alto del sensor `<key>` |
| `obd_thr_<key>_hi2` | float | (fábrica) | Umbral crítico alto del sensor `<key>` |
| `obd_thr_<key>_lo1` | float | (fábrica) | Umbral de aviso bajo del sensor `<key>` |
| `obd_thr_<key>_lo2` | float | (fábrica) | Umbral crítico bajo del sensor `<key>` |
| `obd_thr_<key>_abs1` | float | (fábrica) | Umbral de aviso por valor absoluto del sensor `<key>` |
| `obd_thr_<key>_abs2` | float | (fábrica) | Umbral crítico por valor absoluto del sensor `<key>` |
| `obd_alert_coolant_rise` | boolean | `true` | Alerta de subida rápida del refrigerante |
| `obd_alert_dtc` | boolean | `true` | Aviso por voz al detectar un DTC nuevo |

### Claves de sensor (`<key>`)

`coolant` · `volt` · `rpm` · `speed` · `load` · `throttle` · `intake` · `fuel` · `stft` · `ltft` · `maf` · `map` · `fuel_rate` · `oilt` · `ambient` · `timing` · `o2_1` · `o2_2` · `run_time` · `mil_dist` · `egr` · `warmups` · `clear_dist` · `baro` · `cat1` · `cat2` · `abs_load` · `lambda` · `throttle_rel` · `throttle_b` · `pedal_d` · `pedal_e` · `throttle_cmd` · `mil_time` · `clear_time` · `o2_trim_1` · `o2_trim_2`

---

*Documentación basada en el código fuente de StradaOS. Las fórmulas y valores corresponden a la implementación en `Obd.java`, `Elm.java` y `DtcInfo.java`.*
