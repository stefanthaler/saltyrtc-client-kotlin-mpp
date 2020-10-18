# saltyrtc-client-kotlin-mpp
SaltyRTC is an end-2-end encrypted signalling protocol for WebRTC clients.

Kotlin multiplatform programming implementation of the SaltyRTC client. 



# Status 


| Feature                      | Common       | JVM          | JS |
| -------------                | -------------| -------------|------------- |
| Documentation                |              |              | |
| Server Authentication        |  Implemented | Implementated| |
| Server Authentication - Tests|              |              | |
| Client Authentication        |              |              | |
| Client Authentication - Tests|              |              | |
| Tasks                        |              |              | |
| Tasks - Tests                |              |              | |
| Tasks - Relayed              |              |              | |
| Tasks - Relayed - Tests      |              |              | |
| Tasks - WebRTC               |              |              | |
| Tasks - WebRTC - Tests       |              |              | |
| Tasks - ORTC                 |              |              | |
| Tasks - ORTC - Tests         |              |              | |

# Links
* [Kotlin MPP](https://kotlinlang.org/docs/reference/mpp-intro.html)
* [SaltyRTC Clients](https://saltyrtc.org/pages/implementations.html) 
* [SaltyRTC Protocol](https://github.com/saltyrtc/saltyrtc-meta/blob/master/Protocol.md) 

# Usage [IN DEVELOPMENT]


## Start server 
* set -x SALTYRTC_SAFETY_OFF yes-and-i-know-what-im-doing
* saltyrtc-server -v7 -c serve  -k keys/permanent-key -p 8765

## Start initiator / responder
* run jvmMain (SaltyRTCClient)

# TODO SaltyRTC – End-to-End-Encrypted Signalling
* https://github.com/saltyrtc/saltyrtc-meta/blob/master/Protocol.md
## Client2Server
* Path Cleaning
* 'drop-responder' Message
* 'disconnected' Message
* 'send-error' Message
## Client2Client
* 'token' Message
* 'key' Message
* 'auth' Message
* 'application' Message
* 'close' Message

## Other
* Unit Tests
* Other build targets (Android, JVM, JS, ...)

# TODO - Tasks
## Relayed Data 
## WebRTC 
## ORTC 


