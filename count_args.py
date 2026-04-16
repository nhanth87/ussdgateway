import re
with open('/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/map/load/mo_sms_build.xml') as f:
    content = f.read()
client_start = content.find('<target name="client"')
client_end = content.find('</target>', client_start) + len('</target>')
client_section = content[client_start:client_end]
args = re.findall(r'<arg value=".*?"\s*/>', client_section)
print(f'Client args count: {len(args)}')
for i, a in enumerate(args):
    print(f'{i}: {a}')
