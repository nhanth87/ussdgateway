import os

jackson_imports = (
    'import com.fasterxml.jackson.dataformat.xml.XmlMapper;\n'
    'import com.fasterxml.jackson.databind.SerializationFeature;\n'
)

base_dir = '/mnt/c/Users/Windows/Desktop/ethiopia-working-dir/jSS7'

for root, _, files in os.walk(base_dir):
    if 'src/test/java' not in root:
        continue
    for fname in files:
        if not fname.endswith('.java'):
            continue
        filepath = os.path.join(root, fname)
        with open(filepath, 'r', encoding='utf-8') as f:
            text = f.read()
        if 'XmlMapper' not in text:
            continue
        if 'import com.fasterxml.jackson.dataformat.xml.XmlMapper;' in text:
            continue
        # Insert after last import or package line
        if 'import ' in text:
            last_import_idx = text.rfind('import ')
            last_import_end = text.find('\n', last_import_idx) + 1
            text = text[:last_import_end] + jackson_imports + text[last_import_end:]
        else:
            pkg_idx = text.find('package ')
            if pkg_idx != -1:
                pkg_end = text.find(';', pkg_idx) + 2
                text = text[:pkg_end] + '\n' + jackson_imports + text[pkg_end:]
            else:
                text = jackson_imports + text
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(text)
        print('Fixed imports:', filepath)
