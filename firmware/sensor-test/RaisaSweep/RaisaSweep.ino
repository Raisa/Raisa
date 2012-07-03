
#include <Servo.h> 
 
Servo myservo;  

int serialSpeed=9600;

// servo
const int servoPin = 2;
int minAngle = 0;
int maxAngle = 180;
int maxDelay = 1000;
int angleStep = 20;
int scanOffset = 0;
boolean servoOn = false;

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

// sound sensors
const int soundPin1 = 6;
const int soundPin2 = 7;

// motors

void setup() 
{ 
  Serial.begin(serialSpeed);  
  if (servoOn) {
    myservo.attach(servoPin);  // attaches the servo on pin 9 to the servo object
  } 
  pinMode(pingPin, INPUT);
  pinMode(irPin, INPUT);
  
  pinMode(motorLeftSpeedPin, OUTPUT);
  pinMode(motorLeftDirectionPin, OUTPUT);
  pinMode(motorRightSpeedPin, OUTPUT);
  pinMode(motorRightDirectionPin, OUTPUT);
  
  Serial.println("RaisaSweep starting");
} 

void handleMessage(int leftSpeed, int leftDirection, int rightSpeed, int rightDirection) {
  // drive motors
  int leftForward = (leftDirection == 'B' ? LOW : HIGH);
  int rightForward = (rightDirection == 'B' ? LOW: HIGH);
    
  analogWrite(motorLeftSpeedPin, leftSpeed);
  digitalWrite(motorLeftDirectionPin, leftForward);
  analogWrite(motorRightSpeedPin, rightSpeed);
  digitalWrite(motorRightDirectionPin, rightForward);
}

char receiveBuffer[10];
char receiveIndex = 0;
char receiveValue = -1;
// include 2 start bytes
const int startBytes = 2;
const int lastCommandIndex = startBytes+4;

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
                    receiveBuffer[startBytes + 2], receiveBuffer[startBytes + 3] );
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

void sendDataToServer(int angle, long distanceUltraSonic, long distanceInfraRed, 
    long soundValue1, long soundValue2, long compassDirection) {
  Serial.print("STA;");
  Serial.print("SR");  
  Serial.print(angle);
  Serial.print(";");
  Serial.print("SD");  
  Serial.print(distanceUltraSonic);
  Serial.print(";");
  Serial.print("IR");  
  Serial.print(angle);
  Serial.print(";");
  Serial.print("ID");  
  Serial.print(distanceInfraRed);
  Serial.print(";");  
  Serial.print("SA");
  Serial.print(soundValue1);
  Serial.print(";");      
  Serial.print("SB");
  Serial.print(soundValue2);
  Serial.print(";");
  Serial.print("CD");
  Serial.print(compassDirection);
  Serial.print(";");
  Serial.println("END;");  
}

void scan(int angle, int scanDelay) {
  if (servoOn) {
    myservo.write(angle);
  }
  delay(scanDelay);
  long distanceUltraSonic = measureDistanceUltraSonic();
  long distanceInfraRed = measureDistanceInfraRed();
  long soundValue1 = analogRead(soundPin1);
  long soundValue2 = analogRead(soundPin2);
  long compassDirection = 42;
  // TODO writing serial takes time
  // organize code so that servo is turning while serial data is sent
  sendDataToServer(angle, distanceUltraSonic, distanceInfraRed, soundValue1, soundValue2, compassDirection);
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
