import os
for subdir in ['docs', 'sctp-api', 'sctp-impl']:
    path = f'/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/sctp/{subdir}/pom.xml'
    with open(path) as f:
        content = f.read()
    start = content.find('<parent>')
    end = content.find('</parent>', start) + len('</parent>')
    print(f'=== {path} ===')
    print(content[start:end])
    print()
