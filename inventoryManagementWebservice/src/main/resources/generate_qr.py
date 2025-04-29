import sys
import os
import qrcode
from PIL import Image, ImageDraw, ImageFont

# The following libraries have to be installed:
# pip3 install qrcode pillow

# Example exection:
# python3 ./generate_qr.py 1 TEST-2022-0001

inventory_id = sys.argv[1]
file_name = inventory_id + '.png'
qr_code_text = sys.argv[2]
qr_code_text_first_line = qr_code_text.split('-')[0] + '-'
qr_code_text_second_line = qr_code_text.split('-')[1] + '-' + qr_code_text.split('-')[2]

relative_path = os.path.dirname(__file__) + '/'
qr_codes_path = relative_path + 'qrcodes/'

qr = qrcode.QRCode(
    version=2,
    box_size=5,
    border=0)

qr.add_data(qr_code_text)
img = qr.make_image(fill='black', back_color='white')

fnt = ImageFont.truetype(relative_path + '_fonts/Roboto-Regular.ttf', 60)
image = Image.new(mode="RGB", size=(566, 165), color="white")
draw = ImageDraw.Draw(image)

img = img.convert("RGB")
image.paste(img, (20, 20, 20 + img.size[0], 20 + img.size[1]))

draw.text((180, 15), qr_code_text_first_line, font=fnt, fill=(0, 0, 0))
draw.text((180, 80), qr_code_text_second_line, font=fnt, fill=(0, 0, 0))
image.save(qr_codes_path + file_name)
