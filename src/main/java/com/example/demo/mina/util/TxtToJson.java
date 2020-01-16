package com.example.demo.mina.util;




import com.example.demo.mina.entity.PackageData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TxtToJson {

    public static void main(String[] args) throws IOException {
        boolean rs=CompareFile.isSameFile("/home/fjkj/result.txt","/home/fjkj/sendMessage.txt");
        System.out.println("数据对比结果："+rs);
        //JSONObject json=readTxtToJson("/home/fjkj/test.txt");
       // String name=(String)json.get("name");
        //int age=(int)json.get("msgId");
        //short a=(short) age;

        //System.out.print(a+"age ");
        //String body=(String)json.get("body");
       // byte[] bytes=body.getBytes();
        //for(int i=0;i<bytes.length;i++){
            //System.out.print(bytes[i]+" ");
        //}
        //System.out.println("name"+name+"age"+age+"sex"+sex);
        //PackageData data=(PackageData) JSONObject.toBean(json,PackageData.class);
        //PackageData data=new PackageData((short) 1);
        //transDataToTxt(data);
    }

    public static JSONObject readTxtToJson(String txt){
        File file=new File(txt);
        String jsonStr="";
        FileInputStream fis=null;
        InputStreamReader isr=null;
        BufferedReader br=null;
        try {
            fis=new FileInputStream(file);//文件输入流
            isr=new InputStreamReader(fis,"utf-8");//包装为字符流
            br = new BufferedReader(isr);
            String line=null;
            while((line=br.readLine())!=null){
                jsonStr += line;
            }
            return  JSONObject.fromObject(jsonStr);//将string转换为json对象

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            return  null;
        }finally {
            if (br!=null){
                try {
                    br.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            if (fis!=null){
                try {
                    br.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            if (isr!=null){
                try {
                    br.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    public static void  transDataToTxt(PackageData data,String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        //User类转JSON
        //输出结果：{"name":"小民","age":20,"birthday":844099200000,"email":"xiaomin@sina.com"}
        String json = mapper.writeValueAsString(data);
        System.out.println(json);
        mapper.writeValue(new File(path),data);

        //Java集合转JSON
        //输出结果：[{"name":"小民","age":20,"birthday":844099200000,"email":"xiaomin@sina.com"}]
       /* List<PackageData> users = new ArrayList<PackageData>();
        users.add(data);
        String jsonlist = mapper.writeValueAsString(users);
        System.out.println(jsonlist);*/
    }
}

