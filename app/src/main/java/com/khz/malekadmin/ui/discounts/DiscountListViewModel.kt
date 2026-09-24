package com.khz.malekadmin.ui.discounts

import com.khz.malekadmin.data.repository.DiscountRepository
import com.khz.malekadmin.domain.model.Discount
import com.khz.malekadmin.ui.components.SimpleListViewModel

class DiscountListViewModel(repo: DiscountRepository) :
    SimpleListViewModel<Discount>({ repo.getDiscounts() })