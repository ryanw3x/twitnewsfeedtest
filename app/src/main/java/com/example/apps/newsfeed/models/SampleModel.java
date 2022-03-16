package com.example.apps.newsfeed.models;

import com.example.apps.newsfeed.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


@Table(database = MyDatabase.class)
public class SampleModel extends BaseModel {

	@PrimaryKey
	@Column
	Long id;

	
	@Column
	private String name;

	public SampleModel() {
		super();
	}

	
	public SampleModel(JSONObject object){
		super();

		try {
			this.name = object.getString("title");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	
	public String getName() {
		return name;
	}

	
	public void setName(String name) {
		this.name = name;
	}

	
	
	
	public static SampleModel byId(long id) {
		return new Select().from(SampleModel.class).where(SampleModel_Table.id.eq(id)).querySingle();
	}

	public static List<SampleModel> recentItems() {
		return new Select().from(SampleModel.class).orderBy(SampleModel_Table.id, false).limit(300).queryList();
	}
}
