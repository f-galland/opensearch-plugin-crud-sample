/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/*
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

package org.opensearch.rest.action;

import org.junit.Before;
import org.mockito.Mockito;
import org.opensearch.client.node.NodeClient;
import org.opensearch.rest.RestHandler;
import org.opensearch.rest.RestRequest;
import org.opensearch.test.OpenSearchTestCase;
import org.opensearch.test.rest.FakeRestChannel;
import org.opensearch.test.rest.FakeRestRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ToDoRestDeleteActionTests extends OpenSearchTestCase {
    private String path;
    private String requestBody;
    private ToDoRestDeleteAction toDoRestDeleteAction;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.path = String.format(Locale.ROOT, "%s/%s", ToDoPlugin.TODO_BASE_URI,  "{id}");
        this.requestBody = "{\"doc\":{\"foo\":\"bar\"}}";
        this.toDoRestDeleteAction = new ToDoRestDeleteAction();
    }

    public void testGetNames() {
        String name = toDoRestDeleteAction.getName();
        assertEquals("todo_plugin_document_delete", name);
    }

    public void testGetRoutes() {
        List<RestHandler.Route> routes = toDoRestDeleteAction.routes();
        assertEquals(this.path, routes.get(0).getPath());
    }

    public void testPrepareRequest() throws IOException {
        Map<String, String> params = new HashMap<>();
        FakeRestRequest request = new FakeRestRequest.Builder(xContentRegistry()).withMethod(RestRequest.Method.PUT)
            .withPath(this.path)
            .withParams(params)
            //.withContent(new BytesArray(this.requestBody), XContentType.JSON)
            .withContent(null, null)
            .build();

        final FakeRestChannel channel = new FakeRestChannel(request, true, 0);

        this.toDoRestDeleteAction.prepareRequest(request, Mockito.mock(NodeClient.class));
        assertEquals(channel.responses().get(), 0);
        assertEquals(channel.errors().get(), 0);
    }
}
