

#include <SPI.h>
#include <Ethernet.h>

#define DATA_1 (PORTC |=  0X01)    // DATA 1    // for UNO
#define DATA_0 (PORTC &=  0XFE)    // DATA 0    // for UNO
#define STRIP_PINOUT (DDRC=0xFF)    // for UNO

float hues[10];

byte mac[] = { 
  0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
IPAddress ip(192,168,1,44);

char server[] = "192.168.1.15";
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

  set_color(255,0,0);
  delay(1000);

  set_color(0,255,0);
  delay(1000);

  set_color(0,0,255);
  delay(1000);

  set_color(255,255,255);
  delay(1000);

  set_color(0,0,0);
  delay(1000);

  Serial.println("runnig");

  lastConnectionTime = 0;
}

void loop()
{
  // handle connections
  if ((!client.connected()) && (millis() - lastConnectionTime > postingInterval))
  {
    Serial.println("try to connect...");

    set_color(255,0,0);
    delay(100);
    set_color(0,0,0);

    if (client.connect(server, port)) 
    {
      set_color(0,255,0);      
      Serial.println("   connected");
      client.println("Hello Server!");      
      set_color(0,0,0);      
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
      Serial.print(r); 
      Serial.print(" ");
      Serial.print(g); 
      Serial.print(" ");
      Serial.print(b); 
      Serial.print(" ");
      Serial.print("\n");

      for (byte i=0;i<10;i++)
      {
        colors[i] = (g*65536)+(b*256)+r;
      }
    }

    else if (valueBuffer.length() == 9*10)
    {
      for (byte i=0;i<10;i++)
      {
        r = valueBuffer.substring(i*9+0, i*9+3).toInt();
        g = valueBuffer.substring(i*9+3, i*9+6).toInt();
        b = valueBuffer.substring(i*9+6, i*9+9).toInt();

        Serial.print("Setting RGB:");
        Serial.print(r); 
        Serial.print(" ");
        Serial.print(g); 
        Serial.print(" ");
        Serial.print(b); 
        Serial.print(" ");
        Serial.print("\n");

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


void set_color(byte r, byte g, byte b)
{
  noInterrupts();
  reset_strip();  
  for(int i=0;i<10;i++)
  {
    send_strip((g*65536)+(b*256)+r);
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








