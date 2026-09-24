package com.khz.malekadmin.ui.notifications

import com.khz.malekadmin.data.repository.NotificationRepository
import com.khz.malekadmin.domain.model.Notification
import com.khz.malekadmin.ui.components.SimpleListViewModel

class NotificationListViewModel(repo: NotificationRepository) :
    SimpleListViewModel<Notification>({ repo.getNotifications() })