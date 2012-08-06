/* Linksprite */

#include <SoftwareSerial.h>

byte incomingbyte;
SoftwareSerial mySerial(9,10);                     //Configure pin 9 and 10
int a=0x0000,j=0,k=0,count=0;                    //Read Starting address       
uint8_t MH,ML;
boolean EndFlag=0;
                               
void SendResetCmd();
void SendTakePhotoCmd();
void SendReadDataCmd();
void StopTakePhotoCmd();

void setup()
{ 
  Serial.begin(19200);
  mySerial.begin(38400);
}

void loop() 
{
    //Serial.print("Beginning of loop");
     SendResetCmd();
     delay(4000);                               //After reset, wait 2-3 second to send take picture command
      
      SendTakePhotoCmd();
    //Serial.print("Sent send picture");

     while(mySerial.available()>0)
      {
        incomingbyte=mySerial.read();

      }   
      byte a[32];
      
      while(!EndFlag)
      {  
         j=0;
         k=0;
         count=0;
         SendReadDataCmd();

         delay(25);
          while(mySerial.available()>0)
          {
               incomingbyte=mySerial.read();
               k++;
               if((k>5)&&(j<32)&&(!EndFlag))
               {
               a[j]=incomingbyte;
               if((a[j-1]==0xFF)&&(a[j]==0xD9))      //Check if the picture is over
               EndFlag=1;                           
               j++;
	       count++;
               }
          }
         
          for(j=0;j<count;j++)
          {   if(a[j]<0x10)
            //Serial.write((byte)a[j]);
          
              Serial.print("0");
              Serial.print(a[j],HEX);
              //Serial.print(" ");
          }                                       //Send jpeg picture over the serial port
          //Serial.println();
      }      
     while(1);
}

//Send Reset command
void SendResetCmd()
{
      mySerial.write((byte)0x56);
      mySerial.write((byte)0x00);
      mySerial.write((byte)0x26);
      mySerial.write((byte)0x00);
}

//Send take picture command
void SendTakePhotoCmd()
{
      mySerial.write((byte)0x56);
      mySerial.write((byte)0x00);
      mySerial.write((byte)0x36);
      mySerial.write((byte)0x01);
      mySerial.write((byte)0x00);  
}

//Read data
void SendReadDataCmd()
{
      MH=a/0x100;
      ML=a%0x100;
      mySerial.write((byte)0x56);
      mySerial.write((byte)0x00);
      mySerial.write((byte)0x32);
      mySerial.write((byte)0x0c);
      mySerial.write((byte)0x00); 
      mySerial.write((byte)0x0a);
      mySerial.write((byte)0x00);
      mySerial.write((byte)0x00);
      mySerial.write((byte)MH);
      mySerial.write((byte)ML);   
      mySerial.write((byte)0x00);
      mySerial.write((byte)0x00);
      mySerial.write((byte)0x00);
      mySerial.write((byte)0x20);
      mySerial.write((byte)0x00);  
      mySerial.write((byte)0x0a);
      a+=0x20;                            //address increases 32£¨set according to buffer size
}

void StopTakePhotoCmd()
{
      mySerial.write((byte)0x56);
      mySerial.write((byte)0x00);
      mySerial.write((byte)0x36);
      mySerial.write((byte)0x01);
      mySerial.write((byte)0x03);        
}










