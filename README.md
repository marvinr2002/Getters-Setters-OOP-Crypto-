# Getters-Setters-OOP-Crypto-
Example of OOP - Getters &amp; Setters - Crypto 
---
# People Vault (Java-only, console, AES-GCM)

A tiny console app (Java SE only) to store **people records** securely in a **single encrypted file**.
All data are **serialized**, **encrypted with AES-GCM** using a password you provide, and saved to `people.enc`.
You can **add**, **list**, and **delete** entries (labeled `user1`, `user2`, …) from the command line.

> ✅ No frameworks, no external libraries. Just Java.

---

## Features

* Store `Person` records: `label`, `name`, `age`, `gender`, `email`, `phone`, `address`.
* Password-based encryption: **PBKDF2-HMAC-SHA256** → 256-bit AES key → **AES-GCM** (with random salt & IV).
* Commands: `add`, `list`, `delete userN`.
* Works in terminals on Windows/macOS/Linux.
  (If running inside some IDE consoles, the password may echo; see notes below.)

---

## Project Structure

```
src/
├─ Person.java              # POJO (Serializable) + getters/setters + toString
├─ CryptoUtils.java         # PBKDF2 key derivation + AES-GCM encrypt/decrypt
├─ PersonRepository.java    # Load/Save List<Person> with encryption
└─ Main.java                # CLI: add | list | delete userN
```

The encrypted database is a single file next to your program: `people.enc`.

---

## Requirements

* **Java 17+** (recommended; works on 11+ if `InputStream.readAllBytes()` is available).
* A terminal/console to run commands.

Check your version:

```bash
java -version
```

---

## Build & Run

From the directory containing the `*.java` files:

```bash
# 1) Compile
javac Person.java CryptoUtils.java PersonRepository.java Main.java

# 2) Run help
java Main

# 3) Add records (prompts for password and fields)
java Main add

# 4) List records (prompts for password)
java Main list

# 5) Delete a specific record (e.g., user2)
java Main delete user2
```

> The **same password** used when you added data is required to list or modify that data.
> If the password is wrong, decryption fails with an authentication error.

---

## Usage Examples

### Add

```text
$ java Main add
Contraseña para cifrar/abrir: ********
Nombre: -------------------
Edad: -----------
Género: Masculino
Email: ------------@example.com
Teléfono: 123-456-7890
Dirección: D-------------------
¿Agregar otra persona? (s/n): n
✅ Guardado en people.enc
```

### List

```text
$ java Main list
Contraseña para abrir: ********

--- Personas ---
user1 | Name: ------ ----- | Age: ---- | Gender: Masculino | Email: ------@example.com | Phone: 123-456-7890 | Address: ------------------
```

### Delete

```text
$ java Main delete user1
Contraseña para abrir: ********
🗑️ Eliminado: user1
```

---

## Command Reference

* `java Main`
  Shows usage help.

* `java Main add`
  Adds one or more people interactively. Records are labeled `userN` automatically.

* `java Main list`
  Decrypts and prints all stored records.

* `java Main delete userN`
  Deletes the record with label `userN` (e.g., `user2`).

---

## Configuration

* **Encrypted file path**: by default `Main.java` uses:

  ```java
  private static final String FILE_NAME = "people.enc";
  ```

  You can change it to an absolute path (e.g., `C:\\Data\\people.enc` or `/var/app/people.enc`).

* **File permissions (Unix-like)**: the repository tries to set strict file permissions (owner-read/write).
  On Windows, manage ACLs via the folder’s security settings if desired.

---

## Security Notes

* **AES-GCM** provides confidentiality + integrity. Any tampering is detected (you’ll see an auth/tag error).
* A **random salt** (PBKDF2) and **random IV** (GCM) are generated for every encryption.
* **Do not forget your password.** There is no recovery: the data cannot be decrypted without it.
* When running inside some IDE consoles, `System.console()` may be `null`. The app will fall back to reading the password as a visible line. For proper hidden password input, run in a real terminal.

---

## Troubleshooting

* **“Contraseña incorrecta o archivo alterado.”**
  You entered the wrong password or `people.enc` was corrupted/modified. Use the original password and keep backups.

* **Password echoes / shows while typing**
  Run from a system terminal (cmd/PowerShell/Terminal) instead of the IDE, or accept the fallback behavior.

* **Java version errors**
  Ensure you’re using Java 17+ (or update the code to avoid APIs not present in your JDK).

---

## Design Overview (how it works)

1. **Add/List/Delete** commands are handled in `Main.java`.
2. `PersonRepository` loads/saves a `List<Person>`:

   * Serialize to bytes via `ObjectOutputStream`.
   * Encrypt bytes via `CryptoUtils.encrypt(...)`.
   * Write to `people.enc`.
     Conversely, for reading: read → decrypt → deserialize.
3. `CryptoUtils`:

   * Derives a 256-bit AES key from the password using **PBKDF2WithHmacSHA256** + random salt.
   * Encrypts/decrypts with **AES/GCM/NoPadding** + random IV and 128-bit auth tag.

---

## Roadmap (optional ideas)

* `changepw` command to re-encrypt with a new password.
* Export/import to CSV (unencrypted) when needed.
* Search & filters (e.g., by name or email).
* Unit tests for `CryptoUtils` and `PersonRepository`.
* Packaging as a runnable JAR:

  ```bash
  jar --create --file people-vault.jar *.class
  java -cp people-vault.jar Main add
  ```

---
