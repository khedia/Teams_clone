# Teams_clone
Project for Microsoft Engage Mentorship Program

You can sign-up for an account in this app with some basic details and create your email and password.
If you already have an account in the app you can sign-in with the email and password.
There is also a Google sign-in option provided to sign-in with your google account.
This teams app can be used to initiate an audio call or a video call with a single user or multiple users.
You can also chat with any user having an account in this app.
It also has a search bar where you can search for a particular user by their name or email.

To run the file in your IDE:
I have deleted my google-services.json file. You can add yours.
Change Authorization-key with your key from firebase project.

Implementation Guide 
1 - Project 
-> Open the Project in your android studio; 
2 - IMPORTANT: Change the Package Name. (https://stackoverflow.com/questions/16804093/android-studio-rename-package)
-> Firebase Panel 
-> Create Firebase Project (https://console.firebase.google.com/); 
-> Get the SHA keys from the IDE and paste in the firebase project
-> Import the file google-service.json into your project 
-> Connect to firebase console authentication and database from your IDE
-> In firebase Storage Rules, change value of "allow read, write:" from "if request.auth != null" to "if true;" 
-> Also change the key in the Constants file with your firebase project's Cloud Messaging Server Key
-> When you change database settings, you likely will need to uninstall and reinstall apps to avoid app crashes due to app caches.
