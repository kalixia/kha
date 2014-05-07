# Support for Zibase Devices

This is a highly experimental and WIP module!


## Development notes

The Zibase API (Zapi2) documentation is available at:
[http://www.zodianet.com/zapi.html](http://www.zodianet.com/zapi.html)

There is a demo account quite handy for development and testing when you don't own a Zibase device:
simply use credentials ``` demo/demo ```.

Some few requests to run:

1. Obtain token for requesting services, from the login and password:
https://zibase.net/api/get/ZAPI.php?login=[your login]&password=[your password]&service=get&target=token
Response will be something like this:
```
{
“head”: “success”
“body”: {
“zibase” : "xxx",
"token" : "xxx",
}
}
```

2. Obtain information about all the devices attached to the Zibase:
https://zibase.net/api/get/ZAPI.php?zibase=[zibaseID]&token=[token]&service=get&target=home