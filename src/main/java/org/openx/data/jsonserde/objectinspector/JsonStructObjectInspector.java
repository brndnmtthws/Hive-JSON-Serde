/*======================================================================*
 * Copyright (c) 2011, OpenX Technologies, Inc. All rights reserved.    *
 *                                                                      *
 * Licensed under the New BSD License (the "License"); you may not use  *
 * this file except in compliance with the License. Unless required     *
 * by applicable law or agreed to in writing, software distributed      *
 * under the License is distributed on an "AS IS" BASIS, WITHOUT        *
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.     *
 * See the License for the specific language governing permissions and  *
 * limitations under the License. See accompanying LICENSE file.        *
 *======================================================================*/
package org.openx.data.jsonserde.objectinspector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StandardStructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.openx.data.jsonserde.json.JSONException;
import org.openx.data.jsonserde.json.JSONObject;

/**
 * This Object Inspector is used to look into a JSonObject object.
 * We couldn't use StandardStructObjectInspector since that expects 
 * something that can be cast to an Array<Object>.
 * @author rcongiu
 */
public class JsonStructObjectInspector extends StandardStructObjectInspector {
    JsonStructOIOptions options = null;

  /*  
    public JsonStructObjectInspector(List<String> structFieldNames,
            List<ObjectInspector> structFieldObjectInspectors) {
        super(structFieldNames, structFieldObjectInspectors);
    } */
    
      public JsonStructObjectInspector(List<String> structFieldNames,
            List<ObjectInspector> structFieldObjectInspectors,JsonStructOIOptions opts) {
        super(structFieldNames, structFieldObjectInspectors);   
        
        options = opts;
    }

      /**
       * Extract the data from the requested field.
       * 
       * @param data
       * @param fieldRef
       * @return 
       */
    @Override
    public Object getStructFieldData(Object data, StructField fieldRef) {
        if (data == null) {
            return null;
        }
        JSONObject obj = (JSONObject) data;
        MyField f = (MyField) fieldRef;

        int fieldID = f.getFieldID();
        assert (fieldID >= 0 && fieldID < fields.size());

        try {
            if (obj.has(getJsonField(fieldRef))) {
               return obj.get(getJsonField(fieldRef));
            } else {
               return null;
            }
        } catch (JSONException ex) {
            // if key does not exist
            return null;
        }
    }
    static List<Object> values = new ArrayList<Object>();
    
    /**
     * called to map from hive to json
     * @param fr
     * @return 
     */
    protected String getJsonField(StructField fr) {
        if(options.getMappings() != null && options.getMappings().containsKey(fr.getFieldName())) {
            return options.getMappings().get(fr.getFieldName());
        } else {
            return fr.getFieldName();
        }
    }

    @Override
    public List<Object> getStructFieldsDataAsList(Object o) {
        JSONObject jObj = (JSONObject) o;
        values.clear();

        for (int i = 0; i < fields.size(); i++) {
                if (jObj.has(getJsonField(fields.get(i)))){
                    values.add(getStructFieldData(o, fields.get(i)));
                } else {
                    values.add(null);
                }
        }
        return values;
    }
}
