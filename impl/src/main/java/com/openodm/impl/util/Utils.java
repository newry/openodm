package com.openodm.impl.util;

import java.util.Calendar;
import java.util.TimeZone;

import com.openodm.impl.entity.ObjectStatus;
import com.openodm.impl.entity.PersistentObject;

public class Utils {
	public static void updatePODataLastModified(PersistentObject po) {
		po.setDateLastModified(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
	}

	public static void updatePODateAdded(PersistentObject po) {
		po.setDateAdded(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
	}

	public static void updatePOStatus(PersistentObject po, ObjectStatus status) {
		po.setStatus(status);
		updatePODataLastModified(po);
	}

}
