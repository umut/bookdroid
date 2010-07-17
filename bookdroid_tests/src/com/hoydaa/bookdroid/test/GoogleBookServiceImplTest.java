package com.hoydaa.bookdroid.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.xmlpull.v1.XmlPullParserException;

import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;

import com.hoydaa.bookdroid.http.DefaultHttpClient;
import com.hoydaa.bookdroid.http.HttpClient;
import com.hoydaa.bookdroid.http.ResponseHandler;
import com.hoydaa.bookdroid.provider.Book;
import com.hoydaa.bookdroid.service.GoogleBookService;

/**
 * Tests {@link GoogleBookServiceImpl} both offline and online.
 * 
 * @author Umut Utkan
 */
public class GoogleBookServiceImplTest extends TestCase {

	private final static String TEST_XML = "<?xml version='1.0' encoding='UTF-8'?>\n"
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

	@MediumTest
	public void testGetBook() throws XmlPullParserException, IOException {
		GoogleBookService service = new GoogleBookService();
		service.setHttpClient(new HttpClient() {

			@Override
			public String getUrlContent(String url) {
				return TEST_XML;
			}

			@Override
			public void retrieveWithHandler(String url, ResponseHandler handler) {
				handler.handle(200, new ByteArrayInputStream(TEST_XML.getBytes()));
			}

			@Override
			public byte[] getUrlBytes(String url) {
				return TEST_XML.getBytes();
			}
		});
		List<Book> books = service.searchBook("9780321579362", null);

		assertNotNull(books);
		assertEquals(1, books.size());
		Book book = books.get(0);
		assertEquals("Succeeding with Agile Software Development Using Scrum", book.getTitle());
		assertEquals("Mike Cohn", book.getAuthor());
		assertTrue(book.getDescription().contains("Proven") && book.getDescription().contains("starting"));
		assertEquals("Addison-Wesley Professional", book.getPublisher());
		assertEquals((Integer) 475, book.getPages());
		assertEquals("2009-07-10", book.getDate());
		assertEquals("0321579364, 9780321579362", book.getIsbn());
		assertNotNull(book.getThumbnail());
	}

	@LargeTest
	public void testGetBookWithInternetConnection() {
		GoogleBookService service = new GoogleBookService();
		service.setHttpClient(new DefaultHttpClient(new org.apache.http.impl.client.DefaultHttpClient()));
		List<Book> books = service.searchBook("9780321579362", null);

		assertNotNull(books);
		assertEquals(1, books.size());
		Book book = books.get(0);
		assertBook(book);

		assertBook(service.searchBook("9780321334879", null).get(0));
	}

	private void assertBook(Book book) {
		assertNotNull(book.getTitle());
		assertNotNull(book.getAuthor());
		assertNotNull(book.getDescription());
		assertNotNull(book.getPublisher());
		assertNotNull(book.getPages());
		assertNotNull(book.getDate());
		assertNotNull(book.getIsbn());
		assertNotNull(book.getThumbnail());
	}

}
