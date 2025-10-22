# System Design Concepts

## System Design Fundamentals

### Client Server Model

Source Code - TCPClientServer folder

#### Version 1
---------

A Simple TCP Client and Server
1. Server listens on PORT 8080
2. Client connects to Server on localhost:8080 or GCP VM 
3. Client send a message
4. Server receives it and displays on the console
5. Server close the socket
6. Client close the socket
7. TCP connection closed
8. JVM exit

#### Behind the Scenes

1. TCP 3-way hanshake happens : When the below client code executes, then the 3-way handshake happens

```code
    client.connect(new InetSocketAddress(LOCALHOST, PORT));
```
```text
Client (App)            Client Kernel          Server Kernel           Server (App)
   | connect()                |                      |                        |
   |----------------SYN------>|                      |                        |
   |                          |------SYN------------>|                        |
   |                          |<-----SYN-ACK---------|                        |
   |<-------SYN-ACK-----------|                      |                        |
   |---------ACK------------->|------ACK------------>|                        |
   | [Handshake complete]     | [Conn established]   | [Conn established]     |
   |----------------------DATA FLOW------------------------------------------>|
```
2. On server code, `server.setKeepAlive(true);` this keeps the TCP connection open




#### Version 2
---------

#### TCP Connection Lifecycle (Client ↔ Server)
```text
Client                  Network/Kernel                 Server
  | ---- SYN ------------> |                              |
  | <--- SYN-ACK --------- |                              |
  | ---- ACK ------------> |                              |
  |                        | <--- accept() returns Socket |
  |                        | --> Executor thread handles I/O
  |=====> data exchange via established TCP connection ===>|
  | ---- FIN ------------> |                              |
  | <--- ACK ------------- |                              |
  | <--- FIN ------------- |                              |
  | ---- ACK ------------> |                              |
```


## Network Protocols

Application Layer (L7)  : [User Space] HTTP, SSH (Hyper-Text Transfer Protocol) (HTTP is a spec and there are implementations like one in Java lib - HttpURLConnection).\
Transport Layer(L4)     : [Kernel] TCP, UDP (Transmission Control Protocol) (TCP is implemented in every OS Kernel on the Network stack). Assign a Port(source, destination) to the TCP Segments.\
Network Layer(L3)       : [Kernel] IP, ICMP, ARP (IP is implemented in every OS Kernel on the Network stack). Attaches the IP address to the above IP Packets and takes care of routing.\
Data Link Layer(L2)     : Softwares. All the network drivers used by the devices on phsycical layer are available. MAC Addressing and data is called Frames here.\
Physical Layer(L1)      : Hardwares. NIC, Ethernet cable or WiFi devices are used to trasmit the data. Data is sent as signals here.\

-> So every network-capable OS includes TCP/IP.\
-> User apps at Application Layer talk to TCP/IP through socket APIs (like socket(), send(), recv()).\
-> How to choose the protocol at Transport layer - based on the lib used. In Java, java.net package there are 2 classes available. Socket(client)/ServerSocket(server) for TCP and DatagramSocket/DatagramPacket for UDP.


Storage\
Latency & Throughput\
Availability\
Scalability\
Caching\
Proxies\
Load Balancers\
Hashing\
Relational Databases\
Key-Value Stores\
Specialized Storage Paradigms\
Replication and Sharding\
Leader Election\
Peer-to-Peer Networks\
Polling & Streaming\
Configuration\
Rate Limiting\
Logging and Monitoring\
Publish/Subscribe Pattern\
MapReduce\
Security & HTTPS\
API Design