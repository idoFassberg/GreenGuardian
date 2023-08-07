/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const functions = require("firebase-functions");
const {onRequest} = require("firebase-functions/v2/https");
const logger = require("firebase-functions/logger");
const axios = require("axios")
const admin = require('firebase-admin');
const moment = require("moment");
admin.initializeApp();

const lastCallTimes = {};
const lastNotificationTimes = {}; // Object to store last notification times for each plant

exports.monitorMoistureLevels = functions.database.ref('/Users/{userId}/plants/{nickName}/currentHumidity')
    .onUpdate((change, context) => {
        const currentHumidity = change.after.val();
        console.log('new currentHumidity:', currentHumidity);
        const nickName = context.params.nickName;
        console.log('test nickName:', nickName);
        const userId = context.params.userId;
        console.log('test userId:', userId);

        const lastCallTimeRef = admin.database().ref(`/Users/${userId}/plants/${nickName}/lastNotificationTime`);

        return lastCallTimeRef.once('value')
            .then(snapshot => {
                const lastNotificationTime = snapshot.val() || 0;
                const currentTime = Date.now();
                const debounceTime = 2 * 60 * 1000; // 2 minutes in milliseconds

                if (currentTime - lastNotificationTime < debounceTime) {
                    console.log('Debounced:', nickName);
                    return null; // Skip processing for now
                }

                // Retrieve the optimalHumidity threshold for the specific plant from the database
                return admin.database().ref(`/Users/${userId}/plants/${nickName}/optimalHumidity`).once('value')
                    .then(snapshot => {
                        const optimalHumidity = snapshot.val();
                        console.log('new optimalHumidity:', optimalHumidity);
                        if (optimalHumidity !== null && currentHumidity < optimalHumidity) {
                            console.log('ok the current humidity is lower then the optimal');
                            // Retrieve the user's FCM token from the database
                            return admin.database().ref(`/Users/${userId}/fcmToken`).once('value');
                        }
                        console.log('ok the current humidity is higher then the optimal');
                        return null;
                    })
                    .then(snapshot => {
                        if (snapshot !== null) {
                            const fcmToken = snapshot.val();
                            console.log('FCM Token:', fcmToken); // Log the value to check if it's defined correctly
                            if (fcmToken) {
                                // Send a push notification to the user with the low moisture level alert
                                const payload = {
                                    notification: {
                                        title: 'Green Guardian Alert',
                                        body: 'Your plant needs water! The moisture level is low.',
                                        click_action: 'OPEN_ACTIVITY'
                                    }
                                };
                                // Update the last notification time and send the notification
                                return Promise.all([
                                    admin.messaging().sendToDevice(fcmToken, payload),
                                    lastCallTimeRef.set(currentTime)
                                ]).then(() => {
                                    console.log('Notification sent and lastNotificationTime updated:', nickName);
                                    return null;
                                });
                            }
                        }
                        return null;
                    });
            })
            .catch(error => {
                console.error('Error sending push notification:', error);
            });
    });

    exports.updateGeneratedKey = functions.database.ref('/Users/{userId}/plants/{nickName}/statsHumidity/{pushId}')
    .onCreate((snapshot, context) => {
        console.log("Function updateGeneratedKey triggered");

        const value = snapshot.val();
        console.log("Value:", value);

        // Create a new map with the formatted timestamp and the value
        const map = {};
        map[moment().format("YYYY-MM-DD HH:mm")] = value;
        console.log("Map:", map);

        const parentRef = snapshot.ref.parent;

        // Query the latest entry
        return parentRef.orderByKey().limitToLast(1).once('value')
            .then(latestSnapshot => {
                const latestKey = Object.keys(latestSnapshot.val())[0];

                // Update the latest entry with the new key and value
                return parentRef.child(latestKey).update(map);
            })
            .catch(error => {
                console.error('Error updating latest entry:', error);
                return null;
            });
    });



exports.getUserData = functions.https.onRequest(async (req, res) => {
  try {
    const userId = req.query.userId; // Get the 'userId' from the query parameters
    if (!userId) {
      return res.status(400).send("Missing 'userId' parameter.");
    }

    // Get a reference to the 'Users' node in the Realtime Database
    const usersRef = admin.database().ref('Users');

    // Retrieve the user data based on the 'userId'
    const userSnapshot = await usersRef.child(userId).once('value');
    const userData = userSnapshot.val();

    if (!userData) {
      return res.status(404).send("User not found.");
    }

    // Return the user data as a response
    return res.status(200).json(userData);
  } catch (error) {
    console.error('Error fetching user data:', error);
    return res.status(500).send("An error occurred while fetching user data.");
  }
});

  