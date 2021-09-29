# saltyrtc-client-kotlin-mpp

SaltyRTC is an end-2-end encrypted signalling protocol for WebRTC clients.

Kotlin multiplatform programming implementation of the SaltyRTC client.

# Status

| Feature                      | Common       | JVM          | Android |
| -------------                | -------------| -------------|------------- |
| Documentation                |              |              | |
| Server Authentication        |  Draft ready | Draft ready  | |
| Server Authentication - Error Handling|              |              | |
| Server Authentication - Unit Tests|              |              | |
| Client Authentication        |  Draft ready     | Draft ready      | |
| Client Authentication - Error Handling|              |              | |
| Data channel Chunking         |  Ongoing  |   | |
| Tasks                        |              |              | |
| Tasks - Relayed              |  Draft ready | Draft ready  | |
| Tasks - Relayed - Tests      |              |              | |
| Tasks - WebRTC               |  Ongoing     | Ongoing      | |
| Tasks - WebRTC - Tests       |              |              | |
| Tasks - ORTC                 |  Not planned | Not planned  | |

# Todos

* Public State for connection
* Continuous integration
* Other build targets (Js, iOS, Native ...)
* Unit Tests
* Test compatibility with other SaltyRTC clients
* Documentation
* Samples
* Publish in Maven repository

# Links

* [Kotlin MPP](https://kotlinlang.org/docs/reference/mpp-intro.html)
* [SaltyRTC Clients](https://saltyrtc.org/pages/implementations.html)
* [SaltyRTC Protocol](https://github.com/saltyrtc/saltyrtc-meta/blob/master/Protocol.md)
* [WebRTC](https://webrtc.org/getting-started/data-channels)
* [TrickleICE](https://webrtc.github.io/samples/src/content/peerconnection/trickle-ice/) This page tests the trickle ICE
  functionality in a WebRTC implementation.

# Usage [IN DEVELOPMENT]

## Open Signalling Channel

* Keys required
*

## WebRTC Task

### STUN and TURN servers

### Offer

### Answer

### ICE candidates

## Start server

* python3 -m pip install virtualenv
* python3 -m venv .env
* . .env/bin/activate.fish
* pip install saltyrtc.server
* pip install saltyrtc.server[logging]
* saltyrtc-server generate /path/to/permanent-key
* set -x SALTYRTC_SAFETY_OFF yes-and-i-know-what-im-doing
* saltyrtc-server -v7 -c serve -k keys/permanent-key -p 8765

## Start initiator / responder

* run jvmMain (SaltyRTCClient)

# TODO SaltyRTC – End-to-End-Encrypted Signalling

* https://github.com/saltyrtc/saltyrtc-meta/blob/master/Protocol.md

# TODO - Tasks

## Relayed Data

## WebRTC

## ORTC 


