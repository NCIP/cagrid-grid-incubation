package org.cagrid.i2b2.ontomapper.processor;

public interface I2B2Queries {

    public static final String CDE_PATHS = 
        "select cd.concept_path from i2b2demodata.concept_dimension cd, i2b2demodata.ENCODING_DIMENSION ed, i2b2demodata.ENCODING_PROJECT ep, i2b2demodata.ENCODING_PROJECT_LINK epl, i2b2demodata.ENCODING_SERVICE es "
        + "where cd.encoding_cd = ed.encoding_cd " 
        + "and ed.encoding_cd = epl.encoding_cd "
        + "and epl.encoding_project_id = ep.encoding_project_id "
        + "and ep.encoding_service_id = es.encoding_service_id "
        + "and ed.cde_public_id = ? and es.service_url = ? and ep.project_name = ? and ep.project_version = ?";
}
