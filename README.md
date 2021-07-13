<h1 align="center">
Teams <svg xmlns="http://www.w3.org/2000/svg" width="30" height="28" viewBox="0 0 30 21"><path d="M17 3v-2c0-.552.447-1 1-1s1 .448 1 1v2c0 .552-.447 1-1 1s-1-.448-1-1zm-12 1c.553 0 1-.448 1-1v-2c0-.552-.447-1-1-1-.553 0-1 .448-1 1v2c0 .552.447 1 1 1zm13 13v-3h-1v4h3v-1h-2zm-5 .5c0 2.481 2.019 4.5 4.5 4.5s4.5-2.019 4.5-4.5-2.019-4.5-4.5-4.5-4.5 2.019-4.5 4.5zm11 0c0 3.59-2.91 6.5-6.5 6.5s-6.5-2.91-6.5-6.5 2.91-6.5 6.5-6.5 6.5 2.91 6.5 6.5zm-14.237 3.5h-7.763v-13h19v1.763c.727.33 1.399.757 2 1.268v-9.031h-3v1c0 1.316-1.278 2.339-2.658 1.894-.831-.268-1.342-1.111-1.342-1.984v-.91h-9v1c0 1.316-1.278 2.339-2.658 1.894-.831-.268-1.342-1.111-1.342-1.984v-.91h-3v21h11.031c-.511-.601-.938-1.273-1.268-2z"/></svg>
</h1>

<h3 align="center">An audio, video and text chat application</h3>

<p align="center">
  <a href="#introduction">Introduction</a> •
  <a href="#key-features">Key Features</a> •
  <a href="#how-to-use">How To Use</a> •
  <a href="#tech-stack">Tech Stack</a> •
  <a href="#documentation">Documentation </a> •
  <a href="#video">Video </a> •
  <a href="#snapshots">Snapshots</a>
</p>

## Introduction

Teams application is compatible on all android apps with minimum SDK version 23. This app is basically used to connect with people with video, audio and Text chat.
I have used Firebase to handle the authentication, database and also the cloud messaging which is used to connect with the other users. It uses jitsi-meet sdk to handle the voice calling and video calling feature.
<br/>

## Key Features

- Sign In with Google account available
- Search bar where you can search for a particular user by their name or email
- Connect on audio and video chat
- Connect with single or multiple users
- Chat box available during the meet
- Multiple features available during the meeting
- Have a one to one chat conversation
- Get to know last message is seen or not

## How To Use

To clone and run this application: I have deleted my google-services.json file. You can add yours.
Change Authorisation-key with your key from firebase project.

Implementation Guide:
- Open the Project in your android studio
- IMPORTANT: Change the Package Name. (https://stackoverflow.com/questions/16804093/android-studio-rename-package)<br/>
- Firebase Panel 
- Create Firebase Project (https://console.firebase.google.com/); 
- Get the SHA keys from the android studio and paste in the firebase project
- Import the file google-service.json into your project 
- Connect to firebase console authentication and database from your IDE
- In firebase Storage Rules, change value of "allow read, write:" from "if request.auth != null" to "if true;" 
- Also change the key in the Constants file with your firebase project's Cloud Messaging Server Key
- When you change database settings, you likely will need to uninstall and reinstall apps to avoid app crashes due to app caches.

## Tech Stack

- Firebase
  - FireStore : to store the data of the users
  - Realtime database : to store the data of the chat messages
  - Cloud messaging : to send messages of the meeting invitation or chat
- Jitsi-meet
  - to handle the audio and video meeting

## [Documentation](https://drive.google.com/file/d/1QKHwmypIhxxJU-bZYOUarN3yuDQq61Is/view?usp=sharing) 

## [Video](https://drive.google.com/file/d/1bhgbx-4QEPZyDLGpPCPUYUrsP5sIx2V1/view?usp=sharing)

<div align="center">

## Snapshots

![ezgif com-resize](https://user-images.githubusercontent.com/68772130/125284688-66a92680-e337-11eb-93bb-4847ac8b0546.gif)

</div>

<h2 align="center">
  Microsoft Engage Mentorship Program
</h2>
