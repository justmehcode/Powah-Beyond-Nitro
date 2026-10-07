"""Recolour original Powah assets without changing pixels, geometry or animation.
Requires Pillow. Usage: python tools/generate_resources.py <Powah-6.2.10-source>
"""
from pathlib import Path
from collections import deque
import colorsys, json, shutil, sys
from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / 'src/main/resources'
UPSTREAM = Path(sys.argv[1]) / 'src/main/resources'
TIERS = [('aurion', 0xFFE4A1), ('viberion', 0x39EAC3), ('oblivion', 0xAE76F8), ('singularity', 0xDBE6FF)]
FAMILIES = ['energy_cell', 'energy_cable', 'energizing_rod', 'furnator', 'magmator',
            'thermo_generator', 'solar_panel', 'reactor', 'ender_cell', 'ender_gate',
            'player_transmitter', 'energy_hopper', 'energy_discharger']

def write_json(path, data):
    path = OUT / path
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, indent=2) + '\n', encoding='utf-8')

def remap(node, tier):
    if isinstance(node, dict): return {k: remap(v, tier) for k, v in node.items()}
    if isinstance(node, list): return [remap(v, tier) for v in node]
    if isinstance(node, str) and node.startswith('powah:') and 'nitro' in node:
        return 'powahbeyondnitro:' + node.split(':',1)[1].replace('nitro', tier)
    return node

def recolour(image, color, only_red=True):
    hue, saturation, _ = colorsys.rgb_to_hsv(*[((color >> shift) & 255) / 255 for shift in (16, 8, 0)])
    output = image.copy()
    pixels = []
    for r, g, b, a in image.getdata():
        h, light, sat = colorsys.rgb_to_hls(r/255, g/255, b/255)
        selected = a and (not only_red or (sat > .15 and (h < .075 or h > .95) and r > g and r > b))
        if selected:
            rgb = colorsys.hls_to_rgb(hue, light, (sat if only_red else 1) * saturation)
            pixels.append(tuple(round(c*255) for c in rgb) + (a,))
        else: pixels.append((r, g, b, a))
    output.putdata(pixels)
    return output

def cutout(reference, left, right):
    im = reference.crop((left, 0, right, reference.height)).convert('RGBA')
    pending = deque((x,y) for x in range(im.width) for y in (0, im.height-1))
    pending.extend((x,y) for y in range(im.height) for x in (0, im.width-1))
    seen = set()
    while pending:
        x,y = pending.popleft()
        if (x,y) in seen or not (0 <= x < im.width and 0 <= y < im.height): continue
        seen.add((x,y))
        r,g,b,a = im.getpixel((x,y))
        if max(r,g,b)-min(r,g,b) > 5 or not 110 <= r <= 180: continue
        im.putpixel((x,y), (r,g,b,0))
        pending.extend(((x-1,y),(x+1,y),(x,y-1),(x,y+1)))
    # Keep the connected crystal silhouette, excluding isolated screenshot noise.
    remaining = {(x,y) for y in range(im.height) for x in range(im.width) if im.getpixel((x,y))[3]}
    components = []
    while remaining:
        seed = remaining.pop()
        group, pending = {seed}, deque([seed])
        while pending:
            x,y = pending.popleft()
            for neighbor in ((x-1,y),(x+1,y),(x,y-1),(x,y+1)):
                if neighbor in remaining:
                    remaining.remove(neighbor)
                    group.add(neighbor)
                    pending.append(neighbor)
        components.append(group)
    silhouette = max(components,key=len)
    for y in range(im.height):
        for x in range(im.width):
            if (x,y) not in silhouette: im.putpixel((x,y),(0,0,0,0))
    im = im.crop(im.getbbox())
    # Recover a native Minecraft pixel grid from the enlarged screenshot.
    # Nearest-neighbour sampling and an opaque mask avoid interpolated/soft edges.
    im.thumbnail((14,14), Image.Resampling.NEAREST)
    alpha = im.getchannel('A').point(lambda a: 255 if a >= 128 else 0)
    im = im.convert('RGB').quantize(colors=12, dither=Image.Dither.NONE).convert('RGBA')
    im.putalpha(alpha)
    canvas = Image.new('RGBA', (16,16))
    canvas.paste(im, ((16-im.width)//2, (16-im.height)//2))
    return canvas

def orb(name, ingredients, energy):
    assert 1 <= len(ingredients) <= 6
    write_json(f'data/powahbeyondnitro/recipe/energizing/{name}.json', {
        'type':'powah:energizing', 'ingredients':[{'item':x} for x in ingredients],
        'energy':energy, 'result':{'id':f'powahbeyondnitro:{name}', 'count':1}})

def clean_contour(icon, color):
    """Replace screenshot-coloured edge flecks with a consistent one-pixel rim.

    Only boundary RGB changes: the silhouette, alpha and interior facets stay exact.
    """
    hue, saturation, _ = colorsys.rgb_to_hsv(*[((color >> shift) & 255) / 255 for shift in (16,8,0)])
    outline = tuple(round(c*255) for c in colorsys.hls_to_rgb(hue, .22, saturation*.65)) + (255,)
    result = icon.copy()
    for y in range(icon.height):
        for x in range(icon.width):
            if not icon.getpixel((x,y))[3]: continue
            if any(not (0 <= nx < icon.width and 0 <= ny < icon.height) or not icon.getpixel((nx,ny))[3]
                   for nx,ny in ((x-1,y),(x+1,y),(x,y-1),(x,y+1))):
                result.putpixel((x,y),outline)
    assert result.getchannel('A').tobytes() == icon.getchannel('A').tobytes()
    return result

reference = Image.open(ROOT / 'art/source/crystal-reference.png')
crystals = [cutout(reference, a,b) for a,b in [(0,54),(54,116),(116,179),(179,245)]]
preview = Image.new('RGB',(768,360),(35,38,44))
draw = ImageDraw.Draw(preview)
lang = {'itemGroup.powahbeyondnitro':'Powah: Beyond Nitro'}
blocks = []
texture_count = 0
# Remove obsolete one-for-one Orb upgrades from earlier releases.
for tier, _ in TIERS:
    for name in [f'{family}_{tier}' for family in FAMILIES + ['capacitor','battery']] + [f'{tier}_crystal_block']:
        (OUT/f'data/powahbeyondnitro/recipe/energizing/{name}.json').unlink(missing_ok=True)

def crafting_remap(node, tier, previous, previous_ns):
    if isinstance(node,dict): return {k:crafting_remap(v,tier,previous,previous_ns) for k,v in node.items()}
    if isinstance(node,list): return [crafting_remap(v,tier,previous,previous_ns) for v in node]
    if isinstance(node,str) and node.startswith('powah:'):
        path = node.split(':',1)[1]
        if 'nitro' in path: return 'powahbeyondnitro:' + path.replace('nitro',tier)
        if 'spirited' in path: return previous_ns + ':' + path.replace('spirited',previous)
    return node

for index, (tier, color) in enumerate(TIERS):
    previous = 'nitro' if index == 0 else TIERS[index-1][0]
    ns = 'powah' if index == 0 else 'powahbeyondnitro'
    energy = 100_000_000 * 4**index
    crystal = f'powahbeyondnitro:crystal_{tier}'
    cap = f'powahbeyondnitro:capacitor_{tier}'
    ingredients = [f'{ns}:crystal_{previous}']
    if index < 3:
        ingredients += [f'allthemodium:{["allthemodium","vibranium","unobtainium"][index]}_ingot']*2 + ['minecraft:nether_star']
        if index == 1: ingredients += ['minecraft:echo_shard']
        if index == 2: ingredients += ['minecraft:dragon_breath']
    else:
        ingredients += ['powahbeyondnitro:crystal_oblivion','allthemodium:allthemodium_ingot','allthemodium:vibranium_ingot','allthemodium:unobtainium_ingot','minecraft:nether_star']
    orb(f'crystal_{tier}', ingredients, energy)
    for source in (UPSTREAM.parents[1]/'generated/resources/data/powah/recipe/crafting').glob('*nitro*.json'):
        if source.name in ('ender_cell_nitro_2.json','ender_gate_nitro_2.json'): continue
        recipe = crafting_remap(json.loads(source.read_text()), tier, previous, ns)
        write_json(f'data/powahbeyondnitro/recipe/crafting/{source.name.replace("nitro",tier)}',recipe)
    for source in (UPSTREAM/'assets/powah/textures').rglob('*nitro*.png'):
        relative = source.relative_to(UPSTREAM/'assets/powah/textures')
        target = OUT/'assets/powahbeyondnitro/textures'/str(relative).replace('nitro',tier)
        target.parent.mkdir(parents=True, exist_ok=True)
        original = Image.open(source).convert('RGBA')
        result = recolour(original,color)
        assert result.size == original.size and result.getchannel('A').tobytes() == original.getchannel('A').tobytes()
        result.save(target)
        meta = source.with_suffix('.png.mcmeta')
        if meta.exists(): shutil.copyfile(meta,target.with_suffix('.png.mcmeta'))
        texture_count += 1
    for suffix in ('machine','gem'):
        (OUT/f'assets/powahbeyondnitro/textures/block/{tier}_{suffix}.png').unlink(missing_ok=True)
    icon = clean_contour(recolour(crystals[index],color,False),color)
    icon.save(OUT/f'assets/powahbeyondnitro/textures/item/crystal_{tier}.png')
    (OUT/f'assets/powahbeyondnitro/textures/item/crystal_{tier}.png.mcmeta').unlink(missing_ok=True)
    preview.paste(icon.resize((128,128),Image.Resampling.NEAREST),(index*192+32,32),icon.resize((128,128),Image.Resampling.NEAREST))
    draw.text((index*192+45,175),tier.title(),fill='white')
    for material in ('crystal','capacitor','battery'):
        name = f'{material}_{tier}'
        source = UPSTREAM/f'assets/powah/models/item/{material}_nitro.json'
        write_json(f'assets/powahbeyondnitro/models/item/{name}.json', remap(json.loads(source.read_text()),tier))
        lang[f'item.powahbeyondnitro.{name}'] = f'{material.title()} ({tier.title()})'
    for original in [f'{family}_nitro' for family in FAMILIES] + ['nitro_crystal_block']:
        name = original.replace('nitro',tier)
        blocks.append(f'powahbeyondnitro:{name}')
        title = 'Crystal Block' if original == 'nitro_crystal_block' else original.removesuffix('_nitro').replace('_',' ').title()
        lang[f'block.powahbeyondnitro.{name}'] = f'{title} ({tier.title()})'
        for folder in ('blockstates','models/item','models/block'):
            for source in (UPSTREAM/f'assets/powah/{folder}').glob(original+'*.json'):
                write_json(f'assets/powahbeyondnitro/{folder}/{source.name.replace("nitro",tier)}',remap(json.loads(source.read_text()),tier))
        source = UPSTREAM/f'data/powah/loot_table/blocks/{original}.json'
        write_json(f'data/powahbeyondnitro/loot_table/blocks/{name}.json',remap(json.loads(source.read_text()),tier))
    tile = Image.open(OUT/f'assets/powahbeyondnitro/textures/block/energy_cell_{tier}.png').convert('RGBA')
    tile = tile.resize((96,96),Image.Resampling.NEAREST)
    preview.paste(tile,(index*192+48,218),tile)
write_json('assets/powahbeyondnitro/lang/en_us.json',lang)
write_json('data/powahbeyondnitro/recipe/crafting/reset_energy_item.json', {
    'type':'powahbeyondnitro:reset_energy_item', 'category':'misc'})
for tag in ('mineable/pickaxe','needs_diamond_tool'):
    write_json(f'data/minecraft/tags/block/{tag}.json',{'replace':False,'values':blocks})
preview.save(ROOT/'art/texture-preview.png')
print(f'Generated 4 Orb recipes, 72 standard crafting recipes and one dynamic reset recipe; recoloured {texture_count} original textures.')
