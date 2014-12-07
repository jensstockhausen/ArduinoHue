#ifndef STRIP_H
#define STRIP_H

#include <arduino.h>

class Strip
{
public:

  Strip();
  ~Strip();
  
  void setColor(byte r, byte g, byte b);
  void setColor(uint32_t data);
  
  void reset();

private:

  void sendData(uint32_t data);
};

#endif // STRIP_H

