#include <Arduino.h>
#include <FirebaseESP8266.h>
#include <WiFiManager.h> // Connect to Wi-Fi using html config file

// Provide the token generation process info.
#include <addons/TokenHelper.h>

// Provide the RTDB payload printing info and other helper functions.
#include <addons/RTDBHelper.h>

const char* webAPIKey = "AIzaSyBfcRrSCqfqCui2Fkt1DlTHaTZTBx0sAIQ";
const char* databaseUrl = "https://greenguardian-app-default-rtdb.europe-west1.firebasedatabase.app/";

FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;
#define soilMoisturePin A0
String macAddress;

WiFiManagerParameter customFirebaseEmail("firebaseEmail", "Firebase User Email", "", 40);
WiFiManagerParameter customFirebasePassword("firebasePassword", "Firebase User Password", "", 40);


void accessWiFiUsingManager(WiFiManager& wifiManager)
{
  // Add custom parameters to the configuration page
  wifiManager.addParameter(&customFirebaseEmail);
  wifiManager.addParameter(&customFirebasePassword);

  // set custom ip for portal
  wifiManager.setAPStaticIPConfig(IPAddress(10,0,1,1), IPAddress(10,0,1,1), IPAddress(255,255,255,0)); // new func name instead of setAPConfig
  wifiManager.setTimeout(120);
  wifiManager.autoConnect("GreenGuardian"); // blocking until connected
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
  auth.user.email = customFirebaseEmail.getValue();
  auth.user.password = customFirebasePassword.getValue();
  Firebase.begin(&config, &auth);
}

unsigned long previousMillis = 0;
const unsigned long interval = 10000; // 10 second interval

void loop()
{
  int soilMoisture = analogRead(soilMoisturePin); // Read the analog input value from the sensor
  
  // Create a time delay using millis()
  unsigned long currentMillis = millis();

  if (Firebase.ready() && (currentMillis - previousMillis >= interval)) { // create delay without using delay()
    previousMillis = currentMillis;
      
    FirebaseJson json;
    json.set("humidity", soilMoisture);

    // Set the data in Firebase under the MAC address node
    String path = "/RealTimeData/" + macAddress;
    Serial.println(Firebase.RTDB.setJSON(&fbdo, path.c_str(), &json) ? "Json sent successfully" : fbdo.errorReason().c_str());
  }
}
