# Aplikasi Mie Ayam Arsy - Katalog Digital & Pemesanan Pelanggan

Aplikasi mobile Android modern untuk usaha kuliner **Mie Ayam Arsy** yang mengintegrasikan katalog menu digital interaktif, pemesanan instan terhubung langsung ke WhatsApp pedagang, serta panel kelola menu dan stok berbasis Cloud dengan akun Google.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> **Keputusan Utama yang Telah Dikonfirmasi**:
> 1. **Fokus Aplikasi**: Katalog menu digital pelanggan & pemesanan langsung, mengadopsi fungsionalitas sistem web app yang sudah berjalan.
> 2. **Alur Pemesanan & Kasir**: Pesanan dikalkulasi secara otomatis di keranjang (lengkap dengan varian pedas dan suhu minuman), memilih opsi bayar (Tunai / Transfer), lalu diteruskan langsung ke WhatsApp pedagang dalam format pesan pesanan rapi dan siap kirim.
> 3. **Penyimpanan Cloud & Akun Google**: Sinkronisasi data katalog menu, status ketersediaan (Tersedia / Habis), serta profil warung ke Firebase Firestore yang diamankan dengan otentikasi Google Sign-In untuk pemilik warung.

- **Nomor WhatsApp Pedagang Default**: `+62 812-3456-789` (Dapat diubah secara dinamis melalui panel pengaturan admin/pemilik).
- **Akses Pengguna**: Pelanggan dapat langsung membuka menu, memilih varian, dan memesan tanpa hambatan pendaftaran; Pemilik warung masuk dengan akun Google untuk mengelola harga, stok, dan menu baru.

---

## 1. Overview & Core Concept

- **Tujuan Aplikasi**: Memberikan pengalaman memesan mie ayam yang cepat, menarik, dan tanpa antre bagi pelanggan Mie Ayam Arsy, sekaligus mempermudah pemilik warung memperbarui daftar menu, varian harga, dan status stok yang tersinkronisasi secara real-time.
- **Target Pengguna**:
  - **Pelanggan**: Pembeli yang ingin melihat daftar menu, foto, harga, memilih level kepedasan (Level 1, 2, 3) atau suhu minuman (Dingin/Panas), dan mengirimkan order via WhatsApp.
  - **Pemilik Warung (Admin)**: Pemilik usaha yang membutuhkan kontrol penuh terhadap penambahan menu baru, perubahan harga, penandaan stok habis, serta pengaturan informasi rekening transfer.
- **Nilai Utama**: Memodernisasi operasional warung tradisional menjadi digital, mengurangi kesalahan pencatatan pesanan manual, dan meningkatkan kecepatan layanan pelanggan.

---

## 2. User Experience & Visual Design

### Alur Pengguna (User Flows)
1. **Katalog & Pemilihan Menu**:
   - Tampilan beranda menampilkan identitas brand **Mie Ayam Arsy**, jam buka, dan pencarian/filter kategori: *Semua*, *Mie Ayam*, *Camilan/Topping*, *Minuman*.
   - Setiap kartu menu menampilkan foto/ilustrasi, nama, badge harga (Rp), serta tombol kuantitas `[-]` dan `[+]`.
   - **Smart Variant Trigger**:
     - Jika menu mengandung varian pedas (misal: *Mie Jebew*): Muncul dialog pemilihan Level Pedas (*Level 1*, *Level 2*, *Level 3*).
     - Jika kategori minuman atau teh/kopi: Muncul dialog pemilihan Suhu (*ICE / Dingin* atau *HOT / Panas*).
2. **Order Bottom Sheet / Bar**:
   - Bar pesanan melayang di bawah layar menampilkan total item dan nominal harga akumulasi (*Rp xx.xxx*).
   - Menekan bar membuka ringkasan keranjang: daftar menu yang dipesan beserta rincian varian, catatan tambahan, dan opsi metode pembayaran (*Tunai* atau *Transfer Bank*).
   - Jika memilih *Transfer Bank*, aplikasi menampilkan nomor rekening (BCA/BRI/Mandiri/QRIS) dan panduan melampirkan bukti transfer.
   - Tombol **Kirim Pesanan via WhatsApp**: Menghasilkan pesan terstruktur dan langsung meluncurkan aplikasi WhatsApp ke nomor warung.
3. **Mode Pemilik (Admin Portal)**:
   - Akses via ikon gembok/pengaturan di pojok atas dengan Google Sign-In resmi.
   - Fitur Admin: Tambah menu baru, ubah harga, ganti status (*Tersedia* / *Habis*), hapus menu, dan konfigurasi nomor WhatsApp warung.

### Identitas Visual & Desain UI (Material Design 3)
- **Tema & Nuansa**: Hangat, menggugah selera kuliner khas Indonesia dengan sentuhan modern profesional (Warm Amber, Deep Noodle Gold, Rich Charcoal, dan Fresh Leaf Green).
- **Palet Warna**:
  - `Primary`: Warm Amber / Golden Honey (`#E65100` / `#F57C00`)
  - `Secondary / Accent`: Fresh Herb Green (`#2E7D32` untuk WhatsApp & status Tersedia)
  - `Surface & Background`: Warm Off-White / Soft Cream (`#FFFDF9` untuk Light mode, `#1E1B18` untuk Dark mode)
  - `Status Badge`: Amber Orange untuk Level Pedas, Sky Blue untuk Es/Dingin, Crimson untuk Stok Habis.
- **Tipografi**: Heading tegas, modern, dan mudah dibaca saat melihat menu, dengan angka harga yang kontras dan jelas (`Rp 15.000`).
- **Sentuhan Interaktif**: Animasi penambahan item ke keranjang yang halus, haptic feedback ringan, dan safe-area insets edge-to-edge agar nyaman di semua ukuran smartphone Android.

---

## 3. Key Product Decisions & Trade-Offs

- **Integrasi Pemesanan Langsung WhatsApp**:
  - *Keputusan*: Pemesanan menggunakan URL scheme resmi WhatsApp (`wa.me/nomor?text=...`) dengan pesan terformat rapi.
  - *Alasan*: Pola ini sudah terbukti sangat ramah pelanggan di Indonesia, tidak membebani pembeli dengan biaya gateway pembayaran, dan pedagang bisa langsung berinteraksi akrab dengan pelanggan.
- **Sistem Dual-Mode (Pelanggan Bebas Login, Admin Terotentikasi)**:
  - *Keputusan*: Pembeli langsung dapat memakai aplikasi tanpa kewajiban login; Google Sign-In hanya diwajibkan saat masuk ke mode Admin/Pengelolaan Toko.
  - *Alasan*: Menghilangkan friksi bagi pembeli baru (zero bounce rate), sementara data konfigurasi dan katalog tetap aman dilindungi otentikasi Google di backend.
- **Penyimpanan Cloud (Firebase Firestore)**:
  - *Keputusan*: Data menu, harga, ketersediaan, dan konfigurasi toko disimpan di Cloud Firestore dengan fallback cache lokal saat koneksi lambat.
  - *Alasan*: Pembaruan harga atau menu habis oleh pemilik warung langsung tercermin seketika ke seluruh pelanggan tanpa perlu update aplikasi di Play Store.

---

## 4. Technical Architecture & Data Strategy

```
┌─────────────────────────────────────────────────────────────┐
│                    Mie Ayam Arsy Android                    │
│                                                             │
│   ┌─────────────────────┐       ┌───────────────────────┐   │
│   │   Customer UI       │       │    Admin Portal UI    │   │
│   │ - Menu Catalog      │       │ - Add / Edit Menu     │   │
│   │ - Variant Selector  │       │ - Stock Toggle        │   │
│   │ - Cart & Checkout   │       │ - Shop Config (WA/Bank│   │
│   └──────────┬──────────┘       └───────────┬───────────┘   │
│              │                              │               │
│              │ [Pesan via WhatsApp]         │ [Google Auth] │
│              ▼                              ▼               │
│   ┌─────────────────────┐       ┌───────────────────────┐   │
│   │ WhatsApp Intent URL │       │ Credential Manager    │   │
│   │ (Direct to Pedagang)│       │ (Firebase Auth Gate)  │   │
│   └─────────────────────┘       └───────────┬───────────┘   │
│                                             │               │
│              ┌──────────────────────────────┴───────────┐   │
│              ▼                                          ▼   │
│   ┌─────────────────────────────────────────────────────┐   │
│   │               Repository & StateFlow                │   │
│   │             (MenuViewModel / CartViewModel)         │   │
│   └──────────────────────────┬──────────────────────────┘   │
│                              │                              │
│                              ▼                              │
│   ┌─────────────────────────────────────────────────────┐   │
│   │       Cloud Firestore (Real-time Snapshot Sync)     │   │
│   │  Collections: 'menus', 'categories', 'shop_config'  │   │
│   └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### Struktur Data (Firestore Entities)
- **`menus`**:
  - `id`: String (Unique ID)
  - `nama`: String (e.g., "Mie Ayam Original", "Mie Jebew Spesial", "Es Teh Manis")
  - `kategori`: String ("Mie Ayam", "Topping", "Minuman")
  - `harga`: Long (e.g., 15000)
  - `status`: String ("Tersedia" / "Habis")
  - `deskripsi`: String
  - `hasSpiceLevel`: Boolean (Memicu pilihan Level 1-3)
  - `hasDrinkTemp`: Boolean (Memicu pilihan ICE/HOT)
- **`shop_config`**:
  - `storeName`: "Mie Ayam Arsy"
  - `whatsappNumber`: String (nomor tujuan pesan pemesanan)
  - `bankInfo`: String (nomor rekening & nama bank penerima transfer)
  - `address`: String

### Rencana Eksekusi Bertahap
1. **Setup Backend & Identitas**:
   - Inisialisasi konfigurasi nama aplikasi di `metadata.json` & `strings.xml`.
   - Setup Firebase Firestore & Auth via tools terintegrasi.
2. **Komponen Model & Data Layer**:
   - Firestore Repository dengan seed data awal menu khas Mie Ayam Arsy (Mie Ayam Bakso, Mie Jebew Level 1-3, Pangsit Goreng, Es Teh, Es Jeruk, dll).
3. **UI Katalog & Keranjang Interaktif**:
   - Filter Kategori, Grid/List Menu M3, Card interaktif.
   - Bottom Sheet Keranjang Belanja, kalkulasi subtotal, radio pembayaran Tunai/Transfer.
   - Generator format teks WhatsApp & launcher intent.
4. **Admin Mode & Google Sign-In**:
   - Dialog otentikasi Google, formulir tambah/edit menu, toggle stok instan.
5. **Verifikasi Build & Polishing**:
   - Edge-to-edge support, dark/light theme, uji build lewat `compile_applet`.
