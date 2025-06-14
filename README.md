# 🛡️ CustomAC - Minecraft Server Anti-Cheat Plugin

**Author:** Zakariya Ali\
**University Project:** BSc Computer Science Final Year Thesis\
**Thesis Title:** _Video Games Anti-Cheats and Cheaters in Minecraft_\
**Date:** May 2023\
**Supervisor:** Tara Collingwoode-Williams

## 🎯 About This Project

CustomAC is a Java-based Anti-Cheat plugin developed for **Spigot-based**
Minecraft servers. It was designed and tested as part of my final year
university dissertation to detect and prevent unfair player behaviour (cheats
such as fly hacks, speed hacks, X-ray, etc.) while maintaining an enjoyable and
competitive multiplayer experience.

The project combines technical research, white-box/black-box testing, user
surveys, and real-world implementation to evaluate its effectiveness compared to
existing solutions like NoCheatPlus.

## 🔧 Features

- 🚨 Real-time cheat detection
- 📦 Easy installation on any Spigot Minecraft server
- 🔍 Logging and optional autoban functionality
- 🛠️ Configurable via `config.yml`
- 🧠 Modular check system (movement, combat, velocity, etc.)
- 🧪 Tested through custom scenarios and user trials

---

## 🚀 Getting Started

### ✅ Prerequisites

- Java JDK 17+
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) or any preferred Java IDE
- [Spigot Server](https://www.spigotmc.org/wiki/buildtools/) set up locally
- Minecraft client (same version as your server)

### 📦 Compilation Instructions

1. **Clone this repository**
   ```bash
   git clone https://github.com/covenlol/CustomAC.git
   cd CustomAC
   ```

2. **Open in IntelliJ or your preferred IDE**

3. **Build the plugin JAR**\
   Make sure to use the correct SDK version (Java 17 or above). Then, build the
   `.jar` file using:
   - IntelliJ: `Build > Build Artifacts > Build`
   - Or using terminal: `javac -d out src/**/*.java` (if structured flatly)

4. **Place the compiled JAR in your server's `plugins/` directory**
   ```bash
   cp out/CustomAC.jar /path/to/your/minecraft-server/plugins/
   ```

5. **Start your Spigot server**
   ```bash
   java -jar spigot-<version>.jar
   ```

---

## 📝 Usage & Configuration

- Default checks are enabled out of the box.
- All detections and bans are logged in a JSON log file.
- Configuration options can be adjusted in `plugins/CustomAC/config.yml`
- Use in-game commands (via `CommandManager.java`) to toggle debug mode or
  manually control cheat logging.

Example:

```bash
/customac debug on
/customac unban <playername>
```

---

## 📊 Testing & Results

As detailed in the thesis:

- ✅ **White-box tests:** Simulated various cheats (speed, fly, walk on water,
  no-fall) and verified detection.
- ✅ **Black-box tests:** 10 participants tested both CustomAC and NoCheatPlus;
  CustomAC detected more cheats.
- 🧪 **User feedback:** CustomAC is more effective, though slightly more
  intrusive due to false positives.

---

## 📁 Structure

- `CheckManager.java` – Core detection logic
- `CommandManager.java` – Admin commands and toggles
- `ConfigManager.java` – Persistent settings
- `BrandProcessor.java`, `CollisionProcessor.java`, `VelocityProcessor.java` –
  Specific cheat detection modules

---

## ⚖️ Legal & GDPR Compliance

CustomAC monitors player behaviour for the sole purpose of cheat detection. It
does **not** collect personal data and complies with GDPR principles:

- Monitors only in-game actions
- Purpose-limited (cheat detection only)
- Clear disclosure to server admins

---

## 📚 Thesis Reference

> “Video Games Anti-Cheats and Cheaters in Minecraft”\
> Full text available on request or as a supplementary document in this repo.

---

## 📄 License

This project is available for educational and non-commercial server use. Please
do not redistribute compiled versions without permission.
