#!/bin/sh
echo ---------------------------------------------------
echo Selenium Auto Exec Server(AES) Startup.
echo ---------------------------------------------------

SELENIUM_AES_CLASSPATH='selenium-autoexec-server-0.1.jar:lib/activation-1.1.jar:lib/commons-collections-3.2.jar:lib/commons-lang-2.3.jar:lib/mail-1.4.jar:lib/selenium-ext-htmlsuite-0.2.jar:lib/selenium-server-0.9.2.jar:lib/svnkit-1.1.4.jar:lib/velocity-1.5.jar'

java -classpath $SELENIUM_AES_CLASSPATH com.enjoyxstudy.selenium.autoexec.AutoExecServer
