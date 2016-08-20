import serial
import json
import requests
import time
import paho.mqtt.client as mqtt
from datetime import datetime

ser = serial.Serial('COM5', 115200)
client = mqtt.Client()
client.connect("test.mosquitto.org", 1883)
while True:
  try:
    txt = ser.readline()
    print(txt)
    serialData = json.loads(txt.decode("utf-8"))
  except Exception as ex:
    print(ex)
    continue
    sendData = { 'value':serialData['distancia'], 'timestamp': datetime.now().strftime('%Y-%m-%dT%H:%M:%S.%f') + '-03:00' }
    print (sendData)
    client.publish("iot-univem/iotime/ultrasom", json.dumps(sendData))