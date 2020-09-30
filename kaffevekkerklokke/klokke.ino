#include "Wire.h"
#define DS3231_I2C_ADDRESS 0x68
#include <TM1637Display.h>

#define CLK 6
#define DIO 7
#define KAFFELYS 2
#define KAFFE A0

TM1637Display display(CLK, DIO);

byte second, minute, hour, dayOfWeek, dayOfMonth, month, year;
int coffeeTime = -4;
boolean kolon;


byte alarmer[] {
//  dag  tim  min
     2,   9,  00,        // Mandag
     3,   7,  40,        // Tirsdag
     4,   6,  50,        // Onsdag
     5,   8,  30,        // Torsdag
     6,   7,  45,        // Fredag
     7,   9,  30,        // Lørdag
     1,  10,  30         // Søndag
};

// Convert normal decimal numbers to binary coded decimal
byte decToBcd(byte val) {
  return( (val/10*16) + (val%10) );
}
// Convert binary coded decimal to normal decimal numbers
byte bcdToDec(byte val) {
  return( (val/16*10) + (val%16) );
}
void setup(){
  pinMode(KAFFELYS, OUTPUT);
  pinMode(KAFFE, OUTPUT);
  digitalWrite(KAFFE, HIGH);
  
  display.setBrightness(0x08);
  Wire.begin();
  Serial.begin(9600);
  // set the initial time here:
  // DS3231 seconds, minutes, hours, day, date, month, year
  // setDS3231time(30,24,15,3,8,5,18);
}
void setDS3231time(byte second, byte minute, byte hour, byte dayOfWeek, byte
dayOfMonth, byte month, byte year) {
  // sets time and date data to DS3231
  Wire.beginTransmission(DS3231_I2C_ADDRESS);
  Wire.write(0); // set next input to start at the seconds register
  Wire.write(decToBcd(second)); // set seconds
  Wire.write(decToBcd(minute)); // set minutes
  Wire.write(decToBcd(hour)); // set hours
  Wire.write(decToBcd(dayOfWeek)); // set day of week (1=Sunday, 7=Saturday)
  Wire.write(decToBcd(dayOfMonth)); // set date (1 to 31)
  Wire.write(decToBcd(month)); // set month
  Wire.write(decToBcd(year)); // set year (0 to 99)
  Wire.endTransmission();
}

void readDS3231time(byte *second,
byte *minute,
byte *hour,
byte *dayOfWeek,
byte *dayOfMonth,
byte *month,
byte *year) {
  Wire.beginTransmission(DS3231_I2C_ADDRESS);
  Wire.write(0); // set DS3231 register pointer to 00h
  Wire.endTransmission();
  Wire.requestFrom(DS3231_I2C_ADDRESS, 7);
  // request seven bytes of data from DS3231 starting from register 00h
  *second = bcdToDec(Wire.read() & 0x7f);
  *minute = bcdToDec(Wire.read());
  *hour = bcdToDec(Wire.read() & 0x3f);
  *dayOfWeek = bcdToDec(Wire.read());
  *dayOfMonth = bcdToDec(Wire.read());
  *month = bcdToDec(Wire.read());
  *year = bcdToDec(Wire.read());
}

void updateTime() {
  // retrieve data from DS3231
  readDS3231time(&second, &minute, &hour, &dayOfWeek, &dayOfMonth, &month,
  &year);
  // send it to the serial monitor
  Serial.print(hour, DEC);
  // convert the byte variable to a decimal number when displayed
  Serial.print(":");
  if (minute<10)
  {
    Serial.print("0");
  }
  Serial.print(minute, DEC);
  Serial.print(":");
  if (second<10)
  {
    Serial.print("0");
  }
  Serial.print(second, DEC);
  Serial.print(" ");
  Serial.print(dayOfMonth, DEC);
  Serial.print("/");
  Serial.print(month, DEC);
  Serial.print("/");
  Serial.print(year, DEC);
  Serial.print(" Day of week: ");
  switch(dayOfWeek){
  case 1:
    Serial.println("Sunday");
    break;
  case 2:
    Serial.println("Monday");
    break;
  case 3:
    Serial.println("Tuesday");
    break;
  case 4:
    Serial.println("Wednesday");
    break;
  case 5:
    Serial.println("Thursday");
    break;
  case 6:
    Serial.println("Friday");
    break;
  case 7:
    Serial.println("Saturday");
    break;
  }
}

boolean kaffetid() {
  for (int i = 0; i < sizeof(alarmer); i += 3) {
    if (dayOfWeek == alarmer[i] && hour == alarmer[i + 1] && minute == alarmer[i + 2] && second == 1) return true; 
  }
  return false;
}

void lagKaffe() {
  digitalWrite(KAFFELYS, HIGH);
  digitalWrite(KAFFE, LOW);
  coffeeTime = (hour * 60) + minute;
}

void avsluttKaffe() {
  if ((hour * 60) + minute == coffeeTime + 3) {
    digitalWrite(KAFFELYS, LOW);
    digitalWrite(KAFFE, HIGH);
    coffeeTime = -4;
  }
}

void loop()
{
  updateTime();
  if (kaffetid()) lagKaffe();
  avsluttKaffe();
  if(kolon) display.showNumberDecEx((hour * 100) + minute, 0b01000000);
  else display.showNumberDecEx((hour * 100) + minute);
  kolon = !kolon;
  delay(500);

  // Siden klokken går 92 sek for fort per dag:
  if (hour == 3 && minute == 0 && second == 0) {
    delay(93160);
    setDS3231time(second + 1, minute, hour, dayOfWeek, dayOfMonth, month, year);
  }
}

