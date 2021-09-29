package net.thalerit.saltyrtc.samples.android

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import net.thalerit.saltyrtc.samples.android.ui.theme.SampleSaltyRTCAndroidTheme
import net.thalerit.saltyrtc.samples.android.webrtc.KotlinPeerConnection
import net.thalerit.saltyrtc.samples.android.webrtc.createOffer
import net.thalerit.saltyrtc.samples.android.webrtc.kotlinPeerConnection
import net.thalerit.saltyrtc.samples.android.webrtc.setLocalDescription
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection

class MainActivity : ComponentActivity() {

    lateinit var wrapper: KotlinPeerConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initFactory(applicationContext as Application)

        val factory = peerConnectionFactory()
        val iceServers = listOf(
            PeerConnection.IceServer.builder("stun:stun.l.google.com:19302")
                .createIceServer()
        )

        wrapper = kotlinPeerConnection(factory, iceServers)

        lifecycleScope.launchWhenResumed {
            wrapper.signallingState.collect {
                l("$it")
            }
        }

        lifecycleScope.launchWhenCreated {
            val offer = wrapper.connection.createOffer(MediaConstraints())
            offer.onSuccess {
                val result = wrapper.connection.setLocalDescription(it)
                result.onSuccess {
                    // TODO signalling channel send local description
                }
            }
        }

        // TODO
        // signalling channel receive


        setContent {
            SampleSaltyRTCAndroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }


    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SampleSaltyRTCAndroidTheme {
        Greeting("Android")
    }
}