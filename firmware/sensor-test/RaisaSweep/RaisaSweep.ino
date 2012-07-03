
#include <Servo.h> 
 
Servo myservo;  

int serialSpeed=9600;

// servo
const int servoPin = 2;
int minAngle = 0;
int maxAngle = 180;
int maxDelay = 400;
int angleStep = 20;
int scanOffset = 0;
boolean servoOn = true;

// ultrasonic sensor
const int pingPin = 3;

// IR sensor
const int irPin = 2;
float irSensorValue;    //Must be of type float for pow()

// sound sensors
const int soundPin1 = 6;
const int soundPin2 = 7;

// motors
boolean motorsOn = false;
//Rear motors
int E3 = 6; //M1 Speed Control 
int E4 = 5; //M2 Speed Control 
int M3 = 8; //M1 Direction Control 
int M4 = 7; //M2 Direction Control 

void setup() 
{ 
  Serial.begin(serialSpeed);  
  if (servoOn) {
    myservo.attach(servoPin);  // attaches the servo on pin 9 to the servo object
  } 
  if (motorsOn) {
    for(int i=5;i<=8;i++) 
      pinMode(i, OUTPUT);   
    analogWrite (E3,255);
    digitalWrite(M3,LOW); 
    analogWrite (E4,255); 
    digitalWrite(M4,LOW); 
  }  
  Serial.println("SensorSweep starting");
} 
 

long measureDistanceUltraSonic() {
  //Used to read in the analog voltage output that is being sent by the MaxSonar device.
  //Scale factor is (Vcc/512) per inch. A 5V supply yields ~9.8mV/in
  //Arduino analog pin goes from 0 to 1024, so the value has to be divided by 2 to get the actual inches
  return ( analogRead(pingPin)/2 ) * 2.54;
}

long measureDistanceInfraRed() {
  irSensorValue = analogRead(irPin);
  // http://arduinomega.blogspot.fi/2011/05/infrared-long-range-sensor-gift-of.html
  //inches = 4192.936 * pow(sensorValue,-0.935) - 3.937;
  return 10650.08 * pow(irSensorValue,-0.935) - 10; //cm
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
  Serial.println("END;");
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
