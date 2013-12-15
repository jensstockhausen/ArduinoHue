#include <SPI.h>
#include <Ethernet.h>

#define DATA_1 (PORTC |=  0X01)    // DATA 1    // for UNO
#define DATA_0 (PORTC &=  0XFE)    // DATA 0    // for UNO
#define STRIP_PINOUT (DDRC=0xFF)    // for UNO

// communication
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

// color handling
unsigned long updateInterval;
unsigned long lastUpdateTime;

// target and current RGB values for fading to color 
long target[3][10]; 
long current[3][10]; 
unsigned long colors[10];

void setup() 
{
  STRIP_PINOUT;   

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

  updateInterval = 3;

  for (int c=0;c<3;c++)
  {
    for (int i=0;i<10;i++)
    {
      target[c][i]  = 0;
      current[c][i] = 0;
    }
  }

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

  if (millis() - lastUpdateTime > updateInterval)
  {
    update_colors();
    lastUpdateTime = millis();
  }

}


void update_colors()
{
  bool needUpdate = false;

  for (int i=0;i<10;i++)
  {
    for (int c=0;c<3;c++)
    {
      if (target[c][i] != current[c][i])
      {
        needUpdate = true;

        if (target[c][i] - current[c][i] > 0)
        {
          current[c][i] = current[c][i] + 1;
        }
        else
        {
          current[c][i] = current[c][i] - 1;
        }
      }
    }
  }

  if (needUpdate)
  {
    for (int i=0;i<10;i++)
    {
      colors[i] = (current[1][i]*65536) + (current[2][i]*256) + current[0][i];  
    }
    
    print_values();
    
    noInterrupts();
    reset_strip();  
    for(int i=0;i<10;i++)
    {
      send_strip(colors[i]);
    }
    interrupts();
  }  
}


void execute()
{
  Serial.println("Execute: " + modeBuffer + " " + valueBuffer);
  byte r,g,b;

  if (modeBuffer.equals("SET"))
  {
    if (valueBuffer.length() == 9)
    {
      r = valueBuffer.substring(0, 3).toInt();
      g = valueBuffer.substring(3, 6).toInt();
      b = valueBuffer.substring(6, 9).toInt();

      for (int i=0; i<10; i++)
      {
        target[0][i] = r;      
        target[1][i] = g;  
        target[2][i] = b; 
      }
    }

    else if (valueBuffer.length() == 9*10)
    {
      for (int i=0;i<10;i++)
      {
        r = valueBuffer.substring(i*9+0, i*9+3).toInt();
        g = valueBuffer.substring(i*9+3, i*9+6).toInt();
        b = valueBuffer.substring(i*9+6, i*9+9).toInt();

        target[0][i] = r;      
        target[1][i] = g;  
        target[2][i] = b; 
      }
    }
    
    print_values();

  }
}

void print_values()
{
  // remove this resturn for debugging the values
  return;
  
  Serial.print("Tar: ");
  for (int i=0; i<10; i++)
  {
    Serial.print(target[0][i],HEX);
    Serial.print(target[1][i],HEX);
    Serial.print(target[2][i],HEX);
    Serial.print(" ");
  }
  Serial.print("\n");

  Serial.print("Cur: ");
  for (int i=0; i<10; i++)
  {
    Serial.print(current[0][i],HEX);
    Serial.print(current[1][i],HEX);
    Serial.print(current[2][i],HEX);
    Serial.print(" ");
  }
  Serial.print("\n");
  
  Serial.print("Col: ");
  for (int i=0; i<10; i++)
  {
    Serial.print(colors[i],HEX);
    Serial.print(" ");
  } 
  Serial.print("\n\n");
  
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
  unsigned long j=0x800000;

  for (byte i=0;i<24;i++)
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












