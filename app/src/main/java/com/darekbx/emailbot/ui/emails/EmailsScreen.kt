package com.darekbx.emailbot.ui.emails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darekbx.emailbot.model.Email
import com.darekbx.emailbot.ui.ErrorView
import com.darekbx.emailbot.ui.ProgressView
import com.darekbx.emailbot.ui.theme.EmailBotTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun EmailsScreen(viewModel: EmailsViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var emailToReport by remember { mutableStateOf<Email?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchEmails()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is EmailsUiState.Idle -> { /* NOOP */ }
            is EmailsUiState.Loading -> ProgressView()
            is EmailsUiState.Error -> ErrorView(state.e) { viewModel.resetState() }
            is EmailsUiState.Success -> EmailsList(state.emails, onReportSpam = { emailToReport = it })
        }
    }

    emailToReport?.let { spamEmail ->
        ReportSpamDialog(
            email = spamEmail,
            onSave = { from, subject ->
                viewModel.reportSpam(from, subject)
                emailToReport = null
            },
            onDismiss = { emailToReport = null }
        )
    }
}

@Composable
private fun EmailsList(emails: List<Email>, onReportSpam: (Email) -> Unit = {}) {
    LazyColumn(Modifier.fillMaxWidth().padding(8.dp)) {
        itemsIndexed(items = emails) { index, email ->
            val backgroundColor = if (index % 2 == 0) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }

            EmailItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(
                        color = backgroundColor,
                        shape = MaterialTheme.shapes.small
                    ),
                email = email,
                onReportSpam = onReportSpam
            )
        }
    }
}

@Composable
private fun EmailItem(modifier: Modifier, email: Email, onReportSpam: (Email) -> Unit = {}) {
    var reportSpamClicked by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1F)) {
            Text(
                modifier = Modifier,
                text = email.dateTime,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                        )
                    ) {
                        append("From: ")
                    }
                    append(email.from)
                },
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                        )
                    ) {
                        append("To: ")
                    }
                    append(email.to)
                },
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = email.subject,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        if (email.isSpam) {
            Text(
                text = "Spam",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 4.dp).rotate(90F)
            )
        } else {
            Button(
                modifier = Modifier.fillMaxHeight().padding(start = 4.dp),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(8.dp),
                onClick = {
                    if (!reportSpamClicked) {
                        onReportSpam(email)
                    }
                    reportSpamClicked = true
                }
            ) {
                if (reportSpamClicked) {
                    Icon(
                        imageVector = Icons.Outlined.Done,
                        contentDescription = "spam",
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    SpamButton()
                }
            }
        }
    }
}

@Composable
private fun SpamButton() {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = Icons.Outlined.Warning, contentDescription = "spam")
        Text(
            text = "Report\nSpam",
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.absoluteOffset(y = 2.dp)
        )
    }
}

@Preview
@Composable
private fun SpamButtonPreview() {
    EmailBotTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            SpamButton()
        }
    }
}

@Preview
@Composable
fun EmailsScreenPreview() {
    EmailBotTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            EmailsList(EmailsViewModel.MOCK_EMAILS)
        }
    }
}
