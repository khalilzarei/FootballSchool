package com.khz.footballschool.ui.news

import com.khz.footballschool.data.repository.NewsRepository
import com.khz.footballschool.domain.model.News
import com.khz.footballschool.ui.components.SimpleListViewModel

class NewsListViewModel(repo: NewsRepository) :
    SimpleListViewModel<News>({ repo.getNews() })