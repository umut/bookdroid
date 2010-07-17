package com.hoydaa.bookdroid.service;

import java.util.List;

import com.hoydaa.bookdroid.provider.Book;

/**
 * Book service to search and retrieve books.
 * 
 * @author Umut Utkan
 */
public interface BookService {

	/**
	 * Searches book and calls {@link BookSearchListener#onBookFound(Book, List)}.
	 * 
	 * @param query
	 *            search query
	 * @param listener
	 *            listener to be invoked
	 * @return all the books found
	 */
	List<Book> searchBook(String query, BookSearchListener listener);

	/**
	 * Retrieves the books according to the given id.
	 * 
	 * @param bookId
	 *            book id
	 */
	Book getBook(String bookId);

	/**
	 * Listener invoked by {@link BookService#search(String)}.
	 * 
	 * @author Umut Utkan
	 */
	public interface BookSearchListener {

		/**
		 * Invoked whenever a book is found
		 * 
		 * @param book
		 *            The book found
		 * @param books
		 *            All the books found so far
		 */
		void onBookFound(Book book, List<Book> books);
		
		void onSearchInfo(BookSearchInfo info);

	}
	
	public class BookSearchInfo {
		
		private int _resultCount = 0;
		
		public void setResultCount(int resultCount) {
			_resultCount = resultCount;
		}
		
		public int getResultCount() {
			return _resultCount;
		}
		
	}

}
