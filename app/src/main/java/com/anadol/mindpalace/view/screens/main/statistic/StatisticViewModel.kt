package com.anadol.mindpalace.view.screens.main.statistic

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anadol.mindpalace.data.statistic.GroupStatisticItem
import com.anadol.mindpalace.data.statistic.StatisticService
import com.anadolstudio.core.tasks.Result
import com.anadolstudio.core.tasks.RxTask

class StatisticViewModel : ViewModel() {

    var groupStatisticItems = MutableLiveData<Result<List<GroupStatisticItem>>>(Result.Empty())

    fun loadGroupStatisticItems(context: Context) {
        groupStatisticItems.value = Result.Loading()

        RxTask.Base.Quick { StatisticService.getGroupStatistic(context) }
            .onSuccess { result -> groupStatisticItems.value = Result.Success(result) }
            .onError { throwable -> groupStatisticItems.value = Result.Error(throwable) }
    }
}
