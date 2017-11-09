/**
 * Copyright (c) 2017 "Neo4j, Inc." <http://neo4j.com>
 *
 * This file is part of Neo4j Graph Algorithms <http://github.com/neo4j-contrib/neo4j-graph-algorithms>.
 *
 * Neo4j Graph Algorithms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.graphalgo.impl;

import org.neo4j.graphalgo.api.Graph;
import org.neo4j.graphalgo.core.utils.ProgressLogger;
import org.neo4j.graphalgo.core.utils.dss.DisjointSetStruct;
import org.neo4j.graphdb.Direction;

/**
 * Sequential UnionFind:
 * <p>
 * The algorithm computes sets of weakly connected components.
 * <p>
 * The impl. is based on the {@link DisjointSetStruct}. It iterates over all relationships once
 * within a single forEach loop and adds each source-target pair to the struct. Therefore buffering
 * would introduce an overhead (a SingleRun-RelationshipIterable might be used here).
 * <p>
 * There are 2 different methods for computing the component-sets. compute() calculates all weakly
 * components regardless of the actual weight of the relationship while compute(threshold:double)
 * on the other hand only takes the transition into account if the weight exceeds the threshold value.
 *
 * @author mknblch
 */
public class GraphUnionFind extends Algorithm<GraphUnionFind> {

    private Graph graph;

    private DisjointSetStruct dss;
    private final int nodeCount;

    public GraphUnionFind(Graph graph) {
        this.graph = graph;
        nodeCount = Math.toIntExact(graph.nodeCount());
        this.dss = new DisjointSetStruct(nodeCount);
    }

    /**
     * compute unions of connected nodes
     *
     * @return a DSS
     */
    public DisjointSetStruct compute() {
        dss.reset();
        final ProgressLogger progressLogger = getProgressLogger();
        graph.forEachNode(node -> {
            if (!running()) {
                return false;
            }
            graph.forEachRelationship(node, Direction.OUTGOING, (source, target, id) -> {
                dss.union(source, target);
                return true;
            });
            progressLogger.logProgress((double) node / (nodeCount - 1));
            return true;
        });
        return dss;
    }

    /**
     * compute unions if relationship weight exceeds threshold
     *
     * @param threshold the minimum threshold
     * @return a DSS
     */
    public DisjointSetStruct compute(final double threshold) {
        dss.reset();
        final ProgressLogger progressLogger = getProgressLogger();
        graph.forEachNode(node -> {
            if (!running()) {
                return false;
            }
            graph.forEachRelationship(node, Direction.OUTGOING, (source, target, id, weight) -> {
                if (weight >= threshold) {
                    dss.union(source, target);
                }
                return true;
            });
            progressLogger.logProgress((double) node / (nodeCount - 1));
            return true;
        });
        return dss;
    }

    @Override
    public GraphUnionFind me() {
        return this;
    }

    @Override
    public GraphUnionFind release() {
        graph = null;
        dss = null;
        return this;
    }
}
