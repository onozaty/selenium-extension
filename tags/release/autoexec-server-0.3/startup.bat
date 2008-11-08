@echo off
rem ---------------------------------------------------
rem  Selenium Auto Exec Server(AES) Startup.
rem ---------------------------------------------------

call set_classpath.bat

java -classpath %SELENIUM_AES_CLASSPATH% com.enjoyxstudy.selenium.autoexec.AutoExecServer
