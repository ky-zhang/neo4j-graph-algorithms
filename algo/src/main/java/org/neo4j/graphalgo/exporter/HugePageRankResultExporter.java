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
package org.neo4j.graphalgo.exporter;

import org.neo4j.graphalgo.api.HugeIdMapping;
import org.neo4j.graphalgo.core.utils.HugeParallelExporter;
import org.neo4j.graphalgo.core.utils.TerminationFlag;
import org.neo4j.kernel.api.DataWriteOperations;
import org.neo4j.kernel.api.exceptions.KernelException;
import org.neo4j.kernel.api.properties.DefinedProperty;
import org.neo4j.kernel.internal.GraphDatabaseAPI;
import org.neo4j.logging.Log;

import java.util.concurrent.ExecutorService;

/**
 * @author mknblch
 */
public class HugePageRankResultExporter extends HugeParallelExporter<PageRankResult> {

    public HugePageRankResultExporter(
            GraphDatabaseAPI db,
            TerminationFlag terminationFlag,
            HugeIdMapping idMapping,
            Log log,
            String writeProperty) {
        super(db, terminationFlag, idMapping, log, writeProperty);
    }

    public HugePageRankResultExporter(
            GraphDatabaseAPI db,
            TerminationFlag terminationFlag,
            HugeIdMapping idMapping,
            Log log,
            String writeProperty,
            ExecutorService executorService) {
        super(db, idMapping, log, writeProperty, executorService,
                terminationFlag);
    }

    @Override
    protected void doWrite(
            DataWriteOperations writeOperations,
            PageRankResult data,
            long offset) throws KernelException {
        writeOperations.nodeSetProperty(
                idMapping.toOriginalNodeId(offset),
                DefinedProperty.doubleProperty(
                        writePropertyId,
                        data.score(offset)));
    }
}


