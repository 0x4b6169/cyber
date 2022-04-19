/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package io.confluent.sigmarules.models;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DetectionsManager {
    final static Logger logger = LogManager.getLogger(DetectionsManager.class);

    private Map<String, SigmaDetections> detections = new HashMap<>();
    private Long windowTimeMS = 0L;

    public DetectionsManager() { }

    public void addDetections(String detectionName, SigmaDetections detectionList) {
        detections.put(detectionName, detectionList);
    }

    public SigmaDetections getDetectionsByName(String detectionName) {
        return detections.get(detectionName);
    }

    public Map<String, SigmaDetections> getAllDetections() {
        return detections;
    }

    public void printDetectionsAndConditions() {
        System.out.println("detection: ");
        for (Map.Entry<String, SigmaDetections> detection : detections.entrySet()) {
            System.out.printf("\t%s:\n", detection.getKey());

            SigmaDetections searchIdentifier = detection.getValue();
            for (SigmaDetection sigmaDetection : searchIdentifier.getDetections()) {
                System.out.printf("\t\t%s:", sigmaDetection.getName());
                if (sigmaDetection.getModifier() != null) {
                    System.out.printf("|%s:", sigmaDetection.getModifier());
                }

                if (sigmaDetection.getValues().size() > 1) {
                    System.out.printf("\n");
                    for (String detectionValue : sigmaDetection.getValues()) {
                        System.out.println("\t\t\t" + detectionValue);
                    }
                } else {
                    System.out.printf("%s\n", sigmaDetection.getValues().get(0));
                }
            }
        }
    }

    public void convertWindowTime(String window) {
        /*
            15s  (15 seconds)
            30m  (30 minutes)
            12h  (12 hours)
            7d   (7 days)
            3M   (3 months)
         */
        Long time = 0L;
        if (StringUtils.contains(window, "s")) {
            time = Long.parseLong(StringUtils.substringBefore(window, "s"));
            setWindowTimeMS(TimeUnit.SECONDS.toMillis(time));
        } else if (StringUtils.contains(window, "m")) {
            time = Long.parseLong(StringUtils.substringBefore(window, "m"));
            setWindowTimeMS(TimeUnit.MINUTES.toMillis(time));
        } else if (StringUtils.contains(window, "h")) {
            time = Long.parseLong(StringUtils.substringBefore(window, "h"));
            setWindowTimeMS(TimeUnit.HOURS.toMillis(time));
        } else if (StringUtils.contains(window, "d")) {
            time = Long.parseLong(StringUtils.substringBefore(window, "d"));
            setWindowTimeMS(TimeUnit.DAYS.toMillis(time));
        } else if (StringUtils.contains(window, "M")) {
            time = Long.parseLong(StringUtils.substringBefore(window, "M"));
            setWindowTimeMS(TimeUnit.DAYS.toMillis(time * 30));
        }
   }

    public Long getWindowTimeMS() {
        return windowTimeMS;
    }

    public void setWindowTimeMS(Long windowTimeMS) {
        this.windowTimeMS = windowTimeMS;
    }
}
