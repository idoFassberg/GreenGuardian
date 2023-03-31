int soilMoisturePin = A0; // Define the analog input pin to which the sensor is connected

void setup() {
  Serial.begin(9600); // Initialize serial communication at 9600 baud rate
}

void loop() {
  int soilMoistureValue = analogRead(soilMoisturePin); // Read the analog input value from the sensor
  Serial.print("Soil Moisture: ");
  Serial.println(soilMoistureValue); // Print the soil moisture value to the serial monitor
  delay(1000); // Wait for 1 second before reading the sensor again
}