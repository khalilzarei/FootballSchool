package com.khz.malekadmin.ui.matches

import com.khz.malekadmin.data.repository.MatchRepository
import com.khz.malekadmin.domain.model.Match
import com.khz.malekadmin.ui.components.SimpleListViewModel

class MatchListViewModel(repo: MatchRepository) :
    SimpleListViewModel<Match>({ repo.getMatches() })