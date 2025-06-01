package com.darekbx.emailbot

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.darekbx.emailbot.ui.theme.EmailBotTheme
import com.darekbx.emailbot.navigation.AppNavHost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.KoinAndroidContext
import java.util.Properties
import javax.mail.Folder
import javax.mail.Session

/**
 * Email Bot
 *
 * TODO:
 *  - Configuration screen where user will configure email account
 *  - Fetch only title, email, date
 *  - Database where will be stored filters
 *  - Service with configured interval (1h/2h/4h/8h) which will check emails, and remove spam
 *  - Notification when flow was completed with summary (how many mails, how many spam, how many important)
 *  - Store in database latest emails, which need to check if they are spam
 *  - Notify/highlight about important emails ( android newsletter, kalejdoskop, unknown news and others)
 *
 *  - Integrations
 *    - Kalejdoskop newsletter
 *    - Infinite Android newsletter
 *    - Unknown news newsletter
*/
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            KoinAndroidContext {
                val navController = rememberNavController()
                EmailBotTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        AppNavHost(navController, modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

@Composable
private fun Test(innerPadding: PaddingValues) {
    Button(modifier = Modifier.padding(innerPadding), onClick = {

        try {

            CoroutineScope(Dispatchers.IO).launch {
                val props = Properties().apply {
                    put("mail.imap.host", "imap.poczta.onet.pl")
                    put("mail.imap.port", "993")
                    put("mail.imap.ssl.enable", "true")
                }

                val session = Session.getInstance(props, null)
                val store = session.getStore("imap")
                store.connect("pwszbp@op.pl", "")

                val folder = store.getFolder("INBOX")
                folder.open(Folder.READ_ONLY)

                val messages = folder.messages.takeLast(10)
                val count = messages.size
                Log.d("sigma", "Number of messages: $count")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }) { Text("Test") }
}
