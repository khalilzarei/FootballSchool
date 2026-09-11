package com.khz.footballschool.ui.matches

import com.khz.footballschool.data.repository.MatchRepository
import com.khz.footballschool.domain.model.Match
import com.khz.footballschool.ui.components.SimpleListViewModel

class MatchListViewModel(repo: MatchRepository) :
    SimpleListViewModel<Match>({ repo.getMatches() })