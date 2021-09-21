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

import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.collection.provider.FilteredInfoCollectionProvider;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.info.filter.DateInfoFilter;
import com.liferay.info.filter.InfoFilter;
import com.liferay.info.filter.KeywordsInfoFilter;
import com.liferay.info.pagination.InfoPage;
import com.liferay.info.pagination.Pagination;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.Serializable;
import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * @author Víctor Galán
 */
@Component(
	immediate = true, service = InfoCollectionProvider.class
)
public class BasicWebContentInfoCollectionProvider
	implements FilteredInfoCollectionProvider<JournalArticle>{

	@Override
	public InfoPage<JournalArticle> getCollectionInfoPage(
		CollectionQuery collectionQuery) {

		try {
			Indexer<?> indexer = JournalSearcher.getInstance();

			SearchContext searchContext = _buildSearchContext(collectionQuery);

			Hits hits = indexer.search(searchContext);

			List<JournalArticle> articles = new ArrayList<>();

			for (Document document : hits.getDocs()) {
				String className = document.get(Field.ENTRY_CLASS_NAME);

				if (className.equals(JournalArticle.class.getName())) {
					long classPK = GetterUtil.getLong(
						document.get(Field.ENTRY_CLASS_PK));

					JournalArticle article =
						_journalArticleLocalService.fetchLatestArticle(
							classPK, WorkflowConstants.STATUS_ANY, false);

					if (article != null) {
						articles.add(article);
					}
				}
			}

			return InfoPage.of(
				articles, collectionQuery.getPagination(), hits.getLength());
		}
		catch (SearchException searchException) {
			if (_log.isWarnEnabled()) {
				_log.warn(searchException, searchException);
			}
		}

		return null;
	}

	@Override
	public String getLabel(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			locale, getClass());

		return "Basic web contents";
	}

	@Override
	public List<InfoFilter> getSupportedInfoFilters() {
		return Arrays.asList(new InfoFilterDate());
	}

	private BooleanClause[] _getDateBooleanClause(
		InfoFilterDate infoFilterDate) {

		BooleanQueryImpl booleanQueryImpl = new BooleanQueryImpl();

		BooleanFilter booleanFilter = new BooleanFilter();

		Format format = FastDateFormatFactoryUtil.getSimpleDateFormat(
			PropsUtil.get(PropsKeys.INDEX_DATE_FORMAT_PATTERN));

		booleanFilter.addRangeTerm(
			Field.DISPLAY_DATE, format.format(infoFilterDate.getDate()),
			format.format(new Date()));

		booleanQueryImpl.setPreBooleanFilter(booleanFilter	);

		return new BooleanClause[] {
			BooleanClauseFactoryUtil.create(
				booleanQueryImpl, BooleanClauseOccur.MUST.getName())
		};
	}

	private SearchContext _buildSearchContext(CollectionQuery collectionQuery) {
		SearchContext searchContext = new SearchContext();

		searchContext.setAndSearch(true);
		searchContext.setAttributes(
			HashMapBuilder.<String, Serializable>put(
				Field.STATUS, WorkflowConstants.STATUS_APPROVED
			).put(
				"ddmStructureKey", "BASIC-WEB-CONTENT"
			).put(
				"head", true
			).put(
				"latest", true
			).build());

		Optional<InfoFilterDate> dateInfoFilterOptional = collectionQuery.getInfoFilterOptional(InfoFilterDate.class);

		System.out.println("entro aqui");


		if (dateInfoFilterOptional.isPresent()) {
			searchContext.setBooleanClauses(_getDateBooleanClause(dateInfoFilterOptional.get()));
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		searchContext.setCompanyId(serviceContext.getCompanyId());

		Pagination pagination = collectionQuery.getPagination();

		searchContext.setEnd(pagination.getEnd());

		searchContext.setGroupIds(
			new long[] {serviceContext.getScopeGroupId()});

		searchContext.setSorts(
			new com.liferay.portal.kernel.search.Sort(
				Field.MODIFIED_DATE,
				com.liferay.portal.kernel.search.Sort.LONG_TYPE, true));

		searchContext.setStart(pagination.getStart());

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		return searchContext;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BasicWebContentInfoCollectionProvider.class);


	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;


	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private Portal _portal;

}
