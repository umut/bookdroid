package com.hoydaa.bookdroid.activity;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.hoydaa.bookdroid.R;
import com.hoydaa.bookdroid.provider.Book;
import com.hoydaa.bookdroid.provider.BookManager;
import com.hoydaa.bookdroid.provider.ResultBook;
import com.hoydaa.bookdroid.util.DialogUtils;
import com.hoydaa.bookdroid.util.StringUtils;
import com.hoydaa.bookdroid.util.Utils;

/**
 * Activity for adding, updating books.
 * 
 * @author Meltem Atesalp
 * @author Umut Utkan
 */
public class EditBookActivity extends Activity implements OnClickListener {

	private static final String LT = EditBookActivity.class.getSimpleName();

	private static final int SAVE_ID = Menu.FIRST;

	private static final int DISCARD_ID = Menu.FIRST + 1;

	private static final int TWEET_ID = Menu.FIRST + 2;

	private ResultBook _book;

	private EditText _titleText;

	private EditText _isbnText;

	private EditText _descriptionText;

	private CheckBox _readCheckBox;

	private CheckBox _ownedCheckBox;

	private Button _loadButton;

	private ImageView _thumbnailImageView;

	private EditText _authorText;

	private EditText _pagesText;

	private EditText _publisherText;

	private EditText _dateText;

	private RatingBar _ratingBar;

	private ProgressDialog _getBookProgressDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.book_edit);
		setupViews();

		if (getIntent().getAction().equals(Intent.ACTION_EDIT)) {
			Book book = BookManager.retrieveBook(getContentResolver(), getIntent().getData());
			if (book != null) {
				bindViews(book);
			}
		} else {
			String isbnTemp = null;
			if (null != (isbnTemp = (null == getIntent().getExtras() ? null : getIntent().getExtras().getString(
					Books.ISBN)))) {
				_isbnText.setText(isbnTemp);
			}
			String bookId = null;
			if (null != (bookId = (null == getIntent().getExtras() ? null : getIntent().getExtras()
					.getString("BOOK_ID")))) {
				new GetBookTask().execute(bookId);
			}
		}

		_loadButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		final String isbnTemp = _isbnText.getText().toString().trim();
		if (isbnTemp.length() > 0) {
			new SearchAndGetBook().execute(isbnTemp);
		} else {
			IntentIntegrator.initiateScan(this);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanResult != null) {
			String isbn = scanResult.getContents();
			_isbnText.setText(isbn);
			new SearchAndGetBook().execute(isbn);
		} else {
			// else continue with any other code you need in the method
		}
	}

	private void setupViews() {
		_isbnText = (EditText) findViewById(R.id.isbn);
		_titleText = (EditText) findViewById(R.id.title);
		_descriptionText = (EditText) findViewById(R.id.comments);
		_ratingBar = (RatingBar) findViewById(R.id.rating);
		_readCheckBox = (CheckBox) findViewById(R.id.read);
		_ownedCheckBox = (CheckBox) findViewById(R.id.owned);

		_loadButton = (Button) findViewById(R.id.load);

		_thumbnailImageView = (ImageView) findViewById(R.id.thumbnail);
		_authorText = (EditText) findViewById(R.id.author);
		_pagesText = (EditText) findViewById(R.id.pages);
		_publisherText = (EditText) findViewById(R.id.publisher);
		_dateText = (EditText) findViewById(R.id.date);
	}

	private void bindViews(Book book) {
		_book = new ResultBook(book);
		_isbnText.setText(book.getIsbn13());
		_titleText.setText(book.getTitle());
		_descriptionText.setText(book.getDescription());
		_readCheckBox.setChecked(book.isRead());
		_ownedCheckBox.setChecked(book.isOwned());
		_ratingBar.setRating(book.getRating());
		_authorText.setText(book.getAuthor());
		_pagesText.setText(book.getPages() + "");
		_publisherText.setText(book.getPublisher());
		_dateText.setText(book.getDate());
		if (_book.getThumbnailDrawable() != null) {
			_thumbnailImageView.setImageDrawable(_book.getThumbnailDrawable());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, SAVE_ID, 0, R.string.save).setIcon(android.R.drawable.ic_menu_save);
		menu.add(0, DISCARD_ID, 0, R.string.discard).setIcon(android.R.drawable.ic_menu_delete);
		menu.add(0, TWEET_ID, 0, R.string.tweet);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case SAVE_ID:
			if (getIntent().getAction().equals(Intent.ACTION_EDIT)) {
				editBook();
			} else {
				addBook();
			}
			finish();
			return true;
		case DISCARD_ID:
			clear();
			return true;
		case TWEET_ID:
			Utils.tweet(this, constructBook());
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private Book constructBook() {
		Book book = new Book();
		book.setId(_book.getBook().getId());
		book.setTitle(_titleText.getText().toString());
		book.setIsbn(_isbnText.getText().toString());
		book.setDescription(_descriptionText.getText().toString());
		book.setRead(_readCheckBox.isChecked());
		book.setOwned(_ownedCheckBox.isChecked());
		book.setRating(_ratingBar.getRating());
		book.setAuthor(_authorText.getText().toString());
		book.setPublisher(_publisherText.getText().toString());
		book.setDate(_dateText.getText().toString());
		book.setPages(StringUtils.hasText(_pagesText.getText().toString()) ? Integer.parseInt(_pagesText.getText()
				.toString()) : null);
		if (_book.getThumbnailDrawable() != null) {
			book.setThumbnail(_book.getBook().getThumbnail());
		}
		return book;
	}

	private boolean addBook() {
		BookManager.addBook(getContentResolver(), constructBook());

		return true;
	}

	private boolean editBook() {
		BookManager.updateBook(getContentResolver(), getIntent().getData(), constructBook());

		return true;
	}

	private boolean clear() {
		_titleText.getText().clear();
		_isbnText.getText().clear();
		_descriptionText.getText().clear();
		_authorText.getText().clear();
		_dateText.getText().clear();
		_publisherText.getText().clear();
		_pagesText.getText().clear();
		_ratingBar.setRating(.0F);
		_ownedCheckBox.setSelected(false);
		_readCheckBox.setSelected(false);

		return true;
	}

	private Dialog createNeutralDialog(String title, String text) {
		return DialogUtils.createNeutralDialog(this, title, text);
	}

	private class SearchAndGetBook extends AsyncTask<String, ResultBook, ResultBook> {

		private boolean moreThanOne = false;

		@Override
		protected void onPreExecute() {
			_getBookProgressDialog = ProgressDialog.show(EditBookActivity.this, "Downloading...",
					"Downloading book information...", true);
			_getBookProgressDialog.show();
		}

		@Override
		protected ResultBook doInBackground(String... params) {
			List<Book> books = new com.hoydaa.bookdroid.service.GoogleBookService().searchBook(params[0], null);
			if (books.size() == 1) {
				return new ResultBook(books.get(0));
			} else if (books.size() > 1) {
				moreThanOne = true;
				return new ResultBook(books.get(0));
			}
			return null;
		}

		@Override
		protected void onPostExecute(ResultBook result) {
			if (moreThanOne) {
				_getBookProgressDialog.dismiss();
				result.getThumbnailDrawable();
				bindViews(result.getBook());
				createNeutralDialog(
						"Info",
						"More than one books found, using the first one. If you want to pinpoint the book you want, please use search search and add.")
						.show();
			} else if (null == result) {
				_getBookProgressDialog.dismiss();
				createNeutralDialog("Info", "No books found.").show();
			} else {
				result.getThumbnailDrawable();
				bindViews(result.getBook());
				_getBookProgressDialog.dismiss();
			}
		}

	}

	private class GetBookTask extends AsyncTask<String, ResultBook, ResultBook> {

		@Override
		protected void onPreExecute() {
			_getBookProgressDialog = ProgressDialog.show(EditBookActivity.this, "Downloading...",
					"Downloading book information...", true);
			_getBookProgressDialog.show();
		}

		@Override
		protected ResultBook doInBackground(String... params) {
			ResultBook book = new ResultBook(new com.hoydaa.bookdroid.service.GoogleBookService().getBook(params[0]));
			book.getThumbnailDrawable();
			return book;
		}

		@Override
		protected void onPostExecute(ResultBook result) {
			result.getThumbnailDrawable();
			bindViews(result.getBook());
			_getBookProgressDialog.dismiss();
		}

	}

}
