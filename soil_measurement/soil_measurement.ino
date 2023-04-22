#include <Arduino.h>
#if defined(ESP32)
#include <WiFi.h>
#include <FirebaseESP32.h>
#elif defined(ESP8266)
#include <ESP8266WiFi.h>
#include <FirebaseESP8266.h>
#elif defined(ARDUINO_RASPBERRY_PI_PICO_W)
#include <WiFi.h>
#include <FirebaseESP8266.h>
#endif

// Provide the token generation process info.
#include <addons/TokenHelper.h>

// Provide the RTDB payload printing info and other helper functions.
#include <addons/RTDBHelper.h>

const char* ssid = "Fassberg_2.4"; // Wi-Fi name
const char* password = "0546366753";
const char* webAPIKey = "AIzaSyBfcRrSCqfqCui2Fkt1DlTHaTZTBx0sAIQ";
const char* databaseUrl = "https://greenguardian-app-default-rtdb.europe-west1.firebasedatabase.app/";
const char* userEmail = "test@gmail.com";
const char* userPassword = "123456";

FirebaseData fbdo;
FirebaseData fbdo2;
FirebaseAuth auth;
FirebaseConfig config;
#define soilMoisturePin A0


void initializeWiFiConnection()
{
  WiFi.begin(ssid, password);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.print(".");
  } 

  Serial.println("");
  Serial.println("WiFi connected");
  Serial.print("NodeMCU IP address: " + WiFi.localIP().toString());
}

void setup()
{
  Serial.begin(9600);

  // initialize Led
  pinMode(LED_BUILTIN, OUTPUT);
  digitalWrite(LED_BUILTIN, LOW);

  initializeWiFiConnection();

  config.api_key = webAPIKey;
  config.database_url = databaseUrl;
  auth.user.email = userEmail;
  auth.user.password = userPassword;
  Firebase.begin(&config, &auth);
}

unsigned long previousMillis = 0;
const unsigned long interval = 5000; // 5 second interval

void loop()
{
  int soilMoisture = analogRead(soilMoisturePin); // Read the analog input value from the sensor

  // Create a time delay using millis()
  unsigned long currentMillis = millis();


  if (currentMillis - previousMillis >= interval) { // create delay without using delay()
      previousMillis = currentMillis;

    if (Firebase.ready())
    {
      Firebase.getBool(fbdo2, FPSTR("/mode"));
      if (!fbdo2.to<bool>())
      {
        Serial.println("mode is OFF");
      }
      else
      {
        Firebase.setInt(fbdo, F("/ido"), soilMoisture);
        Serial.println("soilMoisture = " + String(soilMoisture));
      }
    }
    else
    {
      Serial.println("firebase is not ready!");
    }
  }
}
