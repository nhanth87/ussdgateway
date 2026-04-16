import re
with open('/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/map/load/mo_sms_build.xml') as f:
    content = f.read()
server_start = content.find('<target name="server"')
server_end = content.find('</target>', server_start) + len('</target>')
server_section = content[server_start:server_end]
args = re.findall(r'<arg value=".*?"\s*/>', server_section)
print(f'Server args count: {len(args)}')
for i, a in enumerate(args):
    print(f'{i}: {a}')
