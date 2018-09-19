require 'rubygems'
require 'mqtt'

# Subscribe example
MQTT::Client.connect('192.168.0.3') do |c|
  puts "connected"
  # If you pass a block to the get method, then it will loop
  c.get('rt_message') do |topic,message|
    puts "#{topic}: #{message}"
  end
end
