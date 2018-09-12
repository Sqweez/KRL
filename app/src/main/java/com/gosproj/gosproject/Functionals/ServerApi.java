package com.gosproj.gosproject.Functionals;

import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ServerApi
{
    String url = "http://kazroadlab.kad.org.kz/mp/scan.php";
    //NEW KEY
/*
    String key = "bW9iaWxlLWFwcDpQQXhwam1HbUNE";
*/
    //OLD KEY
    String key = "bW9iaWxlLWFwcDpOSktXSHBOOFZj";


    public boolean UpLoadFile(File file) throws IOException
    {
        InputStream inputStream;
        inputStream = new FileInputStream(file);
        byte[] data;

        data = IOUtils.toByteArray(inputStream);

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

        InputStreamBody inputStreamBody = new InputStreamBody(new ByteArrayInputStream(data), file.getName());
        MultipartEntity multipartEntity = new MultipartEntity();
        multipartEntity.addPart("file", inputStreamBody);
        httpPost.setHeader("authorization", key);
        httpPost.setHeader("cache-control", "no-cache");
        httpPost.setEntity(multipartEntity);

        HttpResponse httpResponse = httpClient.execute(httpPost);

        if(httpResponse != null)
        {
            String responseStr = EntityUtils.toString(httpResponse.getEntity());
            Log.d("MYLog", responseStr);

            return true;
        }
        else
        {
            Log.d("MYLog", "NOTHING TO SHOW");
            return false;
        }
    }
}
