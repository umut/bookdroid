package com.hoydaa.bookdroid.provider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import junit.framework.TestCase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.net.Uri;

import com.hoydaa.bookdroid.http.DefaultHttpClient;
import com.hoydaa.bookdroid.http.ResponseHandler;
import com.hoydaa.bookdroid.service.GoogleBookService;
import com.hoydaa.bookdroid.service.BookService.BookSearchInfo;

/**
 * Tests {@link GoogleBookService}.
 * 
 * @author Umut Utkan
 */
public class GoogleBookServiceTest extends TestCase {

	public void testBuildGetBookQuery() {
		GoogleBookService s = new GoogleBookService();
		Uri.Builder builder = s.buildGetBookQuery("QWERTY");
		assertEquals("/books/feeds/volumes/QWERTY", builder.build().toString());
	}

	public void testBuildSearchBookQuery() {
		GoogleBookService s = new GoogleBookService();
		Uri.Builder builder = s.buildSearchBookQuery("Soul Music");
		assertEquals("/books/feeds/volumes?q=Soul%20Music&start-index=1&max-results=10", builder.build().toString());
	}

	public void testParseSearchInfo() throws XmlPullParserException, IOException {
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser p = factory.newPullParser();
		p.setInput(new StringReader(SAMPLE_CONTENT_10_BOOKS));
		GoogleBookService s = new GoogleBookService();
		
		BookSearchInfo info = new BookSearchInfo();
		s.parseSearchInfo(p, info);
		
		assertEquals(10, info.getResultCount());
	}
	
	public void testFindNextBook() throws XmlPullParserException, IOException {
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser p = factory.newPullParser();
		p.setInput(new StringReader(SAMPLE_CONTENT_ONE_BOOK));
		GoogleBookService s = new GoogleBookService();
		assertTrue(s.findNextBook(p));
		// consume the next element
		p.next();
		assertFalse(s.findNextBook(p));

		p = factory.newPullParser();
		p.setInput(new StringReader(SAMPLE_CONTENT_10_BOOKS));
		for (int i = 0; i < 10; i++) {
			assertTrue(s.findNextBook(p));
			p.next();
			p.next();
		}
		assertFalse(s.findNextBook(p));
	}

	public void testSearchBook() {
		GoogleBookService s = getService(SAMPLE_CONTENT_ONE_BOOK);

		List<Book> books = s.searchBook("not important", null);

		assertNotNull(books);
		assertEquals(1, books.size());
		Book book = books.get(0);
		assertEquals("R7ZqPgAACAAJ", book.getId());
		assertEquals("Succeeding with Agile Software Development Using Scrum", book.getTitle());
		assertEquals("Mike Cohn", book.getAuthor());
		assertTrue(book.getDescription().contains("Proven") && book.getDescription().contains("starting"));
		assertEquals("Addison-Wesley Professional", book.getPublisher());
		assertEquals((Integer) 475, book.getPages());
		assertEquals("2009-07-10", book.getDate());
		assertEquals("0321579364, 9780321579362", book.getIsbn());
		assertEquals(2, book.getImages().size());
		// TODO: will be corrected
		// assertNotNull(book.getThumbnail());

		s = getService(SAMPLE_CONTENT_10_BOOKS);
		books = s.searchBook("not important", null);
		assertEquals(10, books.size());
	}

	private GoogleBookService getService(final String content) {
		GoogleBookService s = new GoogleBookService();
		s.setHttpClient(new DefaultHttpClient() {

			@Override
			public void retrieveWithHandler(String url, ResponseHandler handler) {
				handler.handle(200, new ByteArrayInputStream(content.getBytes()));
			}

		});
		return s;
	}

	private final static String SAMPLE_CONTENT_ONE_BOOK = "<?xml version='1.0' encoding='UTF-8'?>\n"
			+ "<feed xmlns='http://www.w3.org/2005/Atom' xmlns:openSearch='http://a9.com/-/spec/opensearchrss/1.0/' xmlns:gbs='http://schemas.google.com/books/2008'\n"
			+ "\txmlns:dc='http://purl.org/dc/terms' xmlns:batch='http://schemas.google.com/gdata/batch' xmlns:gd='http://schemas.google.com/g/2005'>\n"
			+ "\t<id>http://www.google.com/books/feeds/volumes</id>\n"
			+ "\t<updated>2010-03-09T21:48:07.000Z</updated>\n"
			+ "\t<category scheme='http://schemas.google.com/g/2005#kind' term='http://schemas.google.com/books/2008#volume' />\n"
			+ "\t<title type='text'>Search results for 9780321579362</title>\n"
			+ "\t<link rel='alternate' type='text/html' href='http://www.google.com' />\n"
			+ "\t<link rel='http://schemas.google.com/g/2005#feed' type='application/atom+xml' href='http://www.google.com/books/feeds/volumes' />\n"
			+ "\t<link rel='self' type='application/atom+xml' href='http://www.google.com/books/feeds/volumes?q=9780321579362' />\n"
			+ "\t<author>\n"
			+ "\t\t<name>Google Books Search</name>\n"
			+ "\t\t<uri>http://www.google.com</uri>\n"
			+ "\t</author>\n"
			+ "\t<generator version='beta'>Google Book Search data API</generator>\n"
			+ "\t<openSearch:totalResults>1</openSearch:totalResults>\n"
			+ "\t<openSearch:startIndex>1</openSearch:startIndex>\n"
			+ "\t<openSearch:itemsPerPage>1</openSearch:itemsPerPage>\n"
			+ "\t<entry>\n"
			+ "\t\t<id>http://www.google.com/books/feeds/volumes/R7ZqPgAACAAJ</id>\n"
			+ "\t\t<updated>2010-03-09T21:48:07.000Z</updated>\n"
			+ "\t\t<category scheme='http://schemas.google.com/g/2005#kind' term='http://schemas.google.com/books/2008#volume' />\n"
			+ "\t\t<title type='text'>Succeeding with Agile</title>\n"
			+ "\t\t<link rel='http://schemas.google.com/books/2008/thumbnail' type='image/x-unknown'\n"
			+ "\t\t\thref='http://bks5.books.google.com/books?id=R7ZqPgAACAAJ&amp;printsec=frontcover&amp;img=1&amp;zoom=5&amp;sig=ACfU3U36gTpl_qCdw5Gb4XLdVT10kF1mNw&amp;source=gbs_gdata' />\n"
			+ "\t\t<link rel='http://schemas.google.com/books/2008/info' type='text/html'\n"
			+ "\t\t\thref='http://books.google.com/books?id=R7ZqPgAACAAJ&amp;dq=9780321579362&amp;ie=ISO-8859-1&amp;source=gbs_gdata' />\n"
			+ "\t\t<link rel='http://schemas.google.com/books/2008/preview' type='text/html'\n"
			+ "\t\t\thref='http://books.google.com/books?id=R7ZqPgAACAAJ&amp;dq=9780321579362&amp;ie=ISO-8859-1&amp;cd=1&amp;source=gbs_gdata' />\n"
			+ "\t\t<link rel='http://schemas.google.com/books/2008/annotation' type='application/atom+xml'\n"
			+ "\t\t\thref='http://www.google.com/books/feeds/users/me/volumes' />\n"
			+ "\t\t<link rel='alternate' type='text/html' href='http://books.google.com/books?id=R7ZqPgAACAAJ&amp;dq=9780321579362&amp;ie=ISO-8859-1' />\n"
			+ "\t\t<link rel='self' type='application/atom+xml' href='http://www.google.com/books/feeds/volumes/R7ZqPgAACAAJ' />\n"
			+ "\t\t<gbs:embeddability value='http://schemas.google.com/books/2008#not_embeddable' />\n"
			+ "\t\t<gbs:openAccess value='http://schemas.google.com/books/2008#disabled' />\n"
			+ "\t\t<gbs:viewability value='http://schemas.google.com/books/2008#view_no_pages' />\n"
			+ "\t\t<dc:creator>Mike Cohn</dc:creator>\n" + "\t\t<dc:date>2009-07-10</dc:date>\n"
			+ "\t\t<dc:description>Proven, 100% Practical Guidance for Making Scrum and Agile Work in Any\n"
			+ "\t\t\tOrganization This is the definitive, realistic, actionable guide to starting\n"
			+ "\t\t\tfast ...</dc:description>\n" + "\t\t<dc:format>475 pages</dc:format>\n"
			+ "\t\t<dc:format>book</dc:format>\n" + "\t\t<dc:identifier>R7ZqPgAACAAJ</dc:identifier>\n"
			+ "\t\t<dc:identifier>ISBN:0321579364</dc:identifier>\n"
			+ "\t\t<dc:identifier>ISBN:9780321579362</dc:identifier>\n"
			+ "\t\t<dc:publisher>Addison-Wesley Professional</dc:publisher>\n"
			+ "\t\t<dc:subject>Computers</dc:subject>\n" + "\t\t<dc:title>Succeeding with Agile</dc:title>\n"
			+ "\t\t<dc:title>Software Development Using Scrum</dc:title>\n" + "\t</entry>\n" + "</feed>";

	private static final String SAMPLE_CONTENT_10_BOOKS = "<?xml version='1.0' encoding='UTF-8'?><feed xmlns='http://www.w3.org/2005/Atom' xmlns:openSearch='http://a9.com/-/spec/opensearchrss/1.0/' xmlns:gbs='http://schemas.google.com/books/2008' xmlns:dc='http://purl.org/dc/terms' xmlns:batch='http://schemas.google.com/gdata/batch' xmlns:gd='http://schemas.google.com/g/2005'><id>http://www.google.com/books/feeds/volumes</id><updated>2010-03-15T22:47:00.000Z</updated><category scheme='http://schemas.google.com/g/2005#kind' term='http://schemas.google.com/books/2008#volume'/><title type='text'>Search results for php</title><link rel='alternate' type='text/html' href='http://www.google.com'/><link rel='http://schemas.google.com/g/2005#feed' type='application/atom+xml' href='http://www.google.com/books/feeds/volumes'/><link rel='self' type='application/atom+xml' href='http://www.google.com/books/feeds/volumes?q=php'/><link rel='next' type='application/atom+xml' href='http://www.google.com/books/feeds/volumes?q=php&amp;start-index=11&amp;max-results=10'/><author><name>Google Books Search</name><uri>http://www.google.com</uri></author><generator version='beta'>Google Book Search data API</generator><openSearch:totalResults>392</openSearch:totalResults><openSearch:startIndex>1</openSearch:startIndex><openSearch:itemsPerPage>10</openSearch:itemsPerPage><entry><id>http://www.google.com/books/feeds/volumes/tywvv3ULal0C</id><updated>2010-03-15T22:47:00.000Z</updated><category scheme='http://schemas.google.com/g/2005#kind' term='http://schemas.google.com/books/2008#volume'/><title type='text'>Programming PHP</title><link rel='http://schemas.google.com/books/2008/thumbnail' type='image/x-unknown' href='http://bks8.books.google.com/books?id=tywvv3ULal0C&amp;printsec=frontcover&amp;img=1&amp;zoom=5&amp;edge=curl&amp;sig=ACfU3U0WFIZOyvLPjIv7jqlX4XZ7GI4TAg&amp;source=gbs_gdata'/><link rel='http://schemas.google.com/books/2008/info' type='text/html' href='http://books.google.com/books?id=tywvv3ULal0C&amp;dq=php&amp;ie=ISO-8859-1&amp;source=gbs_gdata'/><link rel='http://schemas.google.com/books/2008/preview' type='text/html' href='http://books.google.com/books?id=tywvv3ULal0C&amp;printsec=frontcover&amp;dq=php&amp;ie=ISO-8859-1&amp;cd=1&amp;source=gbs_gdata'/><link rel='http://schemas.google.com/books/2008/annotation' type='application/atom+xml' href='http://www.google.com/books/feeds/users/me/volumes'/><link rel='alternate' type='text/html' href='http://books.google.com/books?id=tywvv3ULal0C&amp;dq=php&amp;ie=ISO-8859-1'/><link rel='self' type='application/atom+xml' href='http://www.google.com/books/feeds/volumes/tywvv3ULal0C'/><gbs:embeddability value='http://schemas.google.com/books/2008#embeddable'/><gbs:openAccess value='http://schemas.google.com/books/2008#disabled'/><gbs:viewability value='http://schemas.google.com/books/2008#view_partial'/><dc:creator>Rasmus Lerdorf</dc:creator><dc:creator>Kevin Tatroe</dc:creator><dc:creator>Peter MacIntyre</dc:creator><dc:date>2006</dc:date><dc:description>With style tips and practical programming advice, this book will help you become not just a PHP programmer, but a &amp;quot;good&amp;quot; PHP programmer.</dc:description><dc:format>521 pages</dc:format><dc:format>book</dc:format><dc:identifier>tywvv3ULal0C</dc:identifier><dc:identifier>ISBN:0596006810</dc:identifier><dc:identifier>ISBN:9780596006815</dc:identifier><dc:publisher>O'Reilly Media, Inc.</dc:publisher><dc:subject>Computers</dc:subject><dc:title>Programming PHP</dc:title></entry><entry><id>http://www.google.com/books/feeds/volumes/B_OCVIL_W80C</id><updated>2010-03-15T22:47:00.000Z</updated><category scheme='http://schemas.google.com/g/2005#kind' term='http://schemas.google.com/books/2008#volume'/><title type='text'>Beginning PHP and MySQL 5</title><link rel='http://schemas.google.com/books/2008/thumbnail' type='image/x-unknown' href='http://bks6.books.google.com/books?id=B_OCVIL_W80C&amp;printsec=frontcover&amp;img=1&amp;zoom=5&amp;edge=curl&amp;sig=ACfU3U26VzBr8Ibcn7gsrYEls0Sz-RRlaw&amp;source=gbs_gdata'/><link rel='http://schemas.google.com/books/2008/info' type='text/html' href='http://books.google.com/books?id=B_OCVIL_W80C&amp;dq=php&amp;ie=ISO-8859-1&amp;source=gbs_gdata'/><link rel='http://schemas.google.com/books/2008/preview' type='text/html' href='http://books.google.com/books?id=B_OCVIL_W80C&amp;printsec=frontcover&amp;dq=php&amp;ie=ISO-8859-1&amp;cd=2&amp;source=gbs_gdata'/><link rel='http://schemas.google.com/books/2008/annotation' type='application/atom+xml' href='http://www.google.com/books/feeds/users/me/volumes'/><link rel='alternate' type='text/html' href='http://books.google.com/books?id=B_OCVIL_W80C&amp;dq=php&amp;ie=ISO-8859-1'/><link rel='self' type='application/atom+xml' href='http://www.google.com/books/feeds/volumes/B_OCVIL_W80C'/><gbs:embeddability value='http://schemas.google.com/books/2008#embeddable'/><gbs:openAccess value='http://schemas.google.com/books/2008#disabled'/><gbs:viewability value='http://schemas.google.com/books/2008#view_partial'/><dc:creator>W. J. Gilmore</dc:creator><dc:date>2006-01-23</dc:date><dc:description>This best-selling book ranks among the most thorough and practical guides in print, covering all of the key concepts and features, and showing you how to ...</dc:description><dc:format>913 pages</dc:format><dc:format>book</dc:format><dc:identifier>B_OCVIL_W80C</dc:identifier><dc:identifier>ISBN:1590595521</dc:identifier><dc:identifier>ISBN:9781590595527</dc:identifier><dc:subject>Computers</dc:subject><dc:title>Beginning PHP and MySQL 5</dc:title><dc:title>from novice to professional</dc:title></entry><entry><id>http://www.google.com/books/feeds/volumes/PVvmMRSGzFEC</id><updated>2010-03-15T22:47:00.000Z</updated><category scheme='http://schemas.google.com/g/2005#kind' term='http://schemas.google.com/books/2008#volume'/><title type='text'>Learning PHP 5</title><link rel='http://schemas.google.com/books/2008/thumbnail' type='image/x-unknown' href='http://bks7.books.google.com/books?id=PVvmMRSGzFEC&amp;printsec=frontcover&amp;img=1&amp;zoom=5&amp;edge=curl&amp;sig=ACfU3U0sEygBBuptUmKXJcGw18llQVCZiA&amp;source=gbs_gdata'/><link rel='http://schemas.google.com/books/2008/info' type='text/html' href='http://books.google.com/books?id=PVvmMRSGzFEC&amp;dq=php&amp;ie=ISO-8859-1&amp;source=gbs_gdata'/><link rel='http://schemas.google.com/books/2008/preview' type='text/html' href='http://books.google.com/books?id=PVvmMRSGzFEC&amp;printsec=frontcover&amp;dq=php&amp;ie=ISO-8859-1&amp;cd=3&amp;source=gbs_gdata'/><link rel='http://schemas.google.com/books/2008/annotation' type='application/atom+xml' href='http://www.google.com/books/feeds/users/me/volumes'/><link rel='alternate' type='text/html' href='http://books.google.com/books?id=PVvmMRSGzFEC&amp;dq=php&amp;ie=ISO-8859-1'/><link rel='self' type='application/atom+xml' href='http://www.google.com/books/feeds/volumes/PVvmMRSGzFEC'/><gbs:embeddability value='http://schemas.google.com/books/2008#embeddable'/><gbs:openAccess value='http://schemas.google.com/books/2008#disabled'/><gbs:viewability value='http://schemas.google.com/books/2008#view_partial'/><dc:creator>David Sklar</dc:creator><dc:date>2004-06-01</dc:date><dc:description>&amp;quot;Learning PHP 5&amp;quot; covers the following topics, and more: How PHP works with your web browser and web server PHP language basics, including data, variables, logic ...</dc:description><dc:format>350 pages</dc:format><dc:format>book</dc:format><dc:identifier>PVvmMRSGzFEC</dc:identifier><dc:identifier>ISBN:0596005601</dc:identifier><dc:identifier>ISBN:9780596005603</dc:identifier><dc:publisher>O'Reilly Media, Inc.</dc:publisher><dc:subject>Computers</dc:subject><dc:title>Learning PHP 5</dc:title></entry><entry><id>http://www.google.com/books/feeds/volumes/hyFqBDv8_UsC</id><updated>2010-03-15T22:47:00.000Z</updated><category scheme='http://schemas.google.com/g/2005#kind' term='http://schemas.google.com/books/2008#volume'/><title type='text'>PHP and MySQL for dynamic Web sites</title><link rel='http://schemas.google.com/books/2008/thumbnail' type='image/x-unknown' href='http://bks5.books.google.com/books?id=hyFqBDv8_UsC&amp;printsec=frontcover&amp;img=1&amp;zoom=5&amp;edge=curl&amp;sig=ACfU3U2cVTSxaOvkX92fn8erZCUhtXaPjQ&amp;source=gbs_gdata'/><link rel='http://schemas.google.com/books/2008/info' type='text/html' href='http://books.google.com/books?id=hyFqBDv8_UsC&amp;dq=php&amp;ie=ISO-8859-1&amp;source=gbs_gdata'/><link rel='http://schemas.google.com/books/2008/preview' type='text/html' href='http://books.google.com/books?id=hyFqBDv8_UsC&amp;printsec=frontcover&amp;dq=php&amp;ie=ISO-8859-1&amp;cd=4&amp;source=gbs_gdata'/><link rel='http://schemas.google.com/books/2008/annotation' type='application/atom+xml' href='http://www.google.com/books/feeds/users/me/volumes'/><link rel='alternate' type='text/html' href='http://books.google.com/books?id=hyFqBDv8_UsC&amp;dq=php&amp;ie=ISO-8859-1'/><link rel='self' type='application/atom+xml' href='http://www.google.com/books/feeds/volumes/hyFqBDv8_UsC'/><gbs:embeddability value='http://schemas.google.com/books/2008#embeddable'/><gbs:openAccess value='http://schemas.google.com/books/2008#disabled'/><gbs:viewability value='http://schemas.google.com/books/2008#view_partial'/><dc:creator>Larry Edward Ullman</dc:creator><dc:date>2003</dc:date><dc:description>If you&amp;#39;re already at home with HTML, you&amp;#39;ll find this volume the perfect launching pad to creating dynamic sites with PHP and MySQL.</dc:description><dc:format>572 pages</dc:format><dc:format>book</dc:format><dc:identifier>hyFqBDv8_UsC</dc:identifier><dc:identifier>ISBN:0321186486</dc:identifier><dc:identifier>ISBN:9780321186485</dc:identifier><dc:publisher>Peachpit Pr</dc:publisher><dc:subject>Computers</dc:subject><dc:title>PHP and MySQL for dynamic Web sites</dc:title></entry><entry><id>http://www.google.com/books/feeds/volumes/eFqIwfehkQAC</id><updated>2010-03-15T22:47:00.000Z</updated><category scheme='http://schemas.google.com/g/2005#kind' term='http://schemas.google.com/books/2008#volume'/><title type='text'>PHP for the World Wide Web</title><link rel='http://schemas.google.com/books/2008/thumbnail' type='image/x-unknown' href='http://bks5.books.google.com/books?id=eFqIwfehkQAC&amp;printsec=frontcover&amp;img=1&amp;zoom=5&amp;sig=ACfU3U3rK4gAFI9WO_OVQke5UtDbQCbj8g&amp;source=gbs_gdata'/><link rel='http://schemas.google.com/books/2008/info' type='text/html' href='http://books.google.com/books?id=eFqIwfehkQAC&amp;dq=php&amp;ie=ISO-8859-1&amp;source=gbs_gdata'/><link rel='http://schemas.google.com/books/2008/preview' type='text/html' href='http://books.google.com/books?id=eFqIwfehkQAC&amp;q=php&amp;dq=php&amp;ie=ISO-8859-1&amp;cd=5&amp;source=gbs_gdata'/><link rel='http://schemas.google.com/books/2008/annotation' type='application/atom+xml' href='http://www.google.com/books/feeds/users/me/volumes'/><link rel='alternate' type='text/html' href='http://books.google.com/books?id=eFqIwfehkQAC&amp;dq=php&amp;ie=ISO-8859-1'/><link rel='self' type='application/atom+xml' href='http://www.google.com/books/feeds/volumes/eFqIwfehkQAC'/><gbs:embeddability value='http://schemas.google.com/books/2008#not_embeddable'/><gbs:openAccess value='http://schemas.google.com/books/2008#disabled'/><gbs:viewability value='http://schemas.google.com/books/2008#view_no_pages'/><dc:creator>Larry Edward Ullman</dc:creator><dc:date>2004-02-02</dc:date><dc:description>Aimed at beginning PHP developers just like yourself, this volume uses step-by-step instructions and plenty of visual aids to get you started testing scripts, ...</dc:description><dc:format>450 pages</dc:format><dc:format>book</dc:format><dc:identifier>eFqIwfehkQAC</dc:identifier><dc:identifier>ISBN:0321245652</dc:identifier><dc:identifier>ISBN:9780321245656</dc:identifier><dc:publisher>Peachpit Pr</dc:publisher><dc:subject>Computers</dc:subject><dc:title>PHP for the World Wide Web</dc:title></entry><entry><id>http://www.google.com/books/feeds/volumes/i8kJAAAACAAJ</id><updated>2010-03-15T22:47:00.000Z</updated><category scheme='http://schemas.google.com/g/2005#kind' term='http://schemas.google.com/books/2008#volume'/><title type='text'>Php Manual</title><link rel='http://schemas.google.com/books/2008/thumbnail' type='image/x-unknown' href='http://bks1.books.google.com/books?id=i8kJAAAACAAJ&amp;printsec=frontcover&amp;img=1&amp;zoom=5&amp;sig=ACfU3U3KrhRcAomZU4C1P_BLLJ91x0Q5gw&amp;source=gbs_gdata'/><link rel='http://schemas.google.com/books/2008/info' type='text/html' href='http://books.google.com/books?id=i8kJAAAACAAJ&amp;dq=php&amp;ie=ISO-8859-1&amp;source=gbs_gdata'/><link rel='http://schemas.google.com/books/2008/preview' type='text/html' href='http://books.google.com/books?id=i8kJAAAACAAJ&amp;dq=php&amp;ie=ISO-8859-1&amp;cd=6&amp;source=gbs_gdata'/><link rel='http://schemas.google.com/books/2008/annotation' type='application/atom+xml' href='http://www.google.com/books/feeds/users/me/volumes'/><link rel='alternate' type='text/html' href='http://books.google.com/books?id=i8kJAAAACAAJ&amp;dq=php&amp;ie=ISO-8859-1'/><link rel='self' type='application/atom+xml' href='http://www.google.com/books/feeds/volumes/i8kJAAAACAAJ'/><gbs:embeddability value='http://schemas.google.com/books/2008#not_embeddable'/><gbs:openAccess value='http://schemas.google.com/books/2008#disabled'/><gbs:viewability value='http://schemas.google.com/books/2008#view_no_pages'/><dc:creator>Stig Saether Bakken</dc:creator><dc:creator>Egon Schmid</dc:creator><dc:creator>Zeev Suraski</dc:creator><dc:date>2000-10</dc:date><dc:format>524 pages</dc:format><dc:format>book</dc:format><dc:identifier>i8kJAAAACAAJ</dc:identifier><dc:identifier>ISBN:0595132286</dc:identifier><dc:identifier>ISBN:9780595132287</dc:identifier><dc:publisher>iUniverse</dc:publisher><dc:subject>Computers</dc:subject><dc:title>Php Manual</dc:title></entry><entry><id>http://www.google.com/books/feeds/volumes/mccNIQAACAAJ</id><updated>2010-03-15T22:47:00.000Z</updated><category scheme='http://schemas.google.com/g/2005#kind' term='http://schemas.google.com/books/2008#volume'/><title type='text'>PHP 5 avancé</title><link rel='http://schemas.google.com/books/2008/info' type='text/html' href='http://books.google.com/books?id=mccNIQAACAAJ&amp;dq=php&amp;ie=ISO-8859-1&amp;source=gbs_gdata'/><link rel='http://schemas.google.com/books/2008/preview' type='text/html' href='http://books.google.com/books?id=mccNIQAACAAJ&amp;dq=php&amp;ie=ISO-8859-1&amp;cd=7&amp;source=gbs_gdata'/><link rel='http://schemas.google.com/books/2008/annotation' type='application/atom+xml' href='http://www.google.com/books/feeds/users/me/volumes'/><link rel='alternate' type='text/html' href='http://books.google.com/books?id=mccNIQAACAAJ&amp;dq=php&amp;ie=ISO-8859-1'/><link rel='self' type='application/atom+xml' href='http://www.google.com/books/feeds/volumes/mccNIQAACAAJ'/><gbs:embeddability value='http://schemas.google.com/books/2008#not_embeddable'/><gbs:openAccess value='http://schemas.google.com/books/2008#disabled'/><gbs:viewability value='http://schemas.google.com/books/2008#view_no_pages'/><dc:creator>Éric Daspet</dc:creator><dc:creator>Cyril Pierre de Geyer</dc:creator><dc:date>2007</dc:date><dc:format>791 pages</dc:format><dc:format>book</dc:format><dc:identifier>mccNIQAACAAJ</dc:identifier><dc:identifier>ISBN:2212121679</dc:identifier><dc:identifier>ISBN:9782212121674</dc:identifier><dc:title>PHP 5 avancé</dc:title></entry><entry><id>http://www.google.com/books/feeds/volumes/If3TLnM0s3kC</id><updated>2010-03-15T22:47:00.000Z</updated><category scheme='http://schemas.google.com/g/2005#kind' term='http://schemas.google.com/books/2008#volume'/><title type='text'>PHP</title><link rel='http://schemas.google.com/books/2008/thumbnail' type='image/x-unknown' href='http://bks5.books.google.com/books?id=If3TLnM0s3kC&amp;printsec=frontcover&amp;img=1&amp;zoom=5&amp;edge=curl&amp;sig=ACfU3U1Nae9B6-xfCfl1d-_3XWV3_WnaLw&amp;source=gbs_gdata'/><link rel='http://schemas.google.com/books/2008/info' type='text/html' href='http://books.google.com/books?id=If3TLnM0s3kC&amp;dq=php&amp;ie=ISO-8859-1&amp;source=gbs_gdata'/><link rel='http://schemas.google.com/books/2008/preview' type='text/html' href='http://books.google.com/books?id=If3TLnM0s3kC&amp;printsec=frontcover&amp;dq=php&amp;ie=ISO-8859-1&amp;cd=8&amp;source=gbs_gdata'/><link rel='http://schemas.google.com/books/2008/annotation' type='application/atom+xml' href='http://www.google.com/books/feeds/users/me/volumes'/><link rel='alternate' type='text/html' href='http://books.google.com/books?id=If3TLnM0s3kC&amp;dq=php&amp;ie=ISO-8859-1'/><link rel='self' type='application/atom+xml' href='http://www.google.com/books/feeds/volumes/If3TLnM0s3kC'/><gbs:embeddability value='http://schemas.google.com/books/2008#embeddable'/><gbs:openAccess value='http://schemas.google.com/books/2008#disabled'/><gbs:viewability value='http://schemas.google.com/books/2008#view_partial'/><dc:creator>Vikram Vaswani</dc:creator><dc:date>2008-10-02</dc:date><dc:description>Essential Skills--Made Easy!Learn how to build dynamic, data-driven Web applications using PHP.</dc:description><dc:format>478 pages</dc:format><dc:format>book</dc:format><dc:identifier>If3TLnM0s3kC</dc:identifier><dc:identifier>ISBN:0071549013</dc:identifier><dc:identifier>ISBN:9780071549011</dc:identifier><dc:publisher>McGraw-Hill/Osborne Media</dc:publisher><dc:subject>Computers</dc:subject><dc:title>PHP</dc:title><dc:title>A BEGINNER'S GUIDE</dc:title></entry><entry><id>http://www.google.com/books/feeds/volumes/i5E_i8ywXSIC</id><updated>2010-03-15T22:47:00.000Z</updated><category scheme='http://schemas.google.com/g/2005#kind' term='http://schemas.google.com/books/2008#volume'/><title type='text'>PHP pocket reference</title><link rel='http://schemas.google.com/books/2008/thumbnail' type='image/x-unknown' href='http://bks3.books.google.com/books?id=i5E_i8ywXSIC&amp;printsec=frontcover&amp;img=1&amp;zoom=5&amp;edge=curl&amp;sig=ACfU3U3AKMTqHmLt3xcVHoYFk7I6sRAwDQ&amp;source=gbs_gdata'/><link rel='http://schemas.google.com/books/2008/info' type='text/html' href='http://books.google.com/books?id=i5E_i8ywXSIC&amp;dq=php&amp;ie=ISO-8859-1&amp;source=gbs_gdata'/><link rel='http://schemas.google.com/books/2008/preview' type='text/html' href='http://books.google.com/books?id=i5E_i8ywXSIC&amp;printsec=frontcover&amp;dq=php&amp;ie=ISO-8859-1&amp;cd=9&amp;source=gbs_gdata'/><link rel='http://schemas.google.com/books/2008/annotation' type='application/atom+xml' href='http://www.google.com/books/feeds/users/me/volumes'/><link rel='alternate' type='text/html' href='http://books.google.com/books?id=i5E_i8ywXSIC&amp;dq=php&amp;ie=ISO-8859-1'/><link rel='self' type='application/atom+xml' href='http://www.google.com/books/feeds/volumes/i5E_i8ywXSIC'/><gbs:embeddability value='http://schemas.google.com/books/2008#embeddable'/><gbs:openAccess value='http://schemas.google.com/books/2008#disabled'/><gbs:viewability value='http://schemas.google.com/books/2008#view_partial'/><dc:creator>Rasmus Lerdorf</dc:creator><dc:date>2002</dc:date><dc:description>This valuable little book provides an authoritative overview of PHP packed into a pocket-sized guide that&amp;#39;s easy to take anywhere.</dc:description><dc:format>132 pages</dc:format><dc:format>book</dc:format><dc:identifier>i5E_i8ywXSIC</dc:identifier><dc:identifier>ISBN:0596004028</dc:identifier><dc:identifier>ISBN:9780596004026</dc:identifier><dc:publisher>O'Reilly Media, Inc.</dc:publisher><dc:subject>Computers</dc:subject><dc:title>PHP pocket reference</dc:title></entry><entry><id>http://www.google.com/books/feeds/volumes/J_y9LPymyZUC</id><updated>2010-03-15T22:47:00.000Z</updated><category scheme='http://schemas.google.com/g/2005#kind' term='http://schemas.google.com/books/2008#volume'/><title type='text'>PHP in a nutshell</title><link rel='http://schemas.google.com/books/2008/thumbnail' type='image/x-unknown' href='http://bks4.books.google.com/books?id=J_y9LPymyZUC&amp;printsec=frontcover&amp;img=1&amp;zoom=5&amp;edge=curl&amp;sig=ACfU3U3XJdHVZC4xjLOV2VcJ_RZKDLzyjQ&amp;source=gbs_gdata'/><link rel='http://schemas.google.com/books/2008/info' type='text/html' href='http://books.google.com/books?id=J_y9LPymyZUC&amp;dq=php&amp;ie=ISO-8859-1&amp;source=gbs_gdata'/><link rel='http://schemas.google.com/books/2008/preview' type='text/html' href='http://books.google.com/books?id=J_y9LPymyZUC&amp;printsec=frontcover&amp;dq=php&amp;ie=ISO-8859-1&amp;cd=10&amp;source=gbs_gdata'/><link rel='http://schemas.google.com/books/2008/annotation' type='application/atom+xml' href='http://www.google.com/books/feeds/users/me/volumes'/><link rel='alternate' type='text/html' href='http://books.google.com/books?id=J_y9LPymyZUC&amp;dq=php&amp;ie=ISO-8859-1'/><link rel='self' type='application/atom+xml' href='http://www.google.com/books/feeds/volumes/J_y9LPymyZUC'/><gbs:embeddability value='http://schemas.google.com/books/2008#embeddable'/><gbs:openAccess value='http://schemas.google.com/books/2008#disabled'/><gbs:viewability value='http://schemas.google.com/books/2008#view_partial'/><dc:creator>Paul Hudson</dc:creator><dc:date>2005-10-01</dc:date><dc:description>This book doesn&amp;#39;t try to compete with or replace the widely available online documentation.</dc:description><dc:format>352 pages</dc:format><dc:format>book</dc:format><dc:identifier>J_y9LPymyZUC</dc:identifier><dc:identifier>ISBN:0596100671</dc:identifier><dc:identifier>ISBN:9780596100674</dc:identifier><dc:publisher>O'Reilly Media, Inc.</dc:publisher><dc:subject>Computers</dc:subject><dc:title>PHP in a nutshell</dc:title></entry></feed>";

}
