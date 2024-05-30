/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.rest.action;
import org.opensearch.client.node.NodeClient;
import org.opensearch.rest.RestRequest;
import org.opensearch.rest.action.document.RestIndexAction;
import java.io.IOException;
import java.util.List;
import static org.opensearch.rest.RestRequest.Method.POST;

public class ToDoRestIndexAction extends RestIndexAction {

  @Override
  public String getName() {
    return "todo_plugin_document_index";
  }

  @Override
  public List<Route> routes() {
    return List.of(
        new Route(POST, "/_plugins/" + ToDoPlugin.TODO_INDEX_NAME + "/create"));
  }

  @Override
  public RestChannelConsumer prepareRequest(RestRequest request, final NodeClient client) throws IOException {
    assert request.params().get("id") == null : "non-null id: " + request.params().get("id");
    request.params().put("index", ToDoPlugin.TODO_INDEX_NAME);
    request.params().putIfAbsent("op_type", "create");
    return super.prepareRequest(request, client);
  }
}