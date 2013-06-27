# Create a user 'jonhdoe'
curl -i -H "Accept: application/json" -X POST -d "{ username: 'johndoe', email: 'john@doe.com', firstName: 'John', lastName: 'Doe' }" http://localhost:8082
# Check if it can be found
curl -i -H "Accept: application/json" http://localhost:8082/johndoe

# Create a device name 'test' for 'johndoe'
curl -i -H "Accept: application/json" -X POST -d "{name: 'test', type: 'RGBLamp' }" http://localhost:8082/johndoe/devices
# Check if it can be found via 'johndoe' list of devices
curl -i -H "Accept: application/json" http://localhost:8082/johndoe/devices
# Check if it can be found via it's name
curl -i -H "Accept: application/json" http://localhost:8082/johndoe/devices/test

curl -i -H "Accept: application/json" -X POST -d "{name: 'temperature', unit: '℃' }" http://localhost:8082/johndoe/devices/sensors