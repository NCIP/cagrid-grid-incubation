/**
*============================================================================
*  The Ohio State University Research Foundation, Emory University,
*  the University of Minnesota Supercomputing Institute
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-grid-incubation/LICENSE.txt for details.
*============================================================================
**/
/**
*============================================================================
*============================================================================
**/
package org.cagrid.gaards.ui.csm;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.cagrid.gaards.csm.client.ProtectionElement;
import org.cagrid.gaards.csm.client.Role;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.configuration.ServiceConfiguration;
import org.cagrid.grape.configuration.ServiceDescriptor;
import org.cagrid.grape.configuration.Services;


public class CSMUIUtils {

    private static Logger log = Logger.getLogger(CSMUIUtils.class);


    public static List<Role> filterRoles(List<Role> master, List<Role> list) {
        List<Role> filtered = new ArrayList<Role>();
        for (int i = 0; i < master.size(); i++) {
            boolean found = false;
            for (int j = 0; j < list.size(); j++) {
                if (master.get(i).getId() == list.get(j).getId()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                filtered.add(master.get(i));
            }
        }

        return filtered;
    }


    public static List<ProtectionElement> sortProtectionElements(List<ProtectionElement> list) {
        List<ProtectionElement> sorted = new ArrayList<ProtectionElement>();
        if (list != null) {
            List<ProtectionElement> preSorted = new ArrayList<ProtectionElement>();
            // Sort By Object Id
            for (int i = 0; i < list.size(); i++) {
                ProtectionElement pe = list.get(i);
                int index = 0;
                for (int j = 0; j < preSorted.size(); j++) {
                    int val = pe.getObjectId().compareToIgnoreCase(preSorted.get(j).getObjectId());
                    if (val <= 0) {
                        index = j;
                        break;
                    } else {
                        index = j + 1;
                    }
                }
                preSorted.add(index, pe);
            }
            return preSorted;

            // Sort By Attribute

        }

        return sorted;
    }


    public static List<Role> sortRoles(List<Role> list) {
        List<Role> sorted = new ArrayList<Role>();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                Role r = list.get(i);
                int index = 0;
                for (int j = 0; j < sorted.size(); j++) {
                    int val = r.getName().compareToIgnoreCase(sorted.get(j).getName());
                    if (val <= 0) {
                        index = j;
                        break;
                    } else {
                        index = j + 1;
                    }
                }
                sorted.add(index, r);
            }
            return sorted;
        }
        return sorted;
    }


    public static List<CSMHandle> getCSMServices() {
        List<CSMHandle> services = new ArrayList<CSMHandle>();
        try {
            ServiceConfiguration conf = (ServiceConfiguration) GridApplication.getContext().getConfigurationManager()
                .getConfigurationObject(CSMUIConstants.UI_CONF);
            Services s = conf.getServices();
            if (s != null) {
                ServiceDescriptor[] list = s.getServiceDescriptor();
                if (list != null) {
                    for (int i = 0; i < list.length; i++) {
                        CSMHandle handle = new CSMHandle(list[i]);
                        services.add(handle);
                    }
                }
            }

        } catch (Throwable e) {
            log.error(e);
        }
        return services;
    }
}
