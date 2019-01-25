# -*- coding: utf-8 -*-

import paho.mqtt.client as mqtt
import time
import json
import requests
import os

# gate control as inputing 1, otherwise inputing 0.
def switch_control(duration=1):
    try:
        os.system("echo 1 > /sys/class/leds/ledblue/brightness")
        time.sleep(duration)
        os.system("echo 0 > /sys/class/leds/ledblue/brightness")
    except Exception as e:
        print(e)
    finally:
        os.system("echo 0 > /sys/class/leds/ledblue/brightness")

        
# The callback for when the client receives a CONNACK response from the server.
def on_connect(client, userdata, flags, rc):
    print("Connected with result code " + str(rc))

    # Subscribing in on_connect() means that if we lose the connection and
    # reconnect then subscriptions will be renewed.
    client.subscribe("rt_message")

    
# The callback for when a PUBLISH message is received from the server.
def on_message(client, userdata, msg):
    print("-" * 10)
    # print(msg.topic+" "+str(msg.payload))
    try:
        msg_dict = json.loads(msg.payload.decode())
        # print(msg_dict)
        # for k, v in msg_dict.items():
        #     print(k, v)
        status = msg_dict.get("status")
        persons = msg_dict.get("persons")
        # person_id = msg_dict.get("person_id")
        # group_id = msg_dict.get("persons")[0].get("group_id")
        # print(status, person_id, group_id)
        
        # call switch_control when receiving known_person msg.
        if status == "known person":
            switch_control()

            # for person in persons:
            #     person_id = person.get("id")
            #     group_id = person.get("group_id")

            #     # url = "http://workaihost.tiegushi.com/restapi/get_name_by_faceid?group_id={}&face_id={}".format(group_id, person_id)
            #     # response = requests.get(url)
            #     # name = json.loads(response.text).get("name")
            #     # person["name"] = name
            #     person["status"] = "known person"

            # call setValue() to set up duration for one person controlling the gate
            #     #from main import setValue
            #     #sign = setValue(person_id)
            #     #if sign:
            #     print("*****", name, time.strftime("%Y-%m-%d %H:%M:%S", time.localtime(person["current_ts"]/1000)))
            #     print(msg.topic+" "+json.dumps(person))
            #     switch_control()
        else:
            print(msg.topic + " " + json.dumps(msg_dict))
    except Exception as e:
        print(e)


client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message

if __name__ == "__main__":
    # ip修改为盒子的地址
    client.connect("127.0.0.1", 1883, 60)

    # Blocking call that processes network traffic, dispatches callbacks and
    # handles reconnecting.
    # Other loop*() functions are available that give a threaded interface and a
    # manual interface.
    client.loop_forever()
