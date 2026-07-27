# Panduan Distribusi Aplikasi (Khusus Developer)

Dokumen ini menjelaskan dua metode untuk mendistribusikan aplikasi **Yo-WG** kepada pengguna Linux:
1. **(Recommended)** Memaketkan aplikasi menjadi file `.deb` native Linux menggunakan `jpackage`.
2. **(Legacy)** Memaketkan aplikasi menjadi `.zip` (Fat JAR).

---

## 1. Distribusi Native (.deb) - Sangat Direkomendasikan

Metode ini membungkus aplikasi beserta Java Runtime Environment (JRE) dan **ikon aplikasi resmi** ke dalam installer native Debian/Ubuntu (`.deb`). Pengguna tidak perlu menginstall Java secara terpisah.

### Lokasi Ikon Aplikasi
- File ikon aplikasi utama tersimpan di: `src/main/resources/com/ryan/yowg/icon.png`.
- Ikon ini digunakan oleh `jpackage` (`--icon`) untuk membuat shortcut di menu aplikasi OS Linux (`.desktop`), serta oleh JavaFX `Stage.getIcons()` untuk titlebar window.

### Cara Membuat Paket (.deb)
Jalankan script berikut dari root folder project:
```bash
./scripts/build-deb.sh
```

**Hasil Akhir:** File installer akan digenerate dan disimpan di `dist/yo-wg_1.0.0_amd64.deb`.

### Cara Install oleh Pengguna
Bagikan file `.deb` tersebut kepada pengguna, lalu mereka cukup menginstallnya dengan perintah:
```bash
sudo dpkg -i dist/yo-wg_1.0.0_amd64.deb
```
*(Atau `sudo apt install ./dist/yo-wg_1.0.0_amd64.deb`)*

Aplikasi akan secara otomatis terinstall di `/opt/yo-wg/` dan **shortcut aplikasi beserta ikon Yo-WG** akan langsung muncul di launcher/menu aplikasi OS pengguna.

---

## 2. Distribusi Legacy (.zip / Fat JAR)

Metode ini hanya meng-compile source code Java dan menyertakan script helper. Pengguna **diwajibkan** sudah memiliki Java 21+ yang terinstall di sistem mereka.

### Cara Membuat Paket (.zip)
Jalankan script berikut dari root folder project:
```bash
./scripts/package.sh
```

**Hasil Akhir:** File ZIP akan digenerate dan disimpan di `dist/yo-wg-linux-installer.zip`.
