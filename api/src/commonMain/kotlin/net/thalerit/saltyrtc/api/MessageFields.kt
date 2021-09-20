package net.thalerit.saltyrtc.api

enum class MessageField {
    TYPE,
    KEY,
    YOUR_COOKIE,
    SUBPROTOCOLS,
    PING_INTERVAL,
    YOUR_KEY,
    SIGNED_KEYS,
    INITIATOR_CONNECTED,
    RESPONDERS,
    ID,
    REASON,
    TASKS,
    TASK,
    DATA,

    //relayed task
    P,
    ;

    companion object
}

