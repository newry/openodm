package com.openodm.impl;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import com.openodm.impl.entity.CodeList;
import com.openodm.impl.entity.ControlTerminnology;
import com.openodm.impl.entity.EnumeratedItem;
import com.openodm.impl.entity.MetaDataVersion;

public class GenerateDDL {

	public static void main(String[] args) {
		try {
			Configuration cfg = new Configuration();
			cfg.setProperty(AvailableSettings.DIALECT,
					"org.hibernate.dialect.PostgreSQL9Dialect");
			cfg.setProperty(AvailableSettings.DRIVER, "org.postgresql.Driver");
			cfg.setProperty(AvailableSettings.URL,
					"jdbc:postgresql://localhost:5432/yoda");
			cfg.setProperty(AvailableSettings.USER, "admin");
			cfg.setProperty(AvailableSettings.PASS, "admin");
			cfg.addAnnotatedClass(MetaDataVersion.class);
			cfg.addAnnotatedClass(CodeList.class);
			cfg.addAnnotatedClass(EnumeratedItem.class);
			cfg.addAnnotatedClass(ControlTerminnology.class);
			SchemaExport export = new SchemaExport(cfg);
			export.setFormat(true);
			export.setDelimiter(";");
			export.setOutputFile("/Users/runiu/workspace/odm/impl/ddl.update.sql");
			export.create(false, false);
		} finally {
			System.exit(0);
		}
	}
}
