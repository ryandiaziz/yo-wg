# Panduan Instalasi yo-wg untuk Linux

Aplikasi **yo-wg** adalah manajer WireGuard berbasis JavaFX yang memungkinkan Anda mengelola konfigurasi WireGuard, mengedit akses, dan resources dengan mudah. Berikut adalah langkah-langkah untuk menginstal dan menjalankan aplikasi ini di Linux.

## Prasyarat (Prerequisites)

Sebelum memulai, pastikan sistem Anda memiliki paket-paket berikut:

1.  **Java JDK 21**: Aplikasi ini dibangun menggunakan Java 21.
    ```bash
    sudo apt update
    sudo apt install openjdk-21-jdk
    ```
2.  **Maven**: Untuk membuild proyek.
    ```bash
    sudo apt install maven
    ```
3.  **WireGuard**: Inti dari fungsionalitas aplikasi.
    ```bash
    sudo apt install wireguard
    ```
4.  **GNOME Terminal**: Aplikasi menggunakan `gnome-terminal` untuk membuka sesi SSH dan Ping.
    ```bash
    sudo apt install gnome-terminal
    ```

## Langkah 1: Build Aplikasi

1.  Clone atau download source code proyek ini.
2.  Buka terminal di direktori root proyek.
3.  Jalankan perintah Maven untuk membuild aplikasi:

    ```bash
    mvn clean package
    ```

    Atau jika Anda menggunakan wrapper yang disertakan:

    ```bash
    ./mvnw clean package
    ```

    Setelah proses selesai, file JAR akan tersedia di folder `target/`, misalnya: `target/yo-wg-1.0-SNAPSHOT-jar-with-dependencies.jar`.

## Langkah 2: Persiapan Direktori dan Konfigurasi

Aplikasi ini mengharapkan struktur direktori tertentu di folder home user Anda (`~/Program/yo-wg`).

1.  **Buat struktur direktori**:

    ```bash
    mkdir -p ~/Program/yo-wg/conf
    mkdir -p ~/Program/yo-wg/lib
    ```

    - `~/Program/yo-wg/conf`: Menyimpan file konfigurasi aplikasi.
    - `~/Program/yo-wg/lib`: Menyimpan database SQLite (`database.db`). Database akan dibuat otomatis jika belum ada.

2.  **Buat File Konfigurasi**:
    Buat file bernama `application.conf` di dalam folder `~/Program/yo-wg/conf/`.

    ```bash
    nano ~/Program/yo-wg/conf/application.conf
    ```

    Isi file tersebut dengan konfigurasi berikut:

    ```hocon
    app {
        # PASSWORD ROOT/SUDO ANDA
        # PERINGATAN: Password ini disimpan dalam plain text. Pastikan file ini aman.
        # Password ini digunakan untuk menjalankan perintah wg-quick dan iptables
        password = "password_sudo_anda"

        # Daftar interface WireGuard yang ingin dikelola
        list_wg = ["wg0", "wg1"]
    }
    ```

    > **PENTING**: Aplikasi ini membutuhkan hak akses root untuk menjalankan perintah `wg-quick` dan memodifikasi file di `/etc/wireguard`.

## Langkah 3: Menjalankan Aplikasi

Setelah build selesai dan konfigurasi siap, Anda dapat menjalankan aplikasi menggunakan perintah sederhana:

```bash
java -jar target/yo-wg-1.0-SNAPSHOT-jar-with-dependencies.jar
```

Catatan:

- Pastikan Anda menjalankan perintah ini dari root folder project Anda, atau sesuaikan path ke file jar.
- Tidak perlu lagi menambahkan parameter `--module-path` atau `--add-modules` karena aplikasi sudah dipackage dengan dependensi yang benar (menggunakan launcher).

## Struktur File Penting

- **Database**: `~/Program/yo-wg/lib/database.db` (Dibuat otomatis saat pertama kali dijalankan).
- **Config**: `~/Program/yo-wg/conf/application.conf`.
- **WireGuard Configs**: Aplikasi akan membaca dan menulis ke `/etc/wireguard/`.

## Troubleshooting

- **Error: Directory /etc/wireguard does not exist**: Pastikan WireGuard terinstall (`sudo apt install wireguard`).
- **Terminal tidak muncul**: Pastikan `gnome-terminal` terinstall.
- **Permission Denied**: Pastikan password di `application.conf` benar dan user Anda memiliki akses sudo.
- **JavaFX Error**: Jika Anda menggunakan text-only setup (server tanpa GUI), aplikasi ini (JavaFX) tidak akan berjalan. Pastikan Anda berada di lingkungan Desktop (GNOME/KDE/dll).
