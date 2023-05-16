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

// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

exports.helloWorld = onRequest((request, response) => {
  logger.info("Hello logs!", {structuredData: true});
  response.send("Hello from Firebase!");
});

exports.api = onRequest(async (req, res) => {
  switch (req.method) {
    case "GET":
      handleGetRequest(req, res);
      break;
    case "POST":
      handlePostRequest(req, res);
      break;
    case "DELETE":
      handleDeleteRequest(req, res);
      break;
    default:
      res.status(400).send("Invalid request method.");
      break;
  }
});

async function handleGetRequest(req, res) {
  // Handle GET request logic here.

  const response = await axios.get("https://jsonplaceholder.typicode.com/users/1");
  res.send(response.data);
}

function handlePostRequest(req, res) {
  // Handle POST request logic here.
  const body = req.body;
  res.send(body);
}

function handleDeleteRequest(req, res) {
  // Handle DELETE request logic here.
  res.send("Handling DELETE request.");
}

exports.userCreated = functions.auth.user().onCreate((user) => {
    const userEmail = user.email;
    const userName = user.displayName;
    console.log(`User created: Email: ${userEmail}, Name: ${userName}`);
        return Promise.resolve();
  });
  
  exports.userDeleted = functions.auth.user().onDelete((user) => {
    const userEmail = user.email;
  const userName = user.displayName;
  console.log(`User deleted: Email: ${userEmail}, Name: ${userName}`);
    return Promise.resolve();
  });

  exports.plantsAdded = functions.firestore.document('/plants/{documentId}').onCreate((snapshot,context) => {
    console.log(snapshot.data());
    return Promise.resolve();
  });

  
