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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import kotlin.math.roundToInt

@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { MyTopBar(title = stringResource(R.string.app_name)) }
    ) { innerPadding ->
        Contents(modifier = Modifier.padding(innerPadding))
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(dimensionResource(id = R.dimen.padding_large))
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large))
    ) {
        var monitorOn by remember { mutableStateOf(false) }

        MySwitch(
            title = stringResource(id = R.string.monitor_charging),
            checked = monitorOn,
            onCheckedChange = { monitorOn = it }
        )

        var monitorLevel by remember { mutableIntStateOf(0) }

        MySlider(
            title = stringResource(id = R.string.monitor_level, monitorLevel),
            startLabel = stringResource(id = R.string.monitor_level_min),
            endLabel = stringResource(id = R.string.monitor_level_max),
            value = monitorLevel.toFloat(),
            onValueChange = { monitorLevel = it.roundToInt() },
            valueRange = 0f..100f,
            steps = 99,
        )

        var notificationDuration by remember { mutableIntStateOf(5) }

        MySlider(
            title = stringResource(R.string.notification_duration, notificationDuration),
            startLabel = stringResource(R.string.notification_duration_min),
            endLabel = stringResource(R.string.notification_duration_max),
            value = notificationDuration.toFloat(),
            onValueChange = { notificationDuration = it.roundToInt() },
            valueRange = 1f..10f,
            steps = 8,
        )

        var repeatCount by remember { mutableIntStateOf(5) }

        MySlider(
            title = stringResource(R.string.repeat_count, repeatCount),
            startLabel = stringResource(R.string.repeat_count_min),
            endLabel = stringResource(R.string.repeat_count_max),
            value = repeatCount.toFloat(),
            onValueChange = { repeatCount = it.roundToInt() },
            valueRange = 1f..10f,
            steps = 8,
        )

        var repeatInterval by remember { mutableIntStateOf(1) }

        if (1 < repeatCount) {
            MySlider(
                title = stringResource(R.string.repeat_interval, repeatInterval),
                startLabel = stringResource(R.string.repeat_interval_min),
                endLabel = stringResource(R.string.repeat_interval_max),
                value = repeatInterval.toFloat(),
                onValueChange = { repeatInterval = it.roundToInt() },
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
            Text(text = startLabel, style = MaterialTheme.typography.titleSmall)
            Text(text = endLabel, style = MaterialTheme.typography.titleSmall)
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
