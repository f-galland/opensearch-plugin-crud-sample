package org.opensearch.rest.action;

import org.opensearch.action.ActionRequestValidationException;
import org.opensearch.action.DocWriteRequest;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.support.ActiveShardCount;
import org.opensearch.action.update.UpdateRequest;
import org.opensearch.client.node.NodeClient;
import org.opensearch.index.VersionType;
import org.opensearch.rest.BaseRestHandler;
import org.opensearch.rest.RestRequest;
import org.opensearch.search.fetch.subphase.FetchSourceContext;

import java.io.IOException;
import java.util.List;

import static org.opensearch.rest.RestRequest.Method.POST;

public class ToDoRestUpdateAction extends BaseRestHandler {
  @Override
  public List<Route> routes() {
    return List.of(new Route(POST, "/_plugins/" + ToDoPlugin.TODO_INDEX_NAME + "/update/{id}"));
  }

  @Override
  public String getName() {
    return "todo_plugin_document_update";
  }

  @Override
  public RestChannelConsumer prepareRequest(final RestRequest request, final NodeClient client) throws IOException {
    UpdateRequest updateRequest;
    request.params().put("index", ToDoPlugin.TODO_INDEX_NAME);
    updateRequest = new UpdateRequest(request.param("index"), request.param("id"));

    updateRequest.routing(request.param("routing"));
    updateRequest.timeout(request.paramAsTime("timeout", updateRequest.timeout()));
    updateRequest.setRefreshPolicy(request.param("refresh"));
    String waitForActiveShards = request.param("wait_for_active_shards");
    if (waitForActiveShards != null) {
      updateRequest.waitForActiveShards(ActiveShardCount.parseString(waitForActiveShards));
    }
    updateRequest.docAsUpsert(request.paramAsBoolean("doc_as_upsert", updateRequest.docAsUpsert()));
    FetchSourceContext fetchSourceContext = FetchSourceContext.parseFromRestRequest(request);
    if (fetchSourceContext != null) {
      updateRequest.fetchSource(fetchSourceContext);
    }

    updateRequest.retryOnConflict(request.paramAsInt("retry_on_conflict", updateRequest.retryOnConflict()));
    if (request.hasParam("version") || request.hasParam("version_type")) {
      final ActionRequestValidationException versioningError = new ActionRequestValidationException();
      versioningError.addValidationError(
          "internal versioning can not be used for optimistic concurrency control. "
              + "Please use `if_seq_no` and `if_primary_term` instead"
      );
      throw versioningError;
    }

    updateRequest.setIfSeqNo(request.paramAsLong("if_seq_no", updateRequest.ifSeqNo()));
    updateRequest.setIfPrimaryTerm(request.paramAsLong("if_primary_term", updateRequest.ifPrimaryTerm()));
    updateRequest.setRequireAlias(request.paramAsBoolean(DocWriteRequest.REQUIRE_ALIAS, updateRequest.isRequireAlias()));

    request.applyContentParser(parser -> {
      updateRequest.fromXContent(parser);
      IndexRequest upsertRequest = updateRequest.upsertRequest();
      if (upsertRequest != null) {
        upsertRequest.routing(request.param("routing"));
        upsertRequest.version(RestActions.parseVersion(request));
        upsertRequest.versionType(VersionType.fromString(request.param("version_type"), upsertRequest.versionType()));
      }
      IndexRequest doc = updateRequest.doc();
      if (doc != null) {
        doc.routing(request.param("routing"));
        doc.version(RestActions.parseVersion(request));
        doc.versionType(VersionType.fromString(request.param("version_type"), doc.versionType()));
      }
    });

    return channel -> client.update(
        updateRequest,
        new RestStatusToXContentListener<>(channel, r -> r.getLocation(updateRequest.routing()))
    );
  }
}
