# Windcal

Windcal is a bridge between api.therainery.com and ical. 
So you can have your weather forecast in your favorite Calendar-App. It can be filtered with < and > over all Float-Fields

For example if you like to kite on the Tempelhofer Feld in Berlin. You prefer abeam reach. 
You like to see only winds between 7 and 27.9 kts in you Calendar. 
Deploy this on heroku [![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy) and subscribe to 
```
https://[your app].herokuapp.com/wind.ics?latlng=52.47301625304697,13.399166578830597&bestWindDirection=0,180&filter=wind%3E6.9,wind%3C28
```
