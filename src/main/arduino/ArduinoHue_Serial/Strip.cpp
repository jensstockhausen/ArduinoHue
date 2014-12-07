#include "Strip.h"

#define DATA_1 (PORTC |=  0X01)    // DATA 1    // for UNO
#define DATA_0 (PORTC &=  0XFE)    // DATA 0    // for UNO
#define STRIP_PINOUT (DDRC=0xFF)    // for UNO

Strip::Strip()
{
  STRIP_PINOUT;
}

Strip::~Strip()
{
}

void Strip::setColor(byte r, byte g, byte b)
{
  uint32_t data = g;

  data <<= 8;
  data  += b;
  data <<= 8;
  data  += r;
  
  setColor(data);
}


void Strip::setColor(uint32_t data)
{
  noInterrupts();
  reset();  
  
  for(int i=0;i<10;i++)
  {
    sendData(data);
  }
  
  interrupts();
}



void Strip::reset()
{
  DATA_0;
  delayMicroseconds(20);
}


void Strip::sendData(uint32_t data)
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
