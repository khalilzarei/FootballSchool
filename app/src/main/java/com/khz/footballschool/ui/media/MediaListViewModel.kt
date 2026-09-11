package com.khz.footballschool.ui.media

import com.khz.footballschool.data.repository.MediaRepository
import com.khz.footballschool.domain.model.Media
import com.khz.footballschool.ui.components.SimpleListViewModel

class MediaListViewModel(repo: MediaRepository) :
    SimpleListViewModel<Media>({ repo.getMedia() })