package jp.androidgroup.nyartoolkit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class QuestActivity extends Activity implements OnItemClickListener{

	// モデルを３つくらい、リスト表示する
	// リスト上でモデルをクリックしたら、NyARTOolkitAndroidActivityに移行し、
	// クリックしたモデルの3DCGを表示する
	private static String TAG ="QuestActivity";
	
	@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.test_layout);
	        
	        // インテントを取得
	        Intent it = getIntent();
	        // インテントが空なら閉じる
	        if(it == null){
	        	Log.e(TAG,"Intent Empty.");
	        	finish();
	        }
	        
	        // 文字列配列を受け取る
	        String[] getlist = it.getStringArrayExtra("FixationModel");
	        // リストビュー作成
	        ListView listview = (ListView)findViewById(R.id.listView1);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,getlist);
			listview.setAdapter(adapter);
			listview.setOnItemClickListener(this);

	 }

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int pos, long id) {
		Intent it = new Intent();
		// 選択した位置(id)
		it.putExtra(NyARToolkitAndroidActivity.RESULT_SELECT_ITEM_ID, pos);
		// リザルトをセット
		setResult(RESULT_OK, it);
		finish();
	}

}
