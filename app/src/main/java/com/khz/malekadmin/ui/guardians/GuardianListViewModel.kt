package com.khz.malekadmin.ui.guardians

import com.khz.malekadmin.data.repository.GuardianRepository
import com.khz.malekadmin.domain.model.Guardian
import com.khz.malekadmin.ui.components.SimpleListViewModel

class GuardianListViewModel(repo: GuardianRepository) :
    SimpleListViewModel<Guardian>({ repo.getGuardians() })