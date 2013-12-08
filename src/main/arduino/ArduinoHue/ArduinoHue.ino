

#include <SPI.h>
#include <Ethernet.h>


#define DATA_1 (PORTC |=  0X01)    // DATA 1    // for UNO
#define DATA_0 (PORTC &=  0XFE)    // DATA 0    // for UNO
#define STRIP_PINOUT (DDRC=0xFF)    // for UNO

float hues[10];

byte mac[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
IPAddress ip(192,168,42,2);

char server[] = "192.168.42.1";
int port(8888);

EthernetClient client;
boolean awaitingResponse = false;
unsigned long lastConnectionTime = 0;   
boolean lastConnected = false; 
const unsigned long postingInterval = 5*1000;

boolean readingMode;
String  modeBuffer;

boolean readingValue;
String  valueBuffer;

void setup() 
{
  
  STRIP_PINOUT;   
     
  for (int i=0;i<10;i++)
  {
    hues[i] = i * 90.0 / 10.0;
  }
  
  
  readingMode  = false;
  readingValue = false;
  
  modeBuffer  = "";
  valueBuffer = "";
  
  Serial.begin(9600);
  Serial.println("starting");

  Ethernet.begin(mac, ip);
 
  Serial.println("wait for init");
  
  for (byte i=0;i<5;i++)
  {
    Serial.println("waiting...");
    delay(1000);
  }
  
  Serial.println("runnig");
  
  lastConnectionTime = 0;
}

void loop()
{
  // handle connections
  if ((!client.connected()) && (millis() - lastConnectionTime > postingInterval))
  {
    Serial.println("try to connect...");
    
    if (client.connect(server, port)) 
    {
      Serial.println("   connected");
      client.println("Hello Server!");
    } 
    else 
    {
      Serial.println("   connection failed");
    }
    
    lastConnectionTime = millis();
  }

  // read from socket  
  if (client.available()) 
  {
    //Serial.println(client.available());
    
    char c = client.read();
    
    if (c == '#')
    {
      readingMode  = true;
      readingValue = false;
    }
    else if ((c == '|') && (readingMode))
    {
      readingMode  = false;
      readingValue = true;
    }
    else if ((c == '\n') && (readingValue))
    {
      readingMode  = false;
      readingValue = false;
      
      execute();
      
      client.println("ACK");
      
      modeBuffer  = "";
      valueBuffer = "";
    }
    else
    {
      if (readingMode)  modeBuffer  += c;
      if (readingValue) valueBuffer += c;
    }    
  }

  // disconnect 
  if (!client.connected())
  {
    //Serial.println("disconnecting");
    client.stop();
  }

}


void execute()
{
  Serial.println("Execute: " + modeBuffer + " " + valueBuffer);
  byte r,g,b;
  unsigned long colors[10];
  
  if (modeBuffer.equals("SET"))
  {
    if (valueBuffer.length() == 9)
    {
      r = valueBuffer.substring(0, 3).toInt();
      g = valueBuffer.substring(3, 6).toInt();
      b = valueBuffer.substring(6, 9).toInt();

      Serial.print("Setting RGB:");
      Serial.print(r); Serial.print(" ");
      Serial.print(g); Serial.print(" ");
      Serial.print(b); Serial.print(" ");
      Serial.print("\n");
      
      for (byte i=0;i<10;i++)
      {
        colors[i] = (g*65536)+(b*256)+r;
      }
    }
    
  }
    
  noInterrupts();
  reset_strip();  
  for(int i=0;i<10;i++)
  {
    send_strip(colors[i]);
  }
  interrupts();   
}


void send_strip(uint32_t data)
{
  int i;
  unsigned long j=0x800000;

  for (i=0;i<24;i++)
  {
    if (data & j)
    {
      DATA_1;
      __asm__("nop\n\t");
      __asm__("nop\n\t");
      __asm__("nop\n\t");
      __asm__("nop\n\t");
      __asm__("nop\n\t");
      __asm__("nop\n\t");
      __asm__("nop\n\t");
      __asm__("nop\n\t");
      __asm__("nop\n\t");    
      __asm__("nop\n\t");
      __asm__("nop\n\t");
      __asm__("nop\n\t");
      __asm__("nop\n\t");
      __asm__("nop\n\t");
      __asm__("nop\n\t");
      __asm__("nop\n\t");
      __asm__("nop\n\t");
      __asm__("nop\n\t");
      DATA_0;
    }
    else
    {
      DATA_1;
      __asm__("nop\n\t");
      __asm__("nop\n\t");
      __asm__("nop\n\t");
      __asm__("nop\n\t");
      __asm__("nop\n\t");
      __asm__("nop\n\t");
      __asm__("nop\n\t");
      __asm__("nop\n\t");
      __asm__("nop\n\t");    
      DATA_0;
    }

    j>>=1;
  }
}


void reset_strip()
{
  DATA_0;
  delayMicroseconds(20);
}






