import re

path = '/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7/m3ua/impl/src/main/java/org/restcomm/protocols/ss7/m3ua/impl/M3UAManagementImpl.java'
with open(path, 'r') as f:
    content = f.read()

# Fix store() assignments
old_store = '''            config.aspFactories = this.aspFactories;
            config.appServers = this.appServers;'''
new_store = '''            config.aspFactories = new CopyOnWriteArrayList<>();
            for (AspFactory af : this.aspFactories) {
                config.aspFactories.add((AspFactoryImpl) af);
            }
            config.appServers = new CopyOnWriteArrayList<>();
            for (As as : this.appServers) {
                config.appServers.add((AsImpl) as);
            }'''
content = content.replace(old_store, new_store)

# Fix loadVer2() assignments
old_load = '''        aspFactories = config.aspFactories != null ? config.aspFactories : new CopyOnWriteArrayList<>();
        appServers = config.appServers != null ? config.appServers : new CopyOnWriteArrayList<>();'''
new_load = '''        aspFactories = config.aspFactories != null ? new CopyOnWriteArrayList<AspFactory>(config.aspFactories) : new CopyOnWriteArrayList<AspFactory>();
        appServers = config.appServers != null ? new CopyOnWriteArrayList<As>(config.appServers) : new CopyOnWriteArrayList<As>();'''
content = content.replace(old_load, new_load)

with open(path, 'w') as f:
    f.write(content)
print('Patched store/loadVer2')
