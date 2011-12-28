package edu.internet2.middleware.grouperInstaller;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.internet2.middleware.grouperInstaller.util.GiDbUtils;
import edu.internet2.middleware.grouperInstaller.util.GrouperInstallerUtils;
import edu.internet2.middleware.grouperInstaller.util.GrouperInstallerUtils.CommandResult;
import edu.internet2.middleware.grouperInstallerExt.org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import edu.internet2.middleware.grouperInstallerExt.org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import edu.internet2.middleware.grouperInstallerExt.org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import edu.internet2.middleware.grouperInstallerExt.org.apache.commons.httpclient.HttpClient;
import edu.internet2.middleware.grouperInstallerExt.org.apache.commons.httpclient.methods.GetMethod;

/**
 * Install grouper
 * @author mchyzer
 *
 */
public class GrouperInstaller {

  /**
   * read a string from stdin
   * @param defaultBoolean null for none, or true of false for if the input is blank
   * @return the string
   */
  private static boolean readFromStdInBoolean(Boolean defaultBoolean) {
    
    //keep trying until we get it
    while(true) {
      String input = readFromStdIn();
      if (GrouperInstallerUtils.isBlank(input) && defaultBoolean != null) {
        return defaultBoolean;
      }
      try {
        boolean inputBoolean = GrouperInstallerUtils.booleanValue(input);
        return inputBoolean; 
      } catch (Exception e) {
        if (defaultBoolean != null) {
          System.out.print("Expecting t or f or <blank> but received: '" + input + "', please try again: ");
        } else {
          System.out.print("Expecting t or f but received: '" + input + "', please try again: ");
        }
      }
    }
  }
  
  /**
   * read a string from stdin
   * @return the string
   */
  private static String readFromStdIn() {
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      String str = in.readLine();
      return GrouperInstallerUtils.trim(str);
    } catch (Exception e) {
      throw new RuntimeException("Problem", e);
    }
  }
  
  /**
   * download a file, delete the local file if it exists
   * @param url
   * @param localFileName
   */
  private static void downloadFile(String url, String localFileName) {
    
    System.out.println("Downloading from URL: " + url + " to file: " + localFileName);
    
    File localFile = new File(localFileName);
    
    HttpClient httpClient = new HttpClient();
    GetMethod getMethod = new GetMethod(url);
    try {
      int result = httpClient.executeMethod(getMethod);
      
      if (result != 200) {
        throw new RuntimeException("Expecting 200 but received: " + result);
      }
      
      InputStream inputStream = getMethod.getResponseBodyAsStream();
      
      if (localFile.exists()) {
        
        System.out.println("File exists: " + localFile.getAbsolutePath() + ", deleting");
        
        if (!localFile.delete()) {
          throw new RuntimeException("Cant delete file: " + localFile.getAbsolutePath() + "!!!!!");
        }
      }
      
      FileOutputStream fileOutputStream = new FileOutputStream(localFile);

      GrouperInstallerUtils.copy(inputStream, fileOutputStream);
      
    } catch (Exception exception) {
      String errorMessage = "Error connecting to URL: " + url;
      System.out.println(errorMessage);
      throw new RuntimeException(errorMessage, exception);
    }
    
    
    
  }
  
  /**
   * @param args
   */
  public static void main(String[] args) {
    
    //mainLogic(args);
  
    GrouperInstaller grouperInstaller = new GrouperInstaller();
    
    //grouperInstaller.mainLogic(args);
    
    grouperInstaller.dbUrl = "jdbc:hsqldb:hsql://localhost:9001/grouper";
    grouperInstaller.dbUser = "sa";
    grouperInstaller.dbPass = "";
    grouperInstaller.giDbUtils = new GiDbUtils(grouperInstaller.dbUrl, grouperInstaller.dbUser, grouperInstaller.dbPass);
    grouperInstaller.untarredApiDir = new File("c:/mchyzer/grouper/trunk/grouper-installer/grouper.apiBinary-2.0.2");
    grouperInstaller.grouperInstallDirectoryString = "C:/mchyzer/grouper/trunk/grouper-installer/";
    
    grouperInstaller.version = "2.0.2";
    
    grouperInstaller.addDriverJarToClasspath();
//      
    grouperInstaller.startHsqlDb();
    grouperInstaller.checkDatabaseConnection();
//      grouperInstaller.initDb();
//      
//      grouperInstaller.addQuickstartSubjects();
//      grouperInstaller.addQuickstartData();
    
    File uiDir = grouperInstaller.downloadUi();
    File unzippedUiFile = unzip(uiDir.getAbsolutePath());
    grouperInstaller.untarredUiDir = untar(unzippedUiFile.getAbsolutePath());

//      grouperInstaller.configureUi();
//
//      File antDir = grouperInstaller.downloadAnt();
//      File unzippedAntFile = unzip(antDir.getAbsolutePath());
//      grouperInstaller.untarredAntDir = untar(unzippedAntFile.getAbsolutePath());
//
//      grouperInstaller.buildUi();

    File tomcatDir = grouperInstaller.downloadTomcat();
    File unzippedTomcatFile = unzip(tomcatDir.getAbsolutePath());
    grouperInstaller.untarredTomcatDir = untar(unzippedTomcatFile.getAbsolutePath());

    grouperInstaller.configureTomcat();
    
    grouperInstaller.configureTomcatUiWebapp();
    
    grouperInstaller.tomcatConfigureGrouperSystem();
    
    grouperInstaller.tomcatBounce("restart");
    
    //CommandResult commandResult = GrouperInstallerUtils.execCommand("cmd /c cd");
    //String result = commandResult.getOutputText();
    //
    //System.out.println(result);


    //editPropertiesFile(new File("C:\\mchyzer\\grouper\\trunk\\grouper-installer\\grouper.apiBinary-2.1.0\\conf\\grouper.hibernate.properties"), 
    //    "hibernate.connection.password", "sdf");
      
    //if started hsql, then we need to exit since that thread will not stop
    System.exit(0);
  }

  /** e.g. 2.1.0 */
  private String version;
  
  /**
   * tag
   * @return the tag
   */
  private String tag() {
    if (this.version == null) {
      throw new RuntimeException("tag is null!");
    }
    
    String underscores = this.version.replace('.', '_');
    
    return "GROUPER_" + underscores;
  }
  
  /**
   * 
   */
  private void buildUi() {
    
    List<String> commands = new ArrayList<String>();
    
    commands.add("cmd");
    commands.add("/c");
    commands.add(this.untarredAntDir.getAbsolutePath() + File.separator + "bin" + File.separator + "ant.bat");
    commands.add("dist");
    
    System.out.println("\n##################################");
    System.out.println("Building UI with command:\n" + this.untarredUiDir.getAbsolutePath() + "> " 
        + GrouperInstallerUtils.join(commands.iterator(), ' ') + "\n");
    
    CommandResult commandResult = GrouperInstallerUtils.execCommand(GrouperInstallerUtils.toArray(commands, String.class),
        true, true, null, this.untarredUiDir, null);
    
    if (!GrouperInstallerUtils.isBlank(commandResult.getErrorText())) {
      System.out.println("stderr: " + commandResult.getErrorText());
    }
    if (!GrouperInstallerUtils.isBlank(commandResult.getOutputText())) {
      System.out.println("stdout: " + commandResult.getOutputText());
    }
    
    System.out.println("\nEnd building UI");
    System.out.println("##################################\n");

    
  }
  
//  /**
//   * @param arg  stop, start, etc
//   * 
//   */
//  private void bounceTomcat(String arg) {
//    
//    List<String> commands = new ArrayList<String>();
//    
//    commands.add("cmd");
//    commands.add("/c");
//    commands.add("start");
//    commands.add("/b");
//    commands.add(this.untarredTomcatDir.getAbsolutePath() + File.separator + "bin" + File.separator + "catalina.bat");
//    commands.add(arg);
//    
//    System.out.println("\n##################################");
//    System.out.println("Tomcat " + arg + " with command:"
//        + GrouperInstallerUtils.join(commands.iterator(), ' ') + "\n");
//    
//    DOESNT WORK WITH ENV VARS!!!!!
//    CommandResult commandResult = GrouperInstallerUtils.execCommand(GrouperInstallerUtils.toArray(commands, String.class),
//        true, true, new String[]{"CATALINA_HOME=" + this.untarredTomcatDir.getAbsolutePath(), 
//        "JAVA_HOME=" + GrouperInstallerUtils.javaHome(),
//        "LOGGING_CONFIG=" + this.untarredTomcatDir + File.separator + "conf" + File.separator + "logging.properties"}, 
//        new File(this.untarredTomcatDir.getAbsolutePath() + File.separator + "bin"));
//        
//    
//    System.out.println("stderr: " + commandResult.getErrorText());
//    System.out.println("stdout: " + commandResult.getOutputText());
//    
//    System.out.println("\nEnd tomcat " + arg );
//    System.out.println("##################################\n");
//
//    
//  }
  
  /**
   * @param arg
   * 
   */
  private void tomcatBounce(String arg) {
    
    if (!GrouperInstallerUtils.equals("start", arg) && !GrouperInstallerUtils.equals("stop", arg) && !GrouperInstallerUtils.equals("restart", arg)) {
      throw new RuntimeException("Expecting arg: start|stop|restart but received: " + arg);
    }
    
    if (GrouperInstallerUtils.equals("restart", arg)) {
      
      tomcatBounce("stop");
      tomcatBounce("start");
      return;
    }
    
    if (GrouperInstallerUtils.equals("stop", arg)) {
      
      if (GrouperInstallerUtils.portAvailable(this.tomcatHttpPort)) {
        System.out.println("Tomcat is supposed to be listening on port: " + this.tomcatHttpPort + ", port not listening, assuming tomcat is not running...");
        return;
      }

      
    } else {
      if (!GrouperInstallerUtils.portAvailable(this.tomcatHttpPort)) {
        System.out.println("Tomcat is supposed to be listening on port: " + this.tomcatHttpPort + ", port is already listening!!!!  Why is this????");
        return;
      }
      
    }
    
    final List<String> commands = new ArrayList<String>();
    
//    <java jar="${tomcat.home}/bin/bootstrap.jar" fork="true">
//    03          <jvmarg value="-Dcatalina.home=${tomcat.home}"/>
//    04      </java>
//    05  </target>
//    06   
//    07  <target name="tomcat-stop">
//    08      <java jar="${tomcat.home}/bin/bootstrap.jar" fork="true">
//    09          <jvmarg value="-Dcatalina.home=${tomcat.home}"/>
//    10          <arg line="stop"/>
//    11      </java>
    
    commands.add(GrouperInstallerUtils.javaCommand());
    commands.add("-Dcatalina.home=" + this.untarredTomcatDir.getAbsolutePath());
    //commands.add("-Djava.util.logging.config.file=" + this.untarredTomcatDir.getAbsolutePath() + File.separator + "conf" + File.separator + "logging.properties");
    
    commands.add("-jar");
    commands.add(this.untarredTomcatDir.getAbsolutePath() + File.separator + "bin" + File.separator + "bootstrap.jar");
    
    if (GrouperInstallerUtils.equals("stop", arg)) {
      commands.add("stop");
    }
    
    System.out.println("\n##################################");
    System.out.println("Tomcat " + arg + " with command (note you need CATALINA_HOME and JAVA_HOME set):\n  "
        + this.untarredTomcatDir.getAbsolutePath() + File.separator + "bin" + File.separator + "startup.bat" + "\n");
    
    //dont wait
    boolean waitFor = GrouperInstallerUtils.equals("stop", arg) ? true : false;
    
    if (waitFor) {
      CommandResult commandResult = GrouperInstallerUtils.execCommand(GrouperInstallerUtils.toArray(commands, String.class),
          true, true, null, 
          new File(this.untarredTomcatDir.getAbsolutePath() + File.separator + "bin"), null);
      
      if (!GrouperInstallerUtils.isBlank(commandResult.getErrorText())) {
        System.out.println("stderr: " + commandResult.getErrorText());
      }
      if (!GrouperInstallerUtils.isBlank(commandResult.getOutputText())) {
        System.out.println("stdout: " + commandResult.getOutputText());
      }
    } else {
      //start in new thread
      Thread thread = new Thread(new Runnable() {
        
        @Override
        public void run() {
          GrouperInstallerUtils.execCommand(GrouperInstallerUtils.toArray(commands, String.class),
              true, true, null, 
              new File(GrouperInstaller.this.untarredTomcatDir.getAbsolutePath() + File.separator + "bin"), 
              GrouperInstaller.this.untarredTomcatDir.getAbsolutePath() + File.separator + "logs" + File.separator + "catalina");
        }
      });
      thread.setDaemon(true);
      thread.start();

    }
    
    System.out.println("\nEnd tomcat " + arg + " (note: logs are in " + this.untarredTomcatDir.getAbsolutePath() + File.separator + "logs)");
    System.out.println("##################################\n");

    System.out.print("Waiting for tomcat to " + arg +  "...");
    boolean success = false;
    for (int i=0;i<60;i++) {
      GrouperInstallerUtils.sleep(1000);
      //check port
      boolean portAvailable = GrouperInstallerUtils.portAvailable(this.tomcatHttpPort);
      if (GrouperInstallerUtils.equals("start", arg)) {
        if (!portAvailable) {
          success = true;
          System.out.println("\nTomcat listening on port: " + this.tomcatHttpPort);
          break;
        }
      } else {
        if (portAvailable) {
          success = true;
          System.out.println("\nTomcat not listening on port: " + this.tomcatHttpPort);
          break;
        }
      }
      System.out.print(".");
    }
    if (!success) {
      throw new RuntimeException("Trying to " + arg + " tomcat but couldnt properly detect " + arg + " on port " + this.tomcatHttpPort);
    }
  }
  
  /**
   * 
   * @param untarredApiDir
   */
  private void addDriverJarToClasspath() {
    String jarName = this.giDbUtils.builtinJarName();
    
    File driverJar = new File(this.untarredApiDir.getAbsolutePath() + File.separator + "lib" + File.separator + "jdbcSamples" + File.separator + jarName);
    GrouperInstallerUtils.classpathAddFile(driverJar);
  }

  /** db url */
  private String dbUrl;

  /** db user */
  private String dbUser;

  /** db pass */
  private String dbPass;

  /** untarred dir */
  private File untarredApiDir;

  /** untarred dir */
  private File untarredUiDir;

  /** untarred dir */
  private File untarredAntDir;

  /** untarred dir */
  private File untarredTomcatDir;

  /** main install dir, must end in file separator */
  private String grouperInstallDirectoryString;
  
  /**
   * 
   */
  private void tomcatConfigureGrouperSystem() {
    System.out.print("Do you want to set the GrouperSystem password in " + this.untarredTomcatDir + File.separator + "conf" + File.separator + "tomcat-users.xml? [t]: ");
    boolean setGrouperSystemPassword = readFromStdInBoolean(true);
    if (setGrouperSystemPassword) {
      System.out.print("Enter the GrouperSystem password: ");
      String grouperSystemPassword = readFromStdIn();
      grouperSystemPassword = GrouperInstallerUtils.defaultString(grouperSystemPassword);
      //write to the tomcat_users file
      //get the password
      File serverXmlFile = new File(this.untarredTomcatDir.getAbsolutePath() + File.separator + "conf" + File.separator + "tomcat-users.xml");
      String existingPassword = GrouperInstallerUtils.xpathEvaluateAttribute(serverXmlFile, "tomcat-users/user[@username='GrouperSystem']", "password");
      
      System.out.println("Editing file: " + serverXmlFile.getAbsolutePath());
      
      if (existingPassword == null) {

        addToXmlFile(serverXmlFile, ">",  new String[]{"<tomcat-users"}, "<user username=\"GrouperSystem\" password=\"" 
            + grouperSystemPassword + "\" />", "Tomcat user GrouperSystem");
         
      } else {
        
        if (GrouperInstallerUtils.equals(existingPassword, grouperSystemPassword)) {
          System.out.println("  - password is already set to that value, leaving file unchanged...");
        } else {
          
          editXmlFile(serverXmlFile, "password=\"([^\"]+)\"", new String[]{"<user", "username=\"GrouperSystem\""}, 
              null, grouperSystemPassword, "Tomcat password for user GrouperSystem");
          
        }
        
      }
    }
    
  }
  
  /**
   * 
   */
  private void configureUi() {
    //build properties file
    File buildPropertiesFile = new File(this.untarredUiDir.getAbsolutePath() + File.separator + "build.properties");
    if (!buildPropertiesFile.exists()) {
      File buildPropertiesTemplateFile = new File(this.untarredUiDir.getAbsolutePath() + File.separator + "build.properties.template");
      System.out.println("Copying file: " + buildPropertiesTemplateFile.getAbsolutePath() + " to file: " + buildPropertiesFile);
      GrouperInstallerUtils.copyFile(buildPropertiesTemplateFile, buildPropertiesFile);
    }
    
    //set the grouper property
    System.out.println("Editing " + buildPropertiesFile.getAbsolutePath() + ": ");
    String apiDir = GrouperInstallerUtils.replace(this.untarredApiDir.getAbsolutePath(),"\\\\", "/");
    apiDir = GrouperInstallerUtils.replace(apiDir, "\\", "/");
    editPropertiesFile(buildPropertiesFile, "grouper.folder", apiDir);
    editPropertiesFile(buildPropertiesFile, "should.copy.context.xml.to.metainf", "false");
    
  }
  
  /**
   * 
   * @param args
   */
  private void mainLogic(String[] args) {
    
    //####################################
    //Find out what directory to install to.  This ends in a file separator
    this.grouperInstallDirectoryString = grouperInstallDirectory();
    
    //####################################
    //System.out.println("Grouper install directory is: " + grouperInstallDirectoryFile.getAbsolutePath());

    System.out.print("Enter the Grouper version to install [" + GrouperInstallerUtils.propertiesValue("default.version", true) + "]: ");
    this.version = readFromStdIn();
    
    if (GrouperInstallerUtils.isBlank(this.version)) {
      this.version = GrouperInstallerUtils.propertiesValue("default.version", true);
    }
    
    //see if it is already downloaded
    
    
    File apiFile = downloadApi();
    
    //####################################
    //unzip/untar the api file
    File unzippedApiFile = unzip(apiFile.getAbsolutePath());
    this.untarredApiDir = untar(unzippedApiFile.getAbsolutePath());
    
    //####################################
    //ask about database
    this.dbUrl = "jdbc:hsqldb:hsql://localhost:9001/grouper";
    this.dbUser = "sa";
    this.dbPass = "";
    
    System.out.print("Do you want to use the default and included hsqldb database (t|f)? [t]: ");
    boolean useHsqldb = readFromStdInBoolean(true);

    if (!useHsqldb) {
      System.out.print("Database URL [jdbc:hsqldb:hsql://localhost:9001/grouper]: ");
      dbUrl = readFromStdIn();
      if (GrouperInstallerUtils.isBlank(dbUrl)) {
        dbUrl = "jdbc:hsqldb:hsql://localhost:9001/grouper";
      }
      System.out.print("Database user [sa]: ");
      dbUser = readFromStdIn();
      if (GrouperInstallerUtils.isBlank(dbUser)) {
        dbUser = "sa";
      }
      System.out.print("Database password (note, you aren't setting the pass here, you are using an existing pass, this will be echoed back) [<blank>]: ");
      dbPass = readFromStdIn();
      if (GrouperInstallerUtils.isBlank(dbPass)) {
        dbPass = "";
      }
    }
    
    this.giDbUtils = new GiDbUtils(this.dbUrl, this.dbUser, this.dbPass);
    
    //####################################
    //change the config file
    //get the config file    
    File grouperHibernatePropertiesFile = new File(untarredApiDir.getAbsoluteFile() + File.separator + "conf" 
        + File.separator + "grouper.hibernate.properties");
    
    //lets edit the three properties:
    System.out.println("Editing " + grouperHibernatePropertiesFile.getAbsolutePath() + ": ");
    editPropertiesFile(grouperHibernatePropertiesFile, "hibernate.connection.url", dbUrl);
    editPropertiesFile(grouperHibernatePropertiesFile, "hibernate.connection.username", dbUser);
    editPropertiesFile(grouperHibernatePropertiesFile, "hibernate.connection.password", dbPass);
    
    //####################################
    //check to see if listening on port?
    
    //####################################
    //lets get the java command

    validateJavaVersion();

    //#####################################
    //add driver to classpath
    this.addDriverJarToClasspath();
    
    //####################################
    //start database if needed (check on port?  ask to change port?)
    if (dbUrl.contains("hsqldb")) {
      //C:\mchyzer\grouper\trunk\grouper-installer\grouper.apiBinary-2.1.0
      startHsqlDb();
    }
    
    //####################################
    //check connection to database
    checkDatabaseConnection();
    
    //####################################
    //ask then init the DB
    initDb();
    addQuickstartSubjects();
    addQuickstartData();
    
    //####################################
    //get UI
    File uiDir = downloadUi();
    
    //####################################
    //unzip/untar the ui file
    File unzippedUiFile = unzip(uiDir.getAbsolutePath());
    this.untarredUiDir = untar(unzippedUiFile.getAbsolutePath());

    //####################################
    //configure UI
    configureUi();

    //####################################
    //get ant
    File antDir = downloadAnt();
    File unzippedAntFile = unzip(antDir.getAbsolutePath());
    this.untarredAntDir = untar(unzippedAntFile.getAbsolutePath());
    
    //####################################
    //build UI
    buildUi();

    //####################################
    //look for or ask or download tomcat
    File tomcatDir = downloadTomcat();
    File unzippedTomcatFile = unzip(tomcatDir.getAbsolutePath());
    this.untarredTomcatDir = untar(unzippedTomcatFile.getAbsolutePath());
    
    //####################################
    //ask for tomcat port
    configureTomcat();
    
    //####################################
    //configureTomcatUiWebapp
    configureTomcatUiWebapp();
    
    //####################################
    //set the GrouperSystem password
    tomcatConfigureGrouperSystem();
    
    //####################################
    //bounce tomcat
    tomcatBounce("restart");
    
    //####################################
    //tell user to go to url
    System.out.println("Go here for the Grouper UI (change hostname if on different host): http://localhost:" + this.tomcatHttpPort + "/" + this.tomcatUiPath + "/");
    
    //####################################
    //download the ws
    
    //####################################
    //configure where api is
    
    //####################################
    //build
    
    //####################################
    //copy to tomcat
    
    //####################################
    //bounce tomcat
    
    //####################################
    //download client
    
    //####################################
    //configure the url
    
    //####################################
    //add a user to tomcat users (ask for password?)
    
    //####################################
    //run a client command
    
    
    
  }

  /**
   * 
   */
  private int tomcatHttpPort = -1;
  
  
  /**
   * 
   */
  private void configureTomcat() {
    
    //see what the current ports are
    this.tomcatHttpPort = -1;
    
    File serverXmlFile = new File(this.untarredTomcatDir.getAbsolutePath() + File.separator + "conf" + File.separator + "server.xml");
    
    int shutdownPort = GrouperInstallerUtils.xpathEvaluateAttributeInt(serverXmlFile, "/Server", "port", -1);
    
    //  /Server/Service/Connector <Connector port="8080" protocol="HTTP/1.1" 
    this.tomcatHttpPort = GrouperInstallerUtils.xpathEvaluateAttributeInt(serverXmlFile, "/Server/Service/Connector[@protocol='HTTP/1.1']", "port", -1);
    // <Connector port="8009" protocol="AJP/1.3" redirectPort="8443" />
    int jkPort = GrouperInstallerUtils.xpathEvaluateAttributeInt(serverXmlFile, "/Server/Service/Connector[@protocol='AJP/1.3']", "port", -1);
    
    while(true) {
      System.out.print("What ports do you want tomcat to run on (HTTP, JK, shutdown): [" + this.tomcatHttpPort + ", " + jkPort + ", " + shutdownPort + "]: ");
      
      String ports = readFromStdIn();
      
      if (GrouperInstallerUtils.isBlank(ports)) {
        break;
      } else {
        String[] portsArray = GrouperInstallerUtils.splitTrim(ports, ",");
        if (GrouperInstallerUtils.length(portsArray) == 3) {
          for (String portString : portsArray) {
            try {
              GrouperInstallerUtils.intValue(portString);
            } catch (Exception e) {
              continue;
            }
          }
        } else {
          continue;
        }
        //ok, we have three integer entries
        this.tomcatHttpPort = GrouperInstallerUtils.intValue(portsArray[0]);
        jkPort = GrouperInstallerUtils.intValue(portsArray[1]);
        shutdownPort = GrouperInstallerUtils.intValue(portsArray[2]);
        
        System.out.println("Editing tomcat config file: " + serverXmlFile.getAbsolutePath());
        //lets edit the file
        //<Connector port="8080" protocol="HTTP/1.1" 
        editXmlFile(serverXmlFile, "port=\"([\\d]+)\"", new String[]{"<Connector", "protocol=\"HTTP/1.1\""}, 
            new String[]{"SSLEnabled=\"true\""}, Integer.toString(this.tomcatHttpPort), "tomcat HTTP port");
        //<Connector port="8009" protocol="AJP/1.3" redirectPort="8443" />
        editXmlFile(serverXmlFile, "port=\"([\\d]+)\"", new String[]{"<Connector", "protocol=\"AJP/1.3\""}, null, Integer.toString(jkPort), "tomcat JK port");
        //<Server port="8005" shutdown="SHUTDOWN">
        editXmlFile(serverXmlFile, "port=\"([\\d]+)\"", new String[]{"<Server", "shutdown=\"SHUTDOWN\""}, null, Integer.toString(shutdownPort), "tomcat shutdown port");
        break;
      }
    }
  }
  
  /**
   * 
   * @param grouperInstallDirectoryString
   * @return the file of the directory of API
   */
  private File downloadApi() {
    String urlToDownload = GrouperInstallerUtils.propertiesValue("download.server.url", true);
    
    if (!urlToDownload.endsWith("/")) {
      urlToDownload += "/";
    }
    urlToDownload += "release/";
    String apiFileName = "grouper.apiBinary-" + this.version + ".tar.gz";
    urlToDownload += this.version + "/" + apiFileName;

    File apiFile = new File(this.grouperInstallDirectoryString + apiFileName);
    
    boolean useLocalApi = false;
    
    if (apiFile.exists()) {
      System.out.print("File exists: " + apiFile.getAbsolutePath() + ", should we use the local file (t|f)? [t]: ");
      useLocalApi = readFromStdInBoolean(true);
      
    }
    
    if (!useLocalApi) {
      downloadFile(urlToDownload, apiFile.getAbsolutePath());
    }
    return apiFile;
  }

  /**
   * 
   * @param grouperInstallDirectoryString
   * @return the file of the directory of API
   */
  private File downloadUi() {
    String urlToDownload = GrouperInstallerUtils.propertiesValue("download.server.url", true);
    
    if (!urlToDownload.endsWith("/")) {
      urlToDownload += "/";
    }
    urlToDownload += "release/";

    String uiFileName = "grouper.ui-" + this.version + ".tar.gz";
    urlToDownload += this.version + "/" + uiFileName;

    File uiFile = new File(this.grouperInstallDirectoryString + uiFileName);
    
    boolean useLocalUi = false;
    
    if (uiFile.exists()) {
      System.out.print("File exists: " + uiFile.getAbsolutePath() + ", should we use the local file (t|f)? [t]: ");
      useLocalUi = readFromStdInBoolean(true);
      
    }
    
    if (!useLocalUi) {
      downloadFile(urlToDownload, uiFile.getAbsolutePath());
    }
    return uiFile;
  }

  /**
   * 
   * @return the file of the directory of ant
   */
  private File downloadAnt() {
    String urlToDownload = GrouperInstallerUtils.propertiesValue("download.server.url", true);
    
    if (!urlToDownload.endsWith("/")) {
      urlToDownload += "/";
    }

    urlToDownload += "downloads/tools/apache-ant-1.8.2-bin.tar.gz";
    
    File antFile = new File(this.grouperInstallDirectoryString + "apache-ant-1.8.2-bin.tar.gz");
    
    boolean useLocalAnt = false;
    
    if (antFile.exists()) {
      System.out.print("File exists: " + antFile.getAbsolutePath() + ", should we use the local file (t|f)? [t]: ");
      useLocalAnt = readFromStdInBoolean(true);
      
    }
    
    if (!useLocalAnt) {
      downloadFile(urlToDownload, antFile.getAbsolutePath());
    }
    return antFile;
  }

  /**
   * 
   * @return the file of the directory of tomcat
   */
  private File downloadTomcat() {
    String urlToDownload = GrouperInstallerUtils.propertiesValue("download.server.url", true);
    
    if (!urlToDownload.endsWith("/")) {
      urlToDownload += "/";
    }

    urlToDownload += "downloads/tools/apache-tomcat-6.0.35.tar.gz";
    
    File tomcatFile = new File(this.grouperInstallDirectoryString + "apache-tomcat-6.0.35.tar.gz");
    
    boolean useLocalTomcat = false;
    
    if (tomcatFile.exists()) {
      System.out.print("File exists: " + tomcatFile.getAbsolutePath() + ", should we use the local file (t|f)? [t]: ");
      useLocalTomcat = readFromStdInBoolean(true);
      
    }
    
    if (!useLocalTomcat) {
      downloadFile(urlToDownload, tomcatFile.getAbsolutePath());
    }
    return tomcatFile;
  }

  /**
   * add quick start subjects
   */
  private void addQuickstartSubjects() {
    
    System.out.print("Do you want to add quick start subjects to DB (t|f)? [t] ");
    boolean addQuickstartSubjects = readFromStdInBoolean(true);
    
    if (addQuickstartSubjects) {
      String url = "http://anonsvn.internet2.edu/cgi-bin/viewvc.cgi/i2mi/tags/" + tag() + "/grouper-qs-builder/subjects.sql?view=co";
      String subjectsSqlFileName = this.untarredApiDir.getParent() + File.separator + "subjects.sql";
      File subjectsSqlFile = new File(subjectsSqlFileName);
      downloadFile(url, subjectsSqlFileName);

      List<String> commands = new ArrayList<String>();
      
      commands.add("cmd");
      commands.add("/c");
      commands.add(gshCommand());
      commands.add("-registry");
      commands.add("-runsqlfile");
      commands.add(subjectsSqlFile.getAbsolutePath());
      commands.add("-noprompt");
      
      System.out.println("\n##################################");
      System.out.println("Adding sample subjects with command: " + GrouperInstallerUtils.join(commands.iterator(), ' ') + "\n");
      
      CommandResult commandResult = GrouperInstallerUtils.execCommand(GrouperInstallerUtils.toArray(commands, String.class));
      
      if (!GrouperInstallerUtils.isBlank(commandResult.getErrorText())) {
        System.out.println("stderr: " + commandResult.getErrorText());
      }
      if (!GrouperInstallerUtils.isBlank(commandResult.getOutputText())) {
        System.out.println("stdout: " + commandResult.getOutputText());
      }
      
      System.out.println("\nEnd adding sample subjects");
      System.out.println("##################################\n");

    }
  }
  
  /**
   * add quick start subjects
   */
  private void addQuickstartData() {
    
    System.out.print("Do you want to add quickstart data to registry (t|f)? [t] ");
    boolean addQuickstartData = readFromStdInBoolean(true);
    
    if (addQuickstartData) {
      String url = "http://anonsvn.internet2.edu/cgi-bin/viewvc.cgi/i2mi/tags/" + tag() + "/grouper-qs-builder/quickstart.xml?view=co";
      String quickstartFileName = this.untarredApiDir.getParent() + File.separator + "quickstart.xml";
      File quickstartFile = new File(quickstartFileName);
      downloadFile(url, quickstartFileName);

      List<String> commands = new ArrayList<String>();
      
      commands.add("cmd");
      commands.add("/c");
      commands.add(gshCommand());
      commands.add("-xmlimportold");
      commands.add("GrouperSystem");
      commands.add(quickstartFile.getAbsolutePath());
      commands.add("-noprompt");
      
      System.out.println("\n##################################");
      System.out.println("Adding quickstart data with command: " + GrouperInstallerUtils.join(commands.iterator(), ' ') + "\n");
      
      CommandResult commandResult = GrouperInstallerUtils.execCommand(GrouperInstallerUtils.toArray(commands, String.class));
      
      if (!GrouperInstallerUtils.isBlank(commandResult.getErrorText())) {
        System.out.println("stderr: " + commandResult.getErrorText());
      }
      if (!GrouperInstallerUtils.isBlank(commandResult.getOutputText())) {

        System.out.println("stdout: " + commandResult.getOutputText());
      }
      System.out.println("\nEnd adding quickstart data");
      System.out.println("##################################\n");

    }
  }
  
  /**
   * 
   */
  private void initDb() {
    System.out.print("Do you want to init the database (delete all existing grouper tables, add new ones) (t|f)? ");
    boolean initdb = readFromStdInBoolean(null);
    
    if (initdb) {
      List<String> commands = new ArrayList<String>();
      commands.add("cmd");
      commands.add("/c");
      commands.add(gshCommand());
      commands.add("-registry");
      commands.add("-drop");
      commands.add("-runscript");
      commands.add("-noprompt");
      
      System.out.println("\n##################################");
      System.out.println("Initting DB with command: " + GrouperInstallerUtils.join(commands.iterator(), ' ') + "\n");
      
      CommandResult commandResult = GrouperInstallerUtils.execCommand(GrouperInstallerUtils.toArray(commands, String.class));
      
      if (!GrouperInstallerUtils.isBlank(commandResult.getErrorText())) {
        System.out.println("stderr: " + commandResult.getErrorText());
      }
      if (!GrouperInstallerUtils.isBlank(commandResult.getOutputText())) {

        System.out.println("stdout: " + commandResult.getOutputText());
      }
      System.out.println("\nEnd Initting DB");
      System.out.println("##################################\n");
      
      
    }

  }
  
  /**
   * 
   * @return the gsh command
   */
  private String gshCommand() {
    //TODO change this for unix
    return this.untarredApiDir.getAbsolutePath() + File.separator + "bin" + File.separator + "gsh.bat";
  }
  
  /**
   * 
   */
  private void checkDatabaseConnection() {
    System.out.println("Checking database with query: " + this.giDbUtils.checkConnectionQuery());
    Exception exception = this.giDbUtils.checkConnection();
    if (exception == null) {
      System.out.println("Successfully tested database connection");
    } else {
      System.out.println("Error: could not connect to the database: ");
      exception.printStackTrace();
      
    }
  }

  /**
   * 
   * @param untarredApiDir
   */
  private void startHsqlDb() {
    System.out.print("Do you want this script to start the hsqldb database (note, it must not be running in able to start) (t|f)? [t]: ");
    boolean startdb = readFromStdInBoolean(true);
    if (startdb) {
      
      shutdownHsql();

      //TODO get right port
      final List<String> command = new ArrayList<String>();
      //command.add("cmd");
      //command.add("/a");
      //command.add("start");
      //command.add("/b");
      command.add(GrouperInstallerUtils.javaCommand());
      command.add("-cp");
      command.add(untarredApiDir + File.separator + "lib" + File.separator + "jdbcSamples" + File.separator 
          + "hsqldb.jar");
      //-cp lib\jdbcSamples\hsqldb.jar org.hsqldb.Server -database.0 file:grouper -dbname.0 grouper
      command.addAll(GrouperInstallerUtils.splitTrimToList("org.hsqldb.Server -database.0 file:" + untarredApiDir + File.separator + "grouper -dbname.0 grouper", " "));
      
//        System.out.println("Starting DB with command: java -cp grouper.apiBinary-" + this.version + File.separator 
//            + "lib" + File.separator + "jdbcSamples" + File.separator 
//            + "hsqldb.jar org.hsqldb.Server -database.0 file:grouper -dbname.0 grouper");

      System.out.println("Starting DB with command: " + GrouperInstallerUtils.join(command.iterator(), " "));
      
      //start in new thread
      //start in new thread
      Thread thread = new Thread(new Runnable() {
        
        @Override
        public void run() {
          GrouperInstallerUtils.execCommand(GrouperInstallerUtils.toArray(command, String.class), true, true);
        }
      });
      thread.setDaemon(true);
      thread.start();

      
    }
    
    //lets sleep for a bit to let it start
    GrouperInstallerUtils.sleep(2000);
    
  }

  /** gi db utils */
  private GiDbUtils giDbUtils = null;
  
  /**
   * 
   */
  private void shutdownHsql() {
    try {
      this.giDbUtils.executeUpdate("SHUTDOWN", null, false);
      System.out.println("Shutting down HSQL before starting it by sending the SQL: SHUTDOWN");
    } catch (Exception e) {
      //e.printStackTrace();
      System.out.println("HSQL was not detected to be running (did not successfully stop it)");
    }
  }

  /**
   * 
   */
  private void configureTomcatUiWebapp() {
    
    File serverXmlFile = new File(this.untarredTomcatDir.getAbsolutePath() 
        + File.separator + "conf" + File.separator + "server.xml");
    
    //C:\mchyzer\grouper\trunk\grouper-installer\grouper.ui-2.0.2\dist\grouper
    //
    //<Context docBase="C:\mchyzer\grouper\trunk\grouper-ws_trunk\webapp" path="/grouper-ws" reloadable="false"/>
    //Server
    //Service
    //Engine
    //Host

    System.out.print("Enter the URL path for the UI [grouper]: ");
    this.tomcatUiPath = readFromStdIn();
    
    if (GrouperInstallerUtils.isBlank(this.tomcatUiPath)) {
      this.tomcatUiPath = "grouper";
    }

    if (this.tomcatUiPath.endsWith("/") || this.tomcatUiPath.endsWith("\\")) {
      this.tomcatUiPath = this.tomcatUiPath.substring(0, this.tomcatUiPath.length()-1);
    }
    if (this.tomcatUiPath.startsWith("/") || this.tomcatUiPath.startsWith("\\")) {
      this.tomcatUiPath = this.tomcatUiPath.substring(1, this.tomcatUiPath.length());
    }
    
    String currentDocBase = GrouperInstallerUtils.xpathEvaluateAttribute(serverXmlFile, 
        "Server/Service/Engine/Host/Context[@path='/" + this.tomcatUiPath + "']", "docBase");

    String shouldBeDocBase = this.untarredUiDir.getAbsolutePath() + File.separator + "dist" + File.separator + this.tomcatUiPath;

    System.out.println("Editing tomcat config file: " + serverXmlFile.getAbsolutePath());
    
    if (GrouperInstallerUtils.isBlank(currentDocBase)) {

      //need to add it
      //<Host appBase="webapps" autoDeploy="true" name="localhost" unpackWARs="true" xmlNamespaceAware="false" xmlValidation="false">
      //<Context docBase="C:\mchyzer\grouper\trunk\grouper-ws_trunk\webapp" path="/grouper-ws" reloadable="false"/>
      addToXmlFile(serverXmlFile, ">", new String[]{"<Host "}, 
          "<Context docBase=\"" + shouldBeDocBase + "\" path=\"/" + this.tomcatUiPath + "\" reloadable=\"false\"/>", "tomcat context for UI");

    } else {

      if (!GrouperInstallerUtils.equals(currentDocBase, shouldBeDocBase)) {
        
        //lets edit the file
        //<Context docBase="C:\mchyzer\grouper\trunk\grouper-ws_trunk\webapp" path="/grouper-ws" reloadable="false"/>
        editXmlFile(serverXmlFile, "docBase=\"([^\"]+)\"", new String[]{"<Context", "path=\"/" + this.tomcatUiPath + "\""}, 
            null, shouldBeDocBase, "tomcat context for UI");

      } else {
        
        System.out.println("  - Context is already set for Grouper UI");
        
      }
      
      
    }
    
    currentDocBase = GrouperInstallerUtils.xpathEvaluateAttribute(serverXmlFile, 
        "Server/Service/Engine/Host/Context[@path='/" + this.tomcatUiPath + "']", "docBase");
    
    if (!GrouperInstallerUtils.equals(currentDocBase, shouldBeDocBase)) {
      System.out.println("Tried to edit server.xml but it didnt work, should have context of: '" 
          + shouldBeDocBase + "', but was: '" + currentDocBase + "'");
    }
    
  }

  /**
   * edit a property in a property file
   * @param file
   * @param valueRegex 
   * @param lineMustHaveRegexes 
   * @param lineCantHaveRegexes 
   * @param newValue 
   * @param description of change for sys out print
   */
  public static void editXmlFile(File file, String valueRegex, String[] lineMustHaveRegexes, String[] lineCantHaveRegexes, String newValue, String description) {
    if (!file.exists() || file.length() == 0) {
      throw new RuntimeException("Why does " + file.getName() + " not exist and have contents? " 
          + file.getAbsolutePath());
    }
    
    String fileContents = GrouperInstallerUtils.readFileIntoString(file);
    
    String newline = newlineFromFile(fileContents);
    
    String[] lines = fileContents.split("[\\r\\n]+");
    
    Pattern pattern = Pattern.compile(valueRegex);
    
    Pattern[] lineMustHavePatterns = new Pattern[GrouperInstallerUtils.length(lineMustHaveRegexes)];
    
    {
      int index = 0;
      for (String lineMustHaveRegex : GrouperInstallerUtils.nonNull(lineMustHaveRegexes, String.class)) {
        Pattern lineMustHavePattern = Pattern.compile(lineMustHaveRegex);
        lineMustHavePatterns[index] = lineMustHavePattern;
        
        index++;
      }
    }    
  
    Pattern[] lineCantHavePatterns = new Pattern[GrouperInstallerUtils.length(lineCantHaveRegexes)];
    
    {
      int index = 0;
      for (String lineCantHaveRegex : GrouperInstallerUtils.nonNull(lineCantHaveRegexes, String.class)) {
        Pattern lineCantHavePattern = Pattern.compile(lineCantHaveRegex);
        lineCantHavePatterns[index] = lineCantHavePattern;
        
        index++;
      }
    }    
  
    StringBuilder newfile = new StringBuilder();
    
    boolean madeChange = false;
    
    OUTER: for (String line : lines) {
      line = GrouperInstallerUtils.defaultString(line);
      
      //lets see if it satisfies all
      for (Pattern lineMustHavePattern : lineMustHavePatterns) {
        if (!lineMustHavePattern.matcher(line).find()) {
          newfile.append(line).append(newline);
          continue OUTER;
        }
      }
      
      //lets see if it doesnt have these
      for (Pattern lineCantHavePattern : lineCantHavePatterns) {
        if (lineCantHavePattern.matcher(line).find()) {
          newfile.append(line).append(newline);
          continue OUTER;
        }
      }
      
      //see if satisfies current
      Matcher matcher = pattern.matcher(line);
      if (!matcher.find()) {
        newfile.append(line).append(newline);
        continue;
      }
      
      String oldValue = matcher.group(1);
      if (GrouperInstallerUtils.equals(newValue, oldValue)) {
        System.out.println(" - old " + description + " value is same as new value: " + newValue);
        newfile.append(line).append(newline);
        continue;
      }
      
      //we need to change the value
      System.out.println(" - changing " + description + " from old value: '" + oldValue + "' to new value: '" + newValue + "'");
      newfile.append(line.substring(0, matcher.start(1)));
      newfile.append(newValue);
      newfile.append(line.substring(matcher.end(1), line.length()));
      newfile.append(newline);
      madeChange = true;
      continue;
    }
    
    if (!madeChange) {
      return;
    }
    
    GrouperInstallerUtils.writeStringToFile(file, newfile.toString());
    
  }

  /** tomcat ui path */
  private String tomcatUiPath = null;
  
  /**
   * 
   */
  private static void validateJavaVersion() {
    CommandResult commandResult = GrouperInstallerUtils.execCommand(
        GrouperInstallerUtils.javaCommand(), 
        new String[]{"-version"});
    String javaResult = commandResult.getOutputText();
    if (GrouperInstallerUtils.isBlank(javaResult)) {
      javaResult = commandResult.getErrorText();
    }
    if (!validJava(javaResult)) {
      throw new RuntimeException("Expecting Java 6+, but received: " + javaResult + ", run the installer jar with a jdk v6");
    }
    
    //try javac
    try {
      commandResult = GrouperInstallerUtils.execCommand(
          GrouperInstallerUtils.javaCommand() + "c", 
          new String[]{"-version"});
      javaResult = commandResult.getOutputText();
      if (GrouperInstallerUtils.isBlank(javaResult)) {
        javaResult = commandResult.getErrorText();
      }
      if (!validJava(javaResult)) {
        throw new RuntimeException("Expecting Java 6+, but received: " + javaResult + ", run the installer jar with a jdk v6");
      }
    } catch (Exception e) {
      throw new RuntimeException("This needs to be run from a jdk, but it is detected to be running from a JRE...  run the installed from a JDK!");
    }
  }

  /**
   * 
   * @return install directory
   */
  private static String grouperInstallDirectory() {
    String grouperInstallDirectoryString;
    {
      File grouperInstallDirectoryFile = new File("");
      System.out.print("Enter in the Grouper install directory (note: better if no spaces or special chars) [" 
          + grouperInstallDirectoryFile.getAbsolutePath() + "]: ");
      String input = readFromStdIn();
      if (!GrouperInstallerUtils.isBlank(input)) {
        grouperInstallDirectoryFile = new File(input);
        if (!grouperInstallDirectoryFile.exists() || !grouperInstallDirectoryFile.isDirectory()) {
          System.out.println("Error: cant find directory: '" + input + "'");
          System.exit(1);
        }
      }
      grouperInstallDirectoryString = grouperInstallDirectoryFile.getAbsolutePath();
      if (!grouperInstallDirectoryString.endsWith(File.separator)) {
        grouperInstallDirectoryString += File.separator;
      }
    }
    return grouperInstallDirectoryString;
  }

  /**
   * 
   * @param javaResult
   * @return if valid
   */
  private static boolean validJava(String javaResult) {
    //C:\mchyzer\grouper\trunk\grouper\bin>java -version
    //java version "1.6.0_21"
    //Java(TM) SE Runtime Environment (build 1.6.0_21-b07)
    //Java HotSpot(TM) Client VM (build 17.0-b17, mixed mode, sharing)
    Pattern pattern = Pattern.compile("(\\d+)\\.(\\d+)\\.\\d+_?\\d*");
    Matcher matcher = pattern.matcher(javaResult);
    if (matcher.find()) {
      int majorVersion = GrouperInstallerUtils.intValue(matcher.group(1));
      int minorVersion = GrouperInstallerUtils.intValue(matcher.group(2));
      if (majorVersion == 1 && minorVersion >= 6) {
        return true;
      }
      return majorVersion >= 6;
    }
    return false;
  }
  
  /**
   * add something to an xml file
   * @param file
   * @param addAfterThisRegex 
   * @param mustPassTheseRegexes 
   * @param lineCantHaveRegexes 
   * @param newValue 
   * @param description of change for sys out print
   */
  public static void addToXmlFile(File file, String addAfterThisRegex, String[] mustPassTheseRegexes, String newValue, String description) {
    if (!file.exists() || file.length() == 0) {
      throw new RuntimeException("Why does " + file.getName() + " not exist and have contents? " 
          + file.getAbsolutePath());
    }
    
    String fileContents = GrouperInstallerUtils.readFileIntoString(file);
    
    String newline = newlineFromFile(fileContents);
    
    Pattern pattern = Pattern.compile(addAfterThisRegex);

    String[] lines = fileContents.split("[\\r\\n]+");
    
    Pattern[] lineMustPassThesePatterns = new Pattern[GrouperInstallerUtils.length(mustPassTheseRegexes)];
    
    boolean[] hasPassedTheseRegexes = new boolean[lineMustPassThesePatterns.length];
    
    for (int i=0;i<hasPassedTheseRegexes.length;i++) {
      hasPassedTheseRegexes[i] = false;
    }
    
    {
      int index = 0;
      for (String lineMustHaveRegex : GrouperInstallerUtils.nonNull(mustPassTheseRegexes, String.class)) {
        Pattern lineMustHavePattern = Pattern.compile(lineMustHaveRegex);
        lineMustPassThesePatterns[index] = lineMustHavePattern;
        
        index++;
      }
    }    

    StringBuilder newfile = new StringBuilder();

    boolean madeChange = false;

    OUTER: for (String line : lines) {
      line = GrouperInstallerUtils.defaultString(line);
      
      //lets see if it satisfies all
      for (int i=0;i<lineMustPassThesePatterns.length;i++) {
        Pattern lineMustHavePattern = lineMustPassThesePatterns[i];
        if (lineMustHavePattern.matcher(line).find()) {
          hasPassedTheseRegexes[i] = true;
        }
      }
      
      //see if we have passed all the prefixes
      for (int i=0;i<hasPassedTheseRegexes.length;i++) {
        if (!hasPassedTheseRegexes[i]) {
          newfile.append(line).append(newline);
          continue OUTER;
        }
      }
      
      //see if satisfies current, and only add once
      Matcher matcher = pattern.matcher(line);
      if (!matcher.find() || madeChange) {
        newfile.append(line).append(newline);
        continue;
      }

      //we need to change the value
      System.out.println(" - adding " + description + " line: '" + newValue + "'");
      newfile.append(line);
      newfile.append(newline);
      newfile.append(newValue);
      newfile.append(newline);
      madeChange = true;
    }
    if (!madeChange) {
      throw new RuntimeException("Couldnt find place to add to server.xml!  Are there newlines that werent there before or something?");
    }
    
    GrouperInstallerUtils.writeStringToFile(file, newfile.toString());
    
  }

  
  /**
   * edit a property in a property file
   * @param file
   * @param propertyName
   * @param propertyValue
   */
  public static void editPropertiesFile(File file, String propertyName, String propertyValue) {
    if (!file.exists() || file.length() == 0) {
      throw new RuntimeException("Why does " + file.getName() + " not exist and have contents? " 
          + file.getAbsolutePath());
    }
    
    String fileContents = GrouperInstallerUtils.readFileIntoString(file);
    
    String newline = newlineFromFile(fileContents);
    
    //if it starts with it, add a newline to start so the regexes work
    if (!fileContents.startsWith(newline)) {
      fileContents = newline + fileContents;
    }
    
    //lets look for property in file
    //this is a newline or form feed then some optional whitespace, and the property name
    //then some optional whitespace then an equals, then optional whitespace, then some text
    String propertyNameRegex = GrouperInstallerUtils.replace(propertyName, ".", "\\.");
    String regex = "[\\n\\r][ \\t]*" + propertyNameRegex + "[ \\t]*=[ \\t]*([^\\n\\r]*)";
    Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
    Matcher matcher = pattern.matcher(fileContents);

    if (matcher.find()) {
      String previousValue = matcher.group(1);
      
      if (GrouperInstallerUtils.trimToEmpty(previousValue).equals(GrouperInstallerUtils.trim(propertyValue))) {
        System.out.println(" - property " + propertyName + " already was set to: " + propertyValue + ", not changing file");
        return;
      }
      
      int startIndex = matcher.start(1);
      
      int endIndex = matcher.end(1);
      
      String newContents = fileContents.substring(0, startIndex) + propertyValue;
      
      //if not the last char
      if (endIndex < fileContents.length()-1) {
        newContents += fileContents.substring(endIndex, fileContents.length());
      }

      //if there is another match, there is a problem
      if (matcher.find()) {
        throw new RuntimeException("Why are there multiple matches for " + propertyName + " in propertyFile: " + file.getAbsolutePath() + "??????");
      }

      System.out.println(" - set property: " 
          + propertyName + " from: " + previousValue + " to: " + propertyValue);
      GrouperInstallerUtils.writeStringToFile(file, newContents);
      return;
    }
    
    //lets see if it is in a comment
    //this is a newline or form feed then some optional whitespace, hash, optional whitespace, and the property name
    //then some optional whitespace then an equals, then optional whitespace, then some text
    regex = ".*[\\n\\r]([ \\t]*#[ \\t]*)" + propertyNameRegex + "[ \\t]*=[ \\t]*([^\\n\\r]*).*";
    pattern = Pattern.compile(regex, Pattern.DOTALL);
    matcher = pattern.matcher(fileContents);

    if (matcher.matches()) {
      String previousValue = matcher.group(2);
      
      int startIndexHash = matcher.start(1);
      
      int endIndexHash = matcher.end(1);

      int startIndex = matcher.start(2);
      
      int endIndex = matcher.end(2);
      
      String newContents = fileContents.substring(0, startIndexHash) + fileContents.substring(endIndexHash, startIndex)
        + propertyValue;
      
      //if not the last char
      if (endIndex < fileContents.length()-1) {
        newContents += fileContents.substring(endIndex, fileContents.length());
      }
      System.out.println(" - uncommented property: " 
          + propertyName + " from: " + previousValue + " to: " + propertyValue);
      GrouperInstallerUtils.writeStringToFile(file, newContents);
      
      return;
    }
    
    //it must have not existed
    //add a newline..
    //add to end in case it was already there, now it will be overwritten
    String newContents = fileContents + newline + "# added by grouper-installer" + newline + propertyName + " = " + propertyValue + newline;
    GrouperInstallerUtils.writeStringToFile(file, newContents);

    System.out.println(" - added to end of property file: " + propertyName + " = " + propertyValue);
    
  }

  /**
   * based on file contents see what the newline type is
   * @param fileContents
   * @return the newline
   */
  private static String newlineFromFile(String fileContents) {
    String newline = "\n";
    if (fileContents.contains("\\r\\n")) {
      newline = "\\r\\n";
    }
    if (fileContents.contains("\\n\\r")) {
      newline = "\\n\\r";
    }
    if (fileContents.contains("\\r")) {
      newline = "\\r";
    }
    return newline;
  }
  
  /**
   * untar a file to a dir
   * @param fileName
   * @return the directory where the files are (assuming has a single dir the same name as the archive)
   */
  private static File untar(String fileName) {
    if (!fileName.endsWith(".tar")) {
      throw new RuntimeException("File doesnt end in .tar: " + fileName);
    }
    String untarredFileName = fileName.substring(0, fileName.length() - ".tar".length());
    
    //ant has -bin which is annoying
    if (untarredFileName.endsWith("-bin")) {
      untarredFileName = untarredFileName.substring(0, untarredFileName.length() - "-bin".length());
    }
    
    File untarredFile = new File(untarredFileName);
    if (untarredFile.exists()) {
      
      System.out.print("Untarred dir exists: " + untarredFileName + ", use untarred dir (t|f)? [t]: ");
      boolean useUnzippedFile = readFromStdInBoolean(true);
      if (useUnzippedFile) {
        return untarredFile;
      }
      
      System.out.println("Deleting: " + untarredFileName);
      GrouperInstallerUtils.deleteRecursiveDirectory(untarredFileName);
    }
    
    System.out.println("Expanding: " + fileName);
    
    String unzippedParent = untarredFile.getParentFile().getAbsolutePath();
    if (unzippedParent.endsWith(File.separator)) {
      unzippedParent = unzippedParent.substring(0, unzippedParent.length()-1);
    }
    
    TarArchiveInputStream tarArchiveInputStream = null;
    
    try {
      tarArchiveInputStream = new TarArchiveInputStream(new FileInputStream(fileName));
      
      while (true) {
  
        TarArchiveEntry tarArchiveEntry = tarArchiveInputStream.getNextTarEntry();
        if (tarArchiveEntry == null) {
          break;
        }
        
        //System.out.println("Entry: " + tarArchiveEntry.getName()
        //    + ", isDirectory: " + tarArchiveEntry.isDirectory()
        //    + ", isFile: " + tarArchiveEntry.isFile());
        String fileEntryName = unzippedParent + File.separator + tarArchiveEntry.getName();
        File tarEntryFile = new File(fileEntryName);
        
        if (tarArchiveEntry.isDirectory()) {
          if (!tarEntryFile.exists() && !tarEntryFile.mkdirs()) {
            throw new RuntimeException("Cant create dirs: " + tarEntryFile.getAbsolutePath());
          }
          continue;
        }
        
        byte[] content = new byte[(int)tarArchiveEntry.getSize()];

        int size = tarArchiveInputStream.read(content, 0, content.length);
        
        //for some reason we get an error when 0 bytes...
        if (size != content.length && (!(size == -1 && content.length == 0))) {
          throw new RuntimeException("Didnt read the right amount of bytes: " + size 
              + ", should have been: " + content.length + " on entry: " + tarArchiveEntry.getName());
        }
        
        ByteArrayInputStream byteArrayInputStream = null;
        FileOutputStream fileOutputStream = null;
        
        try {
          
          //create parent directories
          if (!tarEntryFile.getParentFile().exists() && !tarEntryFile.getParentFile().mkdirs()) {
            throw new RuntimeException("Cant create dirs: " + tarEntryFile.getParentFile().getAbsolutePath());
          }

          fileOutputStream = new FileOutputStream(tarEntryFile);
          byteArrayInputStream = new ByteArrayInputStream(content);
          GrouperInstallerUtils.copy(byteArrayInputStream, fileOutputStream);
          
        } catch (Exception e) {
          throw new RuntimeException("Probem with entry: " + tarArchiveEntry.getName(), e);
        } finally {
          GrouperInstallerUtils.closeQuietly(byteArrayInputStream);
          GrouperInstallerUtils.closeQuietly(fileOutputStream);
        }
        
      }
    } catch (Exception e) {
      throw new RuntimeException("Error untarring: " + fileName, e);
    } finally {
      GrouperInstallerUtils.closeQuietly(tarArchiveInputStream);
    }
    return untarredFile;
  }
  
  /**
   * unzip a file to another file
   * @param fileName
   * @return the unzipped file
   */
  private static File unzip(String fileName) {
    if (!fileName.endsWith(".gz")) {
      throw new RuntimeException("File doesnt end in .gz: " + fileName);
    }
    String unzippedFileName = fileName.substring(0, fileName.length() - ".gz".length());
    
    File unzippedFile = new File(unzippedFileName);
    if (unzippedFile.exists()) {
      
      System.out.print("Unzipped file exists: " + unzippedFileName + ", use unzipped file (t|f)? [t]: ");
      boolean useUnzippedFile = readFromStdInBoolean(true);
      if (useUnzippedFile) {
        return unzippedFile;
      }
      System.out.println("Deleting: " + unzippedFileName);
      if (!unzippedFile.delete()) {
        throw new RuntimeException("Cant delete file: " + unzippedFileName);
      }
    }

    System.out.println("Unzipping: " + fileName);
    
    GzipCompressorInputStream gzipCompressorInputStream = null;
    FileOutputStream fileOutputStream = null;
    try {
      gzipCompressorInputStream = new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(new File(fileName))));
      fileOutputStream = new FileOutputStream(unzippedFile);
      GrouperInstallerUtils.copy(gzipCompressorInputStream, fileOutputStream);
    } catch (Exception e) {
      throw new RuntimeException("Cant unzip file: " + fileName, e);
    } finally {
      GrouperInstallerUtils.closeQuietly(gzipCompressorInputStream);
      GrouperInstallerUtils.closeQuietly(fileOutputStream);
    }
    return unzippedFile;
  }
  
}
