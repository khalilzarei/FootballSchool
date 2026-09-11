package com.khz.footballschool.ui.coaches

import com.khz.footballschool.data.repository.CoachRepository
import com.khz.footballschool.domain.model.Coach
import com.khz.footballschool.ui.components.SimpleListViewModel

class CoachListViewModel(repo: CoachRepository) :
    SimpleListViewModel<Coach>({ repo.getCoaches() })