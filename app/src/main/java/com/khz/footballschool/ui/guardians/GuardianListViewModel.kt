package com.khz.footballschool.ui.guardians

import com.khz.footballschool.data.repository.GuardianRepository
import com.khz.footballschool.domain.model.Guardian
import com.khz.footballschool.ui.components.SimpleListViewModel

class GuardianListViewModel(repo: GuardianRepository) :
    SimpleListViewModel<Guardian>({ repo.getGuardians() })