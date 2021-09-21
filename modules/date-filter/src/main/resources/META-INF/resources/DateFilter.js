/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

import {
	getCollectionFilterValue,
	setCollectionFilterValue,
} from '@liferay/fragment-renderer-collection-filter-impl';

export default function DateFilter({
	fragmentEntryLinkId,
	fragmentEntryLinkNamespace,
}) {
	const form = document.getElementById(`${fragmentEntryLinkNamespace}form`);
	const dateInput = form && form.elements['date'];

	if (!form || !dateInput) {
		return;
	}

	const handleChange = () => {
		const nextDate = new Date(dateInput.value);

		setCollectionFilterValue(
			'date',
			fragmentEntryLinkId,
			`${nextDate.getFullYear()}-${(nextDate.getMonth() + 1)
				.toString()
				.padStart(2, '0')}-${nextDate
				.getDate()
				.toString()
				.padStart(2, '0')}`
		);
	};

	const urlValue = getCollectionFilterValue('date', fragmentEntryLinkId);
	dateInput.value = isNaN(new Date(urlValue).getTime()) ? '' : urlValue;

	form.addEventListener('change', handleChange);

	return {
		dispose() {
			form.removeEventListener('change', handleChange);
		},
	};
}
