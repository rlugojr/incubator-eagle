/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.apache.eagle.stream.application;


import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public final class TopologyFactory {
    public static Logger LOG = LoggerFactory.getLogger(TopologyFactory.class);
    private final static Map<String, TopologyExecutable> topologyCache = Collections.synchronizedMap(new HashMap<String, TopologyExecutable>());
    public static TopologyExecutable getTopologyInstance(String topologyClass) throws TopologyException {
        TopologyExecutable instance;
        if(topologyCache.containsKey(topologyClass)){
            instance = topologyCache.get(topologyClass);
        } else {
            try {
                LOG.info("load class " + topologyClass + "with classLoader " + TopologyFactory.class.getClassLoader().toString());
                instance = (TopologyExecutable) Class.forName(topologyClass).newInstance();
                topologyCache.put(topologyClass, instance);
            } catch (ClassNotFoundException e) {
                throw new TopologyException("Topology in type of " + topologyClass + " is not found",e);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new TopologyException(e);
            }
        }
        return instance;
    }

    public static void submit(String topologyClass, Config config) throws TopologyException {
        TopologyExecutable topology = getTopologyInstance(topologyClass);
        topology.submit(topologyClass, config);
    }
}
