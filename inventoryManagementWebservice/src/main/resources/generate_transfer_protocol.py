import sys
import os
import datetime
import base64
from io import BytesIO
from PIL import Image
from reportlab.pdfgen import canvas
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.lib.pagesizes import A4
from reportlab.lib.utils import ImageReader

# The following libraries have to be installed:
# pip3 install reportlab pillow

# Example exection:
# python3 ./generate_transfer_protocol.py 1 "Max Mustermann" "NB-2022-0001" "Notebook" "MacBook Pro" "space grey, 16 GB RAM, 1 TB, 2022" "123456789123456" "Eisenstadt" "IT Mitarbeiter:in" "data:image/png;base64,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"

relative_path = os.path.dirname(__file__) + '/'
date = str(datetime.datetime.now().strftime('%d.%m.%Y, %H:%M'))
logo = relative_path + '_logos/logo_small.png'
inventory_id = sys.argv[1]
received_by = sys.argv[2]
inventory_number = sys.argv[3]
item_category = sys.argv[4]
item_type = sys.argv[5]
item_name = sys.argv[6]
serial_number = sys.argv[7]
item_location = sys.argv[8]
received_from = sys.argv[9]
signature = ImageReader(Image.open(BytesIO(base64.b64decode(sys.argv[10].split(',')[1]))))

pdfmetrics.registerFont(
    TTFont('Roboto', relative_path + '_fonts/Roboto-Regular.ttf'))
pdfmetrics.registerFont(
    TTFont('Roboto-Bold', relative_path + '_fonts/Roboto-Bold.ttf'))

c = canvas.Canvas(relative_path + 'temp_protocol.pdf', pagesize=A4)

c.drawImage(logo, 300, 750, 250, 75)

c.setFont('Roboto-Bold', 24)
c.drawString(185, 700, 'Übernahmeprotokoll')

c.setFont('Roboto', 12)
c.drawString(50, 650, 'Übernehmer:in:')
c.drawString(150, 650, received_by)
c.drawString(
    50, 625, 'Der:die Übernehmer:in bestätigt hiermit, den unten angeführten Gegenstand')
c.drawString(
    50, 610, 'in ordnungsgemäßem Zustand in Empfang genommen zu haben:')
c.drawString(50, 585, 'Inventarnummer:')
c.drawString(150, 585, inventory_number)
c.drawString(50, 570, 'Kategorie:')
c.drawString(150, 570, item_category)
c.drawString(50, 555, 'Typ:')
c.drawString(150, 555, item_type)
c.drawString(50, 540, 'Beschreibung:')
c.drawString(150, 540, item_name)
c.drawString(50, 525, 'Seriennummer:')
c.drawString(150, 525, serial_number)
c.drawString(50, 510, 'Standort:')
c.drawString(150, 510, item_location)

c.drawImage(signature, 50, 420, 150, 75)
c.drawString(50, 410, received_by)
c.drawString(50, 395, date)

c.drawString(50, 60, 'Ausgegeben von:')
c.drawString(150, 60, received_from)

c.save()