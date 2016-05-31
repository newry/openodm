package com.openodm.impl;

import java.io.File;
import java.net.URL;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import com.openodm.impl.entity.ct.CTVersion;
import com.openodm.impl.entity.ct.CodeList;
import com.openodm.impl.entity.ct.ControlTerminology;
import com.openodm.impl.entity.ct.CustomizedCodeList;
import com.openodm.impl.entity.ct.CustomizedEnumeratedItem;
import com.openodm.impl.entity.ct.EnumeratedItem;
import com.openodm.impl.entity.ct.ExtendedEnumeratedItem;
import com.openodm.impl.entity.sdtm.SDTMDomain;
import com.openodm.impl.entity.sdtm.SDTMOrigin;
import com.openodm.impl.entity.sdtm.SDTMProject;
import com.openodm.impl.entity.sdtm.SDTMProjectDomainXref;
import com.openodm.impl.entity.sdtm.SDTMProjectKeyVariableXref;
import com.openodm.impl.entity.sdtm.SDTMProjectLibrary;
import com.openodm.impl.entity.sdtm.SDTMProjectVariableXref;
import com.openodm.impl.entity.sdtm.SDTMVariable;
import com.openodm.impl.entity.sdtm.SDTMVariableRef;
import com.openodm.impl.entity.sdtm.SDTMVersion;

public class GenerateDDL {

	public static void main(String[] args) {
		try {
			Configuration cfg = new Configuration();
			cfg.setProperty(AvailableSettings.DIALECT, "org.hibernate.dialect.PostgreSQL9Dialect");
			cfg.setProperty(AvailableSettings.DRIVER, "org.postgresql.Driver");
			cfg.setProperty(AvailableSettings.URL, "jdbc:postgresql://localhost:5432/yoda");
			cfg.setProperty(AvailableSettings.USER, "admin");
			cfg.setProperty(AvailableSettings.PASS, "admin");
			cfg.addAnnotatedClass(CTVersion.class);
			cfg.addAnnotatedClass(CustomizedCodeList.class);
			cfg.addAnnotatedClass(CodeList.class);
			cfg.addAnnotatedClass(EnumeratedItem.class);
			cfg.addAnnotatedClass(CustomizedEnumeratedItem.class);
			cfg.addAnnotatedClass(ExtendedEnumeratedItem.class);
			cfg.addAnnotatedClass(ControlTerminology.class);
			cfg.addAnnotatedClass(SDTMVersion.class);
			cfg.addAnnotatedClass(SDTMDomain.class);
			cfg.addAnnotatedClass(SDTMVariableRef.class);
			cfg.addAnnotatedClass(SDTMVariable.class);
			cfg.addAnnotatedClass(SDTMProject.class);
			cfg.addAnnotatedClass(SDTMProjectVariableXref.class);
			cfg.addAnnotatedClass(SDTMProjectKeyVariableXref.class);
			cfg.addAnnotatedClass(SDTMProjectDomainXref.class);
			cfg.addAnnotatedClass(SDTMProjectLibrary.class);
			cfg.addAnnotatedClass(SDTMOrigin.class);
			SchemaExport export = new SchemaExport(cfg);
			export.setFormat(true);
			export.setDelimiter(";");
			URL url = GenerateDDL.class.getClassLoader().getResource("ddl.update.sql");
			export.setOutputFile(new File(url.getFile()).getAbsolutePath());
			export.create(true, false);
		} finally {
			System.exit(0);
		}
	}
}
