/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.rest.action;

import com.google.common.collect.ImmutableList;
import org.opensearch.cluster.metadata.IndexNameExpressionResolver;
import org.opensearch.cluster.node.DiscoveryNodes;
import org.opensearch.common.settings.ClusterSettings;
import org.opensearch.common.settings.IndexScopedSettings;
import org.opensearch.common.settings.Settings;
import org.opensearch.common.settings.SettingsFilter;
import org.opensearch.plugins.ActionPlugin;
import org.opensearch.plugins.Plugin;
import org.opensearch.rest.RestController;
import org.opensearch.rest.RestHandler;

import java.util.List;
import java.util.function.Supplier;

public class ToDoPlugin extends Plugin implements ActionPlugin {

    static final String TODO_INDEX_NAME = "todo";

    @Override
    public List<RestHandler> getRestHandlers(final Settings settings,
                                             final RestController restController,
                                             final ClusterSettings clusterSettings,
                                             final IndexScopedSettings indexScopedSettings,
                                             final SettingsFilter settingsFilter,
                                             final IndexNameExpressionResolver indexNameExpressionResolver,
                                             final Supplier<DiscoveryNodes> nodesInCluster) {

        //return singletonList(new ToDoRestIndexAction());
        ToDoRestIndexAction toDoRestIndexAction = new ToDoRestIndexAction();
        ToDoRestGetAction toDoRestGetAction = new ToDoRestGetAction();
        ToDoRestUpdateAction toDoRestUpdateAction = new ToDoRestUpdateAction();
        ToDoRestDeleteAction toDoRestDeleteAction = new ToDoRestDeleteAction();
        return ImmutableList.of(
            toDoRestIndexAction,
            toDoRestGetAction,
            toDoRestUpdateAction,
            toDoRestDeleteAction
        );
    }

}