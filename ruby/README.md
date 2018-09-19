## Install Dependencies

```
gem install mqtt
```

## Change the IP address to your facebox

```
MQTT::Client.connect('your facebox ip') do |c|
```

## Run the sample code

```
ruby example.rb
```
