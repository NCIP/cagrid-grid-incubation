/*
============================================================================
  The Ohio State University Research Foundation, Emory University,
  the University of Minnesota Supercomputing Institute

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/cagrid-grid-incubation/LICENSE.txt for details.
============================================================================
*/
select cd.concept_path from i2b2demodata.concept_dimension cd, i2b2demodata.ENCODING_DIMENSION ed, 
i2b2demodata.ENCODING_PROJECT ep, i2b2demodata.ENCODING_PROJECT_LINK epl, i2b2demodata.ENCODING_SERVICE es
    where cd.encoding_cd = ed.encoding_cd
        and ed.encoding_cd = epl.encoding_cd 
        and epl.encoding_project_id = ep.encoding_project_id 
        and ep.encoding_service_id = es.encoding_service_id 
        and ed.cde_public_id = 2422269 and es.service_url = 'cadsrapi.nci.nih.gov' and ep.project_name = 'HSDB' and ep.project_version = '1.0';
