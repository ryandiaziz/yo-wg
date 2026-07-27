#!/bin/bash

echo "=== Memulai Build .deb Package ==="

# 1. Pastikan target bersih dan compile ulang
echo "[1/3] Packaging project dengan Maven (Fat JAR)..."
mvn clean package

if [ $? -ne 0 ]; then
    echo "Error: Maven build gagal!"
    exit 1
fi

# 2. Siapkan input untuk jpackage
echo "[2/3] Menyiapkan input untuk jpackage..."
rm -rf target/jpackage-input
mkdir -p target/jpackage-input
cp target/yo-wg-1.0-SNAPSHOT-jar-with-dependencies.jar target/jpackage-input/

# 3. Jalankan jpackage
echo "[3/3] Menjalankan jpackage untuk membuat .deb..."
# Kita menggunakan jpackage yang sudah tersedia di JDK 14+
# Pindahkan output ke folder dist/ agar lebih rapi
mkdir -p dist

jpackage \
  --name "yo-wg" \
  --input target/jpackage-input \
  --main-jar yo-wg-1.0-SNAPSHOT-jar-with-dependencies.jar \
  --main-class com.ryan.yowg.Launcher \
  --type deb \
  --icon src/main/resources/com/ryan/yowg/icon.png \
  --resource-dir packaging/linux \
  --app-version 1.0.0 \
  --description "WireGuard Manager by Ryan" \
  --linux-shortcut \
  --dest dist/

if [ $? -eq 0 ]; then
    echo "=== SELESAI ==="
    echo "File .deb berhasil dibuat dan disimpan di folder dist/!"
    echo "Anda bisa menginstallnya dengan:"
    echo "sudo dpkg -i dist/yo-wg_1.0.0_amd64.deb"
else
    echo "Error: jpackage gagal membuat .deb package!"
    exit 1
fi
