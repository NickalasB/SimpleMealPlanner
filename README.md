# Simple Meal Planner

Simple Meal Planner is an app created to solve a simple but persistant problem in most any family home... **_"What are we gonna make for dinner this week?"_**

The app allows users to search for recipes using ingredient or title keywords with a clean and simple UI.

Once a recipe is selected, users can either save the recipe as a **personal favorite** or save the recipe in a **meal plan** assigned to a specific day of the week. 
Once a user has saved a recipe to a meal plan it can easily be shared with any other registered users of the app. The recipient of a shared recipe will be notified on their device via a rich app notification. 

The app is a work-in-progress with more features planned. It was built primarily as a solution to figuring out what to feed our teenage boys. I wanted a way to discover recipes and easily share them with my wife. 

Enjoy!

# Getting Started

Simple Meal Planner finds recipes using Edamam's Recipe Search API.

The app also uses a Firebase Realtime Database for storing recipes and Firebase Authentication to allow signing into the app via Gmail (needed to save and share recipes).

There are a few things you need to setup if you want to build and run the app locally.

* First you need to sign up as an Edamam developer and obtain an *App Id* and *App Key* from [Edamam's website](https://developer.edamam.com/edamam-recipe-api)
    * Once you have obtained an *App Id* and *App Key* from Edamam you'll need to add them to the projecet.
    * Create a new file at the top level of the project titled apikey.properties (Right-click on SimpleMealPlanner -> New -> File -> apikey.properties)
    * Next paste your appId and appKey values into that file with the following format:
```
EDAMAM_APP_ID="[YOUR APP_ID]"
EDAMAM_APP_KEY="[YOUR APP_KEY]"
```
* Next you need to configure the app to use Firebase. Follow the directions found [here](https://firebase.google.com/docs/android/setup#assistant)
    * For the package name use com.zonkey.simplemealplanner
    * When you get to the _"Add Firebase to your Android App"_ step, you will need to generate a _Debug signing certificate SHA-1_. Follow the steps [here](https://developers.google.com/android/guides/client-auth) to obtain your signing certificate.
    * Once configured and added you need to set up your Realtime Database by selecting the following on the left-side of the portal: _Develop -> Database -> Create database (under Realtime Database) -> Start in test mode_
    * You also need to enable Google Authentication by selecting the following on the left-side of the portal: _Authentication -> Sign-in method -> Google -> Enable_

From there you should be able to sync, build, install, and enjoy all the features of the app. Enjoy
