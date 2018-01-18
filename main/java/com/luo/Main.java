package com.luo;


import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    /**
     *  读入 res/email.csv
     *  1.去除非法的邮箱格式，并将非法的邮箱写入 res/output/invalid.csv中
     *  2.在output下按域名建文件夹，如google.com.csv放所有域名为google.com的邮箱，按字符排序，如：
     *  abc@123.com
     *  johncl@123.com
     *  johwq@123.com
     *
     * @param args main参数
     */
    public static void main(String[] args) {

        Main main = new Main();
        String path = main.getProjectPath();
        String csvPath = path + "res/email.csv";
        String invalidPath = path + "res/output/invalid.csv";
        System.out.println("//-------***开始处理。。。。**--------------");
        List<String> csvContents = readCsv(csvPath);
        StringBuilder legalEmail = new StringBuilder();
        Map<String,List<String>> sortContent=new HashMap<>();
        for (String email: csvContents ) {
            System.out.println("email:" + email);
            //判断邮箱是否合法
            if(isEmail(email)){
                String[] str = email.split("@");
                if(str.length == 2){
                    //获取当前邮箱域名对应的邮箱集合，并将邮箱放入对应集合后放回总map集合
                    List<String> nowEmailList= sortContent.get(str[1]);
                    if(nowEmailList == null){
                        nowEmailList = new ArrayList<>();
                    }
                    nowEmailList.add(email);
                    sortContent.put(str[1],nowEmailList);
                    //放入合法邮箱集合
                    legalEmail.append(email).append("\n");
                }else{
                    System.out.println("email的值是：---"+ email + "，当前方法=Main.main()");
                }
            }else{
                //将非法邮箱写入invalid.csv
                appendWriterFile(invalidPath , email , true);
            }
        }
        Iterator<Map.Entry<String, List<String>>> iterator = sortContent.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, List<String>> entry= iterator.next();
            List<String> listVal= entry.getValue();
            //文件路径
            String emailCsvPath = path + "res/output/" + entry.getKey() + ".csv";
            //集合排序
            Collections.sort(listVal, new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    String str1=(String) o1;
                    String str2=(String) o2;
                    if (str1.compareToIgnoreCase(str2)<0){
                        return -1;
                    }
                    return 1;
                }
            });

            //取出集合内容，拼接写入csv的字符串
            StringBuilder tmpsb = new StringBuilder();
            for ( String str: listVal) {
                tmpsb.append(str).append("\n");
            }
            //写入对应文件
            appendWriterFile(emailCsvPath , tmpsb.toString() ,false);
        }
        //重新保存去除非法邮箱后的email.csv文件内容
        appendWriterFile(csvPath ,legalEmail.toString() , false);
        System.out.println("//-------***处理完成***--------------");
    }

    /**
     * 读取csv文件并返回list 集合
     * @param path 路径
     * @return 读取的list集合
     */
    public static List<String> readCsv(String path){
        File csvF = new File(path);
        //加缓存读取
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(csvF));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        List<String> allStr = new ArrayList<>();
        String line;
        try {
            //按行读取
            while ( (line = br.readLine()) != null ){
                allStr.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  allStr;
    }


    /**
     * 获取当前项目据对路径（路径需调整适配）
     * @return
     */
    public String getProjectPath(){
        
        return  this.getClass().getResource("/").getPath();        
    }

    /**
     * 判断邮箱是否合法
     * @param string
     * @return
     */
    public static boolean isEmail(String string) {
        if (string == null)
            return false;
        String regEx1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p;
        Matcher m;
        p = Pattern.compile(regEx1);
        m = p.matcher(string);
        if (m.matches())
            return true;
        else
            return false;
    }

    /**
     * 追加csv文件
     *
     * @param filePath
     * @param content
     */
    public static void appendWriterFile(String filePath, String content , boolean isAppend) {
        FileWriter writer = null;
        try {
            File file = new File(filePath);
            //判断文件是否存在,不存在则创建
            if( !file.exists()){
                file.createNewFile();
            }

            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer = new FileWriter(filePath, isAppend);
            content += "\n";
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(writer != null){
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
