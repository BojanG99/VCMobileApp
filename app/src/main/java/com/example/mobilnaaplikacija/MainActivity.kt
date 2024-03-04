package com.example.mobilnaaplikacija



import android.content.Intent
import android.os.Bundle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.activity.ComponentActivity
import com.example.mobilnaaplikacija.ui.LogginActivity
import com.example.mobilnaaplikacija.ui.WelcomeActivity
import com.example.mobilnaaplikacija.ui.theme.MobilnaAplikacijaTheme
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.File
import java.security.Security


class MainActivity : ComponentActivity(){
    companion object {
        init {
            Security.removeProvider("BC") //remove old/legacy Android-provided BC provider
            Security.addProvider(BouncyCastleProvider()) // add 'real'/correct BC provider
        }
        const val KEYS_FILE = "privateKeys.enc"
        const val DID_FILE = "did.txt"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(isSignedIn()){
            val intent = Intent(this, LogginActivity::class.java)
            startActivity(intent)
        }
        else{
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
        }
        finish()

    }
    private fun isSignedIn():Boolean{

        val keysFile = File(filesDir,KEYS_FILE)
        val didFile = File(filesDir,DID_FILE)

        return keysFile.exists() && didFile.exists()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MobilnaAplikacijaTheme {
        Greeting("Android")
    }
}