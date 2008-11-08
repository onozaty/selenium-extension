@echo off
rem ---------------------------------------------------
rem  Selenium Auto Exec Server(AES) Shutdown.
rem ---------------------------------------------------

call set_classpath.bat

java -classpath %SELENIUM_AES_CLASSPATH% com.enjoyxstudy.selenium.autoexec.AutoExecServer shutdown
