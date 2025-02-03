package com.example.batterychargechecker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.batterychargechecker.ui.theme.TextAlpha
import kotlin.math.roundToInt

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = viewModel()
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { MyTopBar(title = stringResource(R.string.app_name)) }
    ) { innerPadding ->
        Contents(
            viewModel = mainViewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyTopBar(
    title: String,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            Text(text = title)
        },
        modifier = modifier,
    )
}

@Composable
private fun Contents(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(dimensionResource(id = R.dimen.padding_large))
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large))
    ) {
        val monitorOn by viewModel.monitorOn.collectAsStateWithLifecycle()

        MySwitch(
            title = stringResource(id = R.string.monitor_charging),
            checked = monitorOn,
            onCheckedChange = { viewModel.setMonitorOn(it) }
        )

        val monitorLevel by viewModel.monitorLevel.collectAsStateWithLifecycle()

        MySlider(
            title = stringResource(id = R.string.monitor_level, monitorLevel),
            startLabel = stringResource(id = R.string.monitor_level_min),
            endLabel = stringResource(id = R.string.monitor_level_max),
            value = monitorLevel.toFloat(),
            onValueChange = { viewModel.setMonitorLevel(it.roundToInt()) },
            valueRange = 0f..100f,
            steps = 99,
        )

        val notificationDuration by viewModel.notificationDuration.collectAsStateWithLifecycle()

        MySlider(
            title = stringResource(R.string.notification_duration, notificationDuration),
            startLabel = stringResource(R.string.notification_duration_min),
            endLabel = stringResource(R.string.notification_duration_max),
            value = notificationDuration.toFloat(),
            onValueChange = { viewModel.setNotificationDuration(it.roundToInt()) },
            valueRange = 1f..10f,
            steps = 8,
        )

        val repeatCount by viewModel.repeatCount.collectAsStateWithLifecycle()

        MySlider(
            title = stringResource(R.string.repeat_count, repeatCount),
            startLabel = stringResource(R.string.repeat_count_min),
            endLabel = stringResource(R.string.repeat_count_max),
            value = repeatCount.toFloat(),
            onValueChange = { viewModel.setRepeatCount(it.roundToInt()) },
            valueRange = 1f..10f,
            steps = 8,
        )

        val repeatInterval by viewModel.repeatInterval.collectAsStateWithLifecycle()

        if (1 < repeatCount) {
            MySlider(
                title = stringResource(R.string.repeat_interval, repeatInterval),
                startLabel = stringResource(R.string.repeat_interval_min),
                endLabel = stringResource(R.string.repeat_interval_max),
                value = repeatInterval.toFloat(),
                onValueChange = { viewModel.setRepeatInterval(it.roundToInt()) },
                valueRange = 1f..10f,
                steps = 8,
            )
        }
    }
}

@Composable
private fun MySwitch(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MySlider(
    title: String,
    startLabel: String,
    endLabel: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        // slider の左右のラベル
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = startLabel,
                modifier = Modifier.alpha(TextAlpha),
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = endLabel,
                modifier = Modifier.alpha(TextAlpha),
                style = MaterialTheme.typography.titleSmall
            )
        }
        Slider(
            value = value,
            onValueChange = {
                onValueChange(it)
            },
            modifier = Modifier.fillMaxWidth(),
            valueRange = valueRange,
            steps = steps,
            track = { sliderState ->
                SliderDefaults.Track(
                    sliderState = SliderState(
                        value = sliderState.value,
                        steps = 0, // no ticks
                        onValueChangeFinished = sliderState.onValueChangeFinished,
                        valueRange = sliderState.valueRange
                    )
                )
            }
        )
    }
}
