$screens = Get-ChildItem -Path 'c:\Users\Dell\OneDrive\Desktop\SIH TeamName\Aastriti\screens' -Filter '*.html'
foreach ($s in $screens) {
    $content = Get-Content $s.FullName -Raw
    $hasStore = $content -match 'store.js'
    $hasNav = $content -match 'nav.js'
    $hasWiring = $content -match 'navigateToScreen'
    $status = if ($hasStore -and $hasNav -and $hasWiring) { 'OK' } else { 'MISSING' }
    Write-Host "[$status] $($s.BaseName)"
}
