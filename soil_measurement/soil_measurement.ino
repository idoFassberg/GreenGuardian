#include <Arduino.h>
#include <Firebase_ESP_Client.h>
#include <WiFiManager.h> // Connect to Wi-Fi using html config file
// Provide the token generation process info.
#include "addons/TokenHelper.h"
// Provide the RTDB payload printing info and other helper functions.
#include "addons/RTDBHelper.h"

const char* webAPIKey = "AIzaSyBfcRrSCqfqCui2Fkt1DlTHaTZTBx0sAIQ";
const char* databaseUrl = "https://greenguardian-app-default-rtdb.europe-west1.firebasedatabase.app/";

FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;
#define soilMoisturePin A0
String macAddress;

unsigned long previousMillis = 0;
//const unsigned long interval = 30000; // 3 second(s) interval
int soilMoisture;
bool signupOK = true;
String plantName, uid, url_currentData, url_statistics;

WiFiManagerParameter customFirebaseEmail("firebaseEmail", "Firebase User Email", "", 40);
WiFiManagerParameter customFirebasePassword("firebasePassword", "Firebase User Password", "", 40);
WiFiManagerParameter customPlantName("firebasePlantName", "User Plant Name", "", 40);


void accessWiFiUsingManager(WiFiManager& wifiManager)
{
  // clear the Wi-Fi credentials from the flash memory to disable auto-connect
  wifiManager.resetSettings();

  // Add custom parameters to the configuration page
  wifiManager.addParameter(&customFirebaseEmail);
  wifiManager.addParameter(&customFirebasePassword);
  wifiManager.addParameter(&customPlantName);

  // set custom ip for portal
  wifiManager.setAPStaticIPConfig(IPAddress(10,0,1,1), IPAddress(10,0,1,1), IPAddress(255,255,255,0)); // new func name instead of setAPConfig
  // Disable auto-connect to previously logged Wi-Fi network
  WiFi.persistent(false);
  // 4 minutes until timeout
  wifiManager.setTimeout(240);
  // Attempt to connect to Wi-Fi
  wifiManager.autoConnect("GreenGuardian"); // blocking until connected
  // Inform user for a successful connection
  Serial.println("Connected.");
}

void setup()
{
  Serial.begin(9600);
  WiFiManager wifiManager;

  accessWiFiUsingManager(wifiManager);

  // Retrieve and print the MAC address
  macAddress = WiFi.macAddress();
  macAddress.replace(":", "");
  Serial.println("MAC Address: " + macAddress);

  config.api_key = webAPIKey;
  config.database_url = databaseUrl; 
  // signup using anomymus user
  /*if (Firebase.signUp(&config, &auth, "", ""))
  {
    Serial.println("Singup ok");
    signupOK = true;
  }
  else
  {
    Serial.println(config.signer.signupError.message.c_str());
  }*/
  plantName = customPlantName.getValue();
  auth.user.email = customFirebaseEmail.getValue();
  auth.user.password = customFirebasePassword.getValue();
  config.token_status_callback = tokenStatusCallback;
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);

  // Getting the user UID might take a few seconds
  Serial.println("Getting User UID");
  while ((auth.token.uid) == "")
  {
    Serial.print('.');
    delay(1000);
  }
  uid = auth.token.uid.c_str();

  url_currentData = "/Users/" + uid + "/plants/" + String(customPlantName.getValue()) + "/currentHumidity";
  url_statistics = "/Users/" + uid + "/plants/" + customPlantName.getValue() + "/statsHumidity";

  Serial.println("Begin firebase: User name: " + String(customFirebaseEmail.getValue()) + " Password: " + String(customFirebasePassword.getValue()) + " Uid: " + uid);
}

void loop()
{
  if (Firebase.ready() && signupOK && (millis() -  previousMillis > 5000 || previousMillis == 0)) // create delay without using delay()
  {
    previousMillis = millis();
    soilMoisture = analogRead(soilMoisturePin); // Read the analog input value from the sensor

    //FirebaseJson json;
    //json.set("humidity", soilMoisture);

    // replace the current value for real-time value
    Serial.println(url_currentData.c_str());
    Serial.println(Firebase.RTDB.setInt(&fbdo, url_currentData.c_str(), soilMoisture) ? "setInt successfully" : fbdo.errorReason().c_str());

    // add values to create statistics
    Serial.println(Firebase.RTDB.pushInt(&fbdo, url_statistics.c_str(), soilMoisture) ? "pushInt successfully" : fbdo.errorReason().c_str());
    Serial.println(url_statistics.c_str());
  }
}