#include <Servo.h> 
#include <Wire.h>
#include <LSM303.h>
#include <Timer.h>

Servo myservo;  

const long serialSpeed=111111L;

// servo
const int servoPin = 2;
int minAngle = 0;
int maxAngle = 180;
int maxDelay = 400;
int angleStep = 20;
int scanOffset = 0;
boolean servoOn = true;

// cool blue leds
const int blueLedPin = 12;

// ultrasonic sensor
const int pingPin = 3;

// IR sensor
const int irPin = 2;
float irSensorValue;    //Must be of type float for pow()

// motors
const int motorRightSpeedPin = 5;
const int motorRightDirectionPin = 7;

const int motorLeftSpeedPin = 6;
const int motorLeftDirectionPin = 8;

// encoders
const int encoderRightPin = 1;
int encoderRightCount = 0;
int encoderRightLastValue = 0;
const int encoderLeftPin = 0;
int encoderLeftCount = 0;
int encoderLeftLastValue = 0;

// sound sensors
const int soundPin1 = 6;
const int soundPin2 = 7;

// compass and accelometer
LSM303 compass;

// dummy timer (no real interrupts)
Timer t;
int encoderEvent;
int readIncomingDataEvent;
long timeMillisBefore;

void configureCompass() {
  compass.init();
  compass.enableDefault();
  
  // Calibration values. Use the Calibrate example program to get the values for
  // your compass.
  compass.m_min.x = -507; compass.m_min.y = -858; compass.m_min.z = -566;
  compass.m_max.x = +389; compass.m_max.y = +114; compass.m_max.z = 402;  
}

void setup() 
{ 
  Serial.begin(serialSpeed);
  Wire.begin();  
  if (servoOn) {
    myservo.attach(servoPin);  
  } 
  
  pinMode(motorLeftSpeedPin, OUTPUT);
  pinMode(motorLeftDirectionPin, OUTPUT);
  pinMode(motorRightSpeedPin, OUTPUT);
  pinMode(motorRightDirectionPin, OUTPUT);

  configureCompass();

  encoderEvent = t.every(20, doEncoderRead);
  readIncomingDataEvent = t.every(20, readIncomingData);
  
  pinMode(blueLedPin, OUTPUT);
  digitalWrite(blueLedPin, HIGH);
  Serial.println("RaisaSweep starting");
} 

void doEncoderRead() {
  //Min value is 400 and max value is 800, so state chance can be done at 600.
  if (analogRead(encoderRightPin) < 600) { 
    if (encoderRightLastValue == 0) {
      encoderRightCount++;
      encoderRightLastValue = 1;
    } 
  } else {
    encoderRightLastValue = 0;
  }  
  if (analogRead(encoderLeftPin) < 600) { 
    if (encoderLeftLastValue == 0) {
      encoderLeftCount++;
      encoderLeftLastValue = 1;
    } 
  } else {
    encoderLeftLastValue = 0;
  }
}

void handleMessage(int leftSpeed, int leftDirection, int rightSpeed, int rightDirection, int control) {
  // drive motors
  int leftForward = (leftDirection == 'B' ? HIGH : LOW);
  int rightForward = (rightDirection == 'B' ? HIGH: LOW);
    
  analogWrite(motorLeftSpeedPin, leftSpeed);
  digitalWrite(motorLeftDirectionPin, leftForward);
  analogWrite(motorRightSpeedPin, rightSpeed);
  digitalWrite(motorRightDirectionPin, rightForward);
  
  // turn lights on/off
  int lightBits = (control & 3);
  switch(lightBits) {
    case 1: digitalWrite(blueLedPin, LOW);
            break;
    case 2: digitalWrite(blueLedPin, HIGH);
            break;
    default: ;// no change
  }
}

char receiveBuffer[10];
char receiveIndex = 0;
char receiveValue = -1;
// include 2 start bytes
const int startBytes = 2;
const int messagePayloadLength = 5;
const int lastCommandIndex = startBytes + messagePayloadLength - 1;

void receiveMessage() {
  while(Serial.available() > 0) {
    receiveValue = Serial.read();
    receiveBuffer[receiveIndex] = receiveValue;
    if((receiveIndex == 0 && receiveValue == 'R')
        ||(receiveIndex == 1 && receiveValue == 'a')
        ||(receiveIndex > 1 && receiveIndex <= lastCommandIndex)) {
      receiveIndex ++;
      receiveBuffer[receiveIndex] = '\0';
    } else if (receiveIndex == lastCommandIndex + 1 
                && receiveValue == 'i') {
      // end of message
      handleMessage(receiveBuffer[startBytes], receiveBuffer[startBytes + 1], 
                    receiveBuffer[startBytes + 2], receiveBuffer[startBytes + 3],
                    receiveBuffer[startBytes + 4]);
      receiveIndex = 0;
    } else {
      // out of sync or message ended
      receiveIndex = 0; 
    }
  }  
}

long measureDistanceUltraSonic() {
  //Used to read in the analog voltage output that is being sent by the MaxSonar device.
  //Scale factor is (Vcc/512) per inch. A 5V supply yields ~9.8mV/in
  //Arduino analog pin goes from 0 to 1024, so the value has to be divided by 2 to get the actual inches
  //return ( analogRead(pingPin)/2 ) * 2.54;
  return analogRead(pingPin);
}

long measureDistanceInfraRed() {
  //irSensorValue = analogRead(irPin);
  // http://arduinomega.blogspot.fi/2011/05/infrared-long-range-sensor-gift-of.html
  //inches = 4192.936 * pow(sensorValue,-0.935) - 3.937;
  //return 10650.08 * pow(irSensorValue,-0.935) - 10; //cm
  return analogRead(irPin);
}

int measureCompassHeading() {
  compass.read();
  int heading = compass.heading((LSM303::vector){0,-1,0});
}

void readIncomingData() {
  receiveMessage();  
}

void sendFieldToServer(char * field, long value) {
  Serial.print(field);
  Serial.print(value);
  Serial.print(";");
  t.update();
}
  
void sendDataToServer(int angle, long distanceUltraSonic, long distanceInfraRed, 
    long soundValue1, long soundValue2, long compassDirection, long timeSinceStart,
    int tmpEncoderLeftCount, int tmpEncoderRightCount) {
  static long messageNumber = 0;
  Serial.print("STA;");
  sendFieldToServer("SR", angle);
  sendFieldToServer("SD", distanceUltraSonic);
  sendFieldToServer("IR", angle);
  sendFieldToServer("ID", distanceInfraRed);
  sendFieldToServer("SA", soundValue1);
  sendFieldToServer("SB", soundValue2);
  sendFieldToServer("CD", compassDirection);
  sendFieldToServer("TI", timeSinceStart);
  sendFieldToServer("NO", ++messageNumber);
  sendFieldToServer("RL", tmpEncoderLeftCount);  
  sendFieldToServer("RR", tmpEncoderRightCount);    
  Serial.println("END;");  
}

void scan(int angle, int scanDelay) {
  if (servoOn) {
    myservo.write(angle);
  }
  
  timeMillisBefore = millis();
  while (scanDelay > (millis() - timeMillisBefore)) {
    t.update();
  }
  long distanceUltraSonic = measureDistanceUltraSonic();
  long distanceInfraRed = measureDistanceInfraRed();
  long soundValue1 = analogRead(soundPin1);
  long soundValue2 = analogRead(soundPin2);
  long compassDirection = measureCompassHeading();
  int tmpEncoderLeftCount = encoderLeftCount;
  encoderLeftCount = 0;
  int tmpEncoderRightCount = encoderRightCount;
  encoderRightCount = 0;
  // TODO writing serial takes time
  // organize code so that servo is turning while serial data is sent
  t.update();
  sendDataToServer(angle, distanceUltraSonic, distanceInfraRed, 
    soundValue1, soundValue2, compassDirection, millis(),
    tmpEncoderLeftCount, tmpEncoderRightCount);
}

void loop() 
{ 
  scanOffset ++;
  if(scanOffset > angleStep) {
    scanOffset = 0;
  }
  for(int angle = minAngle + scanOffset; angle + scanOffset < maxAngle; angle += angleStep)
  {                                   
    scan(angle, maxDelay);
  } 
  for(int angle = maxAngle - scanOffset; angle - scanOffset > minAngle; angle-=angleStep) 
  {                                
    scan(angle, maxDelay);
  } 
} 
