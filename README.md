<p align="center">
  <img src="https://i.imgur.com/NLJXVc7.png" width="890"/>
</p>
<p align="center">
  <a href="https://www.spigotmc.org/resources/smartchat-ai-chat-moderation.128072/"><img src="https://img.shields.io/badge/View%20on%20SpigotMC-ED8106?style=flat-square&logo=spigotmc&logoColor=white" alt="SpigotMC"/></a>
</p>
<p align="center">
  <img src="https://img.shields.io/spiget/version/128072?label=SpigotMC&style=flat-square&logo=spigotmc&logoColor=white&color=ED8106" alt="Version"/>
  <img src="https://img.shields.io/spiget/downloads/128072?style=flat-square&logo=spigotmc&logoColor=white&color=ED8106" alt="Downloads"/>
</p>
<br>
<p align="center">
  SmartChat is a Minecraft Spigot/Paper plugin that uses Google’s Gemini to deliver context-aware, real-time chat moderation. It supports per-world policies, time-based rules, and custom thresholds, logging actions to a local SQLite database for analytics and an appeal workflow. With progressive punishments, lightweight performance, and simple commands/permissions, SmartChat gives you fine-grained control without extra bloat. (Requires a Gemini API key.)
</p>
<br>
<br>

<img src="https://i.imgur.com/tTTbtT5.png" width="590"/>
<p align="center">
  
<details>
  <summary><b>SmartChat — Features (click to expand)</b></summary>

  
<pre>
• AI-Powered Analysis — Uses Google Gemini for context-aware moderation
• SQLite Database — Local storage for violations, appeals, and analytics
• Extensive Customization — Per-world settings, time-based rules, custom thresholds
• Progressive Punishment — Escalating actions based on violation history
• Appeal System — Players can appeal moderation actions
• Real-time Analytics — Track moderation statistics and patterns
</pre>

</details>
</p>

<br>
<br>

<img src="https://i.imgur.com/rc4YJIV.png" width="590"/>

1. Download `SmartChat.jar` from the releases
2. Place the JAR file in your server's `plugins` folder
3. Restart your server
4. The plugin will create a `SmartChat` folder with configuration files

<br>
<br>
<img src="https://i.imgur.com/SVD0fxU.png" width="590"/>

### Getting Your API Key

1. Visit [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Create a free API key
3. Add the key to `config.yml`:

```yaml
gemini-api-key: "YOUR_API_KEY_HERE"
```

### Basic Configuration

Edit `config.yml` to customize:

- **Thresholds**: Adjust sensitivity for different violation types
- **Actions**: Configure what happens at each severity level
- **World Settings**: Different rules for different worlds
- **Messages**: Customize all plugin messages in `messages.yml`

### Example Configurations

**Family Server** (Strict):
```yaml
thresholds:
  toxicity: 0.50
  categories:
    profanity: 0.40
    adult-content: 0.30
```

**PvP Server** (Lenient):
```yaml
thresholds:
  toxicity: 0.85
  categories:
    harassment: 0.80
    threats: 0.90
```
<br>
<br>

<img src="https://i.imgur.com/uddelf0.png" width="590"/>

- `/smartchat` (alias: `/sc`) - Main command
- `/sc reload` - Reload configuration
- `/sc stats` - View moderation statistics
- `/sccheck <message>` - Manually check a message
- `/appeal <reason>` - Appeal a moderation action

<br>
<br>

<img src="https://i.imgur.com/ruw3INp.png" width="590"/>

- `smartchat.admin.*` - All admin permissions
- `smartchat.bypass` - Bypass all moderation
- `smartchat.bypass.<category>` - Bypass specific category
- `smartchat.appeal` - Submit appeals
- `smartchat.notify` - Receive staff notifications

<br>
<br>

<img src="https://i.imgur.com/RglwDmJ.png" width="590"/>

- Report issues on GitHub
- Check the console for detailed error messages
- Enable debug mode in config.yml for more information
