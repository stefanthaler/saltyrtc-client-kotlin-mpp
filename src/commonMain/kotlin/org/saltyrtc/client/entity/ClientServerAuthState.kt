package org.saltyrtc.client.entity

enum class ClientServerAuthState {
    DISCONNECTED, // receive server-hello, send client-auth
    SERVER_AUTH, // receive server-auth
    CONNECTED, // connected
}