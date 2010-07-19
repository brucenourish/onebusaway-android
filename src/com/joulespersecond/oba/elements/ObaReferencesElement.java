/*
 * Copyright (C) 2010 Paul Watts (paulcwatts@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.joulespersecond.oba.elements;

import java.util.ArrayList;
import java.util.List;

public final class ObaReferencesElement implements ObaReferences {
    public static final ObaReferencesElement EMPTY_OBJECT = new ObaReferencesElement();

    private final ObaStopElement[] stops;
    private final ObaRouteElement[] routes;
    //private final ObaTripElement[] trips;
    private final ObaAgencyElement[] agencies;

    public ObaReferencesElement() {
        stops = ObaStopElement.EMPTY_ARRAY;
        routes = ObaRouteElement.EMPTY_ARRAY;
        agencies = ObaAgencyElement.EMPTY_ARRAY;
    }

    @Override
    public ObaStop getStop(String id) {
        return findById(stops, id);
    }

    @Override
    public List<ObaStop> getStops(String[] ids) {
        return findList(stops, ids);
    }

    @Override
    public ObaRoute getRoute(String id) {
        return findById(routes, id);
    }

    @Override
    public List<ObaRoute> getRoutes(String[] ids) {
        return findList(routes, ids);
    }

    /*
    public ObaTripElement getTrip(String id) {

    }
    */

    @Override
    public ObaAgency getAgency(String id) {
        return findById(agencies, id);
    }

    @Override
    public List<ObaAgency> getAgencies(String[] ids) {
        return findList(agencies, ids);
    }

    //
    // TODO: This will be much easier when we convert to HashMap storage.
    //
    private static <T extends ObaElement> T findById(T[] objects, String id) {
        final int len = objects.length;
        for (int i=0; i < len; ++i) {
            final T obj = objects[i];
            if (obj.getId().equals(id)) {
                return obj;
            }
        }
        return null;
    }
    private static <E extends ObaElement, T extends E> List<E> findList(
            T[] objects, String[] ids) {
        ArrayList<E> result = new ArrayList<E>();
        final int len = ids.length;
        for (int i=0; i < len; ++i) {
            final String id = ids[i];
            final T obj = findById(objects, id);
            if (obj != null) {
                result.add(obj);
            }
        }
        return result;
    }
}