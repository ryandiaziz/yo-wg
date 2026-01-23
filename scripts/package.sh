#!/bin/bash

# Script ini digunakan oleh DEVELOPER untuk mem-package aplikasi menjadi file .zip siap kirim
# Jalankan dari root project: ./scripts/package.sh

APP_NAME="yo-wg"
VERSION="1.0-SNAPSHOT"
TARGET_JAR="target/$APP_NAME-$VERSION-jar-with-dependencies.jar"
OUTPUT_DIR="dist"
PACKAGE_NAME="$APP_NAME-linux-installer"

echo "=== Memulai Packaging Yo-WG ==="

# 1. Clean & Build Maven
echo "[1/4] Building Project..."
./mvnw clean package
if [ $? -ne 0 ]; then
    echo "Error: Build gagal."
    exit 1
fi

# 2. Siapkan Folder Dist
echo "[2/4] Menyiapkan folder distribusi..."
rm -rf "$OUTPUT_DIR"
mkdir -p "$OUTPUT_DIR/$PACKAGE_NAME"

# 3. Copy File-File Penting
echo "[3/4] Mengumpulkan file..."

# Copy JAR dan rename jadi nama simpel (yo-wg.jar) agar install.sh tidak perlu berubah tiap update versi
cp "$TARGET_JAR" "$OUTPUT_DIR/$PACKAGE_NAME/yo-wg.jar"

# Copy Installer Script & Desktop File
cp assets/install.sh "$OUTPUT_DIR/$PACKAGE_NAME/"
cp assets/yo-wg.desktop "$OUTPUT_DIR/$PACKAGE_NAME/"

# Buat README.txt untuk user
cat > "$OUTPUT_DIR/$PACKAGE_NAME/README.txt" <<EOF
Panduan Instalasi Yo-WG
=======================

1. Buka folder ini di Terminal.
2. Jalankan perintah berikut:
   chmod +x install.sh
   ./install.sh

3. Ikuti petunjuk di layar.
4. Jangan lupa edit file konfigurasi di ~/Program/yo-wg/conf/application.conf setelah instalasi!

Terima kasih.
EOF

# 4. Zip Folder
echo "[4/4] Membuat arsip ZIP..."
cd "$OUTPUT_DIR"
zip -r "$PACKAGE_NAME.zip" "$PACKAGE_NAME"
cd ..

echo "=== SELESAI ==="
echo "File siap kirim ada di: $OUTPUT_DIR/$PACKAGE_NAME.zip"
