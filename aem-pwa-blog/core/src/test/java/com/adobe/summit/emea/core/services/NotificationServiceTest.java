/*
 *  Copyright 2018 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.adobe.summit.emea.core.services;

import com.adobe.summit.emea.core.services.impl.NotificationServiceImpl;
import com.google.gson.JsonElement;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Rule;
import org.junit.Test;

import com.google.gson.JsonObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NotificationServiceTest {

    @Rule
    public final SlingContext context = new SlingContext();

    private NotificationService notificationService = new NotificationServiceImpl();

    @Test
    public void testSendMessage() throws Exception {

       notificationService.sendCommonMessage("Summit Lab EH", "An event occured in the blog, we know you might be inetrested");
    }
}
