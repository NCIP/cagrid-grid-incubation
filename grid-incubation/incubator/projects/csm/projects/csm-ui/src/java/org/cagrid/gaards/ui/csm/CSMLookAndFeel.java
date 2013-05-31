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

import javax.swing.ImageIcon;

import org.cagrid.gaards.ui.common.GAARDSLookAndFeel;
import org.cagrid.grape.utils.IconUtils;

public class CSMLookAndFeel extends GAARDSLookAndFeel {
    
    public final static ImageIcon getCSMIcon() {
        return IconUtils.loadIcon("/caGrid-csm-icon.png");
    }
    
    public final static ImageIcon getCSMIcon22() {
        return IconUtils.loadIcon("/caGrid-csm-icon-22.png");
    }
    
    public final static ImageIcon getGrouperIcon22x22() {
        return IconUtils.loadIcon("/grouper_logo_22x22.png");
    }
    
    public final static ImageIcon getProtectionElementIcon() {
        return IconUtils.loadIcon("/caGrid-csm-emblem-readonly.png");
    }
    
    public final static ImageIcon getProtectionGroupIcon() {
        return IconUtils.loadIcon("/caGrid-csm-protection_group.png");
    }
    
    public final static ImageIcon getRoleIcon() {
        return IconUtils.loadIcon("/caGrid-csm-table_key.png");
    }
    
    public final static ImageIcon getPrivilegeIcon() {
        return IconUtils.loadIcon("/caGrid-csm-textfield_key.png");
    }
    
    public final static ImageIcon getAssociationIcon() {
        return IconUtils.loadIcon("/caGrid-csm-textfield_key.png");
    }
    
    public final static ImageIcon getAddProtectionElementToProtectionGroupIcon() {
        return IconUtils.loadIcon("/up.png");
    }
    
    public final static ImageIcon getRemoveProtectionElementFromProtectionGroupIcon() {
        return IconUtils.loadIcon("/down.png");
    }

}
