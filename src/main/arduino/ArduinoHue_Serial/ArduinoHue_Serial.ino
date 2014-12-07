
#include "Strip.h"
Strip strip;

uint32_t data;


/*boolean awaitingResponse = false;
unsigned long lastConnectionTime = 0;   
boolean lastConnected = false; 
const unsigned long postingInterval = 5*1000;

boolean readingMode;
String  modeBuffer;

boolean readingValue;
String  valueBuffer;

// color handling
boolean isFading;
unsigned long updateInterval;
unsigned long lastUpdateTime;

// target and current RGB values for fading to color 
long target[3][10]; 
long current[3][10]; 
unsigned long colors[10];
*/

void setup() 
{ 
  Serial.begin(9600);
  Serial.println("starting"); 
  
  strip.setColor(0xffffff); delay(500);
  strip.setColor(0x0000ff); // ggbbrr
  delay(1000);
  strip.setColor(255,0,0);
  delay(1000);  
  
  strip.setColor(0xffffff); delay(500);
  strip.setColor(0xff0000); // ggbbrr
  delay(1000);
  strip.setColor(0,255,0);
  delay(1000);  
  
  strip.setColor(0xffffff); delay(500);
  strip.setColor(0x00ff00); // ggbbrr
  delay(1000);
  strip.setColor(0,0,255);
  delay(1000);  
  
  data = 0xFF;
  

/*  
  readingMode  = false;
  readingValue = false;

  modeBuffer  = "";
  valueBuffer = "";

  Serial.begin(9600);
  Serial.println("starting");

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

  isFading = true;
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
*/
}

void loop()
{
  strip.setColor(data);
  data <<= 1;
  delay(100);
}

/*
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
        
        if (!isFading)
        {
          current[c][i] = target[c][i];
        }
        else 
        {
          if (target[c][i] - current[c][i] > 0)
          {
            current[c][i] = current[c][i] + 1;
          }
          else
          {
            current[c][i] = current[c][i] - 1;
          }
        } //fading
      
      }//c
    }//i
    
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

  //#SET|00025500
  //#SET|00025500000255000002550000025500000255000002550000025500000255000002550000025500
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
  
  //#FADE|ON100
  //#FADE|OF020
  //#FADE|ON006
  else if (modeBuffer.equals("FADE"))
  {
    isFading = (valueBuffer.substring(0, 2).equals("ON"));
    updateInterval = valueBuffer.substring(2, 5).toInt();
    
    Serial.print("Fade: ");
    if (isFading) Serial.print("ON  ");
    else Serial.print("OFF ");
    Serial.print(updateInterval);
    Serial.print("\n");
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
*/











