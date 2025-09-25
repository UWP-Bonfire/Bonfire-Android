package com.example.bonfire

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Message()
        }
    }
}

@Composable
fun Message() {
    Row(modifier = Modifier
        .padding(6.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.amaice_profile_picture),
            contentDescription = "Contact profile picture",
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .align(alignment = Alignment.CenterVertically)
        )
        Text(
            text = "amaice",
            modifier = Modifier
                .padding(6.dp, 0.dp, 0.dp, 0.dp)
                .align(alignment = Alignment.CenterVertically)
        )
    }
}

@Composable
fun CircleShape() {
    TODO("Not yet implemented")
}

@Preview
@Composable
fun PreviewMessageCard() {
    Message()
}