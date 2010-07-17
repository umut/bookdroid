package com.hoydaa.bookdroid.service;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.net.Uri;
import android.util.Log;

import com.hoydaa.bookdroid.provider.Book;
import com.hoydaa.bookdroid.provider.Book.ImageSize;

/**
 * Uses google book rest api.
 * 
 * @author Umut Utkan
 */
public class GoogleBookService extends BaseBookService {
	
	private final static String LT = GoogleBookService.class.getSimpleName();

	private static final String API_REST_HOST = "books.google.com";
	private static final String API_REST_URL = "/books/feeds/volumes";
//	private static final String API_REST_HOST = "localhost";
//	private static final String API_REST_URL = "/book.php/books/feeds/volumes";

	private static final String API_ITEM_LOOKUP = "q";

	private static final String PARAM_MAX_RESULTS = "max-results";
	private static final String PARAM_START_INDEX = "start-index";

	private static final String VALUE_MAX_RESULTS = "10";
	private static final String VALUE_START_INDEX = "1";

	private static final String RESPONSE_TAG_FEED = "feed";
	private static final String RESPONSE_TAG_ENTRY = "entry";
	private static final String RESPONSE_TAG_ITEMS_PER_PAGE = "itemsPerPage";
	private static final String RESPONSE_TAG_TOTAL_RESULTS = "totalResults";
	private static final String RESPONSE_TAG_IDENTIFIER = "identifier";
	private static final String RESPONSE_TAG_TITLE = "title";
	private static final String RESPONSE_TAG_PUBLISHER = "publisher";
	private static final String RESPONSE_TAG_CREATOR = "creator";
	private static final String RESPONSE_TAG_DESCRIPTION = "description";
	private static final String RESPONSE_TAG_LINK = "link";
	private static final String RESPONSE_TAG_FORMAT = "format";
	private static final String RESPONSE_TAG_DATE = "date";

	private static final String RESPONSE_ATTR_REL = "rel";
	private static final String RESPONSE_ATTR_HREF = "href";

	private static final String RESPONSE_VALUE_THUMBNAIL = "http://schemas.google.com/books/2008/thumbnail";
	private static final String RESPONSE_VALUE_INFO = "http://schemas.google.com/books/2008/info";
	private static final String RESPONSE_VALUE_PAGES_SUFFIX = "pages";
	
	private final static String NAMESPACE_PURL = "http://purl.org/dc/terms";

	@Override
	public Uri.Builder buildGetBookQuery(String id) {
		final Uri.Builder uri = buildGetMethod();
		uri.appendPath(id);
		return uri;
	}

	@Override
	public Uri.Builder buildSearchBookQuery(String query) {
		final Uri.Builder uri = buildGetMethod();
		uri.appendQueryParameter(API_ITEM_LOOKUP, query);
		uri.appendQueryParameter(PARAM_START_INDEX, VALUE_START_INDEX);
		uri.appendQueryParameter(PARAM_MAX_RESULTS, VALUE_MAX_RESULTS);
		return uri;
	}

	@Override
	public boolean parseSearchInfo(XmlPullParser parser, BookSearchInfo info) throws XmlPullParserException, IOException {
		int maxResults = 10;
		int totalResults = 10;
		int type;
		while((type = parser.next()) != XmlPullParser.END_DOCUMENT) {
			if (type != XmlPullParser.START_TAG) {
				continue;
			}

			if(RESPONSE_TAG_TOTAL_RESULTS.equals(parser.getName())) {
				if(parser.next() == XmlPullParser.TEXT) {
					totalResults = Integer.parseInt(parser.getText());
				}
			} else 
			if (RESPONSE_TAG_ITEMS_PER_PAGE.equals(parser.getName())) {
				if(parser.next() == XmlPullParser.TEXT) {
					maxResults = Integer.parseInt(parser.getText());
					info.setResultCount(totalResults < maxResults ? totalResults : maxResults);
//					String text = parser.getText();
//					info.setResultCount(Integer.parseInt(text));
					return true;
				}
			}
		}

		return false;
	}
	
	@Override
	public boolean findNextBook(XmlPullParser parser) throws XmlPullParserException, IOException {
		if (RESPONSE_TAG_ENTRY.equals(parser.getName())) {
			return true;
		}

		int type;
//		final int depth = parser.getDepth();
//		while (((type = parser.next()) != XmlPullParser.END_TAG || parser.getDepth() > depth)
//				&& type != XmlPullParser.END_DOCUMENT) {
		while((type = parser.next()) != XmlPullParser.END_DOCUMENT) {
			if (type != XmlPullParser.START_TAG) {
				continue;
			}

			if (RESPONSE_TAG_ENTRY.equals(parser.getName())) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected boolean parseBook(XmlPullParser parser, Book book) throws XmlPullParserException, IOException {
		Log.i(LT, "Parsing new book.");
		
		int type;
		String name;
		boolean inEntry = false;
		boolean isValid = false;
		final int depth = parser.getDepth();

		if (RESPONSE_TAG_ENTRY.equals(parser.getName()))
			isValid = inEntry = true;

		while (((type = parser.next()) != XmlPullParser.END_TAG || parser.getDepth() > depth)
				&& type != XmlPullParser.END_DOCUMENT) {
			if (type != XmlPullParser.START_TAG) {
				continue;
			}

			name = parser.getName();
			
			if (RESPONSE_TAG_ITEMS_PER_PAGE.equals(name)) {
				if (parser.next() != XmlPullParser.TEXT || !"1".equals(parser.getText())) {
					throw new IOException("Invalid request, 1 result is required");
				} else {
					isValid = true;
				}
//			} else if(RESPONSE_TAG_ID.equals(name)) {
//				if(parser.next() == XmlPullParser.TEXT) {
//					book.setId(parser.getText());
//				}			
			} else if (RESPONSE_TAG_IDENTIFIER.equals(name)) {
				if (parser.next() == XmlPullParser.TEXT) {
					String value = parser.getText();
					if (value.startsWith("ISBN:")) {
						value = value.substring(5);
						switch (value.length()) {
						case 10:
							book.addIsbn(value);
							break;
						case 13:
							book.addIsbn(value);
							break;
						}
					} else {
						book.setId(value);
					}
				}
			} else if (RESPONSE_TAG_ENTRY.equals(name)) {
				inEntry = true;
			} else if (RESPONSE_TAG_TITLE.equals(name) && inEntry && NAMESPACE_PURL.equals(parser.getNamespace())) {
				if (parser.next() == XmlPullParser.TEXT) {
					book.addTitle(parser.getText());
				}
			} else if (RESPONSE_TAG_PUBLISHER.equals(name)) {
				if (parser.next() == XmlPullParser.TEXT) {
					book.setPublisher(parser.getText());
				}
			} else if (RESPONSE_TAG_CREATOR.equals(name)) {
				if (parser.next() == XmlPullParser.TEXT) {
					// TODO: add author olmasi lazim
					book.setAuthor(parser.getText());
				}
			} else if (RESPONSE_TAG_DESCRIPTION.equals(name)) {
				if (parser.next() == XmlPullParser.TEXT) {
					book.setDescription(parser.getText());
				}
			} else if (RESPONSE_TAG_LINK.equals(name)) {
				final String rel = parser.getAttributeValue(null, RESPONSE_ATTR_REL);
				if (RESPONSE_VALUE_THUMBNAIL.equals(rel)) {
					final String url = parser.getAttributeValue(null, RESPONSE_ATTR_HREF);
					book.putImage(ImageSize.TINY, url);
					book.putImage(ImageSize.THUMNAIL, url.replace("zoom=5", "zoom=1"));
				} else if (RESPONSE_VALUE_INFO.equals(rel)) {
					// book.mDetailsUrl = parser.getAttributeValue(null, RESPONSE_ATTR_HREF);
				}
			} else if (RESPONSE_TAG_FORMAT.equals(name)) {
				if (parser.next() == XmlPullParser.TEXT) {
					String format = parser.getText();
					if (format.endsWith(RESPONSE_VALUE_PAGES_SUFFIX)) {
						book.setPages(Integer.parseInt(format.substring(0,
								format.length() - RESPONSE_VALUE_PAGES_SUFFIX.length()).trim()));
					}
				}
			} else if (RESPONSE_TAG_DATE.equals(name)) {
				if (parser.next() == XmlPullParser.TEXT) {
					// final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					// try {
					// book.mPublicationDate = format.parse(parser.getText());
					// } catch (ParseException e) {
					// // Ignore
					// }
					book.setDate(parser.getText());
				}
			}
		}

		isValid = isValid && (book.getIsbn13() != null || book.getIsbn10() != null);
		return isValid;
	}

	@Override
	protected String getHttpHost() {
		return "http://" + API_REST_HOST;
	}

	private static Uri.Builder buildGetMethod() {
		final Uri.Builder builder = new Uri.Builder();
		builder.path(API_REST_URL);
		return builder;
	}

}
