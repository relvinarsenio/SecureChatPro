# Secure Chat Pro

Proyek ini adalah aplikasi Android "SecureChat" yang dibuat untuk Ujian Tengah Semester Ganjil TA. 2025/2026 mata kuliah Mobile dan Digital Forensic.

Aplikasi ini mendemonstrasikan enkripsi RSA 512-bit untuk menyimpan pesan secara aman di log lokal, sekaligus mensimulasikan skenario kebocoran data (eksfiltrasi) saat mode debug aktif.

## Tampilan Aplikasi
| Tampilan awal | Saat chat normal | Saat Debug Exfil Aktif |
|:----------:|:-----------:|:-----------:|
|<img src="https://github.com/user-attachments/assets/ece0d677-9504-4559-b308-0d85c18eef9a" alt="Screenshot" width="240">| <img width="240" alt="Screenshot_1762624805" src="https://github.com/user-attachments/assets/c444f9e1-df3a-45b8-88f6-4dd6d7d291f2" />|<img width="240" alt="Screenshot_1762624892" src="https://github.com/user-attachments/assets/c6f40232-bd91-43a2-9af1-f6c2e99fe16b" />|

## Prasyarat

  * Android Studio 2025.2.1 Otter
  * Android Emulator (Penting! Agar IP `10.0.2.2` berfungsi)
  * Python 3
  * Flask (library Python)

## 1\. Cara Menjalankan Server (Flask C2)

Server ini (`mock_c2.py`) bertugas untuk menerima data plaintext yang dibocorkan (eksfiltrasi) oleh aplikasi.

1.  Buka terminal atau command prompt.
2.  Pastikan lo udah install Flask. Kalo belom:
    ```bash
    pip install Flask
    ```
3.  Masuk ke direktori tempat lo nyimpen file `server.py`.
4.  Jalankan server:
    ```bash
    python server.py
    ```
5.  Server akan aktif dan *listening* di `http://127.0.0.1:5000`. Biarin terminal ini tetep kebuka.

## 2\. Cara Menjalankan Aplikasi (Android)

1.  Buka folder proyek `SecureChatPro-...` menggunakan Android Studio.
2.  Tunggu Gradle selesai *sync*.
3.  Pastikan server Flask (langkah 1) udah jalan.
4.  Jalankan aplikasi dengan klik tombol 'Run' (▶) di Android Studio.
5.  **WAJIB:** Pilih **Android Emulator** sebagai *target device*.
      * *Kenapa?* Aplikasi di-hardcode di `ChatViewModel.kt` untuk ngirim data ke `EXFIL_URL = "http://10.0.2.2:5000/upload"`. IP `10.0.2.2` adalah alamat khusus emulator Android untuk ngomong sama `127.0.0.1` (localhost) komputer lo.

## 3\. Cara Menjalankan Skenario Uji

### A. Uji Normal (Aman)

1.  Jalankan aplikasi di emulator.
2.  **Jangan** klik ikon kutu/bug (🐞) di pojok kanan atas.
3.  Ketik pesan apa aja (misal: "ini pesan aman") lalu klik tombol kirim (▶).
4.  **Hasil:**
      * Di aplikasi, bakal muncul balasan "Tersimpan: security\_log.txt...".
      * Di terminal server Flask, **TIDAK AKAN** terjadi apa-apa. Ini nunjukkin data *tidak* bocor.

### B. Uji Insiden (Kebocoran Data)

1.  Pastikan server Flask masih jalan.
2.  Di aplikasi (emulator), klik **ikon kutu/bug (🐞)** di *app bar* atas.
3.  Sebuah *banner* kuning "Exfiltration enabled" akan muncul.
4.  Ketik pesan baru (misal: "ini data bocor").
5.  Klik tombol kirim (yang sekarang warnanya jadi kuning/amber).
6.  **Hasil:**
      * Lihat terminal **mock_c2.py** lo.
      * Server akan langsung nge-print pesan *plaintext* yang lo kirim tadi, lengkap dengan timestamp dan infonya. Ini bukti eksfiltrasi berhasil.
