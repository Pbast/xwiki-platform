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
package org.xwiki.model.internal.reference;

import static org.junit.Assert.*;

import org.junit.Test;
import org.xwiki.model.reference.DocumentReference;

/**
 * Unit tests for {@link PathStringDocumentReferenceResolver} and {@link PathStringEntityReferenceSerializer}.
 * 
 * @version $Id$
 * @since 5.3M1
 */
public class PathStringDocumentReferenceResolverTest
{
    private PathStringEntityReferenceSerializer serializer = new PathStringEntityReferenceSerializer();

    private PathStringDocumentReferenceResolver resolver = new PathStringDocumentReferenceResolver();

    @Test
    public void serializeAndResolve()
    {
        test("wiki", "Space", "Page");
        test("w/i?ki", "Sp.a#ce", "P a@g&e");
    }

    private void test(String wiki, String space, String page)
    {
        DocumentReference documentReference = new DocumentReference(wiki, space, page);
        assertEquals(documentReference, resolver.resolve(serializer.serialize(documentReference)));
    }
}
