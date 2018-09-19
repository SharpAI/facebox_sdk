var mqtt = require('mqtt')
var client  = mqtt.connect('mqtt://192.168.0.3')
 
client.on('connect', function () {
  client.subscribe('rt_message', function (err) {
    if (!err) {
      client.publish('presence', 'Hello mqtt')
    }
  })
})
 
client.on('message', function (topic, message) {
  // message is Buffer
  console.log(message.toString())
})
