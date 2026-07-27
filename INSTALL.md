# Panduan Instalasi Yo-WG untuk Linux

Aplikasi **Yo-WG** adalah manajer WireGuard berbasis JavaFX yang memungkinkan Anda mengelola konfigurasi WireGuard, Access Servers, dan Resources dengan mudah.

---

## 1. Instalasi Pengguna (Menggunakan Paket `.deb`)

Metode ini adalah cara paling mudah untuk menginstal **Yo-WG** di Debian, Ubuntu, Linux Mint, Pop!_OS, dan distro turunan Debian lainnya. Paket `.deb` sudah mengemas Java Runtime (JRE), sehingga Anda **tidak perlu menginstal Java secara manual**.

### Prasyarat Sistem
Buka terminal dan pastikan paket-paket pendukung sistem berikut sudah terinstall:

```bash
sudo apt update
sudo apt install wireguard gnome-terminal sshpass
```

### Langkah Instalasi
1. Unduh file `yo-wg_1.0.0_amd64.deb` dari halaman [Releases](https://github.com/ryandiaziz/yo-wg/releases) atau direktori `dist/`.
2. Buka terminal di folder tempat file `.deb` berada, lalu jalankan:

```bash
sudo apt install ./yo-wg_1.0.0_amd64.deb
```
*(Atau menggunakan `sudo dpkg -i yo-wg_1.0.0_amd64.deb`)*

3. Setelah proses instalasi selesai, aplikasi **Yo-WG** akan tersedia di menu aplikasi OS Anda (`/opt/yo-wg/`) dan siap dijalankan.

---

## 2. Penggunaan Pertama Kali (Initial Setup)

1. Buka aplikasi **Yo-WG** dari menu aplikasi atau jalankan perintah `yo-wg` di terminal.
2. Saat pertama kali dibuka, jendela **Initial Setup** akan muncul meminta password `sudo` sistem Anda.
3. Masukkan password `sudo` Anda dan klik **Save Password**. Password ini disimpan secara lokal di database aplikasi (`~/.local/share/yo-wg/database.db`) untuk mengotomatisasi perintah WireGuard (`wg-quick up/down`).
4. Aplikasi akan secara otomatis mendeteksi dan mengimpor seluruh file konfigurasi WireGuard yang ada di `/etc/wireguard/*.conf`.

---

## 3. Build dari Source Code (Khusus Developer)

Jika Anda ingin melakukan kompilasi atau mengembangkan proyek dari *source code*:

### Prasyarat Build
- **Java JDK 21+**
- **Maven 3.8+**

```bash
sudo apt update
sudo apt install openjdk-21-jdk maven wireguard gnome-terminal sshpass
```

### Membangun Paket `.deb` (Native Package)
Untuk memaketkan proyek menjadi file `.deb` menggunakan `jpackage`:

```bash
./scripts/build-deb.sh
```
Hasil paket `.deb` akan disimpan di `dist/yo-wg_1.0.0_amd64.deb`.

### Menjalankan Mode Development
Untuk menjalankan aplikasi langsung dari terminal dalam mode development:

```bash
mvn clean compile javafx:run
```

Atau membuat executable Fat JAR:

```bash
mvn clean package
java -jar target/yo-wg-1.0-SNAPSHOT-jar-with-dependencies.jar
```

---

## 4. Lokasi File & Data Penting

- **Database Aplikasi**: `~/.local/share/yo-wg/database.db` (Dibuat otomatis).
- **Kunci SSH Shared**: `~/.ssh/yo-wg/id_yowg_shared` (Digunakan untuk fitur autologin SSH).
- **Konfigurasi WireGuard Sistem**: `/etc/wireguard/*.conf`.

---

## 5. Troubleshooting

- **Gagal Mengontrol Tunnel WireGuard**: Pastikan paket `wireguard` terinstall dan password `sudo` yang Anda masukkan saat *Initial Setup* sudah benar. Anda dapat mengubah password sudo di menu **Settings** pada sidebar aplikasi.
- **Fitur Deploy SSH Key Gagal**: Pastikan `sshpass` terinstall (`sudo apt install sshpass`).
- **Terminal SSH / Ping Tidak Muncul**: Pastikan `gnome-terminal` terinstall (`sudo apt install gnome-terminal`).
