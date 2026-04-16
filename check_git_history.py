import subprocess
import re

result = subprocess.run(
    ["git", "-C", "/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7", "show", "HEAD~1:map/load/mo_sms_build.xml"],
    capture_output=True, text=True
)
content = result.stdout
client_start = content.find('<target name="client"')
client_end = content.find('</target>', client_start) + len('</target>')
client_section = content[client_start:client_end]
args = re.findall(r'<arg value=".*?"\s*/>', client_section)
print(f'Client args count in HEAD~1: {len(args)}')
for i, a in enumerate(args):
    print(f'{i}: {a}')
