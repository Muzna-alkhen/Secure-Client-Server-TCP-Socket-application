# Description:

The goal is to create a simple application similar to the work of a web server and browser, but instead of web content, the content will be text only.The system is built on a Server-Client Model, reliance on sockets based on TCP/IP connection, The server is Multi-Threading (that is, it can serve more than one client at the same time), So that the final results of the project support information security ideas, especially in the following aspects:

1.Confidentiality

2.Integrity

3.Non-Repudiation

4.Authentication, Authorization

5.Ensure that the person or server being contacted is really the person you want to communicate with
Avoid using weak encryption algorithms and methods

# Features:

- Implementing TCP connection (multi-threaded) every time a new client attempts to connect. 
- Implementing Symmetric encryption between clients and server.
- Implementing Asymmetric encryption between clients and server.
- Implementing Pretty Good Privacy (PGP) encryption between clients and server.
- Implementing  Digital Signature between clients and server.

# Tools:

**The project has been implemented in java SE using these libraries :**
 - java.net 
 - javax.crypto
 - java.security
 

