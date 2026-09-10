$screensMap = @{
    "screen1"  = "8d0c8462ac4d41778ed8f792bfe5e452"
    "screen2"  = "59ecbad44dd24089b979373d9d8526c3"
    "screen4"  = "17f0449a8a254d988331a71c4aaad3ab"
    "screen5"  = "4659b4fb05c04675b0ded013837c1640"
    "screen6"  = "ae321fcf4add408c884222dbe02e87d1"
    "screen7"  = "903d70b6a5194910aba22610ec51d13a"
    "screen8"  = "54a8338ba1824539bbfba0e0319753da"
    "screen9"  = "e969ee12216647c0814154bd8c1bc54b"
    "screen10" = "214dcdbf62a04c1b99cb2a8e69fb6bbf"
    "screen11" = "2e8f490bf1344fef8cac4c92a64a0d00"
    "screen12" = "769b2826ef034bd5942f66c0bcb96541"
    "screen13" = "128d30bc7f5e4e31ad6e3a508c7a7381"
    "screen14" = "967fa1368b654e0781a16d34b6302f62"
    "screen15" = "7535bade1c174fa19c804014b4727f76"
    "screen16" = "519aec6ba7e846b39a53eacc2fda1fef"
    "screen17" = "3682d39a4d8747dc90bfa6b5b5c83af0"
    "screen18" = "7ea03fb9a55446b0a5a4ab89f8e021bb"
    "screen19" = "bba4b5f4f2d64459b921b2979ef4ed88"
    "screen20" = "48b17a2af4694255a2fe2d74a20d6a99"
    "screen21" = "5cbf6c0519344dc6852f08db30b23173"
    "screen22" = "1ddacd6362664c75ac187ec0c35b69ae"
    "screen23" = "e6f9fd4623064166940b484df72db5f3"
    "screen24" = "308ac0cb5c9745bf9ab1ed596658558f"
    "screen25" = "56cab93902ef4a54afab57da039464f5"
    "screen26a"= "1e534a2783b84f67bb57aca6114c0459"
    "screen26b"= "e10383b0b3c64abe8ed567cdbcc6a811"
    "screen27" = "05e40d77fea841f8871bc50d64a8cf75"
    "screen28" = "289d9339f955420181e54ddc43ea2660"
    "screen29" = "9707389dfbc04c9fb8c3d3d0e8296b72"
    "screen30" = "e309b23dde2e4980b163beec170fc07e"
    "screen31" = "b490004e9c3746cd8c81c6cde0cc096f"
    "screen32" = "f965093389ff4c8999c832afc66274f0"
    "screen33" = "108cf88556e142f2b7abdc73d1c18749"
    "screen34" = "e18ab28d73694ac9b574c7fa54d598f9"
}

$data = Get-Content 'C:\Users\Dell\.gemini\antigravity-ide\brain\e04f7d9e-a79d-4f0a-be53-eb5a6e4bcc19\.system_generated\steps\77\output.txt' -Raw | ConvertFrom-Json

$outDir = "c:\Users\Dell\OneDrive\Desktop\SIH TeamName\Aastriti\raw_screens"
if (!(Test-Path $outDir)) {
    New-Item -ItemType Directory -Path $outDir | Out-Null
}

$downloadCount = 0
foreach ($key in $screensMap.Keys) {
    $screenId = $screensMap[$key]
    $match = $data.screens | Where-Object { $_.name.EndsWith($screenId) }
    if ($match) {
        $outFile = Join-Path $outDir "$key.html"
        Write-Host "Downloading $key ($($match.title))..."
        Invoke-WebRequest -Uri $match.htmlCode.downloadUrl -OutFile $outFile -UseBasicParsing
        $downloadCount++
    } else {
        Write-Warning "Could not find screen with ID $screenId for $key"
    }
}
Write-Host "`nCompleted! Downloaded $downloadCount screens to $outDir"
