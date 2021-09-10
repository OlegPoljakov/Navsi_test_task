# Navsi_test_task

The task is to create a client/server program that uses Spring. Client has to send binary data package consisted of four fields:  
npp - number of coordinate;  
id - number of client;    
lat - latitude;  
lng - longitude;

All packets should be stored in an array or in any other structure and must be sent each 0.5 seconds. Server has to accept more than one connecton, process the received data and store that data in an object that could be used later. It will be plus to add CRC (checksum), tha server answer that the package has been received succesfully and logs.

To start the program press "Run" on server main method and then press "Run" on client server main method.
