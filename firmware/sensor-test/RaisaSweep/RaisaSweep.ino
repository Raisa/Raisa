#include <Servo.h> 
#include <Wire.h>
#include <LSM303.h>
#include <L3G4200D.h>
#include <Timer.h>
#include <SoftwareSerial.h>

const long serialSpeed=111111L;

boolean servosOn = true;

// servo
const int servoPin = 2;
int minAngle = 0;
int maxAngle = 180;
int maxDelay = 120;
int angleStep = 10;
int scanOffset = 0;
Servo myservo;

// pan & tilt system
const int panServoPin = 4;
const int tiltServoPin = 3;
int currentPanServoAngle = 0;
int currentTiltServoAngle = 0;
Servo panServo;
Servo tiltServo;

// cool blue leds
const int blueLedPin = 13;
boolean blueLedOn = false;

// ultrasonic sensors
const int pingPinForward = 6;
const int pingPinBackward = 7;

// IR sensors
const int irPinForward = 2;
const int irPinBackward = 3;
float irSensorValue;    //Must be of type float for pow()

// motors
const int motorRightSpeedPin = 5;
const int motorRightDirectionPin = 7;
int motorRightSpeed = 0;
boolean motorRightForward = true;

const int motorLeftSpeedPin = 6;
const int motorLeftDirectionPin = 8;
int motorLeftSpeed = 0;
boolean motorLeftForward = true;

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
long soundMeasurement1Sum = 0;
long soundMeasurement2Sum = 0;
short soundMeasurementCount = 0;

// compass and accelometer
LSM303 compass;
long accMeasurementSumX = 0;
long accMeasurementSumY = 0;
long accMeasurementSumZ = 0;
long compassMeasurementSum = 0;
short imuMeasurementCount = 0;

// JPEG-camera
const long cameraSerialSpeed=38400L;
SoftwareSerial cameraSerial(11,12);
int cameraCurrentReadAddress = 0x0000;
uint8_t cameraReadAddressMH, cameraReadAddressML;
boolean cameraTakePictureFlag = false;

// Gyroscope
L3G4200D gyro;

// dummy timer (no real interrupts)
Timer t;
int encoderEvent;
int imuReadEvent;
int readIncomingDataEvent;
int readSoundEvent;
long timeMillisBefore;

void configureCompass() {
  compass.init();
  compass.enableDefault();
  
  // Calibration values. Use the Calibrate example program to get the values for
  // your compass.
  compass.m_min.x = -592; compass.m_min.y = -649; compass.m_min.z = -545;
  compass.m_max.x = +356; compass.m_max.y = +480; compass.m_max.z = +748;  
}

void configureGyro() {
  gyro.enableDefault();
}

void setup() 
{ 
  pinMode(blueLedPin, OUTPUT);
  digitalWrite(blueLedPin, LOW);   
  
  Serial.begin(serialSpeed);
  cameraSerial.begin(cameraSerialSpeed);
  
  Wire.begin();  
  if (servosOn) {
    myservo.attach(servoPin);  
    //panServo.attach(panServoPin);
    //tiltServo.attach(tiltServoPin);
  } 
  
  pinMode(motorLeftSpeedPin, OUTPUT);
  pinMode(motorLeftDirectionPin, OUTPUT);
  pinMode(motorRightSpeedPin, OUTPUT);
  pinMode(motorRightDirectionPin, OUTPUT);

  configureCompass();
  configureGyro();
  
  encoderEvent = t.every(20, doEncoderRead);
  readIncomingDataEvent = t.every(20, receiveMessage);
  //imuReadEvent = t.every(10, measureCompassAndAccelerometer);
  //readSoundEvent = t.every(20, readSoundIntensity);
  
  delay(1000);
  sendCameraResetCmd();
  delay(1000);
  blink(1);
} 

void blink(int times) {
  for (int i = 0; i < times; i++) {
    digitalWrite(blueLedPin, LOW);
    delay(100);
    digitalWrite(blueLedPin, HIGH);
    delay(100);
  }
  if (blueLedOn) {
    digitalWrite(blueLedPin, HIGH);
  } else {
    digitalWrite(blueLedPin, LOW);  
  }  
}

void readSoundIntensity() {
  soundMeasurement1Sum = analogRead(soundPin1);
  soundMeasurement2Sum = analogRead(soundPin2);
  soundMeasurementCount++;
}

void doTurnOffPanServo() {
  panServo.detach();
}

void doTurnOffTiltServo() {
  tiltServo.detach();
}

void doEncoderRead() {
  //Min value is 400 and max value is 800, so state chance can be done at 600.
  if (analogRead(encoderRightPin) < 600) { 
    if (encoderRightLastValue == 0) {
      encoderRightCount += (motorRightForward ? 1 : -1);
      encoderRightLastValue = 1;
    } 
  } else {
    if (encoderRightLastValue == 1) {
      encoderRightCount += (motorRightForward ? 1 : -1);
      encoderRightLastValue = 0;
    }
  }  
  if (analogRead(encoderLeftPin) < 600) { 
    if (encoderLeftLastValue == 0) {
      encoderLeftCount += (motorLeftForward ? 1 : -1);
      encoderLeftLastValue = 1;
    } 
  } else {
    if (encoderLeftLastValue == 1) {
      encoderLeftCount += (motorLeftForward ? 1 : -1);
      encoderLeftLastValue = 0;
    }
  }
}

void handleMessage(int leftSpeed, int leftDirection, 
    int rightSpeed, int rightDirection, 
    int panServoAngle, int tiltServoAngle, int control, int seqNo) {
  // drive motors
  motorLeftForward = (leftDirection == 'B' ? false : true);
  motorRightForward = (rightDirection == 'B' ? false : true); 
  motorLeftSpeed = leftSpeed;
  motorRightSpeed = rightSpeed;
  
  analogWrite(motorLeftSpeedPin, leftSpeed);
  digitalWrite(motorLeftDirectionPin, (motorLeftForward ? LOW : HIGH));
  analogWrite(motorRightSpeedPin, rightSpeed);
  digitalWrite(motorRightDirectionPin, (motorRightForward ? LOW : HIGH));
  
  // prevent camera from breaking mechanically by constraining servo angles
  if (servosOn) {
    if (currentPanServoAngle != panServoAngle && panServoAngle >= 40 && panServoAngle <= 140) {
      panServo.attach(panServoPin);
      panServo.write(panServoAngle);
      currentPanServoAngle = panServoAngle;
      t.after(1000, doTurnOffPanServo);
    }
    if (currentTiltServoAngle != tiltServoAngle && tiltServoAngle >= 0 && tiltServoAngle <= 120) {
      tiltServo.attach(tiltServoPin);
      tiltServo.write(tiltServoAngle);
      currentTiltServoAngle = tiltServoAngle;
      t.after(1000, doTurnOffTiltServo);
    }
  }
  
  // turn lights on/off
  int lightBits = (control & 3);
  switch(lightBits) {
    case 1: 
      digitalWrite(blueLedPin, LOW);
      blueLedOn = false;
      break;
    case 2: 
      digitalWrite(blueLedPin, HIGH);
      blueLedOn = true;
      break;
    default: ;// no change
  }
  
  int cameraBit = (control & 4);
  switch(cameraBit) {
    case 4: 
      cameraTakePictureFlag = true;
      break;
    default: ;
  }
  
  int servoBit = (control & 8);
  switch(servoBit) {
    case 8:
      if (servosOn) {
        servosOn = false;
        myservo.detach();
      }
      break;
    case 0:
      if (!servosOn) {
        servosOn = true;
        myservo.attach(servoPin);
      }
      break;
    default: ; 
  }

  Serial.print("ACK");
  Serial.println(seqNo);
  Serial.flush();
}

// include 2 start bytes
const int startBytes = 2;
const int messagePayloadLength = 8;
const int lastCommandIndex = startBytes + messagePayloadLength - 1;
const int bufferSize = 13;
char receiveBuffer[bufferSize];
char receiveIndex = 0;
char receiveValue = -1;
int controlWaitCount = 0;

void receiveMessage() {
  while(Serial.available() > 0 || (receiveIndex != 0 && controlWaitCount < 100)) {
    if (Serial.available() == 0) {
      delay(1);
      controlWaitCount++;
      continue;
    }
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
                    (unsigned byte)receiveBuffer[startBytes + 4], (unsigned byte)receiveBuffer[startBytes + 5],
                    receiveBuffer[startBytes + 6], receiveBuffer[startBytes + 7]);
      receiveIndex = 0;
    } else {
      // out of sync or message ended
      receiveIndex = 0; 
    }
  }
  controlWaitCount = 0;
}

long measureDistanceUltraSonic(int pin) {
  //Used to read in the analog voltage output that is being sent by the MaxSonar device.
  //Scale factor is (Vcc/512) per inch. A 5V supply yields ~9.8mV/in
  //Arduino analog pin goes from 0 to se24, so the value has to be divided by 2 to get the actual inches
  //return ( analogRead(pingPin)/2 ) * 2.54;
  return analogRead(pin);
}

long measureDistanceInfraRed(int pin) {
  //irSensorValue = analogRead(irPin);
  // http://arduinomega.blogspot.fi/2011/05/infrared-long-range-sensor-gift-of.html
  //inches = 4192.936 * pow(sensorValue,-0.935) - 3.937;
  //return 10650.08 * pow(irSensorValue,-0.935) - 10; //cm
  return analogRead(pin);
}

void measureCompassAndAccelerometer() {
  // TODO set and handle timeouts 
  // accelorometer readings are available in compass.a.{x,y,z}
  compass.read();
  compassMeasurementSum += compass.heading((LSM303::vector){0,-1,0});
  accMeasurementSumX += (long)compass.a.x;
  accMeasurementSumY += (long)compass.a.y;
  accMeasurementSumZ += (long)compass.a.z;
  imuMeasurementCount++;
}

// results are available in gyro.g.x, gyro.g.y, gyro.g.z
void measureGyro() {
  gyro.read();
}

void sendFieldToServer(char * field, long value) {
  Serial.print(field);
  Serial.print(value);
  Serial.print(";");
}
  
void sendDataToServer(int angle, long distanceUltraSonicForward, long distanceUltraSonicBackward, 
    long distanceInfraRedForward, long distanceInfraRedBackward, 
    long compassDirection, long timeSinceStart,
    int tmpEncoderLeftCount, int tmpEncoderRightCount,
    long accelerationX, long accelerationY, long accelerationZ) {
  static long messageNumber = 0;
  Serial.print("STA;");
  sendFieldToServer("SR", angle);
  sendFieldToServer("SD", distanceUltraSonicForward);
  sendFieldToServer("TR", angle - 180);
  sendFieldToServer("TD", distanceUltraSonicBackward);
  sendFieldToServer("IR", angle);
  sendFieldToServer("ID", distanceInfraRedForward);
  sendFieldToServer("JR", angle - 180);
  sendFieldToServer("JD", distanceInfraRedBackward);
  //sendFieldToServer("SA", soundValue1);
  //sendFieldToServer("SB", soundValue2);
  sendFieldToServer("CD", compassDirection);
  sendFieldToServer("TI", timeSinceStart);
  sendFieldToServer("NO", ++messageNumber);
  sendFieldToServer("RL", tmpEncoderLeftCount);  
  sendFieldToServer("RR", tmpEncoderRightCount);
  
  // when uncommented, sample size exceeds arduino serial buffer size 128 bytes
  // ==> have problems and should add flush & delay in the middle of the sample
  //sendFieldToServer("GX", (long)gyro.g.x);
  //sendFieldToServer("GY", (long)gyro.g.y);
  //sendFieldToServer("GZ", (long)gyro.g.z);
  //sendFieldToServer("AX", accelerationX);
  //sendFieldToServer("AY", accelerationY);
  //sendFieldToServer("AZ", accelerationZ);
  if (cameraTakePictureFlag) {
    handleTakeAndSendPicture();
  }
  Serial.println("END;");  
  Serial.flush();
}

void scan(int angle, int scanDelay) {
  if (servosOn) {
    myservo.write(angle);
  }
  
  timeMillisBefore = millis();
  while (scanDelay > (millis() - timeMillisBefore)) {
    t.update();
  }

  //long soundValue1 = soundMeasurement1Sum / soundMeasurementCount;
  //long soundValue2 = soundMeasurement2Sum / soundMeasurementCount;
  //soundMeasurement1Sum = 0;
  //soundMeasurement2Sum = 0;
  //soundMeasurementCount = 0;

  int tmpEncoderLeftCount = encoderLeftCount;
  encoderLeftCount = 0;
  int tmpEncoderRightCount = encoderRightCount;
  encoderRightCount = 0;
  
  // direction used to be averaged over multiple measuments, but this causes
  // instability in direction when getting values between 350 and 10 degrees
  measureCompassAndAccelerometer();
  long compassDirection = compassMeasurementSum / imuMeasurementCount;
  long tmpAccMeasurementX = accMeasurementSumX / imuMeasurementCount;
  long tmpAccMeasurementY = accMeasurementSumY / imuMeasurementCount;
  long tmpAccMeasurementZ = accMeasurementSumZ / imuMeasurementCount;
  compassMeasurementSum = 0;
  accMeasurementSumX = 0;
  accMeasurementSumY = 0;
  accMeasurementSumZ = 0;
  imuMeasurementCount = 0;

  measureGyro();  
  t.update();
  long distanceUltraSonicForward = measureDistanceUltraSonic(pingPinForward);
  t.update();
  long distanceInfraRedForward = measureDistanceInfraRed(irPinForward);
  t.update();
  long distanceUltraSonicBackward = measureDistanceUltraSonic(pingPinBackward);
  t.update();
  long distanceInfraRedBackward = measureDistanceInfraRed(irPinBackward);
  t.update();
  
  // TODO writing serial takes time
  // organize code so that servo is turning while serial data is sent
  sendDataToServer(angle, distanceUltraSonicForward, distanceUltraSonicBackward, distanceInfraRedForward, distanceInfraRedBackward, 
    compassDirection, millis(),
    tmpEncoderLeftCount, tmpEncoderRightCount,
    tmpAccMeasurementX, tmpAccMeasurementY, tmpAccMeasurementZ);
}

void handleTakeAndSendPicture() {
  blink(3);

  analogWrite(motorLeftSpeedPin, 0);
  analogWrite(motorRightSpeedPin, 0);
  
  // clear stuff that may be waiting in the line
  if (cameraSerial.available()>0) {
    cameraSerial.read();
  }
  
  sendCameraTakePhotoCmd();
  //delay(500);
  readAndSendCameraPicture();
  
  analogWrite(motorLeftSpeedPin, motorLeftSpeed);
  analogWrite(motorRightSpeedPin, motorRightSpeed);
  cameraTakePictureFlag = false;
  cameraCurrentReadAddress = 0x0000;
}

void readAndSendCameraPicture() {
  int j = 0, k = 0, count = 0, waitCounter = 0;
  byte incomingByte;
  boolean cameraReadEndFlag = false;
  byte a[32];
  long timeMillisBefore = millis();
  
  Serial.print("CA");
  while(!cameraReadEndFlag) {  
    j = 0;
    k = 0;
    count = 0;
    sendCameraReadDataCmd();
    
    // wait for camera response
    waitCounter = 0;
    while(waitCounter<100 && !cameraSerial.available()) {
      delay(1);
      waitCounter++;
    }
    
    waitCounter = 0;
    while(!cameraReadEndFlag && waitCounter<100 && k<42) {
      if(cameraSerial.available()>0) {
        incomingByte = cameraSerial.read();
        k++;
        if((k>5)&&(j<32)&&(!cameraReadEndFlag)) {
          a[j] = incomingByte;
          //Check if the picture is over
          if((a[j-1]==0xFF)&&(a[j]==0xD9)) { 
            cameraReadEndFlag = true;     
          }          
          j++;
	  count++;
        }
      } else {
        delay(1);
        waitCounter++;
      }
    }
     
    //Send jpeg picture over the serial port as hexadecimal    
    for(j=0;j<count;j++) {   
      if(a[j]<0x10) {
        Serial.print("0");
      }
      Serial.print(a[j],HEX);
    }
    Serial.flush();
    
    if ((millis() - timeMillisBefore) > 20000) {
      cameraReadEndFlag = true;           
    }
  }
  Serial.print(";");  
  sendCameraStopTakePhotoCmd();
  //sendCameraResetCmd();
  cameraCurrentReadAddress = 0x0000;
}

void sendCameraReadDataCmd() {
  cameraReadAddressMH = cameraCurrentReadAddress / 0x100;
  cameraReadAddressML = cameraCurrentReadAddress % 0x100;
  cameraSerial.write((byte)0x56);
  cameraSerial.write((byte)0x00);
  cameraSerial.write((byte)0x32);
  cameraSerial.write((byte)0x0c);
  cameraSerial.write((byte)0x00); 
  cameraSerial.write((byte)0x0a);
  cameraSerial.write((byte)0x00);
  cameraSerial.write((byte)0x00);
  cameraSerial.write((byte)cameraReadAddressMH);
  cameraSerial.write((byte)cameraReadAddressML);   
  cameraSerial.write((byte)0x00);
  cameraSerial.write((byte)0x00);
  cameraSerial.write((byte)0x00);
  cameraSerial.write((byte)0x20); // read 20h = 32 bytes at a time
  cameraSerial.write((byte)0x00);  
  cameraSerial.write((byte)0x0a);
  cameraCurrentReadAddress += 0x20;
}

// note: camera requires 2-3 seconds after reset before accepting "take picture"-command
void sendCameraResetCmd() {
  cameraSerial.write((byte)0x56);
  cameraSerial.write((byte)0x00);
  cameraSerial.write((byte)0x26);
  cameraSerial.write((byte)0x00);
}

void sendCameraTakePhotoCmd() {
  cameraSerial.write((byte)0x56);
  cameraSerial.write((byte)0x00);
  cameraSerial.write((byte)0x36);
  cameraSerial.write((byte)0x01);
  cameraSerial.write((byte)0x00); 
  cameraSerial.flush();
  
  // read response "76 00 36 00 00" from the camera
  int loopCounter = 0;
  int byteCounter = 0;
  while(loopCounter < 1000 && byteCounter < 5) {
    while (cameraSerial.available()>0) {
      cameraSerial.read();
      byteCounter++;
    }
    if (byteCounter < 5) {
      delay(1);
    }
    loopCounter++;   
  }   
}

void sendCameraStopTakePhotoCmd() {
  cameraSerial.write((byte)0x56);
  cameraSerial.write((byte)0x00);
  cameraSerial.write((byte)0x36);
  cameraSerial.write((byte)0x01);
  cameraSerial.write((byte)0x03);  
}


void loop() { 
  scanOffset ++;
  if(scanOffset > angleStep) {
    scanOffset = 0;
  }
  for(int angle = minAngle + scanOffset; angle + scanOffset < maxAngle; angle += angleStep) { 
    if (!servosOn) {
      break;
    }
    scan(angle, maxDelay);
  } 
  for(int angle = maxAngle - scanOffset; angle - scanOffset > minAngle; angle-=angleStep) {    
    if (!servosOn) {
      break;
    }
    scan(angle, maxDelay);    
  } 
  while(!servosOn) {
    scan((minAngle + maxAngle)/2, maxDelay);
  }
} 


