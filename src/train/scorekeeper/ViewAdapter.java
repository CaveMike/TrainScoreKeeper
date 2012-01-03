package train.scorekeeper;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

class ViewAdapter extends BaseAdapter {
	private final List<View> views;

	public ViewAdapter(Context context, List<View> views) {
		super();
		this.views = views;
	}

	@Override
	public int getCount() {
		return views.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		if (view == null) {
			view = views.get(position);
		}

		return view;
	}

	public void setEnabled(boolean enabled) {
		for (View view : views) {
			view.setEnabled(enabled);
		}
	}
}