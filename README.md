# CT3用 Digichalizer

チャレンジタッチ契約者用改造補助アプリ

[![Build](https://github.com/s1204IT/CT3_Digichalizer/actions/workflows/build.yml/badge.svg?branch=master&event=push)](https://github.com/s1204IT/CT3_Digichalizer/actions/workflows/build.yml)

---

### インストール

1. [**ここ**](https://github.com/s1204IT/CT3_Digichalizer/releases/latest/download/CT3_Digichalizer.apk)からAPKをダウンロード
2. ADB経由でインストール
   ```
   adb install -i jp.co.benesse.dcha.dchaservice -r .\CT3_Digichalizer.apk
   ```

---

#### 権限の付与
```
adb shell pm grant me.s1204.benesse.dcha.e android.permission.WRITE_SECURE_SETTINGS
```
これを行わないと動作しません｡
