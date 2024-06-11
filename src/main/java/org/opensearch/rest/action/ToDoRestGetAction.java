/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.rest.action;

import org.opensearch.action.get.GetRequest;
import org.opensearch.action.get.GetResponse;
import org.opensearch.client.node.NodeClient;
import org.opensearch.core.common.Strings;
import org.opensearch.core.rest.RestStatus;
import org.opensearch.index.VersionType;
import org.opensearch.rest.BaseRestHandler;
import org.opensearch.rest.RestRequest;
import org.opensearch.search.fetch.subphase.FetchSourceContext;

import java.io.IOException;
import java.util.List;

import static org.opensearch.core.rest.RestStatus.NOT_FOUND;
import static org.opensearch.core.rest.RestStatus.OK;
import static org.opensearch.rest.RestRequest.Method.GET;

public class ToDoRestGetAction extends BaseRestHandler {
  @Override
  public String getName() {
    return "todo_plugin_document_get";
  }

  @Override
  public List<Route> routes() {
    return List.of(new Route(GET, ToDoPlugin.TODO_BASE_URI + "/{id}"));
  }

  @Override
  public RestChannelConsumer prepareRequest(final RestRequest request, final NodeClient client) throws IOException {
    request.params().put("index", ToDoPlugin.TODO_INDEX_NAME);
    GetRequest getRequest = new GetRequest(request.param("index"), request.param("id"));
    getRequest.refresh(request.paramAsBoolean("refresh", getRequest.refresh()));
    getRequest.routing(request.param("routing"));
    getRequest.preference(request.param("preference"));
    getRequest.realtime(request.paramAsBoolean("realtime", getRequest.realtime()));
    if (request.param("fields") != null) {
      throw new IllegalArgumentException(
          "the parameter [fields] is no longer supported, "
              + "please use [stored_fields] to retrieve stored fields or [_source] to load the field from _source"
      );
    }
    final String fieldsParam = request.param("stored_fields");
    if (fieldsParam != null) {
      final String[] fields = Strings.splitStringByCommaToArray(fieldsParam);
      if (fields != null) {
        getRequest.storedFields(fields);
      }
    }

    getRequest.version(RestActions.parseVersion(request));
    getRequest.versionType(VersionType.fromString(request.param("version_type"), getRequest.versionType()));

    getRequest.fetchSourceContext(FetchSourceContext.parseFromRestRequest(request));

    return channel -> client.get(getRequest, new RestToXContentListener<GetResponse>(channel) {
      @Override
      protected RestStatus getStatus(final GetResponse response) {
        return response.isExists() ? OK : NOT_FOUND;
      }
    });
  }
}
