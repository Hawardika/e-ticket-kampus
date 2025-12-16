
```markdown
# 🎫 E-Ticket Kampus Backend (Ktor)

Backend service untuk aplikasi E-Ticket Kampus, dibangun menggunakan **Kotlin** dan **Ktor Framework**.

## 📋 Prasyarat (Requirements)
Sebelum menjalankan, pastikan komputer kamu sudah terinstall:
1.  **JDK 17** (Java Development Kit) atau yang lebih baru.
2.  **Docker Desktop** (Disarankan untuk menjalankan Database).

---

## 🚀 Cara Menjalankan (Step-by-Step)

### 1. Siapkan Database
Proyek ini menggunakan **PostgreSQL**. Cara termudah adalah menggunakan Docker Compose yang sudah disediakan.

Buka terminal di folder proyek, lalu jalankan:
```bash
docker-compose up -d

```

*(Jika tidak menggunakan Docker, pastikan kamu punya PostgreSQL local yang berjalan dan sesuaikan konfigurasi DB di `src/main/resources/application.conf`)*.

###2. Jalankan ServerGunakan perintah **Gradle Wrapper** bawaan proyek agar tidak perlu install Gradle manual.

**Untuk Windows (PowerShell/CMD):**

```powershell
.\gradlew.bat run

```

**Untuk macOS / Linux:**

```bash
./gradlew run

```

Tunggu hingga proses build selesai dan muncul pesan:

> `Application started: http://0.0.0.0:8080`

###3. Cek AplikasiBuka browser atau Postman dan akses:
`http://localhost:8080`

---

##🛠️ Testing APIKamu bisa menguji API menggunakan:

1. **QA Dashboard:** Buka file `index.html` pada folder `qa-dashboard` (Frontend testing tool).
2. **Postman:** Import collection manual atau gunakan script automation.

##⚠️ Troubleshooting* **Error `./gradlew` not found (Windows):** Pastikan kamu mengetik `.\gradlew.bat` (pakai backslash `\`), bukan `./gradlew`.
* **Port Already in Use:** Pastikan tidak ada aplikasi lain yang berjalan di port `8080`.
* **Database Connection Refused:** Pastikan container Docker sudah status `Running` (cek dengan `docker ps`).

```

```
