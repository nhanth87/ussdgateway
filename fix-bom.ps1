# Fix BOM in Java files
$files = @(
    "C:\Users\Windows\Desktop\ethiopia-working-dir\jSS7\m3ua\impl\src\main\java\org\restcomm\protocols\ss7\m3ua\impl\JacksonXMLHelper.java",
    "C:\Users\Windows\Desktop\ethiopia-working-dir\jSS7\m3ua\impl\src\main\java\org\restcomm\protocols\ss7\m3ua\impl\M3UAManagementImpl.java",
    "C:\Users\Windows\Desktop\ethiopia-working-dir\jSS7\map\map-impl\src\main\java\org\restcomm\protocols\ss7\map\MAPJacksonHelper.java",
    "C:\Users\Windows\Desktop\ethiopia-working-dir\jSS7\cap\cap-impl\src\main\java\org\restcomm\protocols\ss7\cap\CAPJacksonXMLHelper.java",
    "C:\Users\Windows\Desktop\ethiopia-working-dir\jSS7\inap\inap-impl\src\main\java\org\restcomm\protocols\ss7\inap\INAPJacksonXMLHelper.java",
    "C:\Users\Windows\Desktop\ethiopia-working-dir\jSS7\isup\isup-impl\src\main\java\org\restcomm\protocols\ss7\isup\impl\ISUPJacksonXMLHelper.java",
    "C:\Users\Windows\Desktop\ethiopia-working-dir\jSS7\tcap\tcap-impl\src\main\java\org\restcomm\protocols\ss7\tcap\TCAPJacksonXMLHelper.java",
    "C:\Users\Windows\Desktop\ethiopia-working-dir\jSS7\tcap-ansi\tcap-ansi-impl\src\main\java\org\restcomm\protocols\ss7\tcapAnsi\TCAPAnsiJacksonXMLHelper.java",
    "C:\Users\Windows\Desktop\ethiopia-working-dir\jSS7\oam\common\jmx\src\main\java\org\restcomm\protocols\ss7\oam\common\jmx\OAMJacksonXMLHelper.java",
    "C:\Users\Windows\Desktop\ethiopia-working-dir\jSS7\tools\simulator\core\src\main\java\org\restcomm\protocols\ss7\tools\simulator\management\ToolsJacksonXMLHelper.java",
    "C:\Users\Windows\Desktop\ethiopia-working-dir\jSS7\sccp\sccp-impl\src\main\java\org\restcomm\protocols\ss7\sccp\impl\SCCPJacksonXMLHelper.java",
    "C:\Users\Windows\Desktop\ethiopia-working-dir\sctp\sctp-impl\src\main\java\org\mobicents\protocols\sctp\SctpJacksonXMLHelper.java"
)

$utf8NoBom = New-Object System.Text.UTF8Encoding $False
foreach ($file in $files) {
    if (Test-Path $file) {
        $content = [System.IO.File]::ReadAllText($file, [System.Text.Encoding]::UTF8)
        [System.IO.File]::WriteAllText($file, $content, $utf8NoBom)
        Write-Host "Fixed: $file" -ForegroundColor Green
    }
}
Write-Host "Done!" -ForegroundColor Cyan
