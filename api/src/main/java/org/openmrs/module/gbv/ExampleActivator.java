/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.gbv;

import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory;
// import org.openmrs.module.ModuleActivator;
// import org.apache.commons.logging.Log;
// import org.apache.commons.logging.LogFactory;
import org.openmrs.module.BaseModuleActivator;


/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class ExampleActivator extends BaseModuleActivator {

	// protected static final Log log = LogFactory.getLog(ExampleActivator.class);
	private Log log = LogFactory.getLog(this.getClass());


	/**
	 * @see #started()
	 */
	public void started() {
		log.info("Started GBV");
	}

	/**
	 * @see #shutdown()
	 */
	public void shutdown() {
		log.info("Shutdown GBV");
	}
}

	/**
	 * @see ModuleActivator#willRefreshContext()
	 *
	public void willRefreshContext() {
		log.info("Refreshing KenyaEMR Add-on GBV Module");
	}
	
	/**
	 * @see ModuleActivator#contextRefreshed()
	 *
	public void contextRefreshed() {
		log.info("KenyaEMR Add-on GBV Module refreshed");
	}
	
	/**
	 * @see ModuleActivator#willStart()
	 *
	public void willStart() {
		log.info("Starting KenyaEMR Add-on GBV Module");
	}
	
	/**
	 * @see ModuleActivator#started()
	 *
	public void started() {
		log.info("KenyaEMR Add-on GBV Module started");
	}
	
	/**
	 * @see ModuleActivator#willStop()
	 *
	public void willStop() {
		log.info("Stopping KenyaEMR Add-on GBV Module");
	}
	
	/**
	 * @see ModuleActivator#stopped()
	 *
	public void stopped() {
		log.info("KenyaEMR Add-on GBV Module stopped");*/

