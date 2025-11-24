$env:ANDROID_HOME = "C:\Users\k1031\AppData\Local\Android\Sdk"
./gradlew installDebug
& "$env:ANDROID_HOME\platform-tools\adb.exe" shell am start -n com.gws.auto.mobile.android/.MainActivity
