<h1><b># Geeks-Zone</b></h1>
A simple group chat android app built using <b>Firebase</b> (Firebase Authentication,FCM, Cloud Firestore). It uses <b>Architecture Components</b> which include View Model, Navigation,Data Binding and Live Data. It uses <b>Retrofit</b> for network requests.

<h2>What you need to do to make this app working?</h2>

Well there are few things missing from the project which you need to add.

First you need to create a project in firebase console and you dont need to add any dependency or any thing except one file.which you can get from project settings in firebase console named as <b>'google-services.json'</b> paste it into the project under the app folder but make sure you are in the project mode not app mode.

Then you need to get your <b>SERVER KEY</b> from project settings in <b>Firebase Console</b> and then under <b>Cloud Messaging</b> section.Paste that key into <b>res>values>strings</b> and there you will find a string named <b>fcm_app_key</b>.
Thats it!

Moreover you can add further checks on username,password or email validation in sign up fragment using regex or whatever you wanna use.
