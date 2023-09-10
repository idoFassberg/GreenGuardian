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
const unsigned long interval = 5000; // 0.5 second(s) interval
int soilMoisture, drySoil = 5, wetSoil = 700; // backup values
bool signupOK = true;
String plantName, uid, url_currentData, url_statistics;

WiFiManagerParameter customFirebaseEmail("firebaseEmail", "Firebase User Email", "", 40);
WiFiManagerParameter customFirebasePassword("firebasePassword", "Firebase User Password", "", 40);
WiFiManagerParameter customPlantName("firebasePlantName", "User Plant Name", "", 40);

int convertRawDataToPrecentages(int rawValue)
{
  const float range = wetSoil - drySoil;

  if (rawValue < drySoil)
    return drySoil;
  else if (rawValue > wetSoil)
    return wetSoil;
  else
    return (((rawValue - drySoil) / (float)range) * 100);
}

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
  Serial.println("Firmware version: 1.0.0");
  WiFiManager wifiManager;

  accessWiFiUsingManager(wifiManager);

  // Retrieve and print the MAC address
  macAddress = WiFi.macAddress();
  macAddress.replace(":", "");
  Serial.println("MAC Address: " + macAddress);

  config.api_key = webAPIKey;
  config.database_url = databaseUrl; 

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

  // Get Configuration data from server 
  String drySoilUrl = "Configuration/drySoil";
  String wetSoilUrl = "Configuration/wetSoil";
  Serial.println(Firebase.RTDB.getInt(&fbdo, drySoilUrl.c_str(), &drySoil) ? "getInt (drySoilUrl) successfully" : fbdo.errorReason().c_str());
  Serial.println(Firebase.RTDB.getInt(&fbdo, wetSoilUrl.c_str(), &wetSoil) ? "getInt (wetSoilUrl) successfully" : fbdo.errorReason().c_str());
  Serial.println("Configuration: wetSoil = " + String(wetSoil) + " drySoil = " + String(drySoil));
}

void loop()
{
  if (Firebase.ready() && signupOK && (millis() -  previousMillis > interval || previousMillis == 0)) // create delay without using delay()
  {
    previousMillis = millis();
    // Read the analog input value from the sensor and convert to precentages and log
    int soilAnalog = analogRead(soilMoisturePin);
    soilMoisture = convertRawDataToPrecentages(soilAnalog);
    Serial.println("Read from controller: " + String(soilAnalog) + ". Sent to DB: " + String(soilMoisture));

    // replace the current value for real-time value
    Serial.println(Firebase.RTDB.setInt(&fbdo, url_currentData.c_str(), soilMoisture) ? "setInt successfully" : fbdo.errorReason().c_str());

    // add values to create statistics
    Serial.println(Firebase.RTDB.pushInt(&fbdo, url_statistics.c_str(), soilMoisture) ? "pushInt successfully" : fbdo.errorReason().c_str());
  }
}