package com.hoydaa.bookdroid.activity;

import java.util.List;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.hoydaa.bookdroid.R;
import com.hoydaa.bookdroid.provider.Book;
import com.hoydaa.bookdroid.provider.BookdroidProvider;
import com.hoydaa.bookdroid.provider.ResultBook;
import com.hoydaa.bookdroid.service.GoogleBookService;
import com.hoydaa.bookdroid.service.BookService.BookSearchInfo;
import com.hoydaa.bookdroid.service.BookService.BookSearchListener;
import com.hoydaa.bookdroid.util.DialogUtils;
import com.hoydaa.bookdroid.util.StringUtils;

/**
 * Searches books on google book and displays them in a list for selection to add.
 * 
 * @author Umut Utkan
 */
public class SearchBookActivity extends ListActivity implements OnClickListener {

	private final static String LT = SearchBookActivity.class.getSimpleName();

	private Button _searchButton;

	private EditText _searchText;

	private ResultBookAdapter _resultBookAdapter;

	private ProgressBar _searchProgressBar;

	private View _searchProgressPanel;

	private SearchTask _searchTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_search);

		_searchButton = (Button) findViewById(R.id.search);
		_searchText = (EditText) findViewById(R.id.isbn);

		_resultBookAdapter = new ResultBookAdapter(this);
		setListAdapter(_resultBookAdapter);

		_searchButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (!StringUtils.hasText(_searchText.getText())) {
			showDialog(0);
			return;
		}

		Log.d(LT, "Started search...");

		_searchTask = new SearchTask();
		_searchTask.execute(_searchText.getText().toString());
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case 0:
			dialog = createNeutralDialog("Error", "Please enter something to search.");
			break;

		default:
			break;
		}
		return dialog;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(Intent.ACTION_INSERT, BookdroidProvider.CONTENT_URI);
		intent.putExtra("BOOK_ID", _resultBookAdapter.getItem(position).getId());
		startActivity(intent);
	}

	private Dialog createNeutralDialog(String title, String text) {
		return DialogUtils.createNeutralDialog(this, title, text);
	}
	
	private void showPanel(View panel, boolean slideUp) {
		panel.startAnimation(AnimationUtils.loadAnimation(this, slideUp ? R.anim.slide_in : R.anim.slide_out_top));
		panel.setVisibility(View.VISIBLE);
	}

	private void hidePanel(View panel, boolean slideDown) {
		panel.startAnimation(AnimationUtils.loadAnimation(this, slideDown ? R.anim.slide_out : R.anim.slide_in_top));
		panel.setVisibility(View.GONE);
	}
	
	private void onCancelImport() {
		_searchTask.cancel(true);
		_searchTask = null;
	}

	private class SearchTask extends AsyncTask<String, ResultBook, Void> implements BookSearchListener {
		
		private int _resultCount = 0;

		@Override
		protected void onPreExecute() {
			if (_searchProgressPanel == null) {
				_searchProgressPanel = ((ViewStub) findViewById(R.id.stub_import)).inflate();
				_searchProgressBar = (ProgressBar) _searchProgressPanel.findViewById(R.id.progress);

				final View cancelButton = _searchProgressPanel.findViewById(R.id.button_cancel);
				cancelButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						onCancelImport();
					}
				});
			}

			_resultBookAdapter.clear();
			_searchProgressBar.setProgress(0);

			showPanel(_searchProgressPanel, true);
		}

		@Override
		public Void doInBackground(String... params) {
			new GoogleBookService().searchBook(params[0], this);

			return null;
		}

		@Override
		public void onProgressUpdate(ResultBook... values) {
			for (ResultBook book : values) {
				_resultBookAdapter.add(book);
			}
		}

		@Override
		protected void onPostExecute(Void result) {
			hidePanel(_searchProgressPanel, true);
			if(_resultCount == 0) {
				createNeutralDialog("Info", "No books found!").show();
			}
		}

		@Override
		protected void onCancelled() {
			hidePanel(_searchProgressPanel, true);
		}

		@Override
		public void onBookFound(Book book, List<Book> books) {
			if (_resultCount != 0) {
				_searchProgressBar.setProgress(new Double(100.0 * books.size() / _resultCount).intValue());
			}
			ResultBook b = new ResultBook(book);
			b.getThumbnailDrawable();
			publishProgress(b);
		}

		public void onSearchInfo(BookSearchInfo info) {
			_resultCount = info.getResultCount();
		}

	}

}
