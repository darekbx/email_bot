package com.darekbx.emailbot.ui.emails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darekbx.emailbot.model.Email
import com.darekbx.emailbot.model.EmailContent

@Composable
fun ReportSpamDialog(email: Email, onSave: (from: String, subject: String) -> Unit, onDismiss: () -> Unit) {
    var fromField by remember { mutableStateOf(email.from) }
    var subjectField by remember { mutableStateOf(email.subject) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Report spam",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Use regex to define spam filter",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = fromField,
                    onValueChange = { fromField = it },
                    label = { Text("From") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = subjectField,
                    onValueChange = { subjectField = it },
                    label = { Text("Subject") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(fromField, subjectField) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun ReportSpamDialogPreview() {
    val mockEmail = EmailsViewModel.MOCK_EMAILS[0]
    ReportSpamDialog(
        email = mockEmail,
        onSave = { _, _ -> },
        onDismiss = { }
    )
}