
## Devices List
#### We are not selling any devices and have no connection with the providers on the list.

### Camera
The best devices we used is [`Dahua IPC-HFW4431R-Z`](https://www.amazon.com/gp/product/B073Q3N847/ref=oh_aui_detailpage_o02_s00?ie=UTF8&psc=1), it balanced price and quality very well. You can easily purchase it on Amazon.
With 2.8mm setup, the Face Recognition distance is 1-3m, if zoom the lens to 12mm, the Face Recognition distance will be 4-8m.

### Facebox hardware
We ported the system/software into RK3399 Set Top Box. Since it's less expensive. 
![screen shot 2018-09-25 at 3 47 03 pm](https://user-images.githubusercontent.com/3085564/46047524-45b9ad00-c0da-11e8-9f94-95490347614d.png)

There's heat problem with this setup due to hardware design(heat sink is located on wrong side), you need open box and setup a fans to keep it running overnight/weeks.

Just for example, [RK3399 STB](https://www.ebay.com/itm/H96-Max-4GB-32GB-Rockchip-RK3399-Six-Core-Android-7-1-TV-Box-Dual-WiFi-Player/322977652640?epid=26023719805&hash=item4b32f7dfa0:g:JU0AAOSwJ-5aTZxR)

Our image only support 4GB 32GB for now.

### POE injector

Most of them works. The Dahua camere is sold without power adapter so [POE injector](https://www.amazon.com/TP-LINK-TL-PoE150S-Injector-Adapter-compliant/dp/B001PS9E5I/ref=sr_1_3?s=electronics&ie=UTF8&qid=1537916196&sr=1-3&keywords=poe+injector) is needed.

## [How to burn firmware](https://github.com/SharpAI/facebox_sdk/tree/master/firmware)

