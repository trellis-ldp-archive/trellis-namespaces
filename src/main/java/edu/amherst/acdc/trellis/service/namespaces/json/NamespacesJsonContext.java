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

import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.amherst.acdc.trellis.spi.NamespaceService;
import org.slf4j.Logger;

/**
 * Create a namespace service based on reading/writing the namespaces to a central JSON file.
 *
 * @author acoburn
 */
public class NamespacesJsonContext implements NamespaceService {

    private static final Logger LOGGER = getLogger(NamespacesJsonContext.class);

    private final static ObjectMapper mapper = new ObjectMapper();
    private final String filePath;
    private final Map<String, String> data;

    /**
     * Create a JSON-based Namespace service
     * @param filePath the file path
     */
    public NamespacesJsonContext(final String filePath) {
        requireNonNull(filePath, "The filePath may not be null!");

        this.filePath = filePath;
        data = read(filePath);
        if (data.isEmpty()) {
            data.putAll(read(getClass().getResource("/defaultNamespaces.json").getPath()));
            if (!data.isEmpty()) {
                write(filePath, data);
            }
        }
    }

    @Override
    public Map<String, String> getNamespaces() {
        return unmodifiableMap(data);
    }

    @Override
    public String getNamespace(final String prefix) {
        return data.get(prefix);
    }

    @Override
    public void setNamespace(final String prefix, final String namespace) {
        requireNonNull(prefix, "The prefix value may not be null!");
        requireNonNull(namespace, "The namespce value may not be null!");

        data.put(prefix, namespace);
        write(filePath, data);
    }

    private static Map<String, String> read(final String filePath) {
        final File file = new File(filePath);
        final Map<String, String> namespaces = new HashMap<>();
        if (file.exists()) {
            try {
                final JsonNode json = mapper.readTree(new File(filePath));
                if (json.isObject()) {
                    json.fields().forEachRemaining(node -> {
                        if (node.getValue().isTextual()) {
                            namespaces.put(node.getKey(), node.getValue().textValue());
                        }
                    });
                }
            } catch (final Exception ex) {
                LOGGER.error("Error reading from JSON: {}", ex.getMessage());
            }
        }
        return namespaces;
    }

    private static void write(final String filePath, final Map<String, String> data) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), data);
        } catch (final Exception ex) {
            LOGGER.error("Error writing JSON: {}", ex.getMessage());
        }
    }
}
