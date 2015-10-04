# File transfer system 
&nbsp;&nbsp;&nbsp;This is a client-server file transfer system using Java's Socket API. The
server is started on a specific port number (using TCP) and
listens for requests. The client is started with the IP address and port number of the server. It sends requests to the server. The server handles the requests and returns replies to the client. The client handles the replies..

## Version
&nbsp;&nbsp;&nbsp;&nbsp; 1.2.0

## Compiling
- To compile the server: 
  ```sh
  $ javac TCPServer.java 
  ```
- To compile the client: 
  ```sh
  $ javac TCPClient.java 
  ```


## Usage
- ### Running
  1. Start the server: 

     ```sh
     $ java TCPServer <PORT_NUMBER> 
     ```
  2. Connect the client to the server:
  
     ```sh
     $ java TCPClient <SERVER_IP_ADDRESS> <SERVER_PORT_NUMBER> 
     ``` 

- ### Commands 
  These Commands can only be used from the client.
  - ###### Print on the client window a listing of the contents of the current directory on the server machine:

    ``` $ client> ls``` 
  - ###### Retrieve a remote file on the server and store it on the client machine. It is given the same name it has on the server machine:

     ``` $ client> get <SERVER_FILE_NAME>``` 
  - ###### Put and store the file from the client machine to the server machine. On the server, it is given the same name it has on the client machine:
 
    ``` $ client> put <CLIENT_FILE_NAME>```   
  - ###### Change the directory on the server:
 
    ``` $ client> cd <DIRECTORY_NAME>```  
  - ###### Create a new sub-directory named DIRECTORY_NAME:
 
    ``` $ client> mkdir <DIRECTORY_NAME>```  


## Author
&nbsp;&nbsp;&nbsp;&nbsp; Abdulrahman Mufti

## Website
&nbsp;&nbsp;&nbsp;&nbsp; [www.aboodmufti.com](http://www.aboodmufti.com)

## License

&nbsp;&nbsp;&nbsp;&nbsp; MIT



