package com.stradaos.launcher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;

/**
 * Tema premium «Cobalto» (2.24, Bloque 14). Diseño propio de StradaOS en azul noche: tres columnas en horizontal
 * (mapa que se difumina hasta la tarjeta del reproductor, reproductor con la carátula redonda y tarjeta del vehículo
 * con el velocímetro arriba, el auto en el medio y el mantenimiento abajo) y la barra de apps abajo. En vertical: el
 * mapa arriba (se difumina hacia las tarjetas), reproductor y vehículo lado a lado y la barra.
 *
 * Igual que Aurora: el fondo se pinta una sola vez a media resolución (Aurora.background lo guarda), sin desenfoques
 * en tiempo real. Usa el resto de la maquinaria de Aurora (barra de arriba, «Solo mapa», transiciones).
 */
final class Cobalto {
    private Cobalto() { }

    static final String ID = "cobalto";
    static final int BLUE = 0xFF3D7BFF, ICE = 0xFF8FD3FF;

    static int base(boolean light) { return light ? 0xFFE6ECF6 : 0xFF060B18; }
    // tarjetas: vidrio azul con el borde de arriba un poco más claro (degradado vertical)
    static int glassTop(boolean light) { return light ? 0xD9FFFFFF : 0x4A2A4680; }
    static int glassBottom(boolean light) { return light ? 0x99E4ECFA : 0x2E0D1A3A; }
    static int glassEdge(boolean light) { return light ? 0xE6FFFFFF : 0x4D6E9BFF; }
    static int pill(boolean light) { return light ? 0x4D8FA3C7 : 0xE60A1328; }
    static int pillEdge(boolean light) { return light ? 0xB3FFFFFF : 0x4D6E9BFF; }
    static int pillIcon(boolean light) { return light ? 0xFF1C2740 : 0xFFE4ECFF; }
    static int soft(boolean light) { return light ? 0x14000000 : 0x1F8FB4FF; }
    static float mapAlpha(boolean light) { return light ? 0.84f : 0.86f; }

    /** Fondo: azul noche con un resplandor detrás de las tarjetas y un reflejo suave abajo, como un piso brillante. */
    static void paint(Canvas c, float w, float h, boolean light, int accent) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        boolean tall = h > w * 1.05f;
        float big = Math.max(w, h);
        p.setShader(new LinearGradient(0, 0, 0, h,
            light ? new int[]{0xFFF1F5FC, 0xFFE2E9F5, 0xFFD5DFEF} : new int[]{0xFF0C1834, 0xFF070D1F, 0xFF04070F},
            new float[]{0f, 0.55f, 1f}, Shader.TileMode.CLAMP));
        c.drawRect(0, 0, w, h, p);
        glow(c, p, w * (tall ? 0.5f : 0.68f), h * (tall ? 0.62f : 0.28f), big * 0.55f, BLUE, light ? 0.20f : 0.34f);
        glow(c, p, w * (tall ? 0.15f : 0.12f), h * (tall ? 0.18f : 0.30f), big * 0.40f, ICE, light ? 0.16f : 0.14f);
        // reflejo: una franja clara muy suave cerca de la barra de apps
        float ry = h * (tall ? 0.90f : 0.86f);
        p.setShader(new LinearGradient(0, ry - h * 0.08f, 0, ry + h * 0.08f,
            new int[]{0, Aurora.a(light ? 0xFFFFFFFF : ICE, light ? 0.55f : 0.10f), 0}, null, Shader.TileMode.CLAMP));
        c.drawRect(0, ry - h * 0.08f, w, ry + h * 0.08f, p);
        p.setShader(null);
    }

    private static void glow(Canvas c, Paint p, float x, float y, float r, int color, float alpha) {
        p.setShader(new RadialGradient(x, y, r, new int[]{Aurora.a(color, alpha), Aurora.a(color, alpha * 0.4f), 0},
            new float[]{0f, 0.5f, 1f}, Shader.TileMode.CLAMP));
        c.drawRect(0, 0, c.getWidth(), c.getHeight(), p);
    }

    /**
     * Auto deportivo de perfil, diseño propio y genérico (no es ningún modelo real). Se dibuja con trazos, así se ve
     * nítido en cualquier tamaño. Mira a la izquierda; faros en el color de acento claro, pilotos en el acento.
     */
    static final class CarSketch extends View {
        private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path body = new Path(), glass = new Path();
        private final RectF r = new RectF();
        private final boolean light;
        private final int accent;
        private float lw = -1, lh;

        CarSketch(Context c, boolean light, int accent) {
            super(c);
            this.light = light;
            this.accent = accent;
        }

        // coordenadas de diseño: 100 de largo por 40 de alto
        private float k, ox, oy;
        private float X(float x) { return ox + x * k; }
        private float Y(float y) { return oy + y * k; }

        private void build(float w, float h) {
            k = Math.min(w / 100f, h / 40f);
            ox = (w - 100 * k) / 2;
            oy = (h - 40 * k) / 2;
            body.reset();
            body.moveTo(X(5), Y(30));
            body.quadTo(X(2.5f), Y(29), X(3), Y(25.5f));
            body.quadTo(X(4), Y(22), X(11), Y(21));
            body.quadTo(X(26), Y(18.6f), X(40), Y(17.4f));
            body.quadTo(X(46), Y(12.5f), X(53), Y(10.4f));
            body.quadTo(X(62), Y(8.8f), X(70), Y(10.6f));
            body.quadTo(X(82), Y(14), X(91), Y(17.4f));
            body.quadTo(X(97), Y(18.6f), X(97.5f), Y(23));
            body.quadTo(X(98), Y(28.5f), X(95), Y(30));
            body.lineTo(X(87.5f), Y(30));
            r.set(X(72.5f), Y(22.5f), X(87.5f), Y(37.5f));
            body.arcTo(r, 0, -180, false);
            body.lineTo(X(27.5f), Y(30));
            r.set(X(12.5f), Y(22.5f), X(27.5f), Y(37.5f));
            body.arcTo(r, 0, -180, false);
            body.close();
            glass.reset();
            glass.moveTo(X(43), Y(17.6f));
            glass.quadTo(X(48), Y(13.2f), X(54), Y(11.6f));
            glass.quadTo(X(62), Y(10.3f), X(68.5f), Y(11.8f));
            glass.quadTo(X(75), Y(13.8f), X(80), Y(16.4f));
            glass.close();
            lw = w;
            lh = h;
        }

        @Override protected void onDraw(Canvas c) {
            float w = getWidth(), h = getHeight();
            if (w <= 0 || h <= 0) return;
            if (w != lw || h != lh) build(w, h);
            p.setStyle(Paint.Style.FILL);
            // sombra en el piso
            p.setShader(new RadialGradient(X(50), Y(37.5f), 52 * k, new int[]{light ? 0x40000000 : 0x8C000000, 0},
                null, Shader.TileMode.CLAMP));
            c.save();
            c.scale(1f, 0.12f, X(50), Y(37.5f));
            c.drawCircle(X(50), Y(37.5f), 52 * k, p);
            c.restore();
            // carrocería metálica
            p.setShader(new LinearGradient(0, Y(9), 0, Y(31),
                light ? new int[]{0xFFF4F7FC, 0xFFB9C3D6, 0xFF6F7C95} : new int[]{0xFFD6DEEC, 0xFF5D6B86, 0xFF1D2639},
                new float[]{0f, 0.45f, 1f}, Shader.TileMode.CLAMP));
            c.drawPath(body, p);
            p.setShader(null);
            // ventanas
            p.setColor(light ? 0xFF2A3550 : 0xFF0A1122);
            c.drawPath(glass, p);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(Math.max(1f, 1.1f * k));
            p.setColor(light ? 0xFF8C97AD : 0xFF4A5875);
            c.drawLine(X(63), Y(10.6f), X(64.5f), Y(17.2f), p);   // pilar
            // línea de carácter y reflejo del techo
            p.setColor(light ? 0x99FFFFFF : 0x66E4ECFF);
            c.drawLine(X(12), Y(23.2f), X(90), Y(21.2f), p);
            c.drawLine(X(55), Y(10.2f), X(68), Y(10.6f), p);
            p.setStyle(Paint.Style.FILL);
            // faro (acento claro con brillo) y piloto (acento)
            p.setShader(new RadialGradient(X(6.5f), Y(23), 9 * k, new int[]{Aurora.a(ICE, 0.55f), 0}, null, Shader.TileMode.CLAMP));
            c.drawCircle(X(6.5f), Y(23), 9 * k, p);
            p.setShader(null);
            p.setColor(0xFFE8F7FF);
            r.set(X(4.5f), Y(22.2f), X(11.5f), Y(23.6f));
            c.drawRoundRect(r, k, k, p);
            p.setColor(accent);
            r.set(X(92.5f), Y(19.2f), X(97.2f), Y(20.8f));
            c.drawRoundRect(r, k, k, p);
            // ruedas
            wheel(c, 20, 30);
            wheel(c, 80, 30);
        }

        private void wheel(Canvas c, float cx, float cy) {
            p.setShader(null);
            p.setStyle(Paint.Style.FILL);
            p.setColor(0xFF0B0F18);
            c.drawCircle(X(cx), Y(cy), 6.2f * k, p);
            p.setShader(new RadialGradient(X(cx), Y(cy), 4.2f * k, new int[]{0xFFC9D2E2, 0xFF55617A}, null, Shader.TileMode.CLAMP));
            c.drawCircle(X(cx), Y(cy), 4.2f * k, p);
            p.setShader(null);
            p.setColor(0xFF1A2232);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(Math.max(1f, 0.9f * k));
            for (int i = 0; i < 5; i++) {
                double a = Math.PI * 2 * i / 5 - Math.PI / 2;
                c.drawLine(X(cx), Y(cy), X(cx) + (float) Math.cos(a) * 3.9f * k, Y(cy) + (float) Math.sin(a) * 3.9f * k, p);
            }
            p.setStyle(Paint.Style.FILL);
            p.setColor(0xFF2A3348);
            c.drawCircle(X(cx), Y(cy), 1.1f * k, p);
        }
    }

    /** Miniatura para la galería (mismas proporciones que buildCobalto en Main). */
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

        @Override protected void onMeasure(int wSpec, int hSpec) {
            int w = MeasureSpec.getSize(wSpec);
            setMeasuredDimension(w, (int) (w * (portrait ? 1.42f : 0.586f)));
        }

        private void rr(Canvas c, float l, float t, float rt, float b, float rad, int color) {
            p.setStyle(Paint.Style.FILL);
            p.setShader(null);
            p.setColor(color);
            r.set(l, t, rt, b);
            c.drawRoundRect(r, rad, rad, p);
        }

        private void card(Canvas c, float l, float t, float rt, float b, float rad) {
            p.setStyle(Paint.Style.FILL);
            p.setShader(new LinearGradient(0, t, 0, b, glassTop(light), glassBottom(light), Shader.TileMode.CLAMP));
            r.set(l, t, rt, b);
            c.drawRoundRect(r, rad, rad, p);
            p.setShader(null);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(Math.max(1f, s * 0.8f));
            p.setColor(glassEdge(light));
            c.drawRoundRect(r, rad, rad, p);
        }

        @Override protected void onDraw(Canvas c) {
            int w = getWidth(), h = getHeight();
            if (w <= 0 || h <= 0) return;
            float u = portrait ? w : h, rad = u * 0.06f, crad = u * 0.04f;
            if (bg == null || bg.getWidth() != w || bg.getHeight() != h) {
                try {
                    bg = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                    paint(new Canvas(bg), w, h, light, Main.RED);
                    Aurora.grain(bg, light);
                } catch (Throwable e) { bg = null; }
            }
            r.set(0, 0, w, h);
            if (bg != null) {
                p.setStyle(Paint.Style.FILL);
                p.setShader(new android.graphics.BitmapShader(bg, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                c.drawRoundRect(r, rad, rad, p);
                p.setShader(null);
            } else rr(c, 0, 0, w, h, rad, base(light));
            int text = light ? 0xFF16203A : 0xFFEAF0FF, muted = light ? 0xFF6E7A93 : 0xFF8494B5;
            float pad = w * (portrait ? 0.035f : 0.022f), gap = u * 0.02f, cw = w - 2 * pad;
            float hy = h * (portrait ? 0.022f : 0.035f), hh = u * 0.035f;
            rr(c, pad, hy, pad + w * (portrait ? 0.24f : 0.14f), hy + hh, hh / 2, Aurora.a(muted, 0.85f));
            p.setColor(Aurora.a(text, 0.35f));
            c.drawCircle(w - pad - hh * 0.6f, hy + hh / 2, hh * 0.75f, p);
            c.drawCircle(w - pad - hh * 2.4f, hy + hh / 2, hh * 0.75f, p);
            float top = hy + hh + gap * 1.6f;
            float pillH = portrait ? w * 0.09f : h * 0.11f, pillY = h - pad - pillH, bottom = pillY - gap;
            float tempH = u * 0.07f;
            if (!portrait) {
                float vw = cw * 0.25f, pw = cw * 0.25f;
                float vx = pad + cw - vw, px = vx - gap - pw;
                temp(c, pad, top, px - pad, tempH, text, muted);
                map(c, pad, top + tempH + gap, px - gap * 0.3f, bottom, crad, u, true);
                player(c, px, top, pw, bottom - top, crad, text, muted);
                vehicle(c, vx, top, vw, bottom - top, crad, text, muted);
            } else {
                float cardsH = h * 0.26f, cy = bottom - cardsH, cwh = (cw - gap) / 2;
                temp(c, pad, top, cw, tempH, text, muted);
                map(c, pad, top + tempH + gap, pad + cw, cy - gap * 0.3f, crad, u, false);
                player(c, pad, cy, cwh, cardsH, crad, text, muted);
                vehicle(c, pad + cwh + gap, cy, cwh, cardsH, crad, text, muted);
            }
            float pw2 = cw * (portrait ? 0.78f : 0.52f), px2 = (w - pw2) / 2;
            rr(c, px2, pillY, px2 + pw2, pillY + pillH, pillH / 2, pill(light));
            p.setColor(Aurora.a(pillIcon(light), 0.9f));
            for (int i = 0; i < 7; i++) c.drawCircle(px2 + pw2 * (i + 0.5f) / 7f, pillY + pillH / 2, pillH * 0.16f, p);
            if (selected) {
                p.setShader(null);
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(2.5f * s);
                p.setColor(Main.RED);
                r.set(1.25f * s, 1.25f * s, w - 1.25f * s, h - 1.25f * s);
                c.drawRoundRect(r, rad, rad, p);
            }
        }

        private void temp(Canvas c, float x, float y, float w, float hh, int text, int muted) {
            p.setStyle(Paint.Style.FILL);
            p.setColor(Aurora.a(text, 0.75f));
            c.drawCircle(x + hh * 0.45f, y + hh / 2, hh * 0.28f, p);
            rr(c, x + hh, y + hh * 0.22f, x + hh + w * 0.12f, y + hh * 0.78f, hh * 0.12f, Aurora.a(text, 0.9f));
            rr(c, x + hh + w * 0.15f, y + hh * 0.38f, x + hh + w * 0.38f, y + hh * 0.62f, hh * 0.1f, Aurora.a(muted, 0.8f));
        }

        private void map(Canvas c, float l, float t, float rt, float b, float crad, float u, boolean fadeRight) {
            int mapBg = light ? 0xFFE9EEF6 : 0xFF14203A, road = light ? 0xFFFFFFFF : 0xFF2E3D5E;
            rr(c, l, t, rt, b, crad, mapBg);
            float mw = rt - l, mh = b - t;
            c.save();
            c.clipRect(l, t, rt, b);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(Math.max(1.5f, u * 0.016f));
            p.setColor(road);
            for (int i = 1; i < 4; i++) c.drawLine(l, t + mh * i / 4f, rt, t + mh * i / 4f - mh * 0.15f, p);
            for (int i = 1; i < 6; i++) c.drawLine(l + mw * i / 6f, t, l + mw * i / 6f + mw * 0.05f, b, p);
            p.setColor(Aurora.a(ICE, 0.9f));
            p.setStrokeWidth(Math.max(2f, u * 0.026f));
            c.drawLine(l + mw * 0.2f, b, l + mw * 0.55f, t + mh * 0.3f, p);
            c.restore();
            // difuminado hacia la tarjeta del reproductor (o hacia las tarjetas de abajo en vertical)
            p.setStyle(Paint.Style.FILL);
            int bc = base(light);
            if (fadeRight) {
                float fx = rt - mw * 0.32f;
                p.setShader(new LinearGradient(fx, 0, rt, 0, bc & 0x00FFFFFF, bc, Shader.TileMode.CLAMP));
                c.drawRect(fx, t, rt + 1, b, p);
            } else {
                float fy = b - mh * 0.30f;
                p.setShader(new LinearGradient(0, fy, 0, b, bc & 0x00FFFFFF, bc, Shader.TileMode.CLAMP));
                c.drawRect(l, fy, rt, b + 1, p);
            }
            p.setShader(null);
        }

        private void player(Canvas c, float x, float y, float w, float hh, float rad, int text, int muted) {
            card(c, x, y, x + w, y + hh, rad);
            float d = Math.min(w * 0.55f, hh * 0.42f), cx = x + w / 2, cy = y + hh * (portrait ? 0.34f : 0.30f);
            p.setStyle(Paint.Style.FILL);
            p.setColor(Aurora.a(Main.RED, 0.85f));
            c.drawCircle(cx, cy, d / 2, p);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(Math.max(1.5f, d * 0.05f));
            p.setColor(Aurora.a(ICE, 0.7f));
            c.drawCircle(cx, cy, d / 2 + d * 0.09f, p);
            rr(c, cx - w * 0.28f, y + hh * 0.60f, cx + w * 0.28f, y + hh * 0.60f + hh * 0.045f, hh * 0.02f, Aurora.a(text, 0.9f));
            rr(c, cx - w * 0.18f, y + hh * 0.68f, cx + w * 0.18f, y + hh * 0.68f + hh * 0.03f, hh * 0.015f, Aurora.a(muted, 0.8f));
            p.setStyle(Paint.Style.FILL);
            float by = y + hh * 0.84f;
            p.setColor(Aurora.a(text, 0.7f));
            c.drawCircle(cx - w * 0.25f, by, hh * 0.03f, p);
            c.drawCircle(cx + w * 0.25f, by, hh * 0.03f, p);
            p.setColor(Main.RED);
            c.drawCircle(cx, by, hh * 0.055f, p);
        }

        private void vehicle(Canvas c, float x, float y, float w, float hh, float rad, int text, int muted) {
            card(c, x, y, x + w, y + hh, rad);
            float cx = x + w / 2, gr = Math.min(w * 0.33f, hh * 0.2f), gy = y + hh * 0.08f + gr;
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(Math.max(1.5f, gr * 0.12f));
            p.setColor(Aurora.a(text, 0.35f));
            r.set(cx - gr, gy - gr, cx + gr, gy + gr);
            c.drawArc(r, 140, 260, false, p);
            p.setColor(Main.RED);
            c.drawArc(r, 140, 90, false, p);
            // auto
            p.setStyle(Paint.Style.FILL);
            float cy0 = y + hh * 0.56f, cw0 = w * 0.78f;
            p.setColor(Aurora.a(light ? 0xFF7C88A0 : 0xFFAAB6CC, 0.9f));
            r.set(cx - cw0 / 2, cy0, cx + cw0 / 2, cy0 + hh * 0.07f);
            c.drawRoundRect(r, hh * 0.035f, hh * 0.035f, p);
            r.set(cx - cw0 * 0.15f, cy0 - hh * 0.05f, cx + cw0 * 0.22f, cy0 + hh * 0.01f);
            c.drawRoundRect(r, hh * 0.03f, hh * 0.03f, p);
            p.setColor(0xFF0B0F18);
            c.drawCircle(cx - cw0 * 0.3f, cy0 + hh * 0.075f, hh * 0.03f, p);
            c.drawCircle(cx + cw0 * 0.3f, cy0 + hh * 0.075f, hh * 0.03f, p);
            rr(c, x + w * 0.08f, y + hh * 0.80f, x + w * 0.92f, y + hh * 0.92f, hh * 0.05f, soft(light));
            rr(c, x + w * 0.12f, y + hh * 0.885f, x + w * 0.6f, y + hh * 0.9f, hh * 0.01f, 0xFF2DBE6C);
        }
    }
}
