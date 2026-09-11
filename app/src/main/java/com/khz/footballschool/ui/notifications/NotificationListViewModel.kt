package com.khz.footballschool.ui.notifications

import com.khz.footballschool.data.repository.NotificationRepository
import com.khz.footballschool.domain.model.Notification
import com.khz.footballschool.ui.components.SimpleListViewModel

class NotificationListViewModel(repo: NotificationRepository) :
    SimpleListViewModel<Notification>({ repo.getNotifications() })