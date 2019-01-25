
## Install Dependencies

```
pip install -r requirements.txt
```

## Run the sample code

```
python3 main.py
```

### Known Person Event
```

{
  "status":"known person",
  "id": Recognized Person ID,
  "name" Known Person Name,
  "uuid": Facebox Device ID,
  "group_id": Group ID,
  "img_url": Recognized Face Image,
  "current_ts": Current timestamp,
  "accuracy": Accuracy,
  "fuzziness": Image Fuzziness Score
}
```

### sdk加入汪汪盒自动启动脚本
1. 在root下执行 cd /.cacheresource
2. vim deepeyeruntime
3. 修改如下：
```
function prepare()
{
   +echo 1 > /sys/class/leds/my-red-power/brightness
    start_avahi
    start_network
    start_watchdog

function deepeyeinit()
{
   -pushd ${RUNTIME}
   -YML=./docker-compose.yml
   +pushd /root/sharpai/docker
   +YML=./docker-compose-prebuilt.yml
    prepare

    if [ $1'x' == 'x' ];then

while [ 1 ]; do
    pushd ${RUNTIME}
  - ./do_update.sh
  - ./checkupdate.sh
  - check_reboot
  + #./do_update.sh
  + #./checkupdate.sh
  + #check_reboot
  +
  + pidsdk=$(ps aux | grep \[m]ain.py |  awk '{print $2}')
  + if [ 'x'$pidsdk = 'x' ];then
  +     echo "pid not found"
  +     python3 /root/facebox_sdk/python/main.py &
  + fi

    pidshell=$(ps aux | grep \[m]onitorrun |  awk '{print $2}')
    if [ 'x'$pidshell = 'x' ];then

```
4. reboot

### 控制闸机开门
1. 3399设备在/sys/class/leds/my-red/brightness写入1则输出12v高电平，写0则输出0v低电平
```
 os.system("echo 1 > /sys/class/leds/my-red/brightness")
 time.sleep(duration)
 os.system("echo 0 > /sys/class/leds/my-red/brightness")
```

2. 3288设备在/sys/class/leds/ledblue/brightness写入1则输出12v高电平，写0则输出0v低电平
```
os.system("echo 1 > /sys/class/leds/ledblue/brightness")
time.sleep(duration)
os.system("echo 0 > /sys/class/leds/ledblue/brightness")
```
