/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.notifications.preferences.internal;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.WikiReference;
import org.xwiki.notifications.NotificationException;
import org.xwiki.notifications.preferences.NotificationPreference;
import org.xwiki.notifications.preferences.NotificationPreferenceProvider;
import org.xwiki.wiki.descriptor.WikiDescriptorManager;

/**
 * This implementation of {@link NotificationPreferenceProvider} handles preferences stored at the wiki level, to
 * provide default preferences for all users of that wiki.
 *
 * @version $Id$
 * @since 10.2RC1
 * @since 9.11.3
 */
@Component
@Singleton
@Named(WikiNotificationPreferenceProvider.NAME)
public class WikiNotificationPreferenceProvider extends AbstractDocumentNotificationPreferenceProvider
{
    /**
     * The name of the provider.
     */
    public static final String NAME = "wiki";

    @Inject
    private WikiDescriptorManager wikiDescriptorManager;

    @Override
    public int getProviderPriority()
    {
        return 100;
    }

    @Override
    public List<NotificationPreference> getPreferencesForUser(DocumentReference user)
            throws NotificationException
    {
        return getPreferencesForWiki(user != null ? user.getWikiReference()
                : new WikiReference(wikiDescriptorManager.getMainWikiId()));
    }

    @Override
    public List<NotificationPreference> getPreferencesForWiki(WikiReference wiki) throws NotificationException
    {
        return cachedModelBridge.getNotificationsPreferences(wiki);
    }
}