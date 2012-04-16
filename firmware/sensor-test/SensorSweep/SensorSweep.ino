/*
 * Attach distance sensor to a servo.
 * Make sweeping motion with the servo and periodically make a distance scan.
 * Results of distance scan is sent via serial line
 */

#include <Servo.h> 
 
Servo myservo;  // create servo object to control a servo 
                // a maximum of eight servo objects can be created 
 
int pos = 0;    // variable to store the servo position 
int minAngle = 0;
int maxAngle = 180;
int maxDelay = 400;
int angleStep = 20;
const int servoPin = 9;
const int pingPin = 7;
long maxDistance = 540;

void setup() 
{ 
  myservo.attach(servoPin);  // attaches the servo on pin 9 to the servo object 
  initSerial();
} 
 
void initSerial() {
  Serial.begin(9600);  
  Serial.println("SensorSweep starting");
}

void sendScan(int angle, long distance) {
  Serial.print("J");
  Serial.print(angle);
  Serial.print(",");
  Serial.print(distance);
  Serial.println("");
}


long microsecondsToCentimeters(long microseconds)
{
  // The speed of sound is 340 m/s or 29 microseconds per centimeter.
  // The ping travels out and back, so to find the distance of the
  // object we take half of the distance travelled.
  return microseconds / 29 / 2;
}

long measureDistance() {
  pinMode(pingPin, OUTPUT);
  digitalWrite(pingPin, LOW);
  delayMicroseconds(2);
  digitalWrite(pingPin, HIGH);
  delayMicroseconds(5);
  digitalWrite(pingPin, LOW);
  // The same pin is used to read the signal from the PING))): a HIGH
  // pulse whose duration is the time (in microseconds) from the sending
  // of the ping to the reception of its echo off of an object.
  pinMode(pingPin, INPUT);
  long duration = pulseIn(pingPin, HIGH);

  // convert the time into a distance
  long dist = microsecondsToCentimeters(duration);
  if(dist > maxDistance) {
    return -1;
  }  
  return dist;
}

void scan(int angle, int scanDelay) {
  myservo.write(angle);
  delay(scanDelay);
}

void loop() 
{ 
  for(int angle = minAngle; pos < maxAngle; pos += angleStep)
  {                                   
    scan(angle, maxDelay);
  } 
  for(int angle = maxAngle; pos>=minAngle; pos-=angleStep) 
  {                                
    scan(angle, maxDelay);
  } 
} 
