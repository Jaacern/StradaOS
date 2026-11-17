package com.stradaos.launcher;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

/**
 * Miniatura de un tema de Personalización (2.24): dibuja con los colores del tema actual cómo queda la pantalla
 * de inicio (tarjetas, mapa, barra). Se dibuja en código para no sumar imágenes al APK y para que siga el modo
 * claro u oscuro. Proporciones tomadas de layoutColumns() en Main (vehículo 300, reproductor 248/300 de 992).
 */
class ThemeThumb extends View {
    final boolean car, playerLeft, gauge, compact;
    boolean selected;
    private final float s;
    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF r = new RectF();
    private final Path path = new Path();

    ThemeThumb(Context c, float s, boolean car, boolean playerLeft, boolean gauge, boolean compact) {
        super(c);
        this.s = s;
        this.car = car;
        this.playerLeft = playerLeft;
        this.gauge = gauge;
        this.compact = compact;
    }

    private int a(int color, float alpha) { return (color & 0x00FFFFFF) | ((int) (alpha * 255) << 24); }

    private void rr(Canvas c, float l, float t, float rt, float b, float rad, int color) {
        p.setStyle(Paint.Style.FILL);
        p.setColor(color);
        r.set(l, t, rt, b);
        c.drawRoundRect(r, rad, rad, p);
    }

    @Override
    protected void onDraw(Canvas c) {
        float w = getWidth(), h = getHeight();
        if (w <= 0 || h <= 0) return;
        float rad = h * 0.06f;
        rr(c, 0, 0, w, h, rad, Main.BG);
        float pad = w * 0.022f, gap = w * 0.012f;

        // cabecera: hora y fecha
        rr(c, pad, h * 0.035f, pad + w * 0.13f, h * 0.085f, h * 0.02f, a(Main.TEXT, 0.85f));
        rr(c, pad + w * 0.15f, h * 0.05f, pad + w * 0.25f, h * 0.075f, h * 0.012f, a(Main.MUTED, 0.8f));
        rr(c, w - pad - w * 0.03f, h * 0.04f, w - pad, h * 0.08f, h * 0.02f, a(Main.MUTED, 0.6f));

        float dockH = compact ? h * 0.12f : h * 0.19f;
        float top = h * 0.12f, bottom = h - dockH - gap;
        float cw = w - 2 * pad;
        float carW = car ? cw * 0.30f : 0, audW = cw * (car ? 0.25f : 0.30f);
        float leftW = playerLeft ? audW : carW, rightW = playerLeft ? carW : audW;
        float x0 = pad;
        float mapX = x0 + (leftW > 0 ? leftW + gap : 0);
        float mapW = cw - leftW - rightW - (leftW > 0 ? gap : 0) - (rightW > 0 ? gap : 0);
        float rightX = mapX + mapW + gap;
        float carX = playerLeft ? rightX : x0, audX = playerLeft ? x0 : rightX;
        float crad = h * 0.045f;

        // mapa
        int mapBg = Main.LIGHT ? 0xFFE4E8ED : 0xFF262C37, road = Main.LIGHT ? 0xFFFFFFFF : 0xFF3B4454;
        rr(c, mapX, top, mapX + mapW, bottom, crad, mapBg);
        c.save();
        r.set(mapX, top, mapX + mapW, bottom);
        c.clipRect(r);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(Math.max(1.5f, h * 0.018f));
        p.setColor(road);
        float mh = bottom - top;
        for (int i = 1; i < 4; i++) c.drawLine(mapX, top + mh * i / 4f, mapX + mapW, top + mh * i / 4f - mh * 0.15f, p);
        for (int i = 1; i < 4; i++) c.drawLine(mapX + mapW * i / 4f, top, mapX + mapW * i / 4f + mapW * 0.08f, bottom, p);
        p.setColor(Main.LIGHT ? 0xFF9ED29B : 0xFF4E7A3F);
        p.setStrokeWidth(Math.max(2f, h * 0.03f));
        c.drawLine(mapX + mapW * 0.2f, bottom, mapX + mapW * 0.75f, top, p);
        c.restore();
        // flecha
        float ax = mapX + mapW * 0.5f, ay = top + mh * 0.55f, as = h * 0.045f;
        path.reset();
        path.moveTo(ax, ay - as);
        path.lineTo(ax + as * 0.75f, ay + as);
        path.lineTo(ax, ay + as * 0.45f);
        path.lineTo(ax - as * 0.75f, ay + as);
        path.close();
        p.setStyle(Paint.Style.FILL);
        p.setColor(Main.RED);
        c.drawPath(path, p);
        if (gauge) {
            // mini velocímetro en una esquina de abajo del mapa
            float gr = mh * 0.17f, gx = playerLeft ? mapX + mapW - gap - gr * 1.1f : mapX + gap + gr * 1.1f, gy = bottom - gap - gr * 1.1f;
            p.setColor(a(Main.CARD, 0.92f));
            c.drawCircle(gx, gy, gr * 1.1f, p);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(Math.max(1.2f, gr * 0.14f));
            p.setColor(a(Main.TEXT, 0.5f));
            r.set(gx - gr * 0.8f, gy - gr * 0.8f, gx + gr * 0.8f, gy + gr * 0.8f);
            c.drawArc(r, 135, 270, false, p);
            p.setColor(Main.RED);
            c.drawArc(r, 135, 70, false, p);
        }

        // tarjeta del vehículo
        if (car) {
            rr(c, carX, top, carX + carW, bottom, crad, Main.CARD);
            float cx = carX + carW / 2, ch = bottom - top;
            rr(c, carX + carW * 0.1f, top + ch * 0.1f, carX + carW * 0.9f, top + ch * 0.55f, crad * 0.7f, a(Main.BTN, 0.9f));
            rr(c, cx - carW * 0.16f, top + ch * 0.25f, cx + carW * 0.16f, top + ch * 0.4f, crad * 0.5f, a(Main.SOFT, 0.55f));
            rr(c, carX + carW * 0.1f, top + ch * 0.68f, carX + carW * 0.28f, top + ch * 0.88f, h * 0.012f, a(Main.TEXT, 0.85f));
            rr(c, carX + carW * 0.32f, top + ch * 0.8f, carX + carW * 0.48f, top + ch * 0.87f, h * 0.01f, a(Main.MUTED, 0.8f));
        }

        // reproductor
        rr(c, audX, top, audX + audW, bottom, crad, Main.CARD);
        float pcx = audX + audW / 2, ph = bottom - top;
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(Math.max(1f, h * 0.008f));
        p.setColor(a(Main.SOFT, 0.5f));
        float art = Math.min(audW * 0.28f, ph * 0.22f);
        c.drawCircle(pcx, top + ph * 0.3f, art, p);
        rr(c, pcx - audW * 0.25f, top + ph * 0.58f, pcx + audW * 0.25f, top + ph * 0.63f, h * 0.012f, a(Main.TEXT, 0.8f));
        rr(c, pcx - audW * 0.18f, top + ph * 0.67f, pcx + audW * 0.18f, top + ph * 0.70f, h * 0.01f, a(Main.MUTED, 0.7f));
        p.setStyle(Paint.Style.FILL);
        float br = Math.min(audW * 0.09f, ph * 0.075f);
        p.setColor(a(Main.BTN, 1f));
        c.drawCircle(pcx - br * 2.4f, top + ph * 0.84f, br * 0.75f, p);
        c.drawCircle(pcx + br * 2.4f, top + ph * 0.84f, br * 0.75f, p);
        p.setColor(Main.RED);
        c.drawCircle(pcx, top + ph * 0.84f, br, p);

        // barra de botones
        float dTop = h - dockH, n = 7;
        if (compact) rr(c, pad, dTop + dockH * 0.12f, w - pad, h - dockH * 0.18f, dockH * 0.35f, a(Main.CARD, 0.9f));
        float step = cw / n, dr = compact ? dockH * 0.2f : dockH * 0.22f, dy = compact ? dTop + dockH * 0.47f : dTop + dockH * 0.4f;
        p.setStyle(Paint.Style.FILL);
        for (int i = 0; i < n; i++) {
            float dx = pad + step * (i + 0.5f);
            p.setColor(a(Main.BTN, 1f));
            c.drawCircle(dx, dy, dr, p);
            if (!compact) rr(c, dx - step * 0.18f, dTop + dockH * 0.72f, dx + step * 0.18f, dTop + dockH * 0.8f, h * 0.008f, a(Main.MUTED, 0.7f));
        }

        if (selected) {
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(2.5f * s);
            p.setColor(Main.RED);
            r.set(1.25f * s, 1.25f * s, w - 1.25f * s, h - 1.25f * s);
            c.drawRoundRect(r, rad, rad, p);
        }
    }
}
