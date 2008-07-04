@echo off
rem ---------------------------------------------------
rem  Selenium Auto Exec Server(AES) Shutdown.
rem ---------------------------------------------------

set SELENIUM_AES_CLASSPATH=selenium-autoexec-server-0.2.jar;lib\activation-1.1.jar;lib\commons-collections-3.2.jar;lib\commons-lang-2.3.jar;lib\mail-1.4.jar;lib\selenium-ext-htmlsuite-0.3.jar;lib\selenium-server-0.9.2.jar;lib\svnkit-1.1.4.jar;lib\velocity-1.5.jar

java -classpath %SELENIUM_AES_CLASSPATH% com.enjoyxstudy.selenium.autoexec.AutoExecServer shutdown
