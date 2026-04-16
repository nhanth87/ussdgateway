import sys
old = '''\t\t\t<!-- Netty SCTP Transport -->\n\t\t\t<dependency>\n\t\t\t\t<groupId>io.netty</groupId>\n\t\t\t\t<artifactId>netty-transport-sctp</artifactId>\n\t\t\t\t<version>${netty.version}</version>\n\t\t\t</dependency>\n\t\t\t<!-- Scheduler -->'''
new = '''\t\t\t<!-- Netty SCTP Transport -->\n\t\t\t<dependency>\n\t\t\t\t<groupId>io.netty</groupId>\n\t\t\t\t<artifactId>netty-transport-sctp</artifactId>\n\t\t\t\t<version>${netty.version}</version>\n\t\t\t</dependency>\n\t\t\t<dependency>\n\t\t\t\t<groupId>io.netty</groupId>\n\t\t\t\t<artifactId>netty-codec-base</artifactId>\n\t\t\t\t<version>${netty.version}</version>\n\t\t\t</dependency>\n\t\t\t<!-- Scheduler -->'''
path = sys.argv[1]
with open(path, 'r') as f:
    content = f.read()
if old in content:
    content = content.replace(old, new)
    with open(path, 'w') as f:
        f.write(content)
    print(f'Updated {path}')
else:
    print(f'Pattern not found in {path}')
