package com.dcvs.util;

import java.awt.Color;
import java.awt.Font;

/**
 * Dark Charcoal + Electric Cyan + Neon Green theme constants.
 */
public final class AppConstants {

    private AppConstants() {}

    // ── Organization ──────────────────────────────────────────────────────────
    public static final String ORG_NAME         = "DCVS Institute of Technology";
    public static final String ORG_TAGLINE      = "Empowering Learners Through Certified Excellence";
    public static final String ORG_WEBSITE      = "https://dcvs.institute";
    public static final String SYSTEM_VERSION   = "2.0";

    // ── Dark Theme Colors ─────────────────────────────────────────────────────
    // Backgrounds
    public static final Color BG_DARKEST  = new Color(10,  14,  20);   // #0A0E14 near-black
    public static final Color BG_DARK     = new Color(15,  20,  30);   // #0F141E main bg
    public static final Color BG_CARD     = new Color(20,  28,  40);   // #141C28 card bg
    public static final Color BG_ELEVATED = new Color(26,  36,  52);   // #1A2434 elevated
    public static final Color BG_INPUT    = new Color(18,  26,  38);   // #121A26 inputs
    public static final Color BG_SIDEBAR  = new Color(12,  18,  28);   // #0C121C sidebar

    // Electric Cyan (primary accent)
    public static final Color CYAN        = new Color(0,   230, 255);   // #00E6FF
    public static final Color CYAN_DIM    = new Color(0,   180, 210);   // #00B4D2
    public static final Color CYAN_DARK   = new Color(0,   100, 130);   // #006482
    public static final Color CYAN_GLOW   = new Color(0,   230, 255, 40); // transparent glow

    // Neon Green (success / valid)
    public static final Color GREEN       = new Color(0,   255, 136);   // #00FF88
    public static final Color GREEN_DIM   = new Color(0,   200, 100);   // #00C864
    public static final Color GREEN_DARK  = new Color(0,    80,  40);   // #005028

    // Alert colors
    public static final Color DANGER      = new Color(255,  60,  80);   // #FF3C50
    public static final Color WARNING     = new Color(255, 190,  50);   // #FFBE32
    public static final Color ORANGE      = new Color(255, 120,  40);   // #FF7828

    // Text
    public static final Color TEXT_PRIMARY = new Color(220, 240, 255);  // #DCF0FF near-white
    public static final Color TEXT_MUTED   = new Color(100, 140, 170);  // #648CAA muted
    public static final Color TEXT_DIM     = new Color(60,  90, 120);   // #3C5A78 very muted

    // Borders
    public static final Color BORDER       = new Color(0,  230, 255, 40);  // cyan transparent
    public static final Color BORDER_SOLID = new Color(30,  60,  90);      // #1E3C5A solid

    // ── Legacy aliases (keeps old code compiling) ─────────────────────────────
    public static final Color PRIMARY   = CYAN;
    public static final Color SECONDARY = BG_SIDEBAR;
    public static final Color ACCENT    = CYAN_DIM;
    public static final Color SUCCESS   = GREEN;
    public static final Color BG        = BG_DARK;
    public static final Color CARD      = BG_CARD;
    public static final Color TEXT_DARK = TEXT_PRIMARY;

    // ── Fonts ─────────────────────────────────────────────────────────────────
    public static final Font FONT_MONO   = new Font("Monospaced", Font.PLAIN, 13);
    public static final Font FONT_TITLE  = new Font("SansSerif",  Font.BOLD,  22);
    public static final Font FONT_BODY   = new Font("SansSerif",  Font.PLAIN, 13);
    public static final Font FONT_LABEL  = new Font("SansSerif",  Font.BOLD,  11);
    public static final Font FONT_SMALL  = new Font("SansSerif",  Font.PLAIN, 11);

    // ── Certificate ───────────────────────────────────────────────────────────
    public static final String AUTHORIZED_SIGNATORY = "Director, Academic Affairs";
}