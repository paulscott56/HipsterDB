HipsterDB
=========

A simple BSON based key value store where keys are strings and values are BSON documents (translated from JSON)

Usage
========
Start the server
Open up a telnet session to localhost on port 11256
PUT something into the store with the format: command/key/value
put/1/{"name":"Paul", "surname":"Scott"}

GET the data back (format command/key):
get/1

Alternatively, run the local server and use in your Java projects!
