# ShiftYTPlaylists

A lightweight Java Swing desktop app to transfer YouTube playlists to Spotify.

## Features

- Enter a YouTube playlist URL and shift it to Spotify.
- Responsive, low-memory GUI.
- Live progress/status log.
- Open-source and easy to run.

---

## Setup Instructions

### 1. Install Java

##### macOS (with Homebrew)

```bash
brew install openjdk@17
brew link --force --overwrite openjdk@17
```

##### Linux (Ubuntu/Debian)

```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

##### Windows

Download and install from `bash https://javadl.oracle.com/webapps/download/AutoDL?xd_co_f=OTQxNDYxYTQtNzZmMy00NzliLTk5MDEtZWZmOGU0NzI5OTc5&BundleId=252044_8a1589aa0fe24566b4337beee47c2d29 `

### Install Maven

##### macOS (with Homebrew)

```bash
brew install maven
```

##### Linux (Ubuntu/Debian)

```bash
sudo apt update
sudo apt install maven
```

##### Windows

Download and install from `bash https://maven.apache.org/download.cgi`
Add Maven bin folder to your system's Path.

---

## Requirements

Before running the app, you’ll need:

### 1. YouTube API Key (YouTube Data API v3)

- Go to [Google Cloud Console](https://console.cloud.google.com/)
- Create a project
- Enable **YouTube Data API v3**
- Go to **APIs & Services > Credentials**
- Click **"Create API key"**
- Copy the API key for use

### 2. Spotify Developer Credentials

- Go to [Spotify Developer Dashboard](https://developer.spotify.com/dashboard)
- Create an app
- Set your **Redirect URI** to:  
  `http://127.0.0.1:8888/callback`  
  ⚠️ _Do NOT use `localhost`. Spotify no longer supports it._
- Copy the **Client ID** and **Client Secret**

---

## Set Environment Variables

Before launching the app, set your API keys and secrets as **environment variables**.

### macOS/Linux (bash, zsh, etc.)

```bash
export YOUTUBE_API_KEY=your_youtube_api_key
export SPOTIFY_CLIENT_ID=your_spotify_client_id
export SPOTIFY_CLIENT_SECRET=your_spotify_client_secret
```

### Windows (Command Prompt)

```bash
set YOUTUBE_API_KEY=your_youtube_api_key
set SPOTIFY_CLIENT_ID=your_spotify_client_id
set SPOTIFY_CLIENT_SECRET=your_spotify_client_secret
```

### Windows (PowerShell)

```bash
$env:YOUTUBE_API_KEY="your_youtube_api_key"
$env:SPOTIFY_CLIENT_ID="your_spotify_client_id"
$env:SPOTIFY_CLIENT_SECRET="your_spotify_client_secret"
```

> You must run the java -jar command in the same terminal session where you set the environment variables.

---

## Build & Run the Application

### 1. Clone the project and go to the root directory:

```bash
cd /path/to/ShiftYTPlaylists

```

### 2. Build the JAR file:

```bash
mvn clean package
```

### Run the application:

```bash
java -jar target/shiftytplaylists-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Troubleshooting

Make sure your YouTube playlist is public or unlisted.
Auto-generated YouTube "Mixes" (e.g. list=RD...) are not supported by the YouTube API.
Use only links like:
https://www.youtube.com/playlist?list=PLxxxxxxxxxxxx

---
