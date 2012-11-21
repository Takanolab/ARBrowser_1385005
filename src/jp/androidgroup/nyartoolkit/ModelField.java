/**
 * モデルを持つクラス
 * 
 */
package jp.androidgroup.nyartoolkit;

import java.util.ArrayList;

import org.takanolab.kGLModel.KGLModelData;

public class ModelField {
	ArrayList<String> modelName = new ArrayList<String>();
	ArrayList<KGLModelData> modelData = new ArrayList<KGLModelData>();
	
	public ModelField(){
	}
	
	public ModelField(String name,KGLModelData data){
		modelName.add(name);
		modelData.add(data);
	}
}
