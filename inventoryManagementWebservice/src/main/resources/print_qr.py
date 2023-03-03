import sys
import os
import subprocess

# The following libraries have to be installed:
# pip3 install brother-ql

# Example exection:
# python3 ./print_qr.py 1 tcp://192.168.0.5 QL-820NWB 3

# Printer label 17x54mm requires 165x566px

inventory_id = sys.argv[1]
file_name = inventory_id + '.png'
printer_ip = sys.argv[2]
printer_model = sys.argv[3]
printer_counter = int(sys.argv[4])

relative_path = os.path.dirname(__file__) + '/'
qr_codes_path = relative_path + 'qrcodes/'


def execute_bash_command(bash_command):
    process = subprocess.Popen(bash_command.split(), stdout=subprocess.PIPE)
    output, error = process.communicate()

# brother_ql -p tcp://192.168.6.181 -m QL-820NWB print -l 17x54 ' + qr_codes_path + file_name

print_command = 'brother_ql -p ' + printer_ip + ' -m ' + printer_model + ' print -l 17x54 ' + qr_codes_path + file_name
for i in range(printer_counter):
    if (i != printer_counter - 1):
        execute_bash_command(print_command + ' --no-cut')
    else:
        execute_bash_command(print_command)
