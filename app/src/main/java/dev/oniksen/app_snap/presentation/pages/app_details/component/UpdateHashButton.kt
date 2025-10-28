package dev.oniksen.app_snap.presentation.pages.app_details.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun UpdateHashButton(
    modifier: Modifier = Modifier,
    updateLastScanHash: () -> Unit
) {
    OutlinedButton(
        modifier = modifier.wrapContentSize(),
        onClick = updateLastScanHash,
    ) {
        Row(
            modifier = Modifier.padding(ButtonDefaults.MediumContentPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.
                padding(end = ButtonDefaults.MediumIconSpacing),
                imageVector = Icons.Outlined.Save,
                contentDescription = null,
            )
            Text(
                text = "Сохранить изменения"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
private fun UpdateHashButton_Light() {
    MaterialExpressiveTheme {
        Surface {
            UpdateHashButton {}
        }
    }
}
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
private fun UpdateHashButton_Dark() {
    MaterialExpressiveTheme(darkColorScheme()) {
        Surface {
            UpdateHashButton {}
        }
    }
}