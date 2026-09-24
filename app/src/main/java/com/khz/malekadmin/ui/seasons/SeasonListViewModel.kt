package com.khz.malekadmin.ui.seasons

import com.khz.malekadmin.data.repository.SeasonRepository
import com.khz.malekadmin.domain.model.Season
import com.khz.malekadmin.ui.components.SimpleListViewModel

class SeasonListViewModel(repo: SeasonRepository) :
    SimpleListViewModel<Season>({ repo.getSeasons() })