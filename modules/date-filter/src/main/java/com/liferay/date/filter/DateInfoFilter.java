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

import com.liferay.info.filter.InfoFilter;

import java.util.Date;

/**
 * @author Pablo Molina
 */
public class DateInfoFilter implements InfoFilter {

	public static final String FILTER_TYPE_NAME = "date";

	public Date getDate() {
		return _date;
	}

	@Override
	public String getFilterTypeName() {
		return FILTER_TYPE_NAME;
	}

	public void setDate(Date date) {
		_date = date;
	}

	private Date _date;

}