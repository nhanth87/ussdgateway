$file = "C:\Users\Windows\Desktop\ethiopia-working-dir\jSS7\map\load\mo_sms_build.xml"
$content = Get-Content $file -Raw

# Fix server target
$content = $content -replace '<delete dir="server" />', '<delete dir="server" failonerror="false" />'
$content = $content -replace '<delete file="log4j-server\.log" />', '<delete file="log4j-server.log" failonerror="false" />'

# Fix client target
$content = $content -replace '<delete dir="client" />', '<delete dir="client" failonerror="false" />'
$content = $content -replace '<delete file="log4j-client\.log"/>', '<delete file="log4j-client.log" failonerror="false" />'

Set-Content -Path $file -Value $content -NoNewline
Write-Host "Fixed mo_sms_build.xml - added failonerror to delete tasks"
