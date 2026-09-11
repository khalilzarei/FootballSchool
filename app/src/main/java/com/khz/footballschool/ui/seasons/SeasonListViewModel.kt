package com.khz.footballschool.ui.seasons

import com.khz.footballschool.data.repository.SeasonRepository
import com.khz.footballschool.domain.model.Season
import com.khz.footballschool.ui.components.SimpleListViewModel

class SeasonListViewModel(repo: SeasonRepository) :
    SimpleListViewModel<Season>({ repo.getSeasons() })