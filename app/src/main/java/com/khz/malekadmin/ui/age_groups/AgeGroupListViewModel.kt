package com.khz.malekadmin.ui.age_groups

import com.khz.malekadmin.data.repository.AgeGroupRepository
import com.khz.malekadmin.domain.model.AgeGroup
import com.khz.malekadmin.ui.components.SimpleListViewModel

class AgeGroupListViewModel(repo: AgeGroupRepository) :
    SimpleListViewModel<AgeGroup>({ repo.getAgeGroups() })