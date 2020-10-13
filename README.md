# Geeks-Zone
A simple group chat android app built using Firebase (Firebase Authentication,FCM, Cloud Firestore). It uses architecture Components which include View Model, Navigation,Data Binding and Live Data. It uses Retrofit for network requests.

What you need to do to make this app working?

Well there are few things missing from the project which you need to add.

First you need to create a project in firebase console and you dont need to add any dependency or any thing except one file.which you can get from project settings in firebase console named as 'google_services.json' or something like that and paste it into the project

Then you need to get the your API KEY from project settings in firebase console and then cloud messaging section and paste that key into res>values>strings and there you will find a string named fcm_api_key.
Thats it!

Moreover you can add further checks on username,password or email validation in sign up fragment using regex or whatever you wanna use.
