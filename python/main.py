# -*- coding: utf-8 -*-
# @Author: rick0626
# @Date:   2018-11-21 14:26:17
# @Last Modified by:   rick0626
# @Last Modified time: 2018-11-21 16:06:08

from mqtt_Client import client
from threading import Timer, Lock
import time
import socket
import os

userId_time = dict()

# 过期时间 30s
EXPIRED_TIME = 30

lock = Lock()

def setValue(userId):
    global userId_time
    sign = None
    lock.acquire()
    if userId_time.get(userId):
        # 字典中存有当前用户， 查看时间是否过期
        # 过期更新时间，开门
        # 未过期，不作操作，不开门
        if time.time() - userId_time[userId] > EXPIRED_TIME:
            userId_time[userId] = time.time()
            sign = True
        else:
            sign = False
    else:
        userId_time[userId] = time.time()
        sign = True
    lock.release()
    return sign

# 以过期时间为周期，清理字典中的过期数据
def clean_dict():
    """
    与当前时间做对比，时间差超过过期时间则视为过期数据
    """
    global userId_time
    lock.acquire()
    if userId_time:
        for k in list(userId_time.keys()):
            if time.time() - userId_time[k] > EXPIRED_TIME:
                userId_time.pop(k)
    lock.release()
    t = Timer(EXPIRED_TIME, clean_dict)
    t.start()

if __name__ == '__main__':
    os.system("echo 0 > /sys/class/leds/ledblue/brightness")

    #clean_dict()

    # ip修改为盒子的地址
    client.connect('127.0.0.1', 1883, 60)

    # Blocking call that processes network traffic, dispatches callbacks and
    # handles reconnecting.
    # Other loop*() functions are available that give a threaded interface and a
    # manual interface.
    client.loop_forever()
