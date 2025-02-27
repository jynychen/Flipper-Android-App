package com.flipperdevices.filemanager.listing.impl.composable.appbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import flipperapp.components.core.ui.res.generated.resources.Res
import flipperapp.components.core.ui.res.generated.resources.material_ic_close
import flipperapp.components.filemngr.listing.impl.generated.resources.fml_selection_deselect_all
import flipperapp.components.filemngr.listing.impl.generated.resources.fml_selection_select_all
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import flipperapp.components.filemngr.listing.impl.generated.resources.Res as FML

@Composable
fun CloseSelectionAppBar(
    onClose: () -> Unit,
    onSelectAll: () -> Unit,
    onDeselectAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    OrangeAppBar(
        startBlock = {
            Icon(
                modifier = modifier
                    .padding(top = 11.dp, bottom = 11.dp, start = 16.dp, end = 2.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .clickableRipple(onClick = onClose),
                painter = painterResource(Res.drawable.material_ic_close),
                contentDescription = null,
                tint = LocalPallet.current.onAppBar
            )
        },
        endBlock = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 14.dp,
                        end = 14.dp,
                        top = 8.dp,
                        bottom = 11.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End)
            ) {
                Text(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickableRipple(onClick = onSelectAll),
                    text = stringResource(FML.string.fml_selection_select_all),
                    style = LocalTypography.current.bodyM14,
                    color = LocalPallet.current.onAppBar,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickableRipple(onClick = onDeselectAll),
                    text = stringResource(FML.string.fml_selection_deselect_all),
                    style = LocalTypography.current.bodyM14,
                    color = LocalPallet.current.onAppBar,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    )
}
