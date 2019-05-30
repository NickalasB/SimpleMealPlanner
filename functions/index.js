//TODO change this use data instead of notification to have app handle background notifications
// https://medium.com/@Miqubel/mastering-firebase-notifications-36a3ffe57c41

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendRecipeSharedNotification = functions.database.ref('/simple_meal_planner/users/{userId}/recipes/{recipeId}')
.onWrite((change, context) => {

  if (change.before.exists() || !change.after.exists()) {
     return null;
  }

  const sharedRecipe = change.after;
  console.log('Recipe = ', sharedRecipe.val());

  const recipeJson = JSON.stringify(sharedRecipe.val());
  console.log('Recipe String= ', recipeJson);

  const recipeName = sharedRecipe.child('label').val();
  console.log('Recipe Name = ', recipeName);

  const recipeImageUrl = sharedRecipe.child('image').val();
  console.log('Recipe Image = ', recipeImageUrl);

  const userWhoShared = sharedRecipe.child('sharedFromUser').val();
  console.log('User who shared = ', userWhoShared);

  const targetUserMessageTokenVal = admin.database().ref('/simple_meal_planner/users/' + context.params.userId + '/messagingToken')
  .once('value', (snapshot) => {

  const token = snapshot.val();
  console.log('Target user token = ', token);

    var message = {
    	data: {
    		title: userWhoShared,
    		body:  recipeName,
    		image: recipeImageUrl,
    		recipeJson: recipeJson
    	}
    };

   admin.messaging().sendToDevice(token, message);
  });

  return Promise.all([targetUserMessageTokenVal]);
});