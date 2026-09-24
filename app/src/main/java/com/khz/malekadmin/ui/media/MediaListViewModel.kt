package com.khz.malekadmin.ui.media

import com.khz.malekadmin.data.repository.MediaRepository
import com.khz.malekadmin.domain.model.Media
import com.khz.malekadmin.ui.components.SimpleListViewModel

class MediaListViewModel(repo: MediaRepository) :
    SimpleListViewModel<Media>({ repo.getMedia() })