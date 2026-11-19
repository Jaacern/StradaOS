package com.stradaos.launcher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;

/**
 * Tema HUD Táctico Militar - Diseño tipo cockpit con elementos verdes fosforescentes,
 * líneas técnicas, indicadores tipo radar y estética de sistemas militares.
 * 
 * Inspirado en interfaces HUD de aviación militar y sistemas de visión nocturna.
 */
final class TacticalHUD {
    private TacticalHUD() { }

    static final String ID = "tactical_hud";
    
    // Paleta HUD militar
    static final int HUD_GREEN = 0xFF00FF41;        // Verde fosforescente principal
    static final int HUD_GREEN_DIM = 0xFF00AA2B;    // Verde atenuado
    static final int HUD_CYAN = 0xFF00FFFF;         // Cyan para alertas
    static final int HUD_AMBER = 0xFFFFAA00;        // Ámbar para advertencias
    static final int HUD_RED = 0xFFFF0033;          // Rojo para crítico
    
    // Colores de fondo y superficies
    static int base(boolean light) { 
        return light ? 0xFF0A1A0F : 0xFF000A03; 
    }
    
    static int glass(boolean light) { 
        return light ? 0x33003311 : 0x1A001A08; 
    }
    
    static int glassEdge(boolean light) { 
        return light ? 0x66005522 : 0x4400FF41; 
    }
    
    static int scanLine(boolean light) {
        return light ? 0x1100FF41 : 0x0800FF41;
    }
    
    // Tarjetas con borde técnico verde
    static int cardBg(boolean light) {
        return light ? 0xE0050F08 : 0xF0000A05;
    }
    
    static int cardBorder(boolean light) {
        return light ? 0x8800FF41 : 0xBB00FF41;
    }
    
    static int textPrimary(boolean light) {
        return light ? 0xFF00FF41 : 0xFF00FF41;
    }
    
    static int textSecondary(boolean light) {
        return light ? 0xFF00AA2B : 0xFF00CC35;
    }
    
    static int gridLine(boolean light) {
        return light ? 0x2200FF41 : 0x1800FF41;
    }

    private static Bitmap cache;
    private static String cacheKey;

    /**
     * Fondo HUD: base oscura con grid técnico, scanlines, y elementos tipo radar
     */
    static Bitmap background(int w, int h, boolean light, int accent) {
        int bw = Math.max(64, w / 2), bh = Math.max(64, h / 2);
        String key = bw + "x" + bh + ":" + light;
        if (cache != null && !cache.isRecycled() && key.equals(cacheKey)) return cache;
        
        Bitmap b;
        try {
            b = Bitmap.createBitmap(bw, bh, Bitmap.Config.ARGB_8888);
        } catch (Throwable e) {
            b = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            b.eraseColor(base(light));
            return b;
        }
        
        Canvas c = new Canvas(b);
        paint(c, bw, bh, light);
        scanLines(b, light);
        
        cache = b;
        cacheKey = key;
        return b;
    }

    /**
     * Pinta el fondo con elementos HUD: grid técnico, gradientes y zonas de atención
     */
    static void paint(Canvas c, float w, float h, boolean light) {
        // Base oscura
        c.drawColor(base(light));
        
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        
        // Grid técnico de fondo
        p.setColor(gridLine(light));
        p.setStrokeWidth(0.5f);
        p.setStyle(Paint.Style.STROKE);
        
        float gridSize = Math.min(w, h) / 12f;
        for (float x = 0; x < w; x += gridSize) {
            c.drawLine(x, 0, x, h, p);
        }
        for (float y = 0; y < h; y += gridSize) {
            c.drawLine(0, y, w, y, p);
        }
        
        // Gradiente radial verde en esquinas (efecto glow)
        float glowRadius = Math.max(w, h) * 0.4f;
        
        // Esquina superior izquierda
        p.setShader(new android.graphics.RadialGradient(
            0, 0, glowRadius,
            new int[]{a(HUD_GREEN, 0.15f), 0},
            new float[]{0f, 1f},
            Shader.TileMode.CLAMP
        ));
        c.drawRect(0, 0, w, h, p);
        
        // Esquina inferior derecha
        p.setShader(new android.graphics.RadialGradient(
            w, h, glowRadius,
            new int[]{a(HUD_CYAN, 0.12f), 0},
            new float[]{0f, 1f},
            Shader.TileMode.CLAMP
        ));
        c.drawRect(0, 0, w, h, p);
        
        // Líneas diagonales de acento
        p.setShader(null);
        p.setColor(a(HUD_GREEN, 0.08f));
        p.setStrokeWidth(1f);
        for (int i = 0; i < 8; i++) {
            float offset = i * (w + h) / 8f;
            c.drawLine(-h + offset, h, offset, 0, p);
        }
    }

    /**
     * Aplica scanlines horizontales tipo CRT/monitor militar
     */
    static void scanLines(Bitmap b, boolean light) {
        int w = b.getWidth(), h = b.getHeight();
        int[] px = new int[w];
        int scanColor = scanLine(light);
        
        for (int y = 0; y < h; y += 2) {
            b.getPixels(px, 0, w, 0, y, w, 1);
            for (int x = 0; x < w; x++) {
                px[x] = blendColor(px[x], scanColor);
            }
            b.setPixels(px, 0, w, 0, y, w, 1);
        }
    }

    private static int blendColor(int base, int overlay) {
        int oa = (overlay >>> 24) & 0xFF;
        if (oa == 0) return base;
        
        int or = (overlay >> 16) & 0xFF;
        int og = (overlay >> 8) & 0xFF;
        int ob = overlay & 0xFF;
        
        int br = (base >> 16) & 0xFF;
        int bg = (base >> 8) & 0xFF;
        int bb = base & 0xFF;
        
        float a = oa / 255f;
        int r = (int)(or * a + br * (1 - a));
        int g = (int)(og * a + bg * (1 - a));
        int bl = (int)(ob * a + bb * (1 - a));
        
        return 0xFF000000 | (r << 16) | (g << 8) | bl;
    }

    static int a(int color, float alpha) {
        return (color & 0x00FFFFFF) | (Math.max(0, Math.min(255, (int)(alpha * 255))) << 24);
    }

    /**
     * Indicador circular tipo radar/brújula para el mapa
     */
    static final class RadarView extends View {
        private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint dashPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF rect = new RectF();
        private float angle = 0f;
        
        public RadarView(Context c) {
            super(c);
            dashPaint.setStyle(Paint.Style.STROKE);
            dashPaint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));
        }
        
        public void setAngle(float degrees) {
            angle = degrees;
            invalidate();
        }
        
        @Override
        protected void onDraw(Canvas c) {
            float w = getWidth(), h = getHeight();
            float cx = w / 2, cy = h / 2;
            float radius = Math.min(w, h) * 0.4f;
            
            // Círculos concéntricos
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(1.5f);
            p.setColor(a(HUD_GREEN, 0.4f));
            for (int i = 1; i <= 3; i++) {
                c.drawCircle(cx, cy, radius * i / 3f, p);
            }
            
            // Líneas cardinales
            dashPaint.setStrokeWidth(1f);
            dashPaint.setColor(a(HUD_GREEN, 0.3f));
            c.drawLine(cx, cy - radius, cx, cy + radius, dashPaint);
            c.drawLine(cx - radius, cy, cx + radius, cy, dashPaint);
            
            // Sweep radar (línea giratoria)
            c.save();
            c.rotate(angle, cx, cy);
            p.setStrokeWidth(2f);
            p.setShader(new LinearGradient(
                cx, cy, cx, cy - radius,
                new int[]{a(HUD_GREEN, 0.8f), a(HUD_GREEN, 0f)},
                new float[]{0f, 1f},
                Shader.TileMode.CLAMP
            ));
            c.drawLine(cx, cy, cx, cy - radius, p);
            p.setShader(null);
            c.restore();
            
            // Centro
            p.setStyle(Paint.Style.FILL);
            p.setColor(HUD_GREEN);
            c.drawCircle(cx, cy, 4f, p);
        }
    }

    /**
     * Tarjeta HUD con bordes técnicos y esquinas cortadas
     */
    static void drawHUDCard(Canvas c, float l, float t, float r, float b, boolean light) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        float corner = Math.min(r - l, b - t) * 0.04f;
        
        // Fondo semitransparente
        Path path = new Path();
        path.moveTo(l + corner, t);
        path.lineTo(r - corner, t);
        path.lineTo(r, t + corner);
        path.lineTo(r, b - corner);
        path.lineTo(r - corner, b);
        path.lineTo(l + corner, b);
        path.lineTo(l, b - corner);
        path.lineTo(l, t + corner);
        path.close();
        
        p.setStyle(Paint.Style.FILL);
        p.setColor(cardBg(light));
        c.drawPath(path, p);
        
        // Borde verde brillante
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(2f);
        p.setColor(cardBorder(light));
        c.drawPath(path, p);
        
        // Detalles en esquinas
        p.setStrokeWidth(3f);
        p.setColor(a(HUD_GREEN, 0.6f));
        float detail = corner * 0.6f;
        c.drawLine(l, t + corner, l, t + corner - detail, p);
        c.drawLine(l + corner, t, l + corner - detail, t, p);
        
        c.drawLine(r - corner, t, r - corner + detail, t, p);
        c.drawLine(r, t + corner, r, t + corner - detail, p);
        
        c.drawLine(r, b - corner, r, b - corner + detail, p);
        c.drawLine(r - corner, b, r - corner + detail, b, p);
        
        c.drawLine(l + corner, b, l + corner - detail, b, p);
        c.drawLine(l, b - corner, l, b - corner + detail, p);
    }

    /**
     * Miniatura del tema para la galería
     */
    static final class Thumb extends View {
        final boolean portrait, light;
        boolean selected;
        private final float s;
        private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF r = new RectF();
        private Bitmap bg;

        Thumb(Context c, float s, boolean portrait, boolean light) {
            super(c);
            this.s = s;
            this.portrait = portrait;
            this.light = light;
        }

        @Override
        protected void onMeasure(int wSpec, int hSpec) {
            int w = MeasureSpec.getSize(wSpec);
            setMeasuredDimension(w, (int)(w * (portrait ? 1.42f : 0.586f)));
        }

        @Override
        protected void onDraw(Canvas c) {
            int w = getWidth(), h = getHeight();
            if (w <= 0 || h <= 0) return;
            
            float rad = Math.min(w, h) * 0.06f;
            
            // Fondo HUD
            if (bg == null || bg.getWidth() != w || bg.getHeight() != h) {
                try {
                    bg = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                    paint(new Canvas(bg), w, h, light);
                    scanLines(bg, light);
                } catch (Throwable e) { 
                    bg = null; 
                }
            }
            
            r.set(0, 0, w, h);
            if (bg != null) {
                p.setStyle(Paint.Style.FILL);
                p.setShader(new android.graphics.BitmapShader(bg, 
                    Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                c.drawRoundRect(r, rad, rad, p);
                p.setShader(null);
            } else {
                p.setColor(base(light));
                c.drawRoundRect(r, rad, rad, p);
            }
            
            float pad = w * 0.04f;
            float cardW = (w - 3 * pad) / 2;
            float cardH = h * 0.25f;
            
            // Dos tarjetas HUD de ejemplo
            drawHUDCard(c, pad, pad, pad + cardW, pad + cardH, light);
            drawHUDCard(c, pad * 2 + cardW, pad, w - pad, pad + cardH, light);
            
            // Texto simulado verde
            p.setStyle(Paint.Style.FILL);
            p.setColor(textPrimary(light));
            float textY = pad + cardH * 0.3f;
            c.drawRect(pad * 1.5f, textY, pad + cardW * 0.6f, textY + 3, p);
            
            p.setColor(textSecondary(light));
            c.drawRect(pad * 1.5f, textY + 8, pad + cardW * 0.4f, textY + 10, p);
            
            // Indicador de selección
            if (selected) {
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(2.5f * s);
                p.setColor(HUD_GREEN);
                r.set(1.25f * s, 1.25f * s, w - 1.25f * s, h - 1.25f * s);
                c.drawRoundRect(r, rad, rad, p);
            }
        }
    }
}
