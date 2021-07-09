package com.spring;

import com.spring.util.JsonUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;


@SpringBootApplication
public class Application implements CommandLineRunner {

    private static String json;

    private static HashMap<String,String> params = new HashMap<>();;

    public static void main(String[] args) throws Exception {

        if (rootCheck()) {
            System.out.println("root 권한으로 실행할 수 없습니다");
            return;
        }
        File file = appInit();
        List<String> list = new ArrayList(Arrays.asList(args));
        if (file != null && file.exists()) {
            String configSet = String.format("--spring.config.location=file:%s", file.getAbsolutePath());
            boolean found = false;
            for(String s : list) {
                if(StringUtils.startsWith(s,"--spring.config.location")) {
                    found = true;
                }
            }
            if(!found) {
                list.add(configSet);
            }
            String[] newArgs = new String[args.length + 1];
            newArgs[newArgs.length - 1] = configSet;
        }
        args = list.toArray(new String[0]);

        int count = 0;
        String[] modes = {"folder", "move", "dummy", "rename", "rollback"};
        String[] assettypes = {"asset","keep","proxy"};
        for (String arg : args) {
            String[] arg_s = StringUtils.split(arg, "=");
            if (arg_s.length > 1) {
                params.put(StringUtils.trim(arg_s[0]), StringUtils.trim(arg_s[1]));
            }
        }
        if (Arrays.asList(modes).contains(MapUtils.getString(params, "mode")) == false) {
            example();
            return;
        }
        json = JsonUtil.object2String(params);
        System.setProperty("json", json);
        SpringApplication app = new SpringApplication(Application.class);
        app.run(args);
    }

    public static File appInit() throws Exception {
        String settingFile = "application.yml";
        String OS = System.getProperty("os.name").toLowerCase();
        String startup = ((OS.indexOf("win") >= 0)) ? "startup.bat" : "startup.sh";
        String commandPath = System.getProperty("sun.java.command");
        commandPath = StringUtils.substringBefore(commandPath," ");
        if(StringUtils.equals(commandPath,"com.spring.Application")) {
            return null;
        }
        String path = FilenameUtils.getPath(commandPath);
        path = (StringUtils.isEmpty(path)) ? "." : path;
        String filePath1 = FilenameUtils.normalize(path + "/" + settingFile);
        String filePath2 = FilenameUtils.normalize(path + "/logs");
        String filePath3 = FilenameUtils.normalize(path + "/" + startup);
        System.setProperty("logging.file.path", filePath2); // = ${LOG_PATH}
        File file1 = new File(filePath1);
        File file2 = new File(filePath2);
        File file3 = new File(filePath3);
        if (!file1.exists()) {
            InputStream in = Application.class.getResourceAsStream("/" + settingFile);
            OutputStream out = new FileOutputStream(file1);
            FileCopyUtils.copy(in, out);
        }
        if (!file2.isDirectory()) {
            file2.mkdirs();
        }
        if (!file3.exists()) {
            InputStream in = Application.class.getResourceAsStream("/runscript/" + startup);
            OutputStream out = new FileOutputStream(file3);
            FileCopyUtils.copy(in, out);
            if(OS.indexOf("win") >= 0) { /* windows */
                in = Application.class.getResourceAsStream("/runscript/tail.exe");
                out = new FileOutputStream(new File(file3.getParentFile(),"tail.exe"));
                FileCopyUtils.copy(in, out);

                in = Application.class.getResourceAsStream("/runscript/startup-start.bat");
                out = new FileOutputStream(new File(file3.getParentFile(),"startup-start.bat"));
                FileCopyUtils.copy(in, out);

                in = Application.class.getResourceAsStream("/runscript/startup-log.bat");
                out = new FileOutputStream(new File(file3.getParentFile(),"startup-log.bat"));
                FileCopyUtils.copy(in, out);
            }
        }
        return file1;
    }

    public static boolean rootCheck() throws Exception {
        String userName = System.getProperty("user.name");
        return StringUtils.equals(userName,"root");
    }

    public static void example() {
        System.out.println("\n[arguments]");
        System.out.println("  mode = [folder,move,dummy,rename,rollback] ");
        System.out.println("  assettype = [asset,keep,proxy] ");
        System.out.println("  type = [all,video,audio,image] ");
        System.out.println("  offset = [offset]");
        System.out.println("  limit = [limit]");
        System.out.println("  max = [0:AUTO]");
        System.out.println("  filepath = [filepath]");
        System.out.println("  java -jar springboot-mybatis-secondary-2021.1.jar mode=[mode] assettype=[assettype] offset=[offset] limit=[limit] max=[max] filepath=[filepath]");
        System.out.println("\n[example]");
        System.out.println("> java -jar springboot-mybatis-secondary-2021.1.jar mode=folder assettype=proxy type=all offset=0 limit=10000 max=5 --spring.profiles.active=nara30");
        System.out.println("> java -jar springboot-mybatis-secondary-2021.1.jar mode=move assettype=proxy type=all offset=0 limit=10000 max=5 --spring.profiles.active=nara30");
        System.out.println("> java -jar springboot-mybatis-secondary-2021.1.jar mode=dummy assettype=proxy type=all offset=0 limit=10000 max=5 --spring.profiles.active=nara30");
        System.out.println("> java -jar springboot-mybatis-secondary-2021.1.jar mode=rename assettype=proxy type=all offset=0 limit=10000 max=5 --spring.profiles.active=nara30");
        System.out.println("> java -jar springboot-mybatis-secondary-2021.1.jar mode=rollback filename=proxy_OK.log");
        System.out.println("");
    }

    @Override
    public void run(String... args) throws Exception {

        Map params = JsonUtil.json2Map(json);
    }
}
