package net.thalerit.saltyrtc.core.entity

enum class ClientServerAuthState {
    UNAUTHENTICATED, // receive server-hello, send client-auth
    SERVER_AUTH, // receive server-auth
    AUTHENTICATED, // connected
}