#include <Keypad.h>
#include <Keyboard.h>
#include <U8g2lib.h>
#include <StringSplitter.h>
#include <EEPROM.h>

/***************
  键盘
***************/
U8G2_SSD1306_128X64_NONAME_F_HW_I2C u8g2(U8G2_R0, /* clock=*/ PB6, /* data=*/ PB7, /* reset=*/ U8X8_PIN_NONE);

const byte ROWS = 4;
const byte COLS = 4;
int display_page = 0;

char keys[ROWS][COLS] = {
  {'a', 'b', 'c', 'd'},
  {'e', 'f', 'g', 'h'},
  {'i', 'j', 'k', 'l'},
  {'m', 'n', 'o', 'p'}
};//建立二维数组，用于设置按键的输出字符

byte rowPins[ROWS] = {PA0, PA1, PA2, PA3}; //定义行引脚
byte colPins[COLS] = {PA4, PA5, PA6, PA7}; //定义列引脚
Keypad keypad = Keypad( makeKeymap(keys), rowPins, colPins, ROWS, COLS );//实例化Keypad 对象

/**********************
   快捷键
 **********************/

void shortcut_keys(char key_0, char key_1);

String special_key[3] = {"CTRL", "SHIFT", "ALT"};
int led = PC13;
String led_recv_status;
// RXD接PA9
// TXD接PA10

/**************
   显示软件名称
 **************/
void choose_sorft(char * sn)
{
  String str;
  String str_0, str_1, str_2;
  
  if (* sn == NULL){
    str = "WMWYT SK";
  }else{
    if(EEPROM.read((int) (*sn - 'a') * 3) >= KEY_LEFT_CTRL){
      str_0 = special_key[EEPROM.read((int) (*sn - 'a') * 3) - KEY_LEFT_CTRL];
    }else{
      str_0 = (char) EEPROM.read((int) (*sn - 'a') * 3);
    }

    if(EEPROM.read((int) (*sn - 'a') * 3 + 1) >= KEY_LEFT_CTRL){
      str_1 = special_key[EEPROM.read((int) (*sn - 'a') * 3 + 1) - KEY_LEFT_CTRL];
    }else{
      str_1 = (char) EEPROM.read((int)( *sn - 'a') * 3 + 1);
    }

    
    if(EEPROM.read((int) (*sn - 'a') * 3 + 2) >= KEY_LEFT_CTRL){
      str_2 = special_key[EEPROM.read((int) (*sn - 'a') * 3 + 2) - KEY_LEFT_CTRL];
    }else{
      str_2 = (char) EEPROM.read((int)( *sn - 'a') * 3 + 2);
    }
    
    str = str_0 + " " + str_1 + " " + str_2;
  }
  
  u8g2.clearBuffer();
  u8g2.setFont(u8g2_font_ncenB10_tr);
  u8g2.setCursor(0, 40);
  u8g2.print(str);
  u8g2.sendBuffer();
}

/*******************
   快捷键调用函数
 *******************/
void shortcut_keys(int add)
{
  if(EEPROM.read(add) != 0)
  Keyboard.press(EEPROM.read(add));
  
  if(EEPROM.read(add + 1) != 0)
  Keyboard.press(EEPROM.read(add + 1));
  
  if(EEPROM.read(add + 2) != 0)
  Keyboard.press(EEPROM.read(add + 2));
  
  delay(100);
  Keyboard.releaseAll();
}

void update_flash(int add, String item){
  Serial.println(item.length());
  if(item.length() > 1)
  {
    for(int i = 0; i < 3; i++)
    {
      if(special_key[i].compareTo(item) == 0)
      {
        EEPROM.update(add, KEY_LEFT_CTRL + i);
      }
    }
  }
  else
  {
    EEPROM.update(add, item.c_str()[0]);
  }
}

char * key_0 = NULL;
char * key_1 = NULL;
char * key_2 = NULL;
char key;

void setup() {
  // put your setup code here, to run once:
  pinMode(PC13, OUTPUT);
  u8g2.begin();
  u8g2.enableUTF8Print();
  Serial.begin(9600);
  Keyboard.begin();
  digitalWrite(PC13, HIGH);
  choose_sorft(NULL);
}

void loop() {
  digitalWrite(PC13, LOW);
  key = keypad.getKey();

  int add = 0;
  
  // put your main code here, to run repeatedly:
  if (Serial.available() > 0)
  {
    led_recv_status = Serial.readString();
    Serial.println(led_recv_status);

    if(led_recv_status.compareTo("clear\n") == 0){
      digitalWrite(PC13, HIGH);
      for (int i = 0 ; i < 47 ; i++) {
        EEPROM.write(i, 0);
      }
      digitalWrite(PC13, LOW);
    }
    else{
      StringSplitter *splitter = new StringSplitter(led_recv_status, ':', 5);  // new StringSplitter(string_to_split, delimiter, limit)
      int itemCount = splitter->getItemCount();
      Serial.println("Item count: " + String(itemCount));
      
      String item[4] = {"0", "0", "0", "0"};
      item[0] = splitter->getItemAtIndex(0);
      item[1] = splitter->getItemAtIndex(1);
      item[2] = splitter->getItemAtIndex(2);
      item[3] = splitter->getItemAtIndex(3);
      
      add = (item[0].c_str()[0] - 'a') * 3;
  
      update_flash(add, item[1]);
      update_flash(add + 1, item[2]);
      update_flash(add + 2, item[3]);
    }
  }
  
  if (key != NO_KEY)
  {
    choose_sorft(&key);
    switch(key){
      case 'a': shortcut_keys(0);  break;
      case 'b': shortcut_keys(3); break;
      case 'c': shortcut_keys(6); break;
      case 'd': shortcut_keys(9); break;
      case 'e': shortcut_keys(12); break;
      case 'f': shortcut_keys(15); break;
      case 'g': shortcut_keys(18); break;
      case 'h': shortcut_keys(21); break;
      case 'i': shortcut_keys(24); break;
      case 'j': shortcut_keys(27); break;
      case 'k': shortcut_keys(30); break;
      case 'l': shortcut_keys(33); break;
      case 'm': shortcut_keys(36); break;
      case 'n': shortcut_keys(39); break;
      case 'o': shortcut_keys(42); break;
      case 'p': shortcut_keys(45); break;
      /*for(int i = 0; i < 16; i++){
                  Serial.print(EEPROM.read(i * 3));
                  Serial.print("\t");
                  Serial.print(EEPROM.read(i * 3 + 1));
                  Serial.print("\t");
                  Serial.println(EEPROM.read(i * 3 + 2));
                }
                break;*/
    }
  }
}
