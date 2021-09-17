# saltyrtc-client-kotlin-mpp

SaltyRTC is an end-2-end encrypted signalling protocol for WebRTC clients.

Kotlin multiplatform programming implementation of the SaltyRTC client.

# Status

| Feature                      | Common       | JVM          | JS |
| -------------                | -------------| -------------|------------- |
| Documentation                |              |              | |
| Server Authentication        |  Draft ready | Draft ready  | |
| Server Authentication - Error Handling|              |              | |
| Server Authentication - Unit Tests|              |              | |
| Client Authentication        |  Draft ready     | Draft ready      | |
| Client Authentication - Error Handling|              |              | |
| Client Authentication - Unit Tests |              |              | |
| Tasks                        |              |              | |
| Tasks - Relayed              |  Ongoing     |              | |
| Tasks - Relayed - Tests      |              |              | |
| Tasks - WebRTC               |              |              | |
| Tasks - WebRTC - Tests       |              |              | |
| Tasks - ORTC                 |              |              | |
| Tasks - ORTC - Tests         |              |              | |

# Todos

* Unit Tests
* Other build targets (Android, JVM, JS, ...)
* Support multiple WebSocket clients (Ktor, other?)
* Test compatibility with other SaltyRTC clients
* Refactor tasks as modules

# Links

* [Kotlin MPP](https://kotlinlang.org/docs/reference/mpp-intro.html)
* [SaltyRTC Clients](https://saltyrtc.org/pages/implementations.html)
* [SaltyRTC Protocol](https://github.com/saltyrtc/saltyrtc-meta/blob/master/Protocol.md)

# Usage [IN DEVELOPMENT]

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


