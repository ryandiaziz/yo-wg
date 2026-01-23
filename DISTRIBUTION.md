# Panduan Distribusi Aplikasi (Khusus Developer)

Dokumen ini menjelaskan cara memaketkan aplikasi **yo-wg** menjadi file `.zip` yang siap dibagikan ke pengguna Linux.

## Struktur Paket Distribusi

Script packaging akan membuat sebuah file ZIP berisi:

1.  **yo-wg.jar**: File aplikasi utama (hasil build maven yg direname).
2.  **install.sh**: Script helper untuk menginstall aplikasi ke komputer user.
3.  **yo-wg.desktop**: Template shortcut menu aplikasi.
4.  **README.txt**: Instruksi singkat untuk user.

## Cara Membuat Paket Distribusi

Cukup jalankan satu perintah berikut dari root folder project:

```bash
./scripts/package.sh
```

### Apa yang dilakukan script ini?

1.  Menjalankan `mvn clean package` untuk memastikan build terbaru.
2.  Membuat folder `dist/yo-wg-linux-installer`.
3.  Menyalin `target/yo-wg-1.0-SNAPSHOT-jar-with-dependencies.jar` menjadi `yo-wg.jar`.
4.  Menyertakan script helper dari folder `assets/`.
5.  Mengkompres folder tersebut menjadi `dist/yo-wg-linux-installer.zip`.

## Hasil Akhir

Setelah script selesai, Anda akan mendapatkan file:
**`dist/yo-wg-linux-installer.zip`**

Inilah file yang Anda bagikan ke pengguna.

---

## Panduan untuk Pengguna (User Guide)

Jika pengguna bertanya cara install, berikan instruksi ini (juga ada di dalam `README.txt` zip):

1.  Extract file `yo-wg-linux-installer.zip`.
2.  Buka terminal di dalam folder hasil extract.
3.  Jalankan:
    ```bash
    chmod +x install.sh
    ./install.sh
    ```
4.  Selesai! Aplikasi "Yo-WG" akan muncul di menu aplikasi komputer mereka.
5.  **PENTING**: User wajib mengedit konfigurasi di `~/Program/yo-wg/conf/application.conf` untuk memasukkan password sudo mereka.
