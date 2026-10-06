import os
from PIL import Image, ImageDraw

SRC_LOGO = r"C:\ClaudeImages3\ChatGPT Image 6 oct. 2026, 12_43_14.png"
SRC_SPLASH = r"C:\ClaudeImages3\ChatGPT Image 6 oct. 2026, 12_43_33.png"
RES = r"C:\Users\Kevin Panafrikaka\eMarket\app\src\main\res"

BG_HEX = (0xF6, 0xF7, 0xF4)

def make_foreground():
    logo = Image.open(SRC_LOGO).convert("RGB")
    w, h = logo.size
    rgba = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    src_px = logo.load()
    dst_px = rgba.load()
    lo, hi = 120, 200
    for y in range(h):
        for x in range(w):
            r, g, b = src_px[x, y]
            if r <= lo:
                a = 255
            elif r >= hi:
                a = 0
            else:
                a = int(255 * (hi - r) / (hi - lo))
            dst_px[x, y] = (r, g, b, a) if a > 0 else (0, 0, 0, 0)

    bbox = rgba.getbbox()
    content = rgba.crop(bbox)
    cw, ch = content.size

    canvas_size = 768
    target_frac = 0.60
    scale = (canvas_size * target_frac) / max(cw, ch)
    new_w, new_h = int(cw * scale), int(ch * scale)
    content_resized = content.resize((new_w, new_h), Image.LANCZOS)

    canvas = Image.new("RGBA", (canvas_size, canvas_size), (0, 0, 0, 0))
    ox = (canvas_size - new_w) // 2
    oy = (canvas_size - new_h) // 2
    canvas.paste(content_resized, (ox, oy), content_resized)
    return canvas

def make_legacy_set(fg_canvas):
    # Full square legacy icon = brand background + centered foreground content
    sizes = {
        "mipmap-mdpi": 48,
        "mipmap-hdpi": 72,
        "mipmap-xhdpi": 96,
        "mipmap-xxhdpi": 144,
        "mipmap-xxxhdpi": 192,
    }
    for folder, size in sizes.items():
        bg = Image.new("RGBA", (size, size), BG_HEX + (255,))
        fg_scaled = fg_canvas.resize((size, size), Image.LANCZOS)
        bg.paste(fg_scaled, (0, 0), fg_scaled)

        out_dir = os.path.join(RES, folder)
        os.makedirs(out_dir, exist_ok=True)
        bg.save(os.path.join(out_dir, "ic_launcher.png"))

        # Round variant: circular mask
        mask = Image.new("L", (size, size), 0)
        d = ImageDraw.Draw(mask)
        d.ellipse((0, 0, size, size), fill=255)
        round_icon = Image.new("RGBA", (size, size), (0, 0, 0, 0))
        round_icon.paste(bg, (0, 0), mask)
        round_icon.save(os.path.join(out_dir, "ic_launcher_round.png"))

def make_foreground_drawable(fg_canvas):
    out_dir = os.path.join(RES, "drawable")
    os.makedirs(out_dir, exist_ok=True)
    fg_canvas.save(os.path.join(out_dir, "ic_launcher_foreground.png"))

def make_playstore_icon(fg_canvas):
    size = 512
    bg = Image.new("RGBA", (size, size), BG_HEX + (255,))
    fg_scaled = fg_canvas.resize((size, size), Image.LANCZOS)
    bg.paste(fg_scaled, (0, 0), fg_scaled)
    bg.convert("RGB").save(os.path.join(r"C:\Users\Kevin Panafrikaka\eMarket", "play_store_icon.png"))

def make_splash():
    splash = Image.open(SRC_SPLASH).convert("RGB")
    target_w = 1080
    scale = target_w / splash.width
    target_h = int(splash.height * scale)
    resized = splash.resize((target_w, target_h), Image.LANCZOS)
    out_dir = os.path.join(RES, "drawable-nodpi")
    os.makedirs(out_dir, exist_ok=True)
    resized.save(os.path.join(out_dir, "splash_market.png"), optimize=True)
    print("splash saved", resized.size)

fg = make_foreground()
make_foreground_drawable(fg)
make_legacy_set(fg)
make_playstore_icon(fg)
make_splash()
print("done")
