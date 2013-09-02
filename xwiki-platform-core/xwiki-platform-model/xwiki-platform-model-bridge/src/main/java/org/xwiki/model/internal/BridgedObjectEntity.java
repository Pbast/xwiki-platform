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
package org.xwiki.model.internal;

import java.util.Locale;
import java.util.Map;

import org.xwiki.model.Entity;
import org.xwiki.model.EntityIterator;
import org.xwiki.model.EntityType;
import org.xwiki.model.ModelException;
import org.xwiki.model.ModelRuntimeException;
import org.xwiki.model.ObjectEntity;
import org.xwiki.model.ObjectPropertyEntity;
import org.xwiki.model.Version;
import org.xwiki.model.reference.EntityReference;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.objects.BaseObject;

/**
 * @since 5.2M2
 */
public class BridgedObjectEntity extends AbstractBridgedEntity implements ObjectEntity
{
    private BaseObject baseObject;

    public BridgedObjectEntity(BaseObject baseObject, XWikiContext xcontext)
    {
        super(xcontext);
        this.baseObject = baseObject;
    }

    @Override public EntityIterator<Entity> getChildren(EntityType type)
    {
        throw new ModelRuntimeException("Not supported");
    }

    @Override public String getIdentifier()
    {
        throw new ModelRuntimeException("Not supported");
    }

    @Override public Locale getLocale()
    {
        throw new ModelRuntimeException("Not supported");
    }

    @Override public Entity getParent()
    {
        throw new ModelRuntimeException("Not supported");
    }

    @Override public EntityType getType()
    {
        throw new ModelRuntimeException("Not supported");
    }

    @Override public Version getVersion()
    {
        throw new ModelRuntimeException("Not supported");
    }

    @Override public boolean isModified()
    {
        throw new ModelRuntimeException("Not supported");
    }

    @Override
    public boolean isRemoved()
    {
        throw new ModelRuntimeException("Not supported");
    }

    @Override public EntityReference getLinkReference()
    {
        throw new ModelRuntimeException("Not supported");
    }

    @Override public void setLinkReference(EntityReference linkedReference)
    {
        throw new ModelRuntimeException("Not supported");
    }

    @Override public void save(String comment, boolean isMinorEdit, Map<String, String> extraParameters)
    {
        throw new ModelRuntimeException("Not supported");
    }

    @Override
    public void discard()
    {
        throw new ModelRuntimeException("Not supported");
    }

    @Override public EntityReference getReference()
    {
        throw new ModelRuntimeException("Not supported");
    }

    @Override public EntityIterator<ObjectPropertyEntity> getObjectPropertyEntities() throws ModelException
    {
        throw new ModelRuntimeException("Not supported");
    }

    @Override public ObjectPropertyEntity getObjectPropertyEntity(String objectPropertyName) throws ModelException
    {
        throw new ModelRuntimeException("Not supported");
    }

    @Override public ObjectPropertyEntity addObjectPropertyEntity(String objectPropertyName)
    {
        throw new ModelRuntimeException("Not supported");
    }

    @Override public void removeObjectPropertyEntity(String objectPropertyName)
    {
        throw new ModelRuntimeException("Not supported");
    }

    @Override public boolean hasObjectPropertyEntity(String objectPropertyName) throws ModelException
    {
        throw new ModelRuntimeException("Not supported");
    }
}
