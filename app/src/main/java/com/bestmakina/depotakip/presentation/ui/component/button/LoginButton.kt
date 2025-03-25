import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign

@Composable
fun LoginButton(onClick: () -> Unit = {}) {
    ElevatedButton(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp).height(150.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
    ) {
        Text(
            text = "Giriş Yapmak İçin Tıklayınız",
            style = MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.primary),
            textAlign = TextAlign.Center
        )
    }
}
