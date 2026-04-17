import os

param_dir = '/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/m3ua/impl/src/main/java/org/restcomm/protocols/ss7/m3ua/impl/parameter'
helper_path = '/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/m3ua/impl/src/main/java/org/restcomm/protocols/ss7/m3ua/impl/M3UAJacksonXMLHelper.java'

impl_files = [f for f in os.listdir(param_dir) if f.endswith('Impl.java')]
mappings = []
imports = []
for f in impl_files:
    name = f[:-9]
    impl_class = f'{name}Impl'
    impl_path = os.path.join(param_dir, f)
    with open(impl_path, 'r') as fi:
        content = fi.read()
    if 'abstract class' in content:
        continue
    mappings.append(f'        m3uaModule.addAbstractTypeMapping({name}.class, {impl_class}.class);')
    imports.append(f'import org.restcomm.protocols.ss7.m3ua.parameter.{name};')
    imports.append(f'import org.restcomm.protocols.ss7.m3ua.impl.parameter.{impl_class};')

with open(helper_path, 'r') as fh:
    hc = fh.read()

for imp in imports:
    if imp not in hc:
        hc = hc.replace('package org.restcomm.protocols.ss7.m3ua.impl;', 'package org.restcomm.protocols.ss7.m3ua.impl;\n' + imp)

old_block_start = '        SimpleModule m3uaModule = new SimpleModule("m3ua-module");'
old_block_end = '        xmlMapper.registerModule(m3uaModule);'

start_idx = hc.find(old_block_start)
end_idx = hc.find(old_block_end) + len(old_block_end)
old_block = hc[start_idx:end_idx]

new_block = '        SimpleModule m3uaModule = new SimpleModule("m3ua-module");\n'
new_block += '        m3uaModule.addAbstractTypeMapping(ASPIdentifier.class, ASPIdentifierImpl.class);\n'
new_block += '        m3uaModule.addAbstractTypeMapping(Asp.class, AspImpl.class);\n'
new_block += '        m3uaModule.addAbstractTypeMapping(As.class, AsImpl.class);\n'
for m in mappings:
    new_block += m + '\n'
new_block += '        xmlMapper.registerModule(m3uaModule);'

hc = hc.replace(old_block, new_block)

with open(helper_path, 'w') as fh:
    fh.write(hc)
print('Done')
