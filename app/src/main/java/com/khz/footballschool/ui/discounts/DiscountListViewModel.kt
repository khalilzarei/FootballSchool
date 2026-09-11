package com.khz.footballschool.ui.discounts

import com.khz.footballschool.data.repository.DiscountRepository
import com.khz.footballschool.domain.model.Discount
import com.khz.footballschool.ui.components.SimpleListViewModel

class DiscountListViewModel(repo: DiscountRepository) :
    SimpleListViewModel<Discount>({ repo.getDiscounts() })