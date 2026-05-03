# 🚀 NovaNotes: AI-Powered Smart Note Manager
**Full-Stack Android Application with Gemini AI Integration**

NovaNotes is a comprehensive note-taking application designed to showcase modern Android development practices, including social authentication, cloud synchronization, and artificial intelligence.

---

## ✨ Key Features
*   **🔐 Secure Auth:** GitHub Social Login via Firebase.
*   **🤖 AI Assistant:** Integrated Google Gemini AI for smart suggestions and chat.
*   **🌐 Full CRUD:** Create, Read, Update, and Delete notes synced with a MySQL backend.
*   **🔍 Smart Search:** Instant filtering of your notes.
*   **🎤 Voice Input:** Speech-to-text integration for quick note creation.
*   **📱 Modern UI:** Material Design 3 with custom adaptive icons and splash screen.

## 🛠 Tech Stack
- **Frontend:** Java, Android SDK (Material Design 3)
- **Backend:** PHP (REST API), XAMPP
- **Database:** MySQL
- **Integrations:** Firebase Auth, Google Gemini API, Glide (Image loading)

---

## ⚙️ Setup & Installation

### 1. Backend (PHP/MySQL)
- Move the `/notes_api2` folder to your XAMPP `htdocs` directory.
- Ensure your MySQL database is named `notes_app`.
- Check `db.php` for your local connection settings.

### 2. Android App
- Open the `/NoteApp2` folder in **Android Studio**.
- **Important:** You need your own `google-services.json` from Firebase to run the project. A template `google-services.json.example` is provided in the `app/` folder.
- Add your Gemini API Key in `ChatActivity.java`.

---

## 🔒 Security Note
For security reasons, all sensitive API keys and Firebase configuration files have been removed from this public repository. Please use your own credentials to test the application.

---
**Developed by:** Valeriia (Student)
*Submission for Android Development Course - April 2026*