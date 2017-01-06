/*
 * Copyright Amherst College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.amherst.acdc.trellis.service.namespaces.json;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.math.BigInteger;
import java.net.URL;
import java.security.SecureRandom;

import org.junit.Test;

/**
 * @author acoburn
 */
public class NamespacesJsonContextTest {

    private static final String nsDoc = "/testNamespaces.json";
    private static final String ldpNs = "http://www.w3.org/ns/ldp#";
    private static final String ldpPrefix = "ldp";

    @Test
    public void testReadFromJson() {
        final URL res = NamespacesJsonContext.class.getResource(nsDoc);
        final NamespacesJsonContext svc = new NamespacesJsonContext(res.getPath());
        assertEquals(2, svc.getNamespaces().size());
        assertEquals(ldpNs, svc.getNamespace(ldpPrefix));
    }

    @Test
    public void testWriteToJson() {
        final File file = new File(NamespacesJsonContext.class.getResource(nsDoc).getPath());
        final String filename = file.getParent() + randomFilename();
        final NamespacesJsonContext svc1 = new NamespacesJsonContext(filename);
        assertEquals(0, svc1.getNamespaces().size());
        svc1.setNamespace(ldpPrefix, ldpNs);
        assertEquals(1, svc1.getNamespaces().size());
        assertEquals(ldpNs, svc1.getNamespace(ldpPrefix));

        final NamespacesJsonContext svc2 = new NamespacesJsonContext(filename);
        assertEquals(1, svc2.getNamespaces().size());
        assertEquals(ldpNs, svc2.getNamespace(ldpPrefix));
    }

    private static String randomFilename() {
        final SecureRandom random = new SecureRandom();
        final String filename = new BigInteger(50, random).toString(32);
        return "/" + filename + ".json";
    }
}
