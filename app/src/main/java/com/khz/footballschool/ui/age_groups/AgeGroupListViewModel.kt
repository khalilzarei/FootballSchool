package com.khz.footballschool.ui.age_groups

import com.khz.footballschool.data.repository.AgeGroupRepository
import com.khz.footballschool.domain.model.AgeGroup
import com.khz.footballschool.ui.components.SimpleListViewModel

class AgeGroupListViewModel(repo: AgeGroupRepository) :
    SimpleListViewModel<AgeGroup>({ repo.getAgeGroups() })