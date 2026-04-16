#!/usr/bin/env python3
"""
Script to fix GMLC compilation errors by commenting out references to missing classes
"""

import os
import re

# Classes that need to be commented out
MISSING_CLASSES = [
    'IMSVoiceOverPsSessionsIndication',
    'DaylightSavingTime', 
    'LocationInformation5GS',
    'TimeZone',
    'ESMLCCellInfoAvp',
    'GERANPositioningInfoAvp',
    'ServingNodeAvp',
    'UTRANPositioningInfoAvp',
    'AccuracyFulfilmentIndicator',
    'UtranAdditionalPositioningData',
    'UtranCivicAddress',
    'LocationEvent',
    'NetworkInitiatedSuplLocation',
    'SuplResponseHelperForMLP',
    'SuplGeoTargetArea',
    'SuplTriggerType',
    'LCSRoutingInfoAVPCodes',
    'AdditionalServingNodeAvp',
    'ServingNodeAvp',
    'UserCSGInformation',
    'NetworkNodeDiameterAddress',
    'DeferredLocationEventType',
    'LocationReportRequest',  # This should be LocationRequest
]

def fix_file(filepath):
    """Fix a single Java file"""
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
        
        original_content = content
        
        # Fix imports
        for class_name in MISSING_CLASSES:
            # Comment out import statements
            pattern = rf'^(\s*)import\s+[^;]*{class_name}[^;]*;'
            replacement = r'\1// import REMOVED_' + class_name + ';'
            content = re.sub(pattern, replacement, content, flags=re.MULTILINE)
        
        # Fix private field declarations
        for class_name in MISSING_CLASSES:
            pattern = rf'^(\s+)private\s+{class_name}\s+\w+\s*;'
            replacement = r'\1// private ' + class_name + ' REMOVED;'
            content = re.sub(pattern, replacement, content, flags=re.MULTILINE)
        
        # Fix method signatures (return types)
        for class_name in MISSING_CLASSES:
            pattern = rf'^(\s+)public\s+{class_name}\s+(get\w+)\s*\(\s*\)'
            replacement = r'\1// public ' + class_name + ' \2()'
            content = re.sub(pattern, replacement, content, flags=re.MULTILINE)
        
        # Fix method parameters
        for class_name in MISSING_CLASSES:
            pattern = rf'^(\s+)public\s+void\s+(set\w+)\s*\(\s*{class_name}\s+\w+\s*\)'
            replacement = r'\1// public void \2(' + class_name + ' REMOVED)'
            content = re.sub(pattern, replacement, content, flags=re.MULTILINE)
        
        if content != original_content:
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"Fixed: {filepath}")
            return True
        return False
    except Exception as e:
        print(f"Error fixing {filepath}: {e}")
        return False

def main():
    base_dir = r'C:\Users\Windows\Desktop\ethiopia-working-dir\gmlc'
    
    # Find all Java files
    java_files = []
    for root, dirs, files in os.walk(base_dir):
        # Skip target directories
        if 'target' in root:
            continue
        for file in files:
            if file.endswith('.java'):
                java_files.append(os.path.join(root, file))
    
    print(f"Found {len(java_files)} Java files to process")
    
    fixed_count = 0
    for filepath in java_files:
        if fix_file(filepath):
            fixed_count += 1
    
    print(f"\nFixed {fixed_count} files")

if __name__ == '__main__':
    main()
