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
package org.xwiki.wiki.descriptor.internal;

import javax.inject.Provider;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.xwiki.bridge.event.WikiDeletedEvent;
import org.xwiki.component.util.DefaultParameterizedType;
import org.xwiki.localization.ContextualLocalizationManager;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.observation.ObservationManager;
import org.xwiki.test.mockito.MockitoComponentMockingRule;
import org.xwiki.wiki.descriptor.WikiDescriptor;
import org.xwiki.wiki.descriptor.WikiDescriptorManager;
import org.xwiki.wiki.internal.descriptor.DefaultWikiDescriptor;
import org.xwiki.wiki.internal.descriptor.builder.WikiDescriptorBuilder;
import org.xwiki.wiki.internal.descriptor.document.DefaultWikiDescriptorDocumentHelper;
import org.xwiki.wiki.internal.manager.DefaultWikiManager;
import org.xwiki.wiki.manager.WikiManagerException;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.store.XWikiStoreInterface;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link org.xwiki.wiki.internal.manager.DefaultWikiManager}.
 *
 * @version $Id$
 * @since 5.3M1
 */
public class DefaultWikiManagerTest
{
    @Rule
    public MockitoComponentMockingRule<DefaultWikiManager> mocker =
            new MockitoComponentMockingRule(DefaultWikiManager.class);

    private WikiDescriptorManager defaultWikiDescriptorManager;

    private Provider<XWikiContext> xcontextProvider;

    private ContextualLocalizationManager localizationManager;

    private ObservationManager observationManager;

    private Logger logger;

    private WikiDescriptorBuilder wikiDescriptorBuilder;

    private DefaultWikiDescriptorDocumentHelper descriptorDocumentHelper;

    private XWikiContext xcontext;

    private com.xpn.xwiki.XWiki xwiki;

    @Before
    public void setUp() throws Exception
    {
        // Injection
        defaultWikiDescriptorManager = mocker.getInstance(WikiDescriptorManager.class);
        xcontextProvider = mocker.getInstance(new DefaultParameterizedType(null, Provider.class, XWikiContext.class));
        localizationManager = mocker.getInstance(ContextualLocalizationManager.class);
        observationManager = mocker.getInstance(ObservationManager.class);
        //logger = mocker.getInstance(Logger.class);
        wikiDescriptorBuilder = mocker.getInstance(WikiDescriptorBuilder.class);
        descriptorDocumentHelper = mocker.getInstance(DefaultWikiDescriptorDocumentHelper.class);

        // Frequent uses
        xcontext = mock(XWikiContext.class);
        when(xcontextProvider.get()).thenReturn(xcontext);
        xwiki = mock(com.xpn.xwiki.XWiki.class);
        when(xcontext.getWiki()).thenReturn(xwiki);
        when(defaultWikiDescriptorManager.getMainWikiId()).thenReturn("xwiki");
    }

    @Test
    public void idAvailable() throws Exception
    {
        // Forbidden list
        when(xwiki.Param("xwiki.virtual.reserved_wikis")).thenReturn("forbidden,wikiid3,toto");

        // When the wiki already exists
        when(defaultWikiDescriptorManager.exists("wikiid1")).thenReturn(true);
        assertFalse(this.mocker.getComponentUnderTest().idAvailable("wikiid1"));

        // When the wiki does not already exists
        when(defaultWikiDescriptorManager.exists("wikiid2")).thenReturn(false);
        assertTrue(this.mocker.getComponentUnderTest().idAvailable("wikiid2"));

        // When the wiki does not already exists but the id is forbidden
        when(defaultWikiDescriptorManager.exists("wikiid3")).thenReturn(false);
        assertFalse(this.mocker.getComponentUnderTest().idAvailable("wikiid3"));
    }

    @Test
    public void createWhenWikiExists() throws Exception
    {
        // When the wiki already exists
        when(defaultWikiDescriptorManager.exists("wikiid1")).thenReturn(true);

        boolean exceptionCaught = false;
        try {
            this.mocker.getComponentUnderTest().create("wikiid1", "wikialias1");
        } catch (WikiManagerException e) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
    }

    @Test
    public void createWhenWikiIdIsForbidden() throws Exception
    {
        // The wiki does not already exist
        when(defaultWikiDescriptorManager.exists("wikiid1")).thenReturn(false);

        // Forbidden list
        when(xwiki.Param("xwiki.virtual.reserved_wikis")).thenReturn("forbidden,wikiid1");

        boolean exceptionCaught = false;
        try {
            this.mocker.getComponentUnderTest().create("wikiid1", "wikialias1");
        } catch (WikiManagerException e) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
    }

    @Test
    public void createWhenWikiIdIsValid() throws Exception
    {
        // The wiki does not already exist
        when(defaultWikiDescriptorManager.exists("wikiid1")).thenReturn(false);

        // The wiki id is valid
        when(xwiki.Param("xwiki.virtual.reserved_wikis")).thenReturn("forbidden");

        // Other mocks
        XWikiStoreInterface store = mock(XWikiStoreInterface.class);
        when(xwiki.getStore()).thenReturn(store);
        DefaultWikiDescriptor descriptor = new DefaultWikiDescriptor("wikiid1", "wikialias1");
        DocumentReference documentReference = new DocumentReference("xwiki", "XWiki", "XWikiServerWikiid1");
        XWikiDocument descriptorDocument = mock(XWikiDocument.class);
        when(wikiDescriptorBuilder.save(eq(descriptor))).thenReturn(descriptorDocument);
        when(descriptorDocument.getDocumentReference()).thenReturn(documentReference);

        // Create
        WikiDescriptor newWikiDescriptor = this.mocker.getComponentUnderTest().create("wikiid1", "wikialias1");
        assertNotNull(newWikiDescriptor);

        // Verify that the wiki descriptor is an instance of DefaultWikiDescriptor
        assertTrue(newWikiDescriptor instanceof DefaultWikiDescriptor);
        // Verify that the wiki has been created
        verify(store).createWiki(eq("wikiid1"), any(XWikiContext.class));
        // Verify that the wiki has been updated
        verify(xwiki).updateDatabase(eq("wikiid1"), eq(true), eq(true), any(XWikiContext.class));
        // Verify that the descriptor document has been saved
        verify(store).saveXWikiDoc(eq(descriptorDocument), any(XWikiContext.class));
    }

    @Test
    public void deleteTheMainWiki() throws Exception
    {
        boolean exceptionCaught = false;
        try {
            this.mocker.getComponentUnderTest().delete("xwiki");
        } catch (WikiManagerException e) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
    }

    @Test
    public void deleteWiki() throws Exception
    {
        // Mock
        XWikiStoreInterface store = mock(XWikiStoreInterface.class);
        when(xwiki.getStore()).thenReturn(store);
        DefaultWikiDescriptor descriptor = new DefaultWikiDescriptor("wikiid", "wikialias");
        descriptor.addAlias("wikialias2");
        XWikiDocument descriptorDocument = mock(XWikiDocument.class);
        when(descriptorDocumentHelper.getDocumentFromWikiId("wikiid")).thenReturn(descriptorDocument);

        // Delete
        this.mocker.getComponentUnderTest().delete("wikiid");

        // Verify that the database has been deleted
        verify(store).deleteWiki(eq("wikiid"), any(XWikiContext.class));
        // Verify that the descriptor document has been deleted
        verify(xwiki).deleteDocument(eq(descriptorDocument), any(XWikiContext.class));
        // Verify that the descriptor has been removed from caches
        //verify(cache).remove(eq(descriptor));
        // Verify that an event has been sent
        verify(observationManager).notify(eq(new WikiDeletedEvent("wikiid")), eq("wikiid"));
    }

    @Test
    public void copyWhenWikiAlreadyExists() throws Exception
    {
        // The wiki already exist
        when(defaultWikiDescriptorManager.exists("existingid")).thenReturn(true);
        boolean exceptionCaught = false;
        try {
            this.mocker.getComponentUnderTest().copy("wikiid", "existingid", "newwikialias", true, true);
        } catch (WikiManagerException e) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
    }

    @Test
    public void copyWhenWikiAvailable() throws Exception
    {
        // The wiki does not already exist
        when(defaultWikiDescriptorManager.exists("wikiid1")).thenReturn(false);

        // The new id is valid
        when(xwiki.Param("xwiki.virtual.reserved_wikis")).thenReturn("forbidden");

        // Other mocks
        XWikiStoreInterface store = mock(XWikiStoreInterface.class);
        when(xwiki.getStore()).thenReturn(store);
        DefaultWikiDescriptor descriptor = new DefaultWikiDescriptor("wikiid1", "wikialias1");
        DocumentReference documentReference = new DocumentReference("xwiki", "XWiki", "XWikiServerWikiid1");
        XWikiDocument descriptorDocument = mock(XWikiDocument.class);
        when(wikiDescriptorBuilder.save(eq(descriptor))).thenReturn(descriptorDocument);
        when(descriptorDocument.getDocumentReference()).thenReturn(documentReference);

        // Create
        WikiDescriptor newWikiDescriptor = this.mocker.getComponentUnderTest().copy("wikiid", "wikiid1",
                "wikialias1", true, true);
        assertNotNull(newWikiDescriptor);

        // Verify that the wiki descriptor is an instance of DefaultWikiDescriptor
        assertTrue(newWikiDescriptor instanceof DefaultWikiDescriptor);
        // Verify that the wiki has been copied
        verify(xwiki).copyWiki(eq("wikiid"), eq("wikiid1"), any(String.class), any(XWikiContext.class));
        // Verify that the descriptor document has been saved
        verify(store).saveXWikiDoc(eq(descriptorDocument), any(XWikiContext.class));
        // Verify that the descriptor has been added to the caches
        //verify(cache).add(eq((DefaultWikiDescriptor) newWikiDescriptor));
    }
}

