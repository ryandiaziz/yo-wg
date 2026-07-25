# Panduan Distribusi Aplikasi (Khusus Developer)

Dokumen ini menjelaskan dua metode untuk mendistribusikan aplikasi **Yo-WG** kepada pengguna Linux:
1. **(Recommended)** Memaketkan aplikasi menjadi file `.deb` native Linux menggunakan `jpackage`.
2. **(Legacy)** Memaketkan aplikasi menjadi `.zip` (Fat JAR).

---

## 1. Distribusi Native (.deb) - Sangat Direkomendasikan

Metode ini akan membungkus aplikasi beserta Java Runtime Environment (JRE) ke dalam installer native Debian/Ubuntu (`.deb`). Pengguna tidak perlu menginstall Java secara terpisah.

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
Aplikasi akan secara otomatis terinstall di `/opt/yo-wg/` dan icon shortcut akan muncul di menu aplikasi OS pengguna.

---

## 2. Distribusi Legacy (.zip / Fat JAR)

Metode ini hanya meng-compile source code Java dan menyertakan script helper. Pengguna **diwajibkan** sudah memiliki Java 21+ yang terinstall di sistem mereka.

### Cara Membuat Paket (.zip)
Jalankan script berikut dari root folder project:
```bash
./scripts/package.sh
```

**Hasil Akhir:** File ZIP akan digenerate dan disimpan di `dist/yo-wg-linux-installer.zip`.

### Cara Install oleh Pengguna (Legacy)
1. Extract file `yo-wg-linux-installer.zip`.
2. Buka terminal di dalam folder hasil extract.
3. Jalankan script installer:
   ```bash
   chmod +x install.sh
   ./install.sh
   ```
4. Aplikasi akan di-copy ke folder `~/Program/yo-wg` dan shortcut desktop akan ditambahkan.
5. **PENTING**: User harus mengedit `~/Program/yo-wg/conf/application.conf` untuk setup awal (jika menggunakan config kustom).
