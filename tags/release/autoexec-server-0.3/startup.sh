#!/bin/sh
# ---------------------------------------------------
# Selenium Auto Exec Server(AES) Startup.
# ---------------------------------------------------

. ./set_classpath

java -classpath $SELENIUM_AES_CLASSPATH com.enjoyxstudy.selenium.autoexec.AutoExecServer
