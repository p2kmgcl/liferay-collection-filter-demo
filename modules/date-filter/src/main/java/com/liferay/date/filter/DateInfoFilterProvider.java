/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.date.filter;

import com.liferay.info.filter.InfoFilterProvider;
import com.liferay.portal.kernel.util.StringUtil;
import org.osgi.service.component.annotations.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * @author Víctor Galán
 */
@Component(immediate = true, service = InfoFilterProvider.class)
public class DateInfoFilterProvider implements InfoFilterProvider<InfoFilterDate> {

	@Override
	public InfoFilterDate create(Map<String, String[]> values) {
		InfoFilterDate infoFilterDate = new InfoFilterDate();

		for (Map.Entry<String, String[]> entry : values.entrySet()) {
			if (!StringUtil.startsWith(
				entry.getKey(),
				InfoFilterDate.FILTER_TYPE_NAME + "_")) {

				try {
					infoFilterDate.setDate(_format.parse(entry.getValue()[0]));
				}
				catch (ParseException e) {
					System.out.println("Invalid date format");
				}
			}
		}

		return infoFilterDate;
	}

	private static final DateFormat _format = new SimpleDateFormat("yyyy-MM-dd");
}
