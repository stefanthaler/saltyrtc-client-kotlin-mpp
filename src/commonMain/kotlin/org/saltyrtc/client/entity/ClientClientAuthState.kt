package org.saltyrtc.client.entity

enum class ClientClientAuthState {
    UNAUTHENTICATED, // receive token / key  as initiator, token or key as responder
    CLIENT_AUTH, // receive auth
    AUTHENTICATED, // connected
}