# Audita - Sistem Audit Forensik & Analisis Transaksi QRIS Cerdas

Aplikasi Android modern dengan antarmuka palet warna pastel segar (*fresh pastel aesthetic*), didukung mesin analisis forensik digital untuk mendeteksi kecurangan (*fraud*) transaksi QRIS merchant perbankan, analisis distribusi Hukum Benford, koneksi VPN tunnel ke database server internal, dan persistensi lokal SQLite Room.

---

## 🚀 Panduan Membuka di Android Studio

Proyek ini telah dikonfigurasi penuh agar kompatibel dan dapat langsung dibuka (*out-of-the-box*) di **Android Studio** (Koala, Ladybug, Meerkat, atau versi yang lebih baru).

### 1. Buka Proyek
1. Buka aplikasi **Android Studio**.
2. Pilih menu **Open** (atau **File > Open...**).
3. Pilih direktori root proyek ini (`Audita`).
4. Tunggu hingga proses **Gradle Sync** selesai secara otomatis.

### 2. Rekomendasi Versi Java / JDK
- Disarankan menggunakan **JDK 17** atau **JDK 21**.
- Konfigurasi JDK di Android Studio:
  - Buka **Settings / Preferences** (`Ctrl + Alt + S` atau `Cmd + ,`).
  - Masuk ke **Build, Execution, Deployment > Build Tools > Gradle**.
  - Pada **Gradle JDK**, pilih **Java 17** atau **Java 21 (Temurin / Corretto / Android Studio Default)**.

### 3. Menjalankan Aplikasi
1. Sambungkan perangkat Android fisik via USB Debugging, atau aktifkan Android Emulator (Android 7.0 API 24 hingga Android 15/16 API 36).
2. Pilih konfigurasi run `app` di toolbar atas.
3. Klik tombol **Run 'app'** (`Shift + F10`).

### 4. Menjalankan Unit Test & Robolectric
Untuk menjalankan seluruh pengujian forensik:
- **Terminal Mac / Linux**:
  ```bash
  ./gradlew test
  ```
- **Terminal Windows**:
  ```cmd
  gradlew.bat test
  ```

---

## 🛡️ Arsitektur & Fitur Utama

- **Digital Forensic Engine**:
  - `Flag_Jam_Tutup`: Mendeteksi transaksi di luar jam operasional (23:00 - 05:00).
  - `Flag_Split_Tx`: Mendeteksi transaksi split oleh konsumen yang sama dalam rentang < 5 menit.
  - `Flag_Nominal_Bulat`: Mendeteksi gestun / cashout bulat besar $\ge$ Rp1.000.000 kelipatan 100rb.
  - `Flag_Self_Tx`: Mendeteksi kasir scan akun pribadi sendiri (kolusi insentif).
  - `Flag_Benford_Anomali`: Menguji deviasi digit pertama (7, 8, 9) pada nominal > 500rb terhadap kurva Hukum Benford.
- **VPN ke Database Server**:
  - Profil koneksi terenkripsi (*Jakarta Central DB*, *Singapore Cloud DR*, *Local Gateway*).
  - Protokol *WireGuard Kernel*, *IPsec IKEv2*, dan *OpenVPN SSL/TLS*.
  - Pemantauan latensi ping, status handshake, dan transfer data Tx/Rx *real-time*.
- **Offline-First Room Database**:
  - Penyimpanan data lokal SQLite Room terenkripsi yang dapat diakses penuh tanpa jaringan.
  - Sinkronisasi otomatis cadangan awan berlapis AES-256 GCM.
- **Ekspor & Integrasi Pihak Ketiga**:
  - Ekspor instan ke format file `hasil_audit_forensik_qris.csv` dan laporan ringkasan eksekutif.
  - Sinkronisasi jadwal audit tutup buku ke Google Calendar perangkat.
- **Tampilan Pastel & Auto Dark Mode**:
  - Palet warna pastel yang ramah mata (*Soft Mint*, *Lavender*, *Peach*, *Rose*).
  - Mode gelap otomatis aktif saat malam hari (19:00 - 06:00 WIB) atau mengikuti sistem.
  - Tipografi lokal berkualitas tinggi menggunakan font **Inter**.
