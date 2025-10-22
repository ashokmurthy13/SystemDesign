# System Design Concepts

## System Design Fundamentals

### Client Server Model

Source Code - TCPClientServer folder

#### Version 1 - Single Thread
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

**TCPServer Side**

```ServerSocket serverSocket = new ServerSocket(5000);```

When this line executes 3 things will happen  
  * bind() call will trigger to associate the socket with a specific IP address and port - happends on the Transport Layer on Server OS Kernel's Network stack. initially in TCP_CLOSE state; after listen(), transitions to TCP_LISTEN
  * listen() call will trigger - this creates 2 queues
    * SYN Queue (incomplete connections) to store connections with SYN received but 3-way handshake not completed - SYN_RECV state
    * ACCEPT Queue (estalished connections) to store connections with completed 3-way handshake waiting for accept()
    * socket moves from TCP_CLOSE to TCP_LISTEN state
    * Backlog parameter - sets maximum lenght for accept queue
  * Now the server socket's accept() is in blocking and waiting for connections from accept queue

```
  // Keep-alive is set on the ACCEPTED sockets (Client), not the listening ServerSocket
  Socket acceptedSocket = serverSocket.accept();
  acceptedSocket.setKeepAlive(true);
```
* if keep-alive set to ```true``` and the socket is ```not closed``` in the code
    * If the program is still running, then the TCP layer in the OS Kernel network stack will do the following
      * there is an idle time (2 hours in MacOS), after 2 hours it sends keep-alive probe
      * by default it sends 9 probes each at 75s interval
      * if there is no peer response, then the kernel closes socket connection and notifies the app, returns -1 or IOException
* if keep-alive set to ```true``` or ```false``` and the socket is ```closed``` or ```not closed``` in the code - ```serverSocket.close()```
    * Once the JVM exists, then the underlying TCP connection will be closed smoothly
    * ALL sockets are automatically closed by the OS (sends FIN/RST)
* if keep-alive set to ```false``` which is default and the socket is ```not closed``` in the code\
    * OS Kernel never send probes
    * the connection can remain "ESTABLISHED" in kernel forever if:
      * No FIN(= graceful close) or RST(abrupt close - when app exits ungracefully) is exchaged
      * No data is being send or received
    * this leads to following issues
      * Memeory Waste - TCP state, buffer, kernel structure remain allocated
      * Resource Leaks - file descriptors remain open (lsof would show it)
      * Hung Read/Writes - app can block indefinitely, waiting for peer that’s long gone

**PROD Tips**

* Explicit close in code (try-with-resources or finally),
* Keepalive enabled for safety,
* Application-level timeouts (heartbeats, idle timers).

**Keep-Alive Default Timings (Vary by OS)**
* Linux: Typically 2 hours idle, then 9 probes every 75 seconds
* Windows: 2 hours idle, variable retries
* macOS: Similar to Linux but configurable

**Key Points:**
* ServerSocket vs Socket: ServerSocket is for listening, Socket is for actual connections
* JVM Exit: Always triggers socket cleanup regardless of keep-alive settings
* Resource Leaks: Only happen when JVM keeps running but application doesn't close sockets
* Detection: Use netstat -an | grep ESTABLISHED or lsof -i TCP:5000 to find leaked connections


**TCPClient Side**

1. `server.setKeepAlive(true);` this keeps the TCP connection open - check above for all use cases.
2. TCP 3-way hanshake happens : When the below client code executes, then the 3-way handshake happens

```code
client.connect(new InetSocketAddress(LOCALHOST, PORT));
```
3. this triggers a system call
4. Client Kernel send SYN packet to server and this get added to the Server's SYN Queue. Now client is in SYN_SENT state
5. Server send SYN+ACK (received)
6. Client send ACK and the connection is established now and this is stored in Server's ACCEPT Queue
7. Client received connect()
8. Server ```accept()``` is in blocking state and ready to accept connections
9. Data can be exchanged

**Client State Transitions**

```CLOSED → connect() → SYN_SENT → (SYN-ACK received) → ESTABLISHED```

```text
Client (App)            Client Kernel          Server Kernel           Server (App)
   | connect()                |                      |                        |
   |--------system call------>|                      |                        |
   |                          |------SYN------------>|                        |
   |                          |<-----SYN-ACK---------|                        |
   |                          |------ACK------------>|                        |
   | connect() returns        |                      |                        |
   |                          |                      |                        |
   | [Handshake complete]     | [Conn established]   | [Conn established]     |
   |----------------------DATA EXCHANGE------------------------------------------>|
```

**Common Client-Side Issues:**
* Connection timeout: Server not responding to SYN
* Connection refused: No process listening on target port
* Firewall blocking: SYN packets being dropped
* Network unreachable: Cannot route to destination

#### Version 2
---------
// TODO


## Network Protocols

Application Layer (L7)  : [User Space] HTTP, SSH (Hyper-Text Transfer Protocol) (HTTP is a spec and there are implementations like one in Java lib - HttpURLConnection).

Transport Layer(L4)     : [Kernel] TCP, UDP (Transmission Control Protocol) (TCP is implemented in every OS Kernel on the Network stack). Assign a Port(source, destination) to the TCP Segments.

Network Layer(L3)       : [Kernel] IP, ICMP, ARP (IP is implemented in every OS Kernel on the Network stack). Attaches the IP address to the above IP Packets and takes care of routing.

Data Link Layer(L2)     : Softwares. All the network drivers used by the devices on phsycical layer are available. MAC Addressing and data is called Frames here.

Physical Layer(L1)      : Hardwares. NIC, Ethernet cable or WiFi devices are used to trasmit the data. Data is sent as signals here.


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