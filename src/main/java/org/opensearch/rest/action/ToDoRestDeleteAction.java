/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.rest.action;

import org.opensearch.action.delete.DeleteRequest;
import org.opensearch.action.support.ActiveShardCount;
import org.opensearch.client.node.NodeClient;
import org.opensearch.index.VersionType;
import org.opensearch.rest.BaseRestHandler;
import org.opensearch.rest.RestRequest;

import java.io.IOException;
import java.util.List;

import static org.opensearch.rest.RestRequest.Method.DELETE;

public class ToDoRestDeleteAction extends BaseRestHandler {
  @Override
  public List<Route> routes() {
    return List.of(new Route(DELETE, "/_plugins/" + ToDoPlugin.TODO_INDEX_NAME + "/{id}"));
  }

  @Override
  public String getName() {
    return "todo_plugin_document_delete";
  }

  @Override
  public RestChannelConsumer prepareRequest(final RestRequest request, final NodeClient client) throws IOException {
    request.params().put("index", ToDoPlugin.TODO_INDEX_NAME);
    DeleteRequest deleteRequest = new DeleteRequest(request.param("index"), request.param("id"));
    deleteRequest.routing(request.param("routing"));
    deleteRequest.timeout(request.paramAsTime("timeout", DeleteRequest.DEFAULT_TIMEOUT));
    deleteRequest.setRefreshPolicy(request.param("refresh"));
    deleteRequest.version(RestActions.parseVersion(request));
    deleteRequest.versionType(VersionType.fromString(request.param("version_type"), deleteRequest.versionType()));
    deleteRequest.setIfSeqNo(request.paramAsLong("if_seq_no", deleteRequest.ifSeqNo()));
    deleteRequest.setIfPrimaryTerm(request.paramAsLong("if_primary_term", deleteRequest.ifPrimaryTerm()));

    String waitForActiveShards = request.param("wait_for_active_shards");
    if (waitForActiveShards != null) {
      deleteRequest.waitForActiveShards(ActiveShardCount.parseString(waitForActiveShards));
    }

    return channel -> client.delete(deleteRequest, new RestStatusToXContentListener<>(channel));
  }
}
