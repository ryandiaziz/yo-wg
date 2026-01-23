#!/bin/bash

# Konfigurasi
APP_NAME="yo-wg"
INSTALL_DIR="$HOME/Program/$APP_NAME"
LIB_DIR="$INSTALL_DIR/lib"
CONF_DIR="$INSTALL_DIR/conf"
JAR_NAME="yo-wg.jar"
DESKTOP_FILE="yo-wg.desktop"

# Warna output
GREEN='\033[0;32m'
NC='\033[0m' # No Color

echo -e "${GREEN}Mulai instalasi $APP_NAME...${NC}"

# 1. Buat Direktori
echo "Membuat direktori instalasi di $INSTALL_DIR..."
mkdir -p "$LIB_DIR"
mkdir -p "$CONF_DIR"

# 2. Copy JAR
echo "Menyalin file aplikasi..."
if [ -f "$JAR_NAME" ]; then
    cp "$JAR_NAME" "$LIB_DIR/"
    echo "File JAR berhasil disalin."
else
    echo "Error: File $JAR_NAME tidak ditemukan di folder ini!"
    exit 1
fi

# 3. Setup Config jika belum ada
if [ ! -f "$CONF_DIR/application.conf" ]; then
    echo "Membuat konfigurasi awal..."
    # Buat file dummy atau copy dari template jika ada
    cat > "$CONF_DIR/application.conf" <<EOF
app {
    # Isikan password sudo Anda di bawah ini
    password = "CHANGE_ME"
    list_wg = ["wg0"]
}
EOF
    echo "File konfigurasi dibuat di $CONF_DIR/application.conf"
    echo "PENTING: Silakan edit file tersebut dan masukkan password sudo Anda."
else
    echo "Konfigurasi sudah ada, melewati langkah ini."
fi

# 4. Install Shortcut Desktop (Lokal user)
echo "Menginstall shortcut aplikasi..."
TARGET_DESKTOP="$HOME/.local/share/applications/$DESKTOP_FILE"

# Baca template desktop file, ganti USER_PLACEHOLDER dengan username saat ini
sed "s|USER_PLACEHOLDER|$USER|g" "$DESKTOP_FILE" > "$TARGET_DESKTOP"

# Set executable
chmod +x "$TARGET_DESKTOP"
update-desktop-database "$HOME/.local/share/applications/" 2>/dev/null

echo -e "${GREEN}Instalasi Selesai!${NC}"
echo "------------------------------------------------"
echo "1. Edit konfigurasi di: $CONF_DIR/application.conf"
echo "2. Masukkan password sudo Anda di file config tersebut."
echo "3. Aplikasi bisa dicari di menu aplikasi dengan nama 'Yo-WG'."
echo "------------------------------------------------"
