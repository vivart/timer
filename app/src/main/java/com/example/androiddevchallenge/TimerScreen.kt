/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay

@Composable
fun TimerScreen(modifier: Modifier = Modifier, timerViewModel: TimerViewModel = viewModel()) {
    val selectedTime by timerViewModel.selectedTime.observeAsState(0)
    var isTimeSelected by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isTimeSelected) {
            Timer(
                selectedTime = selectedTime,
                onAdjustSelected = { remainingTime ->
                    isTimeSelected = false
                    timerViewModel.updateTime(remainingTime)
                }
            )
        } else {
            EditTimer(selectedTime = selectedTime, onTimeSelected = { isTimeSelected = true })
        }
    }
}

@Composable
fun Timer(
    selectedTime: Long,
    onAdjustSelected: (Long) -> Unit,
) {
    val context = LocalContext.current
    var time by remember { mutableStateOf(selectedTime) }
    var active by remember { mutableStateOf(false) }

    LaunchedEffect(time, active) {
        if (time > 0 && active) {
            delay(1000)
            time -= 1
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Gray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = 1 - (time / selectedTime.toFloat()),
                modifier = Modifier.size(300.dp),
                strokeWidth = 10.dp
            )
            Text(
                text = time.toDisplayTime,
                style = TextStyle(fontSize = 50.sp),
                modifier = Modifier.padding(16.dp)
            )
        }
        if (time == 0L) {
            Text(text = stringResource(R.string.times_up), style = TextStyle(fontSize = 25.sp))
            active = false
            showNotification(context = context)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(onClick = { active = !active }, modifier = Modifier.padding(16.dp)) {
                if (active) {
                    Text(text = stringResource(R.string.stop))
                } else {
                    Text(text = stringResource(R.string.start))
                }
            }
            Button(
                onClick = { onAdjustSelected(time) },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = stringResource(R.string.adjust))
            }
        }
    }
}

@Composable
fun EditTimer(
    selectedTime: Long,
    onTimeSelected: () -> Unit,
    timerViewModel: TimerViewModel = viewModel(),
) {
    val editTime by timerViewModel.editTime.observeAsState("")
    Box(
        modifier = Modifier
            .padding(16.dp)
            .clip(CircleShape)
            .border(2.dp, Color.Gray, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = .0f,
            modifier = Modifier.size(300.dp),
            strokeWidth = 10.dp
        )
        Text(
            text = editTime,
            style = TextStyle(fontSize = 50.sp),
            modifier = Modifier.padding(16.dp)
        )
    }
    NumberPad(selectedTime = selectedTime, onTimeSelected = { onTimeSelected() })
}

@Composable
fun NumberPad(
    selectedTime: Long,
    timerViewModel: TimerViewModel = viewModel(),
    onTimeSelected: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (j in 1..9 step 3) {
            Row {
                for (i in j..j + 2) {
                    OutlinedButton(
                        onClick = { timerViewModel.add(i.toLong()) },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = i.toString(), style = TextStyle(fontSize = 30.sp))
                    }
                }
            }
        }

        Row {
            OutlinedButton(
                onClick = { timerViewModel.add(0) },
                modifier = Modifier.padding(8.dp),
            ) {
                Text(text = "0", style = TextStyle(fontSize = 30.sp))
            }
            OutlinedButton(
                onClick = { timerViewModel.remove() },
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_input_delete),
                    contentDescription = stringResource(
                        R.string.delete
                    ),
                    Modifier.size(40.dp)
                )
            }
        }
        if (selectedTime > 0) {
            Button(
                onClick = { onTimeSelected() },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = stringResource(R.string.select))
            }
        }
    }
}

val Long.toDisplayTime: String
    get() {
        val hour = this / 3600
        val minute = (this % 3600) / 60
        val second = this % 60
        return "${hour.toString().padStart(2, '0')} : ${
        minute.toString().padStart(2, '0')
        } : ${second.toString().padStart(2, '0')}"
    }
