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

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class TimerViewModel : ViewModel() {
    private val timeAsList = MutableLiveData(listOf(0L, 0L, 0L, 0L, 0L, 0L))
    val editTime =
        Transformations.map(timeAsList) { "${it[0]}${it[1]} : ${it[2]}${it[3]} : ${it[4]}${it[5]}" }
    var selectedTime =
        Transformations.map(timeAsList) { (it[0] * 10 + it[1]) * 3600 + (it[2] * 10 + it[3]) * 60 + it[4] * 10 + it[5] }

    fun add(number: Long) {
        val temp = timeAsList.value?.toMutableList()
        temp?.removeFirst()
        temp?.add(number)
        timeAsList.value = temp
    }

    fun remove() {
        val temp = timeAsList.value?.toMutableList()
        temp?.removeLast()
        temp?.add(0, 0)
        timeAsList.value = temp
    }

    fun updateTime(time: Long) {
        val hour = time / 3600
        val minute = (time % 3600) / 60
        val second = time % 60
        timeAsList.value =
            listOf(hour / 10, hour % 10, minute / 10, minute % 10, second / 10, second % 10)
    }
}
