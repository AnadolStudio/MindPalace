package com.anadol.mindpalace.view.screens.main.statistic

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.anadol.mindpalace.R
import com.anadol.mindpalace.data.statistic.GroupStatisticItem
import com.anadol.mindpalace.databinding.FragmentStatisticBinding
import com.anadol.mindpalace.view.screens.BaseFragment
import com.anadol.mindpalace.view.screens.main.statistic.charts.StatisticPieChart
import com.anadol.mindpalace.view.screens.main.statistic.charts.StatisticPieChartData
import com.anadolstudio.core.tasks.Result
import com.anadolstudio.core.viewbinding.viewBinding

class StatisticFragment : BaseFragment(R.layout.fragment_statistic) {

    companion object {
        fun newInstance(): StatisticFragment = StatisticFragment()
    }

    private val viewModel: StatisticViewModel by viewModels()
    private val binding by viewBinding { FragmentStatisticBinding.bind(requireView()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.pieChart.apply {
            StatisticPieChart.init(
                context = requireContext(),
                pieChart = this,
                primaryTextColor = ContextCompat.getColor(requireContext(), R.color.colorPrimaryText)
            )
        }

        viewModel.groupStatisticItems.observe(viewLifecycleOwner) { result ->
            showLoading(result is Result.Loading)

            when (result) {
                is Result.Success -> updatePieChart(result.data)
                is Result.Empty -> updatePieChart()
                is Result.Error -> result.error.printStackTrace()
                else -> {}
            }
        }
    }

    private fun updatePieChart(data: List<GroupStatisticItem>? = null) {
        var needToLearn = 0F
        var learning = 0F
        var learned = 0F

        data?.let {
            for (group in data) {
                needToLearn += group.needToLearn
                learning += group.learning
                learned += group.learned
            }
        }

        StatisticPieChart.setData(
            StatisticPieChartData(
                list = listOf(needToLearn, learning, learned),
                labels = getLabels(),
                labelColors = getLabelColors()
            ),
            binding.pieChart
        )
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadGroupStatisticItems(requireContext())
    }

    private fun getLabels(): List<String> = listOf(
        getString(R.string.not_learned),
        getString(R.string.learning),
        getString(R.string.learned)
    )

    private fun getLabelColors(): List<Int> = listOf(
        ContextCompat.getColor(requireContext(), R.color.colorNeedToLearn),
        ContextCompat.getColor(requireContext(), R.color.colorLearning),
        ContextCompat.getColor(requireContext(), R.color.colorLearned)
    )
}
